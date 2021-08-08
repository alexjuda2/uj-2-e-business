import React from 'react';


export default function ProductDetails({ product, onProductAddToCart }) {
    return (
        <div className="container">
            <section className="section">
                <h2>{product.name}</h2>
                <p>{product.description}</p>
            </section>
            <section className="section">
                <button onClick={() => onProductAddToCart(product)}>add to cart</button>
            </section>
        </div>
    );
}

