let allProducts = [];
let linkedProducts = [];
const supplierSelect = document.getElementById("supplierCnpj");
const branchSelect = document.getElementById("branchCode");
const itemRows = document.getElementById("item-rows");
const formCreateInvoice = document.getElementById("invoice-form");

itemRows.addEventListener("item-row-added", function (evt) {
    fillProductSelect(evt.detail.row);
})
itemRows.addEventListener("change", function (evt) {
    if (evt.target.matches('[data-field="destination"]')) {
        fillProductSelect(evt.target.closest(".item-row"));
    }
})

branchSelect.addEventListener("change", async function () {
    const branchCode = branchSelect.value;
    if (branchCode === "") {
        linkedProducts = [];
    } else {
        const response = await fetch(`/branches/${encodeURIComponent(branchCode)}/products`);
        const branchProducts = await response.json();
        linkedProducts = allProducts.filter(product =>
            branchProducts.some(branchProduct => branchProduct.productCode === product.code))
    }
    for (const row of document.querySelectorAll(".item-row")) {
        fillProductSelect(row);
    }
})

async function loadInitialData() {
    const getSuppliers = await fetch("/suppliers");
    const suppliers = await getSuppliers.json();
    const getBranches = await fetch("/branches");
    const branches = await getBranches.json();
    const getProducts = await fetch("/products");
    const products = await getProducts.json();

    for (const supplier of suppliers) {
        const optSupplier = document.createElement("option");
        optSupplier.value = supplier.cnpj;
        optSupplier.textContent = supplier.name;
        supplierSelect.append(optSupplier);
    }

    for (const branch of branches) {
        const optBranch = document.createElement("option");
        optBranch.value = branch.code;
        optBranch.textContent = branch.code + " - " + branch.name;
        branchSelect.append(optBranch);
    }

    allProducts = products;
}

function fillProductSelect(row) {
    const destinationSelect = row.querySelector(`[data-field="destination"]`);
    const productSelect = row.querySelector(`[data-field="productCode"]`);
    let listProduct;
    if (destinationSelect.value === "STOCK") {
        listProduct = linkedProducts;
    } else {
        listProduct = allProducts;
    }
    productSelect.length = 1;
    for (const product of listProduct) {
        const newOpt = document.createElement("option");
        newOpt.value = product.code;
        newOpt.textContent = product.code + " - " + product.name;
        productSelect.append(newOpt);
    }
}

formCreateInvoice.addEventListener("submit", async function (evt) {
        evt.preventDefault();
        document.getElementById("create-message").hidden = true;
        const errors = document.querySelectorAll("[data-error-for]");
        for (const error of errors) {
            error.textContent = "";
        }
        const cnpj = supplierSelect.value;
        if (cnpj === "") {
            document.querySelector(`[data-error-for="supplierCnpj"]`).textContent = "Selecione o fornecedor.";
            return;
        }
        const itemsRow = document.querySelectorAll(".item-row");
        const items = [];
        for (const row of itemsRow) {
            const productCode = row.querySelector(`[data-field="productCode"]`).value;
            let quantity = row.querySelector(`[data-field="quantity"]`).value;
            let unitPrice = row.querySelector(`[data-field="unitPrice"]`).value;
            const destination = row.querySelector(`[data-field="destination"]`).value;
            if (quantity === "") {
                quantity = null;
            }
            if(unitPrice === ""){
                unitPrice = null;
            }
            items.push({
                "productCode": productCode,
                "quantity": quantity,
                "unitPrice": unitPrice,
                "destination": destination
            })
        }
        const createMessage = document.getElementById("create-message");
        const number = document.getElementById("number").value;
        const branchCode = branchSelect.value;
        const invoice = {
            "number": number,
            "branchCode": branchCode,
            "items": items
        }
        const response = await fetch(`/suppliers/${encodeURIComponent(cnpj)}/invoices`, {
            method: "POST",
            body: JSON.stringify(invoice),
            headers: {
                "Content-Type": "application/json"
            }
        })
        if (response.ok) {
            const responseInvoice = await response.json();
            createMessage.hidden = false;
            createMessage.className = "message message-success"
            createMessage.textContent = "Nota " + responseInvoice.number + " cadastrada. Total: R$" + responseInvoice.totalValue.toFixed(2);
            formCreateInvoice.reset();
        }
        if (response.status === 400) {
            const result = await response.json();
            if (result.errors) {
                for (const [field, message] of Object.entries(result.errors)) {
                    document.querySelector(`[data-error-for="${field}"]`).textContent = message;
                }
            } else {
                createMessage.className = "message message-error"
                createMessage.hidden = false;
                createMessage.innerText = result.message ?? "Dados inválidos. Confira os campos e tente novamente.";
            }
        }
        if (response.status === 404) {
            const result = await response.json();
            createMessage.hidden = false;
            createMessage.className = "message message-error"
            createMessage.innerText = result.message;
        }
        if (response.status === 409) {
            const result = await response.json();
            createMessage.hidden = false;
            createMessage.className = "message message-error"
            createMessage.innerText = result.message;
        }

    }
)

loadInitialData();