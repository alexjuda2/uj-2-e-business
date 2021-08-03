import React from 'react';
import { useState } from 'react';
import WithNavbar from './WithNavbar';


function App() {
    const [selectedSection, setSelectedSection] = useState("categories");
    return (
        <div>
            <WithNavbar
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
                    }
                ]} />
            <h1>Hello, world!</h1>
            <p>Selected "{selectedSection}"</p>
        </div>
    );
}

export default App;
