#!/bin/bash

set -e
set -x

declare -a TARGETS=(
    "backend/app/models/ProductPromotion.scala"
    "backend/app/models/ProductPromotionRepo.scala"
    "backend/app/controllers/ssr/ProductPromotionController.scala"
    "backend/app/views/ssr/productPromotions/_new.scala.html"
    "backend/app/views/ssr/productPromotions/edit.scala.html"
    "backend/app/views/ssr/productPromotions/index.scala.html"
)


for TARGET in "${TARGETS[@]}"
do
    sed -i 's/currency/productPromotion/g' $TARGET
    sed -i 's/currencies/productPromotions/g' $TARGET
    sed -i 's/Currency/ProductPromotion/g' $TARGET
    sed -i 's/Currencies/ProductPromotions/g' $TARGET
done
