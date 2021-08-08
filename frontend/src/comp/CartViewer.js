import React from "react";


function CartItemRow({ item }) {
    return (
        <tr>
            <td>{item.product.name}</td>
            <td>{item.quantity}</td>
        </tr>
    );
}


export default function CartViewer({ cartItems }) {
    return (
        <table>
            <thead>
                <tr>
                    <th>Product</th>
                    <th>Quantity</th>
                </tr>
            </thead>
            <tbody>
                {cartItems.map((item, itemI) => <CartItemRow key={itemI} item={item} />)}
            </tbody>
        </table>
    );
}

