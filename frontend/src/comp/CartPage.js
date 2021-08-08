import React from "react";
import { useState, useEffect } from "react";
import Loader from "./Loader";
import CartViewer from "./CartViewer";
import * as Api from "../api";


export default function CartPage({ apiProps }) {
    const [items, setItems] = useState({ state: "empty" });

    useEffect(async () => {
        setItems({ state: "loading" });

        const shallowCartItems = await Api.userCartItems(apiProps);
        const deepCartItems = await Promise.all(shallowCartItems.map(async item => {
            const product = await Api.productById(apiProps, item.product);
            return {
                ...item,
                product,
            };
        }));

        setItems({
            state: "loaded",
            value: deepCartItems,
        });
    }, []);



    return <Loader predicate={() => { return items.state === "loaded"; }}>
        <CartViewer cartItems={items.value} />
    </Loader>
}

