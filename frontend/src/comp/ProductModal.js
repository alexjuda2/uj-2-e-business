import React from 'react';
import { useState, useEffect, useRef } from "react";
import * as Api from "../api";
import Loader from "./Loader";


function ProductDetails({ product }) {
    return (
        <div className="container">
            <section className="section">
                <h2>{product.name}</h2>
                <p>{product.description}</p>
            </section>
            <section className="section">
                <button>add to cart</button>
            </section>
        </div>
    );
}


export default function ProductModal({ apiProps, productId, onClose }) {
    const modalRef = useRef(null);

    useEffect(() => {
        M.Modal.init(modalRef.current, {
            onCloseEnd: onClose,
        });
        const modal = M.Modal.getInstance(modalRef.current);
        modal.open();
    }, []);

    const [product, setProduct] = useState({ state: "empty" });
    useEffect(async () => {
        setProduct({
            state: "loading",
        });

        const fetchedProduct = await Api.productById(apiProps, productId);

        setProduct({
            state: "loaded",
            value: fetchedProduct,
        });
    }, []);

    return (



        <div className="modal" ref={modalRef}>
            <Loader predicate={() => { return product.state === "loaded"; }}>
                <ProductDetails product={product.value} />
            </Loader>
        </div>
    );
}
