const codeProduct = document.getElementById("codeProduct");
const nameProduct = document.getElementById("nameProduct");
const registerButton = document.getElementById("registerButton");
const getProductsButton = document.getElementById("getProducts");
const getProductButton = document.getElementById("getProduct");

registerButton.addEventListener("click", registerProduct);
getProductsButton.addEventListener("click", getProducts);
getProductButton.addEventListener("click", getProduct);

async function getProduct() {
    const searchMessage = document.getElementById("search-message");
    const showProduct = document.getElementById("showProduct");
    searchMessage.hidden = true;
    showProduct.textContent = "";

    const code = document.getElementById("productCode").value;
    if (code.trim() === "") {
        searchMessage.hidden = false;
        searchMessage.className = "message message-error";
        searchMessage.textContent = "Informe o código do produto.";
        return;
    }

    const response = await fetch(`/products/${encodeURIComponent(code)}`);
    const result = await response.json();

    if (response.ok) {
        const pCode = document.createElement("p");
        const pName = document.createElement("p");

        const bCode = document.createElement("b");
        const bName = document.createElement("b");

        bCode.textContent = "Código: ";
        bName.textContent = "Nome: ";

        pCode.appendChild(bCode);
        pCode.append(result.code);

        pName.appendChild(bName);
        pName.append(result.name);

        showProduct.appendChild(pCode);
        showProduct.appendChild(pName);
    }
    if (response.status === 404) {
        searchMessage.hidden = false;
        searchMessage.className = "message message-error";
        searchMessage.textContent = result.message;
    }
}

async function getProducts() {
    const listMessage = document.getElementById("list-message");
    listMessage.hidden = true;

    const response = await fetch("/products");
    if (!response.ok) {
        listMessage.hidden = false;
        listMessage.className = "message message-error";
        listMessage.textContent = "Não foi possível carregar os produtos.";
        return;
    }

    const products = await response.json();

    const productList = document.getElementById("productList");
    productList.textContent = "";

    for (let i = 0; i < products.length; i++) {
        const divProduct = document.createElement("div");

        const pCode = document.createElement("p");
        const pName = document.createElement("p");

        const strongCode = document.createElement("strong");
        const strongName = document.createElement("strong");

        strongCode.textContent = "Código: ";
        strongName.textContent = "Nome: ";

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
    const createMessage = document.getElementById("create-message");
    createMessage.hidden = true;
    const dataErrors = document.querySelectorAll("[data-error-for]");
    for (const errorsClear of dataErrors) {
        errorsClear.textContent = "";
    }

    const product = {
        code: codeProduct.value,
        name: nameProduct.value
    }

    const response = await fetch("/products", {
        method: "POST",
        body: JSON.stringify(product),
        headers: {
            "Content-Type": "application/json"
        }
    })

    if (response.ok) {
        const savedProduct = await response.json();
        createMessage.hidden = false;
        createMessage.className = "message message-success";
        createMessage.textContent = "Produto " + savedProduct.code + " cadastrado com sucesso!";
        codeProduct.value = "";
        nameProduct.value = "";
    }
    if (response.status === 400) {
        const result = await response.json();
        if (result.errors) {
            for (const [field, message] of Object.entries(result.errors)) {
                document.querySelector(`[data-error-for="${field}"]`).textContent = message;
            }
        } else {
            createMessage.hidden = false;
            createMessage.className = "message message-error";
            createMessage.textContent = result.message ?? "Dados inválidos. Confira os campos e tente novamente.";
        }
    }
    if (response.status === 409) {
        const result = await response.json();
        createMessage.hidden = false;
        createMessage.className = "message message-error";
        createMessage.textContent = result.message;
    }
}
