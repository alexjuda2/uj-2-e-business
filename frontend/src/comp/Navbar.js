import React from "react";

export default function Navbar({ sections }) {
    return (
        <nav>
            <div className="container">
                <a href="#" className="brand-logo">frontend app</a>
                <div className="nav-wrapper">
                    <ul id="nav-mobile" className="right hide-on-med-and-down">
                        {sections.map((section, sectionI) => {
                            return (
                                <li key={sectionI}>
                                    <a onClick={section.onClick}>{section.text}</a>
                                </li>
                            );
                        })}
                    </ul>
                </div>
            </div>
        </nav>
    );
}

