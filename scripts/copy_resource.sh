#!/bin/bash

set -e
set -x

TARGET="ProductPromotion"
TARGETS="productPromotions"

cp backend/app/models/Currency.scala     "backend/app/models/${TARGET}.scala"
cp backend/app/models/CurrencyRepo.scala "backend/app/models/${TARGET}Repo.scala"

# cp backend/app/controllers/ssr/CurrencyController.scala "backend/app/controllers/ssr/${TARGET}Controller.scala"

# cp -r backend/app/views/ssr/currencies "backend/app/views/ssr/${TARGETS}"

