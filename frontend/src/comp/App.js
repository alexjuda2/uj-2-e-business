import React from 'react';
import WithNavbar from './WithNavbar';


function App() {
    return (
        <div>
            <WithNavbar sections={[{ text: "Products", }, { text: "Categories", }, { text: "Orders", }]} />
            <h1>Hello, world!</h1>
        </div>
    );
}

export default App;
