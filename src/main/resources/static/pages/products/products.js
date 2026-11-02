const codeProduct = document.getElementById("codeProduct");
const nameProduct = document.getElementById("nameProduct");
const registerButton = document.getElementById("registerButton");
const getProductsButton = document.getElementById("getProducts");
const getProductButton = document.getElementById("getProduct");


registerButton.addEventListener("click", registerProduct);
getProductsButton.addEventListener("click", getProducts);
getProductButton.addEventListener("click", getProduct);

async function getProduct() {
    const code = document.getElementById("productCode").value;
    const response = await fetch("/products/" + code, {
        method: "GET",
    })

    if (response.ok) {
        const showProduct = document.getElementById("showProduct");
        const product = await response.json();
        showProduct.innerHTML = "<p><b>Código:</b> " + product.code + "</p><p><b>Name:</b> " + product.name + "</p>"
    } else {
        const responseError = await response.json();
        alert(responseError.message);
    }
}

async function getProducts() {
    const response = await fetch("/products", {method: "GET"})

    const products = await response.json();

    const productList = document.getElementById("productList");

    productList.innerHTML = "";
    for (let i = 0; i < products.length; i++) {
        const divProduct = document.createElement("div");

        const pCode = document.createElement("p");
        const pName = document.createElement("p");

        const strongCode = document.createElement("strong");
        const strongName = document.createElement("strong");

        strongCode.textContent = "Código: ";
        strongName.textContent = "Name: ";

        pCode.appendChild(strongCode);
        pCode.append(products[i].code);

        pName.appendChild(strongName);
        pName.append(products[i].name);

        divProduct.appendChild(pCode);
        divProduct.appendChild(pName);

        productList.appendChild(divProduct);
    }
}


async function registerProduct() {
    const product = {
        code: codeProduct.value,
        name: nameProduct.value
    }
    console.log(product)

    const response = await fetch("/products", {
        method: "POST",
        body: JSON.stringify(product),
        headers: {
            "Content-Type": "application/json"
        }
    })
    if (response.ok) {
        alert("Produto criado com sucesso!")
    } else if (response.status === 400) {
        const codeError = document.getElementById("codeProductError");
        const nameError = document.getElementById("nameProductError");
        codeError.textContent = "";
        nameError.textContent = "";
        const responseError = await response.json();
        for (const [field, message] of Object.entries(responseError.errors)) {
            if (field === "code") {
                codeError.textContent = message;
            } else if (field === "name") {
                nameError.textContent = message;
            }
        }
    } else if (response.status === 409) {
        const responseError = await response.json();
        alert(responseError.message)
    }


}