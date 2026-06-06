-- BE-006: Performance indexes for orders table
-- Run manually or via Flyway/Liquibase when migration tooling is connected.

CREATE INDEX IF NOT EXISTS idx_orders_trader_id ON orders(trader_id);
CREATE INDEX IF NOT EXISTS idx_orders_status    ON orders(status);