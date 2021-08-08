--- !Ups

CREATE TABLE "category"
(
    "id"   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" VARCHAR NOT NULL
);

CREATE TABLE "currency"
(
    "id"         INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "code"       VARCHAR NOT NULL,
    "ratioToUSD" DOUBLE NOT NULL,
    "symbol"     VARCHAR NOT NULL
);

CREATE TABLE "product"
(
    "id"          INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name"        VARCHAR NOT NULL,
    "description" TEXT    NOT NULL,
    "category"    INT     NOT NULL,
    "currency"    INT     NOT NULL,

    FOREIGN KEY (category) references category (id),
    FOREIGN KEY (currency) references currency (id)
);

CREATE TABLE "promotion"
(
    "id"   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" VARCHAR NOT NULL
);

CREATE TABLE "productPromotion"
(
    "id"        INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "product"   INT     NOT NULL,
    "promotion" INT     NOT NULL
);

--- !Downs

DROP TABLE "category";
DROP TABLE "currency";
DROP TABLE "product";
DROP TABLE "promotion";
DROP TABLE "productPromotion";