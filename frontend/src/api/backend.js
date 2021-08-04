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

