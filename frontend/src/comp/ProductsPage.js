import React from "react";
import Loader from "./Loader";
import ProductModal from "./ProductModal";
import * as Api from "../api";


function CategoryChooser({ categories, selectedId, onClick }) {
    return (
        <div className="collection">
            {categories.map((category, categoryI) => {
                return (
                    <a key={categoryI}
                        className={category.id === selectedId ? "collection-item active" : "collection-item"}
                        onClick={() => { onClick(category); }}>
                        {category.name}
                    </a>
                );
            })}
        </div>
    );
}


function truncate(text, maxLength) {
    if (text.length <= maxLength) {
        return text;
    } else {
        return text.substr(0, maxLength) + "...";
    }
}


function ProductsList({ products, onClick }) {
    return (
        <table>
            <thead>
                <tr>
                    <th>Name</th>
                    <th>Description</th>
                </tr>
            </thead>
            <tbody>
                {products.map((product, productI) => {
                    return (
                        <tr key={productI} onClick={() => { onClick(product); }}>
                            <td>{product.name}</td>
                            <td>{truncate(product.description || "", 100)}</td>
                        </tr>
                    );
                })}
            </tbody>
        </table>
    );
}

export default function ProductsPage({ apiProps }) {
    const [categories, setCategories] = React.useState({ state: "empty" });
    const [selectedCategoryId, setSelectedCategoryId] = React.useState();
    const [products, setProducts] = React.useState({ state: "empty" });
    const [selectedProductId, setSelectedProductId] = React.useState();

    React.useEffect(async () => {
        setCategories({ state: "loading" });

        const apiResult = await Api.allCategories(apiProps);

        setCategories({
            state: "loaded",
            value: apiResult,
        });
    }, []);

    React.useEffect(async () => {
        if (!selectedCategoryId) {
            return;
        }
        setProducts({ state: "loading" });

        const apiResult = await Api.productsByCategory(apiProps, selectedCategoryId);

        setProducts({
            state: "loaded",
            value: apiResult,
        });
    }, [selectedCategoryId]);

    const productModal = selectedProductId
        ? <ProductModal apiProps={apiProps} productId={selectedProductId} onClose={() => { setSelectedProductId(null); }} />
        : null;

    return (
        <div className="row">
            <div className="col s3">
                <Loader predicate={() => { return categories.state === "loaded"; }}>
                    <CategoryChooser
                        categories={categories.value}
                        selectedId={selectedCategoryId}
                        onClick={category => {
                            setSelectedCategoryId(category.id);
                        }} />
                </Loader>
            </div>

            {products.state === "empty"
                ? <div></div>
                : <div className="col s9">
                    <Loader predicate={() => { return products.state === "loaded"; }}>
                        <ProductsList
                            products={products.value}
                            onClick={product => {
                                console.log(product);
                                setSelectedProductId(product.id);
                            }} />
                    </Loader>
                </div>
            }

            {productModal}
        </div>
    );
}

