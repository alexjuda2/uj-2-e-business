import React from "react";

export default function Loader({ children, predicate }) {
    if (predicate()) {
        return children;
    } else {
        return (
            <div className="progress">
                <div className="indeterminate"></div>
            </div>
        );
    }
}

