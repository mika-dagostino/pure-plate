---------------------------------------------------------------
-- Creating the tables of the DB
---------------------------------------------------------------

CREATE TABLE User (
    userId INT NOT NULL PRIMARY KEY,
    address VARCHAR(300) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phoneNumber VARCHAR(20),
    name VARCHAR(100) NOT NULL,
    password VARCHAR(512) NOT NULL,

    CONSTRAINT validEmail CHECK(email LIKE '%@%.%'),
    CONSTRAINT validName CHECK(LENGTH(name) > 2)
);

CREATE TABLE Customer (
    userId INT NOT NULL PRIMARY KEY,
    loyaltyPoints INT DEFAULT 0,

    FOREIGN KEY (userId) REFERENCES User(userId),
    CONSTRAINT validPoints CHECK(loyaltyPoints >= 0)
);

CREATE TABLE FoodRestriction (
    userId INT NOT NULL,
    restriction VARCHAR(25) NOT NULL,

    PRIMARY KEY (userId, restriction),
    FOREIGN KEY (userId) REFERENCES Customer(userId),
    CONSTRAINT validRestriction CHECK(restriction IN ('gluten', 'dairy', 'nuts', 'vegan', 'vegetarian'))
);

CREATE TABLE DeliveryDriver (
    userId INT NOT NULL PRIMARY KEY,
    licensePlate VARCHAR(10) NOT NULL UNIQUE,
    salary DECIMAL(8, 2) NOT NULL,
    carModel VARCHAR(25) NOT NULL,

    FOREIGN KEY (userId) REFERENCES User(userId),
    CONSTRAINT validSalary CHECK(salary >= 0)
);

CREATE TABLE Plan (
    name VARCHAR(50) NOT NULL PRIMARY KEY,
    price DECIMAL(6, 2) NOT NULL,
    mealFrequency INT NOT NULL,

    CONSTRAINT validPrice CHECK(price >= 0),
    CONSTRAINT validFrequency CHECK(mealFrequency > 0)
);

CREATE TABLE Subscription (
    customerId INT NOT NULL PRIMARY KEY, 
    planName VARCHAR(50) NOT NULL, 
    startDate DATE NOT NULL, 
    endDate DATE NOT NULL,
    status VARCHAR(8) NOT NULL,

    FOREIGN KEY (customerId) REFERENCES Customer(userId),
    FOREIGN KEY (planName) REFERENCES Plan(name),
    CONSTRAINT validStatus CHECK(status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT validStartDate CHECK(startDate < endDate)
);

CREATE TABLE Meal (
    mealId INT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(5, 2) NOT NULL,
    type VARCHAR(50),

    CONSTRAINT validPrice CHECK(price >= 0),
    CONSTRAINT validType CHECK(type IS NULL OR type IN ('breakfast', 'lunch', 'dinner', 'other'))
);

CREATE TABLE Review (
    customerId INT NOT NULL,
    mealId INT NOT NULL,
    description VARCHAR(1000),
    rating DECIMAL(2,1) NOT NULL,

    PRIMARY KEY (customerId, mealId),
    FOREIGN KEY (customerId) REFERENCES Customer(userId),
    FOREIGN KEY (mealId) REFERENCES Meal(mealId),
    CONSTRAINT validRating CHECK(rating IN (1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5))
);

CREATE TABLE Cart (
    cartId INT NOT NULL PRIMARY KEY,
    customerId INT NOT NULL UNIQUE,
    totalPrice DECIMAL(6,2) DEFAULT 0,

    FOREIGN KEY (customerId) REFERENCES Customer(userId),
    CONSTRAINT validTotalPrice CHECK(totalPrice >= 0)
);

CREATE TABLE CartMeal(
    cartId INT NOT NULL,
    mealId INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,

    PRIMARY KEY (cartId, mealId),
    FOREIGN KEY (cartId) REFERENCES Cart(cartId),
    FOREIGN KEY (mealId) REFERENCES Meal(mealId),
    CONSTRAINT validQuantity CHECK(quantity >= 1)
);

CREATE TABLE Payment (
    paymentId INT NOT NULL PRIMARY KEY,
    amount DECIMAL(6,2) NOT NULL,
    cardInfo CHAR(12) NOT NULL,

    CONSTRAINT validAmount CHECK(amount >= 0),
    CONSTRAINT validCardNumber CHECK(cardInfo NOT LIKE '% %')
);

CREATE TABLE Orders (
    orderId INT NOT NULL PRIMARY KEY,
    paymentId INT NOT NULL,
    customerId INT NOT NULL,
    deliveryDriverId INT NOT NULL,
    datePlaced DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL,
    totalPrice DECIMAL(6, 2) NOT NULL,

    FOREIGN KEY (paymentId) REFERENCES Payment(paymentId),
    FOREIGN KEY (customerId) REFERENCES Customer(userId),
    FOREIGN KEY (deliveryDriverId) REFERENCES DeliveryDriver(userId),
    CONSTRAINT validStatus CHECK(status IN ('preparing', 'in_delivery', 'delivered', 'cancelled', 'other')),
    CONSTRAINT validPrice CHECK(totalPrice >= 0)
);

CREATE TABLE OrderItem (
    orderId INT NOT NULL,
    mealId INT NOT NULL,
    price DECIMAL(6, 2) NOT NULL,
    quantity INT NOT NULL DEFAULT 1,

    PRIMARY KEY (orderId, mealId),
    FOREIGN KEY (orderId) REFERENCES Orders(orderId),
    FOREIGN KEY (mealId) REFERENCES Meal(mealId),
    CONSTRAINT validPrice CHECK(price >= 0),
    CONSTRAINT validQuantity CHECK(quantity >= 1)
);

---------------------------------------------------------------
-- Creating the views of this DB
---------------------------------------------------------------

CREATE VIEW ActiveSubscriptions AS
SELECT customerId, planName, startDate, endDate
FROM Subscription
WHERE status = 'ACTIVE';

CREATE VIEW CustomerOrders AS
SELECT u.userId, u.name, o.orderId, o.datePlaced, o.status, o.totalPrice
FROM User u
JOIN Customer c ON u.userId = c.userId
JOIN Orders o ON c.userId = o.customerId;
