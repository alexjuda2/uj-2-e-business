--- !Ups

-- Category

INSERT INTO `category` (name)
VALUES ("Plants");

INSERT INTO `category` (name)
VALUES ("Music making");

-- Currency

INSERT INTO `currency` (code, symbol)
VALUES ("PLN", "zł");

INSERT INTO `currency` (code, symbol)
VALUES ("USD", "$");

-- Product

INSERT INTO `product` (name, description, category, currency)
VALUES ("Sansevieria", "Snake plant", 1, 1);

INSERT INTO `product` (name, description, category, currency)
VALUES ("Ficus Elastica", "Really nice house plant", 1, 2);

INSERT INTO `product` (name, description, category, currency)
VALUES ("Fender P-Bass", "Classic Fender Precision Bass", 2, 1);

INSERT INTO `product` (name, description, category, currency)
VALUES ("Fender J-Bass", "Fender Jazz Bass", 2, 1);

-- Promotion

INSERT INTO `promotion` (name)
VALUES ("Small houseplant discount");

-- ProductPromotion

INSERT INTO `productPromotion` (product, promotion)
VALUES (1, 1);

INSERT INTO `productPromotion` (product, promotion)
VALUES (2, 1);

-- User

INSERT INTO `user` (providerId, providerKey, email)
VALUES ("manual", "manual1", "manuel.seed@gmail.com");

-- Review

INSERT INTO `review` (content, rating, user)
VALUES ("Recommend. Impossible to kill. Looks great.", 10, 1);

-- CartItem

INSERT INTO `cartItem` (quantity, product, user)
VALUES (5, 1, 1);

-- WishList

INSERT INTO `wishList` (name, user)
VALUES ("Other plants to check out", 1);

-- WishListItem

INSERT INTO `wishListItem` (product, wishList)
VALUES (2, 1);

--- !Downs
