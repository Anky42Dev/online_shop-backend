-- BE-011: Full-Text Product Search
-- Функциональные индексы для регистронезависимого поиска по name и description.
-- LOWER() на стороне PostgreSQL позволяет планировщику использовать их при
-- запросах вида: WHERE LOWER(name) LIKE LOWER('%query%')

CREATE INDEX IF NOT EXISTS idx_products_name_lower
    ON products(LOWER(name));

CREATE INDEX IF NOT EXISTS idx_products_desc_lower
    ON products(LOWER(description));