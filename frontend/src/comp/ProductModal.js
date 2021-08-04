import React from "react";
import { useState, useEffect, useRef } from "react";


export default function ProductModal() {
    const modalRef = useRef(null);

    useEffect(() => {
        M.Modal.init(modalRef.current);
        const modal = M.Modal.getInstance(modalRef.current);
        console.log(modal);
        modal.open();
    }, []);

    return (
        <div className="modal" ref={modalRef}>
            <p>hi!</p>
        </div>
    );
}
