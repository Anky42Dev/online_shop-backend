CREATE TABLE IF NOT EXISTS roles (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    full_name   VARCHAR(255) NOT NULL,
    username    VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    is_approved BOOLEAN DEFAULT FALSE,
    is_rejected BOOLEAN DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS categories (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    description    TEXT,
    price          NUMERIC(19,2) NOT NULL,
    stock_quantity INT NOT NULL,
    category_id    BIGINT REFERENCES categories(id),
    trader_id      BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS customers (
    id             BIGSERIAL PRIMARY KEY,
    user_entity_id BIGINT NOT NULL UNIQUE REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS couriers (
    id             BIGSERIAL PRIMARY KEY,
    user_entity_id BIGINT NOT NULL UNIQUE REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS carts (
    id          BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL UNIQUE REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS cart_items (
    id         BIGSERIAL PRIMARY KEY,
    cart_id    BIGINT NOT NULL REFERENCES carts(id),
    product_id BIGINT NOT NULL REFERENCES products(id),
    quantity   INT NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id                 BIGSERIAL PRIMARY KEY,
    customer_entity_id BIGINT REFERENCES customers(id),
    trader_id          BIGINT REFERENCES users(id),
    status             VARCHAR(50),
    total_price        NUMERIC(19,2),
    city               VARCHAR(255),
    address            VARCHAR(255),
    created_at         TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id            BIGSERIAL PRIMARY KEY,
    order_id      BIGINT REFERENCES orders(id),
    product_id    BIGINT REFERENCES products(id),
    quantity      INT NOT NULL,
    current_price NUMERIC(19,2)
);

CREATE TABLE IF NOT EXISTS delivery_tasks (
    id           BIGSERIAL PRIMARY KEY,
    order_id     BIGINT REFERENCES orders(id),
    courier_id   BIGINT REFERENCES couriers(id),
    order_status VARCHAR(50),
    evidence_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS revoked_tokens (
    id         BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    revoked_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);
