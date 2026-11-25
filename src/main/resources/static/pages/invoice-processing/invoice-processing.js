// Tela "Processar notas": mostra a fila de notas pendentes e dá entrada da nota escolhida no estoque.
//
// Endpoints usados:
//   GET  /invoices/pending?branchCode=...              notas não processadas, da mais antiga para a mais nova
//   GET  /suppliers, /branches, /products              nomes para exibir no lugar dos códigos
//   GET  /branches/{branchCode}/products               saldo atual de cada produto na filial
//   POST /suppliers/{cnpj}/invoices/{number}/process   processa a nota (200, 404 ou 409)

const money = new Intl.NumberFormat("pt-BR", {style: "currency", currency: "BRL"});

const processMessage = document.getElementById("process-message");
const branchFilter = document.getElementById("branch-filter");
const refreshButton = document.getElementById("refresh-button");
const pendingCount = document.getElementById("pending-count");
const queueMessage = document.getElementById("queue-message");
const pendingList = document.getElementById("pending-list");

const detailCard = document.getElementById("detail-card");
const detailEmpty = document.getElementById("detail-empty");
const detailContent = document.getElementById("detail-content");
const detailNumber = document.getElementById("detail-number");
const detailInfo = document.getElementById("detail-info");
const detailItems = document.getElementById("detail-items");
const detailSummary = document.getElementById("detail-summary");
const processButton = document.getElementById("process-button");

const confirmDialog = document.getElementById("confirm-dialog");
const confirmText = document.getElementById("confirm-text");
const confirmCancel = document.getElementById("confirm-cancel");
const confirmOk = document.getElementById("confirm-ok");

const supplierNames = new Map();
const branchNames = new Map();
const productNames = new Map();

let pendingInvoices = [];
let selectedKey = null;

// Número da nota só é único dentro do fornecedor, então a chave junta os dois.
function invoiceKey(invoice) {
    return invoice.supplierCnpj + "|" + invoice.number;
}

function selectedInvoice() {
    return pendingInvoices.find(invoice => invoiceKey(invoice) === selectedKey) ?? null;
}

// ===== Chamadas à API =====

// Devolve sempre {ok, status, data}. Se a API estiver fora do ar, status é 0.
async function requestJson(url, options) {
    let response;
    try {
        response = await fetch(url, options);
    } catch (error) {
        return {
            ok: false,
            status: 0,
            data: {message: "Não foi possível conectar à API. Confira se a aplicação está rodando."}
        };
    }

    let data = null;
    try {
        data = await response.json();
    } catch (error) {
        data = null;
    }
    return {ok: response.ok, status: response.status, data};
}

function errorText(result, fallback) {
    return result.data && result.data.message ? result.data.message : fallback;
}

async function loadReferenceData() {
    const [suppliers, branches, products] = await Promise.all([
        requestJson("/suppliers"),
        requestJson("/branches"),
        requestJson("/products")
    ]);

    if (suppliers.ok) {
        for (const supplier of suppliers.data) {
            supplierNames.set(supplier.cnpj, supplier.name);
        }
    }
    if (branches.ok) {
        for (const branch of branches.data) {
            branchNames.set(branch.code, branch.name);
            const option = document.createElement("option");
            option.value = branch.code;
            option.textContent = branch.code + " - " + branch.name;
            branchFilter.append(option);
        }
    }
    if (products.ok) {
        for (const product of products.data) {
            productNames.set(product.code, product.name);
        }
    }
}

async function loadPendingInvoices() {
    hideMessage(queueMessage);
    refreshButton.disabled = true;

    const branchCode = branchFilter.value;
    let url = "/invoices/pending";
    if (branchCode !== "") {
        url += "?branchCode=" + encodeURIComponent(branchCode);
    }
    const result = await requestJson(url);
    refreshButton.disabled = false;

    if (result.ok) {
        pendingInvoices = result.data;
    } else {
        pendingInvoices = [];
        showMessage(queueMessage, "error", errorText(result, "Não foi possível carregar as notas pendentes."));
    }

    if (!selectedInvoice()) {
        selectedKey = null;
    }
    renderQueue();
    await renderDetail();
}

// Saldo atual de cada produto na filial: Map(productCode -> {quantity, location}), ou null se falhar.
async function loadBranchStock(branchCode) {
    const result = await requestJson("/branches/" + encodeURIComponent(branchCode) + "/products");
    if (!result.ok) {
        return null;
    }
    const stock = new Map();
    for (const branchProduct of result.data) {
        stock.set(branchProduct.productCode, branchProduct);
    }
    return stock;
}

