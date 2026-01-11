package com.shop.onlineshop.models.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank
        String fullName,

        @Email
        @NotBlank
        String email,

        @Size(min = 8)
        String password,

        @Size(min = 8)
        String confirmPassword

) {}

