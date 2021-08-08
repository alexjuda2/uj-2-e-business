import React from 'react';
import * as Api from "../api";
import Loader from "./Loader";
import ProductDetails from "./ProductDetails";



export default function ProductModal({ apiProps, productId, onClose }) {
    const modalRef = React.useRef(null);

    React.useEffect(() => {
        M.Modal.init(modalRef.current, {
            onCloseEnd: onClose,
        });
        const modal = M.Modal.getInstance(modalRef.current);
        modal.open();
    }, []);

    const [product, setProduct] = React.useState({ state: "empty" });
    React.useEffect(async () => {
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
                <ProductDetails product={product.value} onProductAddToCart={
                    addedProduct => {
                        Api.addProductToUserCart(apiProps, addedProduct.id)
                    }
                } />
            </Loader>
        </div>
    );
}
