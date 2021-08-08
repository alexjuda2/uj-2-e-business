#!/bin/bash

set -e
set -x

# TARGET="backend/app/models/PromotionRepo.scala"
# TARGET="backend/app/controllers/ssr/PromotionController.scala"
# TARGET="backend/app/views/ssr/promotions/_new.scala.html"
# TARGET="backend/app/views/ssr/promotions/index.scala.html"

sed -i 's/currency/promotion/g' $TARGET
sed -i 's/currencies/promotions/g' $TARGET
sed -i 's/Currency/Promotion/g' $TARGET
sed -i 's/Currencies/Promotions/g' $TARGET
