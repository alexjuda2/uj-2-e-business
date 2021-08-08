// based on https://stackoverflow.com/a/63736799
export async function getJson(url) {
    const response = await fetch(url, {
        credentials: "include",
        headers: {
            "Accept": "application/json",
            "Access-Control-Allow-Credentials": true,
        },
    });
    return response.json();
}

export async function postJson(url, json, csrfToken) {
    const response = await fetch(url, {
        method: "POST",
        credentials: "include",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Access-Control-Allow-Credentials": true,
            "Csrf-Token": csrfToken,
        },
        body: JSON.stringify(json),
    });
    return response.json();
}

// queries

export async function allProducts(apiProps) {
    const { baseUrl } = apiProps;
    const url = `${baseUrl}/products`;
    return await getJson(url);
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
    return await getJson(url);
}

export async function sessionInfo(apiProps) {
    const { baseUrl } = apiProps;
    const url = `${baseUrl}/sessionInfo`;
    return await getJson(url);
}

export async function allCartItems(apiProps) {
    const { baseUrl } = apiProps;
    return await getJson(`${baseUrl}/cartItems`);
}

export async function cartItemsByUserId(apiProps, userId) {
    return (await allCartItems(apiProps)).filter(item => {
        return item.user === userId;
    });
}

export async function userCartItems(apiProps) {
    const fetchedSessionInfo = await sessionInfo(apiProps);
    return await cartItemsByUserId(apiProps, fetchedSessionInfo.userId);
}

// mutations

export async function createCartItem(apiProps, cartItem) {
    const { baseUrl } = apiProps;
    const fetchedNewCartItem = await getJson(`${baseUrl}/cartItems/new`);
    const csrfToken = fetchedNewCartItem.token;

    await postJson(`${baseUrl}/cartItems`, cartItem, csrfToken);
}

export async function addProductToUserCart(apiProps, productId) {
    const fetchedSessionInfo = await sessionInfo(apiProps);

    await createCartItem(apiProps, {
        quantity: 1,
        product: productId,
        user: fetchedSessionInfo.userId,
    });
}

export async function createOrderFromUserCart(apiProps, address) {
    const { baseUrl } = apiProps;
    const fetchedNewCartItem = await getJson(`${baseUrl}/cartItems/new`);
    const csrfToken = fetchedNewCartItem.token;
    return await postJson(`${baseUrl}/user/orders`, { address }, csrfToken)
}

