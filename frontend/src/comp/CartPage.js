import React from "react";
import { useState, useEffect } from "react";
import Loader from "./Loader";
import CartViewer from "./CartViewer";
import * as Api from "../api";


export default function CartPage({ apiProps }) {
    const [items, setItems] = useState({ state: "empty" });

    useEffect(async () => {
        setItems({ state: "loading" });

        const apiResult = await Api.userCartItems(apiProps);

        setItems({
            state: "loaded",
            value: apiResult,
        });
    }, []);



    return <Loader predicate={() => { return items.state === "loaded"; }}>
        <CartViewer cartItems={items.value} />
    </Loader>
}

