--- !Ups

CREATE TABLE "review"
(
    "id"      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "content" VARCHAR,
    "rating"  INTEGER,
    "user"    INTEGER NOT NULL,

    FOREIGN KEY (user) references user (id)
);

CREATE TABLE "cartItem"
(
    "id"       INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "quantity" INTEGER NOT NULL,
    "product"  INTEGER NOT NULL,
    "user"     INTEGER NOT NULL,

    FOREIGN KEY (product) references product (id),
    FOREIGN KEY (user) references user (id)
);

--- !Downs

DROP TABLE "review";
DROP TABLE "cartItem";