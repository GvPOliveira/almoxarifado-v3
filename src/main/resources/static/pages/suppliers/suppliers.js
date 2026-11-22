const formCreateSupplier = document.getElementById("create-form");
const formSearchSupplier = document.getElementById("search-form")

formCreateSupplier.addEventListener("submit", async function (evt) {
    evt.preventDefault();
    document.getElementById("create-message").hidden = true
    const dataErrors = document.querySelectorAll("[data-error-for]");
    for (const errorsClear of dataErrors) {
        errorsClear.textContent = "";
    }
    const name = document.getElementById("name").value;
    const cnpj = document.getElementById("cnpj").value;
    const supplier = {
        "name": name,
        "cnpj": cnpj
    }

    const response = await fetch("/suppliers", {
        method: "POST",
        body: JSON.stringify(supplier),
        headers: {
            "Content-Type": "application/json"
        }
    })
    const success = document.getElementById("create-message")
    if (response.ok) {
        success.hidden = false;
        success.className = "message message-success"
        success.innerText = "Fornecedor criado com sucesso!"
        formCreateSupplier.reset();
    }
    if (response.status === 400) {
        const result = await response.json();
        if (result.errors) {
            for (const [field, message] of Object.entries(result.errors)) {
                document.querySelector(`[data-error-for="${field}"]`).textContent = message;
            }
        } else {
            success.hidden = false;
            success.className = "message message-error"
            success.innerText = result.message;
        }
    }
    if (response.status === 409) {
        const error = await response.json();
        success.hidden = false;
        success.className = "message message-error"
        success.innerText = error.message;
    }
})

formSearchSupplier.addEventListener("submit", async function (evt) {
    evt.preventDefault();
    document.getElementById("search-result").hidden = true
    document.getElementById("search-message").hidden = true
    const cnpj = document.getElementById("search-cnpj").value;
    const normalizedCnpj = cnpj.toUpperCase().replace(/[^A-Z0-9]/g, "");

    const response = await fetch(`/suppliers/${normalizedCnpj}`);
    const supplier = await response.json();
    if (response.ok) {
        const result = document.getElementById("search-result");
        result.hidden = false;
        result.textContent = "Nome: " + supplier.name + " - CNPJ: " + supplier.cnpj;
    }
    if (response.status === 404) {
        const error = document.getElementById("search-message");
        error.hidden = false;
        error.className = "message message-error"
        error.textContent = supplier.message;
    }
})