const formCreateBranchProduct = document.getElementById("create-form")

formCreateBranchProduct.addEventListener("submit", async function (evt) {
    evt.preventDefault();
    document.getElementById("create-message").hidden = true;
    const dataErrors = document.querySelectorAll("[data-error-for]");
    for (const errorsClear of dataErrors) {
        errorsClear.textContent = "";
    }
    const branchCode = document.getElementById("branchCode").value;
    if (branchCode.trim() === "") {
        document.querySelector(`[data-error-for="branchCode"]`).textContent = "O código da filial é obrigatório."
        return;
    }
    const productCode = document.getElementById("productCode").value;
    const location = document.getElementById("location").value;
    const product = {
        "productCode": productCode,
        "location": location
    }

    const response = await fetch(`/branches/${encodeURIComponent(branchCode)}/products`, {
        method: "POST",
        body: JSON.stringify(product),
        headers: {
            "Content-Type": "application/json"
        }
    })
    const createMessage = document.getElementById("create-message")
    if (response.ok) {
        createMessage.hidden = false;
        createMessage.className = "message message-success"
        createMessage.textContent = "Vínculo de produto na filial criado com sucesso!"

        const dtoResponse = await response.json();
        const sessionList = document.getElementById("session-list");
        const div = document.createElement("div");
        div.className = "branch-product-card"
        const bpCodeProduct = document.createElement("b");
        const bpCodeBranch = document.createElement("b");
        const bpLocation = document.createElement("b")
        const pCodeProduct = document.createElement("p")
        const pCodeBranch = document.createElement("p")
        const pLocation = document.createElement("p")
        pCodeProduct.appendChild(bpCodeProduct);
        pCodeBranch.appendChild(bpCodeBranch);
        pLocation.appendChild(bpLocation);
        bpCodeProduct.textContent = "Código Produto: "
        bpCodeBranch.textContent = "Código Filial: "
        bpLocation.textContent = "Localização: "
        pCodeProduct.append(dtoResponse.productCode);
        pCodeBranch.append(dtoResponse.branchCode);
        pLocation.append(dtoResponse.location ?? "Sem localização.");
        div.append(pCodeProduct);
        div.append(pCodeBranch);
        div.append(pLocation);
        sessionList.prepend(div);
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
            createMessage.textContent = result.message;
        }
    }
    if (response.status === 404) {
        const result = await response.json();
        createMessage.hidden = false;
        createMessage.className = "message message-error"
        createMessage.textContent = result.message;
    }
    if (response.status === 409) {
        const result = await response.json();
        createMessage.hidden = false;
        createMessage.className = "message message-error"
        createMessage.textContent = result.message;
    }
})
