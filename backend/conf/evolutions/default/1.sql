# --- !Ups

CREATE TABLE "product" (
                           "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                           "name" VARCHAR NOT NULL,
                           "description" TEXT NOT NULL
);

# --- !Downs

DROP TABLE "product"