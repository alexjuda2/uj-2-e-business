export function allProducts(apiProps) {
    return [{
        id: 1,
        name: "Fender Made in Japan Traditional 60S Stratocaster Fiesta Red",
        details: "Since it is used, there is damage due to normal use as a whole, but there is no problem in playing.",
        currency: "usd",
        unitPrice: 80940,
        categoryId: 1,
    }, {
        id: 2,
        name: "Fernandes LIMITED EDITION JB-55 Natural Made in Japan 1992s",
        details: "This is a limited edition model produced only in 1992. It was produced at the Fujigen factory from the data of Neck.",
        currency: "usd",
        unitPrice: 62035,
        categoryId: 1,
    }, {
        id: 3,
        name: "Fender japan tl62b",
        currency: "usd",
        unitPrice: 96900,
        categoryId: 1,
    }, {
        id: 4,
        name: "1966 Fender Precision Bass Lake Placid Blue, 100% Original w/ Case",
        details: "Up for sale, a 1966 Fender Precision Bass in exceptional, 100% original condition and in perfect working order, complete with the original hardshell case. This factory double custom color bass features a lightly greened Lake Placid Blue finish over Candy Apple Red with a white primer coat, stunningly well-kept for the past 55 years. Weighing 9lbs 3oz and strung with flatwound 50-110 strings, this P Bass has an authoritative and round sound. The tonewood combination of an alder body, maple neck, and Brazilian rosewood fingerboard yields a balanced, detailed tone with excellent treble detail and smooth thump with the Tone knob rolled back to taste. Professionally setup, we've had this bass dialed in here at Mike & Mike's Guitar Bar to ensure optimal action and intonation.",
        currency: "usd",
        unitPrice: 1649999,
        categoryId: 1,
    }];
}

export function productsByCategory(apiProps, categoryId) {
    return allProducts(apiProps).filter(product => {
        return product.categoryId === categoryId; 
    });
}

export function allCategories(apiProps) {
    return [{
        id: 1,
        name: "music",
    }, {
        id: 2,
        name: "plants",
    }];
}
