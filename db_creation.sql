

CREATE TABLE `Genre` (
  `name` VARCHAR(100) PRIMARY KEY
);

INSERT INTO `Genre` (`name`) VALUES 
  ('Fiction'),
  ('Non-Fiction'),
  ('Personal Development'),
  ('Business & Finance'),
  ('Science & Technology'),
  ('Health & Wellness'),
  ('Education & Academic'),
  ('Biography'),
  ('Childrens Books'),
  ('Religion');

CREATE TABLE `Admin` (
  `admin_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `hash_password` VARCHAR(255) NOT NULL,
  `display_name` VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE `NormalUser` (
  `user_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL UNIQUE,
  `hash_password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `address` TEXT,
  `birth_date` DATE,
  `phone_number` VARCHAR(20),
  `balance` DECIMAL(10,2) DEFAULT 0.00
);

CREATE TABLE `Book` (
  `book_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(255) NOT NULL,
  `author` VARCHAR(255) NOT NULL,
  `genre` VARCHAR(100) NOT NULL,
  `publisher` VARCHAR(255),
  `publication_date` DATE,
  `isbn` VARCHAR(20) UNIQUE,
  `description` TEXT,
  `overview` VARCHAR(500),
  `number_of_pages` INT,
  `language` VARCHAR(50),
  `is_available` BOOLEAN DEFAULT TRUE,
  `stock` INT DEFAULT 0,
  `price` DECIMAL(10,2) NOT NULL,
  `discount_percentage` DECIMAL(5,2) DEFAULT 0.00,
  `added_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`genre`) REFERENCES `Genre`(`name`) ON DELETE RESTRICT
);

CREATE TABLE `BookImage` (
  `image_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `book_ID` BIGINT NOT NULL,
  `url` VARCHAR(255) NOT NULL,
  `is_main` BOOLEAN DEFAULT FALSE,
  FOREIGN KEY (`book_ID`) REFERENCES `Book`(`book_ID`) ON DELETE CASCADE
);

CREATE TABLE `UserInterest` (
  `interest_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_ID` BIGINT NOT NULL,
  `genre` VARCHAR(100) NOT NULL,
  FOREIGN KEY (`user_ID`) REFERENCES `NormalUser`(`user_ID`) ON DELETE CASCADE,
  FOREIGN KEY (`genre`) REFERENCES `Genre`(`name`) ON DELETE CASCADE,
  UNIQUE KEY `unique_user_genre` (`user_ID`, `genre`)
);

CREATE TABLE `Wishlist` (
  `wishlist_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_ID` BIGINT NOT NULL,
  `book_ID` BIGINT NOT NULL,
  FOREIGN KEY (`user_ID`) REFERENCES `NormalUser`(`user_ID`) ON DELETE CASCADE,
  FOREIGN KEY (`book_ID`) REFERENCES `Book`(`book_ID`) ON DELETE CASCADE,
  UNIQUE KEY `unique_wishlist_item` (`user_ID`, `book_ID`)
);

CREATE TABLE `BookOrder` (
  `order_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_ID` BIGINT NOT NULL,
  `order_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `address` TEXT NOT NULL,
  `payment_method` VARCHAR(50) NOT NULL,
  `shipping_cost` DECIMAL(10,2) DEFAULT 0.00,
  FOREIGN KEY (`user_ID`) REFERENCES `NormalUser`(`user_ID`) ON DELETE RESTRICT
);

CREATE TABLE `CartItem` (
  `cart_item_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_ID` BIGINT NOT NULL,
  `book_ID` BIGINT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  FOREIGN KEY (`user_ID`) REFERENCES `NormalUser`(`user_ID`) ON DELETE CASCADE,
  FOREIGN KEY (`book_ID`) REFERENCES `Book`(`book_ID`) ON DELETE CASCADE,
  UNIQUE KEY `unique_cart_item` (`user_ID`, `book_ID`)
);

CREATE TABLE `OrderItem` (
  `order_item_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `order_ID` BIGINT NOT NULL,
  `book_ID` BIGINT NOT NULL,
  `quantity` BIGINT NOT NULL,
  `price` DECIMAL(10,2) NOT NULL,
  `discount_percentage` DECIMAL(5,2) DEFAULT 0.00,
  FOREIGN KEY (`order_ID`) REFERENCES `BookOrder`(`order_ID`) ON DELETE CASCADE,
  FOREIGN KEY (`book_ID`) REFERENCES `Book`(`book_ID`) ON DELETE RESTRICT
);

CREATE TABLE `PurchaseHistory` (
  `receipt_ID` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_ID` BIGINT NOT NULL,
  `purchase_datetime` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `total_paid` DECIMAL(10,2) NOT NULL,
  `receipt_file_URL` VARCHAR(255) NOT NULL,
  FOREIGN KEY (`user_ID`) REFERENCES `NormalUser`(`user_ID`) ON DELETE RESTRICT
);