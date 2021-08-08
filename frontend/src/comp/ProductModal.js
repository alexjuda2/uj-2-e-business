import React from "react";
import { useState, useEffect, useRef } from "react";


export default function ProductModal({ apiProps, productId, onClose }) {
    const modalRef = useRef(null);

    useEffect(() => {
        M.Modal.init(modalRef.current, {
            onCloseEnd: onClose,
        });
        const modal = M.Modal.getInstance(modalRef.current);
        modal.open();
    }, []);

    return (
        <div className="modal" ref={modalRef}>
            <p>hi!</p>
        </div>
    );
}