// ===== Fila =====

function renderQueue() {
    pendingCount.textContent = pendingInvoices.length;
    pendingList.replaceChildren();

    if (pendingInvoices.length === 0) {
        const empty = document.createElement("li");
        empty.className = "empty-state";
        const title = document.createElement("strong");
        title.textContent = "Nenhuma nota pendente";
        empty.append(title, "As notas cadastradas em Notas fiscais aparecem aqui até serem processadas.");
        pendingList.append(empty);
        return;
    }

    for (const invoice of pendingInvoices) {
        const key = invoiceKey(invoice);
        const button = document.createElement("button");
        button.type = "button";
        button.className = "queue-item";
        if (key === selectedKey) {
            button.setAttribute("aria-current", "true");
        }

        const top = document.createElement("span");
        top.className = "queue-item-top";
        top.append(textSpan("NF " + invoice.number), textSpan(money.format(invoice.totalValue)));

        const meta = document.createElement("span");
        meta.className = "queue-item-meta";
        meta.append(
            textSpan(supplierName(invoice.supplierCnpj)),
            textSpan("Filial " + invoice.branchCode + " · " + plural(invoice.items.length, "item", "itens"))
        );

        button.append(top, meta);
        button.addEventListener("click", () => selectInvoice(key));

        const li = document.createElement("li");
        li.append(button);
        pendingList.append(li);
    }
}

async function selectInvoice(key) {
    selectedKey = key;
    hideMessage(processMessage);
    renderQueue();
    await renderDetail();

    // No celular a conferência fica abaixo da fila: rola até ela.
    if (detailCard.getBoundingClientRect().top > window.innerHeight * 0.6) {
        detailCard.scrollIntoView({behavior: "smooth", block: "start"});
    }
}

// ===== Conferência =====

async function renderDetail() {
    const invoice = selectedInvoice();
    if (!invoice) {
        detailEmpty.hidden = false;
        detailContent.hidden = true;
        return;
    }

    const key = selectedKey;
    const stock = await loadBranchStock(invoice.branchCode);
    if (key !== selectedKey) {
        return; // o usuário escolheu outra nota enquanto o saldo carregava
    }

    detailEmpty.hidden = true;
    detailContent.hidden = false;
    detailNumber.textContent = "NF " + invoice.number;

    detailInfo.replaceChildren();
    addDetail("Fornecedor", supplierName(invoice.supplierCnpj));
    addDetail("CNPJ", invoice.supplierCnpj);
    addDetail("Filial", branchLabel(invoice.branchCode));
    addDetail("Total da nota", money.format(invoice.totalValue));

    detailItems.replaceChildren();
    // Saldo "corrente" por produto: se o mesmo produto aparece em duas linhas, a segunda parte do saldo da primeira.
    const runningStock = new Map();
    for (const item of invoice.items) {
        detailItems.append(itemRow(item, stock, runningStock));
    }

    detailSummary.textContent = summaryText(invoice);
}

function itemRow(item, stock, runningStock) {
    const row = document.createElement("tr");
    const isStock = item.destination === "STOCK";

    const productCell = document.createElement("td");
    const code = document.createElement("span");
    code.className = "code";
    code.textContent = item.productCode;
    const name = document.createElement("span");
    name.className = "product-name";
    name.textContent = productNames.get(item.productCode) ?? "";
    productCell.append(code, name);
    const location = stock && stock.get(item.productCode) ? stock.get(item.productCode).location : null;
    if (isStock && location) {
        const small = document.createElement("small");
        small.textContent = "Local: " + location;
        productCell.append(small);
    }

    const destinationCell = document.createElement("td");
    const badge = document.createElement("span");
    badge.className = isStock ? "badge badge-stock" : "badge";
    badge.textContent = isStock ? "Estoque" : "Aplicação direta";
    destinationCell.append(badge);

    const stockCell = numberCell("");
    if (!isStock) {
        stockCell.append(textSpan("não movimenta", "no-stock"));
    } else if (!stock) {
        stockCell.textContent = "—";
    } else {
        const current = runningStock.has(item.productCode)
            ? runningStock.get(item.productCode)
            : (stock.get(item.productCode)?.quantity ?? 0);
        const after = current + item.quantity;
        runningStock.set(item.productCode, after);

        const change = document.createElement("span");
        change.className = "stock-change";
        const afterText = document.createElement("strong");
        afterText.textContent = after;
        change.append(current + " → ", afterText);
        stockCell.append(change);
    }

    row.append(
        productCell,
        destinationCell,
        numberCell(item.quantity),
        numberCell(money.format(item.unitPrice)),
        numberCell(money.format(item.quantity * item.unitPrice)),
        stockCell
    );
    return row;
}

