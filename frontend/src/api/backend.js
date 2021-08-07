// based on https://stackoverflow.com/a/63736799
export async function jsonRequest(url) {
    const response = await fetch(url, {
        credentials: "include",
        headers: {
            "Accept": "application/json",
            "Access-Control-Allow-Credentials": true,
        },
    });
    return response.json();
}


async function sleepMs(ms) {
    await new Promise(resolve => setTimeout(resolve, ms));
}


export async function allProducts(apiProps) {
    const { baseUrl } = apiProps;
    const url = `${baseUrl}/topSecretProducts`;
    return await jsonRequest(url);
}

export async function productsByCategory(apiProps, categoryId) {
    await sleepMs(200);

    return (await allProducts(apiProps)).filter(product => {
        return product.categoryId === categoryId; 
    });
}

export async function allCategories(apiProps) {
    await sleepMs(200);

    return [{
        id: 1,
        name: "music",
    }, {
        id: 2,
        name: "plants",
    }];
}

