import React from 'react';
import Navbar from './Navbar';
import ProductsPage from './ProductsPage';
import CartPage from './CartPage';
import * as Consts from "../consts";


function contentForSection(section) {
    switch (section) {
        case "products":
            return <ProductsPage apiProps={{
                baseUrl: Consts.apiUrl,
            }} />;

    case "cart":
            return <CartPage apiProps={{
                baseUrl: Consts.apiUrl,
            }} />;

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
    window.location.href = Consts.apiUrl + "/authenticate/google";
}


export default function App() {
    const [selectedSection, setSelectedSection] = React.useState("products");
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
