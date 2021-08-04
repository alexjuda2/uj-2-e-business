import React from 'react';
import { useState } from 'react';
import Navbar from './Navbar';
import ProductsPage from './ProductsPage';


function contentForSection(section) {
    switch (section) {
        case "products":
            return <ProductsPage apiProps={{}} />;
        default:
            return (
                <div>
                    <h1>Hello, world!</h1>
                    <p>Selected "{section}"</p>
                </div>
            );
    }
}


function redirectToSignIn() {
    window.location.href = "http://localhost:9000/authenticate/google";
}


export default function App() {
    const [selectedSection, setSelectedSection] = useState("products");
    const content = contentForSection(selectedSection);
    return (
        <div>
            <Navbar
                sections={[
                    {
                        text: "Products",
                        onClick: () => { setSelectedSection("products"); }
                    }, {
                        text: "Cart",
                        onClick: () => { setSelectedSection("cart"); }
                    }, {
                        text: "Orders",
                        onClick: () => { setSelectedSection("orders"); }
                    }, {
                        text: "Sign in",
                        onClick: () => { redirectToSignIn(); }
                    }
                ]} />

            <div className="container">
                {content}
            </div>
        </div>
    );
}
