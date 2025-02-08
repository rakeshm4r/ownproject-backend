
-- For user_type_details
CREATE TABLE user_type_details (
    userId BIGINT AUTO_INCREMENT PRIMARY KEY,
    emailId VARCHAR(255) UNIQUE NOT NULL,
    userName VARCHAR(255) NOT NULL,
    userPassword VARCHAR(255) NOT NULL,
    userRole VARCHAR(100),
    userStatus VARCHAR(100),
    userLoginDate TIMESTAMP,
    mobileNo VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    pinCode VARCHAR(20),
    country VARCHAR(100),
    profileImage BLOB
);

-- For Product
CREATE TABLE product (
    productId BIGINT AUTO_INCREMENT PRIMARY KEY,
    productName VARCHAR(255) UNIQUE NOT NULL,
    productPrice DOUBLE NOT NULL,
    noOfItems INT NOT NULL,
    categoryId BIGINT,
    productImage BLOB,
    FOREIGN KEY (categoryId) REFERENCES product_category(categoryId)
);

-- For Product_category 
CREATE TABLE product_category (
    categoryId BIGINT AUTO_INCREMENT PRIMARY KEY,
    productCategoryName VARCHAR(255) UNIQUE NOT NULL
);

--For dynamic_heading_tags
CREATE TABLE dynamic_heading_tags (
    headingId BIGINT AUTO_INCREMENT PRIMARY KEY,
    headerName VARCHAR(255) UNIQUE NOT NULL,
    headerStatus VARCHAR(100)
);


--For Cart
CREATE TABLE cart (
    cartId BIGINT AUTO_INCREMENT PRIMARY KEY,
    productId BIGINT,
    userId BIGINT,
    cartRemoveStatus VARCHAR(100),
    FOREIGN KEY (productId) REFERENCES product(productId),
    FOREIGN KEY (userId) REFERENCES user_type_details(userId)
);

CREATE TABLE Orders (
    orderId INT AUTO_INCREMENT PRIMARY KEY,  
    productId INT,
    userId INT,
    ordersStatusId INT,
    FOREIGN KEY (productId) REFERENCES Product(productId),
    FOREIGN KEY (userId) REFERENCES UserTypeDetails(userId),
    FOREIGN KEY (ordersStatusId) REFERENCES OrdersStatus(ordersStatusId)
);


