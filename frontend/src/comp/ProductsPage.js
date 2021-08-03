import React from "react";
import { useState, useEffect } from "react";
import Loader from "./Loader";
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


function ProductsList({ products }) {
    return <div></div>;
}

export default function ProductsPage({ apiProps }) {
    const [categories, setCategories] = useState([]);
    const [selectedCategoryId, setSelectedCategoryId] = useState();

    useEffect(() => {
        setTimeout(() => {
            setCategories(Api.allCategories(apiProps));
        }, 200);
    }, []);

    return (
        <div className="row">
            <div className="col s4">
                <Loader predicate={() => { return categories.length > 0; }}>
                    <CategoryChooser
                        categories={categories}
                        selectedId={selectedCategoryId}
                        onClick={category => {
                            setSelectedCategoryId(category.id); 
                        }} />
                </Loader>
            </div>

            <div className="col s8">
                <ProductsList />
            </div>
        </div>
    );
}

