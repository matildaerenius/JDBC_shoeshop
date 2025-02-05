DROP DATABASE IF EXISTS shop2;
CREATE DATABASE shop2;
USE shop2;


CREATE TABLE customer (
id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
first_name varchar(50) NOT NULL,
last_name varchar(50) NOT NULL,
city varchar(50) NOT NULL,
email varchar(75) NOT NULL,
password varchar(50) NOT NULL);


INSERT INTO customer(first_name,last_name,city,email,password) VALUES
('Rose','Philipsen','Solna','rose@gmail.com','ros123'),
('Matilda','Erenius','Stockholm','matilda@gmail.com','mat123'),
('Sandra','Lindqvist','Nacka','sandra@gmail.com','san123'),
('Magnus','Eriksson','Huddinge','magnus@gmail.com','mag123'),
('Emil','Svensson','Sundbyberg','emil@gmail.com','emi123'),
('Kevin','Hamilton','Bromma','kevin@gmail.com','kev123');


CREATE TABLE orders (
id int NOT NULL AUTO_INCREMENT PRIMARY KEY ,
date date NOT NULL,
customer_id int NOT NULL,
status varchar(10) NOT NULL DEFAULT 'ACTIVE',
FOREIGN KEY (customer_id) REFERENCES customer (id)
);

INSERT INTO orders(date,customer_Id,status) VALUES
('2024-12-24',1,'ACTIVE'),
('2025-01-01',1,'PAID'),
('2025-01-06',2,'ACTIVE'),
('2024-12-31',3,'PAID'),
('2025-01-03',5,'ACTIVE'),
('2025-01-05',6,'ACTIVE');


CREATE TABLE product (
id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
size int NOT NULL,
color varchar(50) NOT NULL,
brand varchar(50) NOT NULL,
price int NOT NULL,
stock int NOT NULL);


INSERT INTO product(size,color,brand,price,stock) VALUES
(40,'White','Timberland',599,3),
(38,'Black','Ecco',1299,3),
(44,'Red','Nike',1599,3),
(42,'Pink','Reebok',299,5),
(35,'Black','Vans',399,5),
(39,'Brown','Puma',799,2),
(36,'Blue','Converse',899,3),
(37,'White','Adidas',1799,1);


CREATE TABLE order_details (
id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
orders_id int NOT NULL,
product_id int NOT NULL,
quantity int NOT NULL,
FOREIGN KEY (orders_id) REFERENCES orders (id) ON DELETE CASCADE,
FOREIGN KEY (product_id) REFERENCES product (id)
);

INSERT INTO order_details(quantity,product_id,orders_id) VALUES
(3,1,1),
(2,2,1),
(1,2,4),
(1,4,3),
(1,8,6),
(2,6,2);


CREATE TABLE category (
id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
name varchar(50) NULL);


INSERT INTO category(name) VALUES
('Running'),
('Sandals'),
('Winter'),
('Women'),
('Men'),
('Kids');


CREATE TABLE product_category (
id int NOT NULL AUTO_INCREMENT PRIMARY KEY,
product_id int NOT NULL,
category_id int NOT NULL,
UNIQUE KEY (product_id, category_id),
FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE CASCADE,
FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
);

INSERT INTO product_category(product_id,category_id) VALUES
(5,4),
(2,2),
(1,3),
(3,5),
(4,4),
(8,1),
(6,2),
(7,6);

CREATE TABLE OutOfStock (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    timestamp DATETIME NOT NULL
);

CREATE INDEX ix_first_name ON customer (first_name);

CREATE INDEX ix_brand ON product(brand);

CREATE UNIQUE INDEX ix_email ON customer (email); -- för snabbare inloggning

CREATE INDEX ix_customer_orders ON orders (customer_id); -- snabbare orderhämtning per kund

CREATE INDEX ix_order_details_orders ON order_details (orders_id); -- snabbare laddning till orderdetaljer baserat på order

CREATE INDEX ix_order_details_product ON order_details (product_id); -- snabbare produktåtkomst från orderdetaljer

-- snabbare produktfiltrering vid sökning
CREATE INDEX ix_product_stock ON product (stock);
CREATE INDEX ix_product_color ON product (color);
CREATE INDEX ix_product_size ON product (size);