function summaryText(invoice) {
    const stockItems = invoice.items.filter(item => item.destination === "STOCK");
    const directItems = invoice.items.length - stockItems.length;
    const units = stockItems.reduce((total, item) => total + item.quantity, 0);

    const parts = [];
    if (stockItems.length > 0) {
        parts.push(plural(stockItems.length, "item entra", "itens entram") + " no estoque da filial "
            + invoice.branchCode + " (" + plural(units, "unidade", "unidades") + ")");
    }
    if (directItems > 0) {
        parts.push(plural(directItems, "item de aplicação direta é registrado", "itens de aplicação direta são registrados")
            + " sem mudar o estoque");
    }
    return "Ao processar, " + parts.join(" e ") + ". A nota sai da fila e a entrada não pode ser desfeita por esta tela.";
}

// ===== Processar =====

processButton.addEventListener("click", () => {
    const invoice = selectedInvoice();
    if (!invoice) {
        return;
    }
    confirmText.textContent = "Dar entrada da NF " + invoice.number + " de " + supplierName(invoice.supplierCnpj)
        + " na filial " + invoice.branchCode + "? " + summaryText(invoice);
    confirmDialog.showModal();
});

confirmCancel.addEventListener("click", () => confirmDialog.close());

confirmOk.addEventListener("click", async () => {
    confirmDialog.close();
    await processSelectedInvoice();
});

async function processSelectedInvoice() {
    const invoice = selectedInvoice();
    if (!invoice) {
        return;
    }

    processButton.disabled = true;
    processButton.textContent = "Processando...";
    const url = "/suppliers/" + encodeURIComponent(invoice.supplierCnpj)
        + "/invoices/" + encodeURIComponent(invoice.number) + "/process";
    const result = await requestJson(url, {method: "POST"});
    processButton.disabled = false;
    processButton.textContent = "Processar entrada";

    if (result.ok) {
        const stockItems = invoice.items.filter(item => item.destination === "STOCK").length;
        showMessage(processMessage, "success", "Nota " + invoice.number + " processada. "
            + plural(stockItems, "item entrou", "itens entraram") + " no estoque da filial " + invoice.branchCode + ".");
        selectedKey = null;
        await loadPendingInvoices();
    } else if (result.status === 404 || result.status === 409) {
        // A nota sumiu ou outra pessoa processou antes: a fila é recarregada para refletir o estado atual.
        showMessage(processMessage, "error", errorText(result, "Não foi possível processar a nota.")
            + " A lista foi atualizada.");
        await loadPendingInvoices();
    } else {
        showMessage(processMessage, "error", errorText(result, "Não foi possível processar a nota. Tente novamente."));
    }
    processMessage.scrollIntoView({behavior: "smooth", block: "nearest"});
}

// ===== Utilitários de tela =====

function showMessage(element, type, text) {
    element.className = "message message-" + type;
    element.textContent = text;
    element.hidden = false;
}

function hideMessage(element) {
    element.hidden = true;
    element.textContent = "";
}

function textSpan(text, className) {
    const span = document.createElement("span");
    span.textContent = text;
    if (className) {
        span.className = className;
    }
    return span;
}

function numberCell(content) {
    const cell = document.createElement("td");
    cell.className = "number";
    cell.textContent = content;
    return cell;
}

function addDetail(label, value) {
    const dt = document.createElement("dt");
    dt.textContent = label;
    const dd = document.createElement("dd");
    dd.textContent = value;
    detailInfo.append(dt, dd);
}

function supplierName(cnpj) {
    return supplierNames.get(cnpj) ?? cnpj;
}

function branchLabel(code) {
    return branchNames.has(code) ? code + " - " + branchNames.get(code) : code;
}

function plural(count, singular, pluralText) {
    return count + " " + (count === 1 ? singular : pluralText);
}

branchFilter.addEventListener("change", loadPendingInvoices);
refreshButton.addEventListener("click", loadPendingInvoices);

(async () => {
    await loadReferenceData();
    await loadPendingInvoices();
})();
