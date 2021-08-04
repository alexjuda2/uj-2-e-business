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

export async function exampleRequest(apiProps) {
    const { baseUrl } = apiProps;
    const url = `${baseUrl}/products`;
    const response = await fetch(url, {
        headers: {
            "Accept": "application/json",
        },
    });
    return response.json();
}