DELIMITER //

CREATE PROCEDURE AddToCart(
    IN p_customerId INT,
    INOUT p_orderId INT,
    IN p_productId INT
)
BEGIN
	DECLARE v_orderId INT;
    DECLARE v_productStock INT DEFAULT 0;
    DECLARE v_productCount INT DEFAULT 0;
	
    DECLARE exit HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        RESIGNAL;  -- skickar vidare SQL-felet till Java
    END;

    START TRANSACTION;

    -- java skickar in en orderId som inte är NULL, kollar om den är aktiv
    IF p_orderId IS NOT NULL THEN
        SELECT id INTO v_orderId
        FROM orders
        WHERE id = p_orderId 
          AND customer_id = p_customerId
          AND status = 'ACTIVE'
        LIMIT 1;
    ELSE
        -- Försöker hämta en redan aktiv order
        SELECT id INTO v_orderId
        FROM orders
        WHERE customer_id = p_customerId
          AND status = 'ACTIVE'
        LIMIT 1;
    END IF;

    -- Finns det ingen aktiv order, skapas en ny
    IF v_orderId IS NULL THEN
        INSERT INTO orders (date, customer_id, status) 
        VALUES (NOW(), p_customerId, 'ACTIVE');
        SET v_orderId = LAST_INSERT_ID();
    END IF;

    -- Returnerar den order vi faktiskt använder
    SET p_orderId = v_orderId;

    -- Kontrollerar om lagersaldo finns kvar
    SELECT stock INTO v_productStock 
    FROM product
    WHERE id = p_productId;

    IF v_productStock <= 0 THEN
        -- Kastar fel om inga varor finns i lager till java
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Product is out of stock';
    END IF;

    -- Kollar om produkten redan finns i ordern
    SELECT COUNT(*) INTO v_productCount
    FROM order_details
    WHERE orders_id = v_orderId
      AND product_id = p_productId;

    -- Om produkten redan finns, öka quantity med 1
    IF v_productCount > 0 THEN
        UPDATE order_details
          SET quantity = quantity + 1
        WHERE orders_id = v_orderId
          AND product_id = p_productId;
    ELSE
        INSERT INTO order_details (orders_id, product_id, quantity)
        VALUES (v_orderId, p_productId, 1);
    END IF;

    -- Minskar lagersaldo med 1
    UPDATE product
       SET stock = stock - 1
     WHERE id = p_productId;

    COMMIT;
END //

DELIMITER ;


DELIMITER //

CREATE TRIGGER trigger_out_of_stock
AFTER UPDATE ON product
FOR EACH ROW
BEGIN
    IF NEW.stock = 0 THEN
        INSERT INTO OutOfStock (product_id, timestamp)
        VALUES (NEW.id, NOW());
    END IF;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE RemoveOneFromCart(
    IN p_customerId INT,
    IN p_orderId INT,
    IN p_productId INT
)
BEGIN

	DECLARE v_orderExists INT DEFAULT 0;
    DECLARE v_currentQty INT DEFAULT 0;
	
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Kollar att ordern existerar, är active och inloggad kund
    SELECT COUNT(*) INTO v_orderExists
    FROM orders
    WHERE id = p_orderId
      AND customer_id = p_customerId
      AND status = 'ACTIVE';

    IF v_orderExists = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No active order found for this customer/orderId';
    END IF;

    -- Hämtar nuvarande quantity
    SELECT quantity INTO v_currentQty
    FROM order_details
    WHERE orders_id = p_orderId
      AND product_id = p_productId
    LIMIT 1;

    IF v_currentQty IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'This product is not in the cart';
    END IF;

    -- Ökar lagersaldo först
    UPDATE product
       SET stock = stock + 1
     WHERE id = p_productId;

    -- Minskar quantity i ordern, eller tar bort helt om v_currentQty=1
    IF v_currentQty > 1 THEN
        UPDATE order_details
           SET quantity = quantity - 1
         WHERE orders_id = p_orderId
           AND product_id = p_productId;
    ELSE
        DELETE FROM order_details
         WHERE orders_id = p_orderId
           AND product_id = p_productId;
    END IF;

    COMMIT;
END //

DELIMITER ;


