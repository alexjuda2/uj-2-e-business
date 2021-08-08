import React from "react";
import Loader from "./Loader";
import CartViewer from "./CartViewer";
import * as Api from "../api";


function OrderMaker({ onSubmit }) {
    const [address, setAddress] = React.useState("");
    return (
        <form>
            <div className="input-field col">
                <input id="address" type="text" onChange={evt => { setAddress(evt.target.value); }} />
                <label for="address">Address</label>
            </div>

            <button className="btn waves-effect waves-light" type="submit" name="action" onClick={evt => {
                evt.preventDefault();
                onSubmit({ address });
            }}>
                Submit
            </button>
        </form>
    );
}


export default function CartPage({ apiProps }) {
    const [items, setItems] = React.useState({ state: "empty" });

    React.useEffect(async () => {
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
        <section>
            <CartViewer cartItems={items.value} />
        </section>
        <section>
            <OrderMaker onSubmit={async ({ address }) => {
                await Api.createOrderFromUserCart(apiProps, address);
                setItems({ state: "loaded", value: [], });
            }} />
        </section>
    </Loader>
}

