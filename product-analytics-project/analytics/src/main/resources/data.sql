DROP TABLE IF EXISTS product_view_count;
CREATE TABLE product_view_count (
      id INT NOT NULL PRIMARY KEY,
      count BIGINT DEFAULT 0
);