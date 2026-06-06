package com.tradeops.service.impl;
import com.tradeops.service.impl.TraderProductServiceImpl;
import com.tradeops.exceptions.ResourceNotFoundException;
import com.tradeops.models.entity.CategoryEntity;
import com.tradeops.models.entity.ProductEntity;
import com.tradeops.models.entity.UserEntity;
import com.tradeops.models.model.OrderStatus;
import com.tradeops.models.request.TraderProductRequest;
import com.tradeops.models.response.TraderProductResponse;
import com.tradeops.repo.CategoryRepo;
import com.tradeops.repo.OrderRepo;
import com.tradeops.repo.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit-тесты для TraderProductServiceImpl.
 *
 * Проверяет:
 *  - ownership: трейдер работает только со своими товарами
 *  - бизнес-правило: нельзя удалить товар, по которому есть активные заказы
 *  - корректность маппинга ответа при успешных сценариях
 *
 * Не поднимает Spring-контекст — работает только через Mockito.
 */
@ExtendWith(MockitoExtension.class)
class TraderProductServiceImplTest {

    // ── моки репозиториев ─────────────────────────────────────────────────────

    @Mock
    private ProductRepo  productRepo;
    @Mock
    private CategoryRepo categoryRepo;
    @Mock
    private OrderRepo    orderRepo;

    @InjectMocks
    private TraderProductServiceImpl service;

    // ── общие фикстуры ────────────────────────────────────────────────────────

    private static final Long TRADER_ID       = 1L;
    private static final Long OTHER_TRADER_ID = 2L;
    private static final Long PRODUCT_ID      = 10L;
    private static final Long CATEGORY_ID     = 100L;

    private UserEntity     ownerTrader;
    private UserEntity     foreignTrader;
    private CategoryEntity category;
    private ProductEntity  product;

    @BeforeEach
    void setUp() {
        ownerTrader = new UserEntity();
        ownerTrader.setId(TRADER_ID);
        ownerTrader.setUsername("owner");
        ownerTrader.setEmail("owner@tradeops.com");
        ownerTrader.setFullName("Owner Trader");
        ownerTrader.setPassword("hashed");

        foreignTrader = new UserEntity();
        foreignTrader.setId(OTHER_TRADER_ID);
        foreignTrader.setUsername("foreign");
        foreignTrader.setEmail("foreign@tradeops.com");
        foreignTrader.setFullName("Foreign Trader");
        foreignTrader.setPassword("hashed");

        category = new CategoryEntity();
        category.setId(CATEGORY_ID);
        category.setName("Electronics");

        product = new ProductEntity();
        product.setId(PRODUCT_ID);
        product.setName("Laptop");
        product.setDescription("Gaming laptop");
        product.setPrice(new BigDecimal("1500.00"));
        product.setStockQuantity(5);
        product.setCategory(category);
        product.setTrader(ownerTrader);
    }

    // ── вспомогательный метод: стандартный валидный запрос ───────────────────

    private TraderProductRequest validUpdateRequest() {
        return new TraderProductRequest(
                "Laptop Pro",
                "Updated description",
                new BigDecimal("1800.00"),
                3,
                CATEGORY_ID
        );
    }

    // =========================================================================
    // updateProduct
    // =========================================================================

    @Nested
    @DisplayName("updateProduct()")
    class UpdateProduct {

        @Test
        @DisplayName("Успех: владелец обновляет свой товар — возвращается обновлённый ответ")
        void testUpdateProduct_Success() {
            // given — репозитории отдают нужные объекты
            given(productRepo.findByIdAndTrader_Id(PRODUCT_ID, TRADER_ID))
                    .willReturn(Optional.of(product));
            given(categoryRepo.findById(CATEGORY_ID))
                    .willReturn(Optional.of(category));

            // save возвращает тот же объект, но уже с новыми полями
            // (JPA в реальности делает это сам; имитируем через ArgumentCaptor)
            ArgumentCaptor<ProductEntity> savedCaptor = ArgumentCaptor.forClass(ProductEntity.class);
            given(productRepo.save(savedCaptor.capture()))
                    .willAnswer(inv -> inv.getArgument(0));

            TraderProductRequest request = validUpdateRequest();

            // when
            TraderProductResponse response = service.updateProduct(ownerTrader, PRODUCT_ID, request);

            // then — поля в ответе соответствуют запросу
            assertThat(response).isNotNull();
            assertThat(response.name()).isEqualTo("Laptop Pro");
            assertThat(response.price()).isEqualByComparingTo("1800.00");
            assertThat(response.stockQuantity()).isEqualTo(3);
            assertThat(response.categoryName()).isEqualTo("Electronics");

            // verify — товар был поиском по паре (productId, traderId), save вызван ровно раз
            verify(productRepo).findByIdAndTrader_Id(PRODUCT_ID, TRADER_ID);
            verify(productRepo).save(any(ProductEntity.class));

            // entity действительно изменён перед сохранением
            ProductEntity captured = savedCaptor.getValue();
            assertThat(captured.getName()).isEqualTo("Laptop Pro");
            assertThat(captured.getTrader().getId()).isEqualTo(TRADER_ID); // владелец не сброшен
        }

        @Test
        @DisplayName("Отказ в доступе: чужой трейдер пытается обновить товар → ResourceNotFoundException")
        void testUpdateProduct_AccessDenied() {
            // given — для foreignTrader товар не найден (findByIdAndTrader_Id возвращает пустой Optional)
            given(productRepo.findByIdAndTrader_Id(PRODUCT_ID, OTHER_TRADER_ID))
                    .willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() ->
                    service.updateProduct(foreignTrader, PRODUCT_ID, validUpdateRequest())
            )
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(String.valueOf(PRODUCT_ID));

