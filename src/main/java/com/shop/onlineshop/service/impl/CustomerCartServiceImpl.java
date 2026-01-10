package com.shop.onlineshop.service.impl;

import com.shop.onlineshop.mapper.CartMapper;
import com.shop.onlineshop.models.request.AddToCartRequest;
import com.shop.onlineshop.models.response.CartResponse;
import com.shop.onlineshop.models.entity.*;
import com.shop.onlineshop.repo.*;
import com.shop.onlineshop.service.CustomerCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerCartServiceImpl implements CustomerCartService {

    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final CustomerRepo customerRepo;
    private final CartMapper cartMapper;

    @Override
    public CartResponse getMyCart(UserEntity user) {
        CartEntity cart = getOrCreateCart(user);
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(UserEntity user, AddToCartRequest request) {
        CartEntity cart = getOrCreateCart(user);
        ProductEntity product = productRepo.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        // Only one trader per cart
        if (!cart.getCartItems().isEmpty()) {
            Long currentTraderId = cart.getCartItems().get(0).getProduct().getTrader().getId();
            if (!currentTraderId.equals(product.getTrader().getId())) {
                throw new IllegalArgumentException("Cannot mix products from different traders in one cart");
            }
        }
        // Update quantity or create new item
        Optional<CartItemEntity> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItemEntity item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.quantity());
        } else {
            CartItemEntity newItem = new CartItemEntity();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(request.quantity());
            cart.getCartItems().add(newItem);
        }

        CartEntity savedCart = cartRepo.save(cart);
        return cartMapper.toResponse(savedCart);
    }

    @Override
    @Transactional
    public void clearCart(UserEntity user) {
        CartEntity cart = getOrCreateCart(user);
        cart.getCartItems().clear();
        cartRepo.save(cart);
    }

    private CartEntity getOrCreateCart(UserEntity user) {
        CustomerEntity customer = customerRepo.findByUserEntity_Username(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User is not a customer"));

        return cartRepo.findByCustomer_Id(customer.getId())
                .orElseGet(() -> {
                    CartEntity newCart = new CartEntity();
                    newCart.setCustomer(customer);
                    newCart.setCartItems(new ArrayList<>());
                    return cartRepo.save(newCart);
                });
    }
}