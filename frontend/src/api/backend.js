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

export async function allProducts(apiProps) {
    const { baseUrl } = apiProps;
    const url = `${baseUrl}/products`;
    return await jsonRequest(url);
}

export async function productById(apiProps, id) {
    return (await allProducts(apiProps)).filter(product => {
        return product.id === id; 
    })[0];
}

export async function productsByCategory(apiProps, categoryId) {
    return (await allProducts(apiProps)).filter(product => {
        return product.category === categoryId; 
    });
}

export async function allCategories(apiProps) {
    const { baseUrl } = apiProps;
    const url = `${baseUrl}/categories`;
    return await jsonRequest(url);
}