            // productRepo.save() никогда не должен быть вызван
            verify(productRepo, never()).save(any());
            // categoryRepo тоже не трогаем — падаем раньше
            verify(categoryRepo, never()).findById(anyLong());
        }
    }

    // =========================================================================
    // deleteProduct
    // =========================================================================

    @Nested
    @DisplayName("deleteProduct()")
    class DeleteProduct {

        @Test
        @DisplayName("Успех: владелец удаляет товар без активных заказов")
        void testDeleteProduct_Success() {
            // given
            given(productRepo.findByIdAndTrader_Id(PRODUCT_ID, TRADER_ID))
                    .willReturn(Optional.of(product));
            given(orderRepo.existsActiveOrdersByProductId(eq(PRODUCT_ID), anyList()))
                    .willReturn(false);

            // when
            service.deleteProduct(ownerTrader, PRODUCT_ID);

            // then — delete вызван ровно раз с нужным объектом
            verify(productRepo).delete(product);
        }

        @Test
        @DisplayName("Отказ в доступе: чужой трейдер пытается удалить товар → ResourceNotFoundException")
        void testDeleteProduct_AccessDenied() {
            // given
            given(productRepo.findByIdAndTrader_Id(PRODUCT_ID, OTHER_TRADER_ID))
                    .willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() ->
                    service.deleteProduct(foreignTrader, PRODUCT_ID)
            )
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(String.valueOf(PRODUCT_ID));

            verify(productRepo, never()).delete(any());
            verify(orderRepo, never()).existsActiveOrdersByProductId(anyLong(), anyList());
        }

        @Test
        @DisplayName("Бизнес-правило: нельзя удалить товар с активными заказами → IllegalStateException")
        void testDeleteProduct_ThrowsExceptionWhenHasActiveOrders() {
            // given — товар принадлежит владельцу, но по нему есть активные заказы
            given(productRepo.findByIdAndTrader_Id(PRODUCT_ID, TRADER_ID))
                    .willReturn(Optional.of(product));
            given(orderRepo.existsActiveOrdersByProductId(eq(PRODUCT_ID), anyList()))
                    .willReturn(true);

            // when / then
            assertThatThrownBy(() ->
                    service.deleteProduct(ownerTrader, PRODUCT_ID)
            )
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("active orders");

            // delete не вызван — транзакция откатится
            verify(productRepo, never()).delete(any());
        }
    }

    // =========================================================================
    // updateStock  (BE-007)
    // =========================================================================

    @Nested
    @DisplayName("updateStock()")
    class UpdateStock {

        @Test
        @DisplayName("Успех: владелец обновляет остаток — новое значение сохранено")
        void testUpdateStock_Success() {
            // given
            given(productRepo.findByIdAndTrader_Id(PRODUCT_ID, TRADER_ID))
                    .willReturn(Optional.of(product));
            given(productRepo.save(any(ProductEntity.class)))
                    .willAnswer(inv -> inv.getArgument(0));

            // when
            TraderProductResponse response = service.updateStock(ownerTrader, PRODUCT_ID, 42);

            // then
            assertThat(response.stockQuantity()).isEqualTo(42);
            verify(productRepo).save(any(ProductEntity.class));
        }

        @Test
        @DisplayName("Отказ в доступе: чужой трейдер пытается изменить остаток → ResourceNotFoundException")
        void testUpdateStock_AccessDenied() {
            // given
            given(productRepo.findByIdAndTrader_Id(PRODUCT_ID, OTHER_TRADER_ID))
                    .willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() ->
                    service.updateStock(foreignTrader, PRODUCT_ID, 99)
            )
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(String.valueOf(PRODUCT_ID));

            verify(productRepo, never()).save(any());
        }
    }

    // =========================================================================
    // createProduct
    // =========================================================================

    @Nested
    @DisplayName("createProduct()")
    class CreateProduct {

        @Test
        @DisplayName("Успех: новый товар создаётся и привязывается к трейдеру")
        void testCreateProduct_Success() {
            // given
            TraderProductRequest request = new TraderProductRequest(
                    "New Phone", "Description", new BigDecimal("999.00"), 10, CATEGORY_ID
            );
            given(categoryRepo.findById(CATEGORY_ID)).willReturn(Optional.of(category));

            // имитируем, что JPA присвоил id после сохранения
            given(productRepo.save(any(ProductEntity.class))).willAnswer(inv -> {
                ProductEntity p = inv.getArgument(0);
                p.setId(99L);
                return p;
            });

            // when
            TraderProductResponse response = service.createProduct(ownerTrader, request);

            // then
            assertThat(response.id()).isEqualTo(99L);
            assertThat(response.name()).isEqualTo("New Phone");
            assertThat(response.categoryName()).isEqualTo("Electronics");

            // Проверяем через captor, что trader действительно проставлен
            ArgumentCaptor<ProductEntity> captor = ArgumentCaptor.forClass(ProductEntity.class);
            verify(productRepo).save(captor.capture());
            assertThat(captor.getValue().getTrader().getId()).isEqualTo(TRADER_ID);
        }

        @Test
        @DisplayName("Несуществующая категория при создании → ResourceNotFoundException")
        void testCreateProduct_CategoryNotFound() {
            // given
            given(categoryRepo.findById(CATEGORY_ID)).willReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() ->
                    service.createProduct(ownerTrader, validUpdateRequest())
            )
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining(String.valueOf(CATEGORY_ID));

            verify(productRepo, never()).save(any());
        }
    }
}