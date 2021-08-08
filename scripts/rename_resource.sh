#!/bin/bash

set -e
set -x

TARGET_UPPER="CartItem"
TARGETS_UPPER="CartItems"
TARGET_LOWER="cartItem"
TARGETS_LOWER="cartItems"
# TARGETS="cartItems"


declare -a FILENAMES=(
    "backend/app/models/${TARGET_UPPER}.scala"
    "backend/app/models/${TARGET_UPPER}Repo.scala"
    "backend/app/controllers/ssr/${TARGET_UPPER}Controller.scala"
    "backend/app/views/ssr/${TARGETS_LOWER}/_new.scala.html"
    "backend/app/views/ssr/${TARGETS_LOWER}/edit.scala.html"
    "backend/app/views/ssr/${TARGETS_LOWER}/index.scala.html"
)


for FILENAME in "${FILENAMES[@]}"
do
    sed -i "s/currency/${TARGET_LOWER}/g" $FILENAME
    sed -i "s/currencies/${TARGETS_LOWER}/g" $FILENAME
    sed -i "s/Currency/${TARGET_UPPER}/g" $FILENAME
    sed -i "s/Currencies/${TARGETS_UPPER}/g" $FILENAME
done
