-- schema.sql

-- 상품 테이블 초기화
DROP TABLE IF EXISTS product;
CREATE TABLE product
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    brand     VARCHAR(5),
    category  VARCHAR(30),
    price     INT,
    insert_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_dt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx1 ON product (brand);
CREATE INDEX idx2 ON product (category);
CREATE INDEX idx3 ON product (price);
