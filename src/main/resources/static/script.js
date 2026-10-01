const codeProduct = document.getElementById("codeProduct");
const nameProduct = document.getElementById("nameProduct");
const registerButton = document.getElementById("registerButton");

registerButton.addEventListener("click", registerProduct);

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
        const result = document.getElementById("result");
        result.innerHTML = "<b>Produto Cadastrado!</b><p>Código: " + codeProduct.value + "</p><p>Name: " + nameProduct.value + "</p>"
    } else {
        const responseError = await response.json()
        alert(responseError.message)
    }

}