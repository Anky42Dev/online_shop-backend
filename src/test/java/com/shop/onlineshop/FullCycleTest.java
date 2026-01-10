package com.shop.onlineshop;

import com.shop.onlineshop.models.entity.*;
import com.shop.onlineshop.models.model.OrderStatus;
import com.shop.onlineshop.models.request.*;
import com.shop.onlineshop.models.response.OrderResponse;
import com.shop.onlineshop.repo.*;
import com.shop.onlineshop.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5439/shop_db",
        "spring.datasource.username=postgres", // Убедитесь, что юзернейм верный
        "spring.datasource.password=postgres"  // <--- ВАЖНО: Замените на ваш пароль из docker-compose
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FullCycleTest {

    @Autowired private UserService userService;
    @Autowired private ProductService productService;
    @Autowired private CustomerOrderService orderService;
    @Autowired private CourierService courierService;
    @Autowired private DeliveryTaskRepo deliveryTaskRepo;
    @Autowired private RoleRepo roleRepo;
    @Autowired private UserEntityRepo userRepo;
    @Autowired private CartRepo cartRepo;
    @Autowired private ProductRepo productRepo;
    @Autowired private CustomerRepo customerRepo;
    @Autowired private CourierRepo courierRepo;

    @BeforeEach
    void setupRoles() {
        // Создаем роли, если их нет (для чистоты теста)
        createRoleIfNotFound("ROLE_ADMIN");
        createRoleIfNotFound("ROLE_TRADER");
        createRoleIfNotFound("ROLE_CUSTOMER");
        createRoleIfNotFound("ROLE_COURIER");
    }

    @Test
    @Transactional
    void testAutoAssignFlow() {
        System.out.println("🚀 НАЧИНАЕМ ТЕСТ АВТОМАТИЧЕСКОГО НАЗНАЧЕНИЯ...");

        // ==========================================
        // 1. ПОДГОТОВКА: СОЗДАЕМ ЕДИНСТВЕННОГО КУРЬЕРА
        // ==========================================
        // Важно создать его ДО заказа, чтобы системе было кого найти
        RegisterRequest courierReq = new RegisterRequest("CourierBob", "courier@test.com", "password", "password" );
        UserEntity courierUser = userService.register(courierReq, roleRepo.findByName("ROLE_COURIER").get());

        // ВАЖНО: Если у тебя Courier - это отдельная сущность, связанная с User, создаем её
        // Если Courier extends UserEntity, то register уже мог это сделать.
        // Но для надежности "findFirstByRoleName" в CustomerOrderService:
        // Убедимся, что Courier сущность существует в базе для этого юзера.
        Courier courierEntity = new Courier();
        courierEntity.setUserEntity(courierUser);
        courierRepo.save(courierEntity); // Сохраняем именно в CourierRepo

        System.out.println("✅ Курьер создан: " + courierUser.getUsername());

        // ==========================================
        // 2. СОЗДАЕМ ТРЕЙДЕРА И ТОВАР
        // ==========================================
        RegisterRequest traderReq = new RegisterRequest( "TraderJoe","trader@test.com", "password", "password");
        UserEntity traderUser = userService.register(traderReq, roleRepo.findByName("ROLE_TRADER").get());
        traderUser.setActive(true);
        traderUser.setVerified(true);// Аппрув
        userRepo.save(traderUser);

        mockLogin(traderUser); // Логинимся как трейдер

        ProductEntity product = new ProductEntity();
        product.setName("Test Phone");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setStockQuantity(50);
        // Привязываем к трейдеру (если поле называется trader или owner)
        product.setTrader(traderUser);
        product = productRepo.save(product);

        System.out.println("✅ Товар создан: " + product.getName());

        // ==========================================
        // 3. СОЗДАЕМ КЛИЕНТА И НАПОЛНЯЕМ КОРЗИНУ
        // ==========================================
        RegisterRequest custReq = new RegisterRequest( "ClientAlice","client@test.com", "password", "password");
        UserEntity clientUser = userService.register(custReq, roleRepo.findByName("ROLE_CUSTOMER").get());

        // Создаем сущность Customer, если register создает только UserEntity
        CustomerEntity customerProfile = new CustomerEntity();
        customerProfile.setUserEntity(clientUser);
        customerProfile = customerRepo.save(customerProfile);

        mockLogin(clientUser); // Логинимся как клиент

        // НАПОЛНЯЕМ КОРЗИНУ (Хак напрямую через репозиторий, чтобы не зависеть от CartService)
        CartEntity cart = new CartEntity();
        cart.setCustomer(customerProfile);
        cart = cartRepo.save(cart);

        CartItemEntity cartItem = new CartItemEntity();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(1);

        List<CartItemEntity> items = new ArrayList<>();
        items.add(cartItem);
        cart.setCartItems(items);

        cartRepo.save(cart);

        System.out.println("✅ Корзина наполнена");

        // ==========================================
        // 4. ОФОРМЛЕНИЕ ЗАКАЗА (САМОЕ ВАЖНОЕ)
        // ==========================================
        PlaceOrderRequest orderReq = new PlaceOrderRequest("Bishkek", "Chuy 123");

        // Вызываем метод, который содержит логику Auto-Assign
        OrderResponse response = orderService.placeOrder(clientUser, orderReq);

        System.out.println("✅ Заказ оформлен. ID: " + response.orderId());

        // ==========================================
        // 5. ПРОВЕРКА АВТОМАТИЧЕСКОГО НАЗНАЧЕНИЯ
        // ==========================================
        List<DeliveryTaskEntity> tasks = deliveryTaskRepo.findAll();
        Assertions.assertFalse(tasks.isEmpty(), "❌ Ошибка: DeliveryTask не создалась!");

        DeliveryTaskEntity task = tasks.get(0);

        // Проверяем, что курьер уже проставлен
        Assertions.assertNotNull(task.getCourier(), "❌ Ошибка: Курьер не назначен автоматически!");
        Assertions.assertEquals("CourierBob", task.getCourier().getUserEntity().getFullName(), "❌ Ошибка: Назначен не тот курьер!");
        Assertions.assertEquals(OrderStatus.ASSIGNED, task.getOrderStatus(), "❌ Ошибка: Статус задачи должен быть ASSIGNED");

        // Проверяем, что статус заказа синхронизирован
        Assertions.assertEquals(OrderStatus.ASSIGNED, task.getOrderEntity().getStatus(), "❌ Ошибка: Статус заказа не обновился на ASSIGNED");

        System.out.println("🎉 УСПЕХ: Система сама нашла курьера и назначила заказ!");

        // ==========================================
        // 6. ДЕЙСТВИЯ КУРЬЕРА
        // ==========================================
        mockLogin(courierUser); // Теперь мы - Курьер

        // ВНИМАНИЕ: Мы НЕ вызываем courierService.acceptTask(),
        // потому что задача УЖЕ имеет курьера, и метод выбросит ошибку.
        // Мы сразу меняем статус на "Принял/Везет".

        System.out.println("🚚 Курьер подтверждает получение...");
        courierService.setStatus(task.getId(), OrderStatus.IN_TRANSIT); // Или ACCEPTED, смотря какой Enum у вас следующий

        // Проверяем обновление
        DeliveryTaskEntity shippingTask = deliveryTaskRepo.findById(task.getId()).get();
        Assertions.assertEquals(OrderStatus.IN_TRANSIT, shippingTask.getOrderStatus());
        Assertions.assertEquals(OrderStatus.IN_TRANSIT, shippingTask.getOrderEntity().getStatus());

        System.out.println("📦 Курьер доставляет...");
        courierService.setStatus(task.getId(), OrderStatus.DELIVERED);

        // Финальная проверка
        DeliveryTaskEntity doneTask = deliveryTaskRepo.findById(task.getId()).get();
        Assertions.assertEquals(OrderStatus.DELIVERED, doneTask.getOrderStatus());
        Assertions.assertEquals(OrderStatus.DELIVERED, doneTask.getOrderEntity().getStatus());

        System.out.println("🏆 FULL CYCLE TEST PASSED!");
    }

    // Хелперы
    private void createRoleIfNotFound(String name) {
        if (roleRepo.findByName(name).isEmpty()) {
            roleRepo.save(new Role(name));
        }
    }

    private void mockLogin(UserEntity user) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(user.getUsername(), null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}