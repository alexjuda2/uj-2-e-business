#!/bin/bash

set -e
set -x

declare -a TARGETS=(
    "backend/app/models/ProductPromotion.scala"
    "backend/app/controllers/ssr/ProductPromotionController.scala"
    "backend/app/views/ssr/productPromotions/_new.scala.html"
    "backend/app/views/ssr/productPromotions/edit.scala.html"
    "backend/app/views/ssr/productPromotions/index.scala.html"
)


for TARGET in "${TARGETS[@]}"
do
    sed -i 's/currency/promotion/g' $TARGET
    sed -i 's/currencies/promotions/g' $TARGET
    sed -i 's/Currency/Promotion/g' $TARGET
    sed -i 's/Currencies/Promotions/g' $TARGET
done
