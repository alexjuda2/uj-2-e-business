import React from "react";

export default function WithNavbar({ sections }) {
    return (
        <nav>
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
        </nav>
    );
}

