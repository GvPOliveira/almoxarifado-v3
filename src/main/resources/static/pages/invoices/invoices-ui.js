// Interatividade da tela de notas fiscais (sem chamadas à API).
// - Adiciona e remove linhas de item a partir do <template id="item-row-template">.
// - Mantém os nomes dos campos no formato que o back usa nos erros: items[0].quantity, items[1].unitPrice...
//   Assim, no seu invoices.js, o erro "items[0].quantity" do 400 acha o campo com
//   document.querySelector('[name="items[0].quantity"]') e o span com '[data-error-for="items[0].quantity"]'.
// - Mostra a prévia do total de cada item e da nota (o total oficial é calculado pelo back).
// - Mostra quantos caracteres válidos o CNPJ tem.
// Para ler os itens no seu JS: percorra document.querySelectorAll(".item-row") e, em cada linha,
// use row.querySelector('[data-field="quantity"]').value (o mesmo para productCode, unitPrice e destination).
// Tudo fica dentro desta função para não conflitar com as variáveis do seu JS da página.
(() => {

    const invoiceForm = document.getElementById("invoice-form");
    const itemRows = document.getElementById("item-rows");
    const itemTemplate = document.getElementById("item-row-template");
    const invoiceTotalText = document.getElementById("invoice-total");
    const addItemButton = document.getElementById("add-item");
    const supplierCnpjInput = document.getElementById("supplierCnpj");
    const supplierHintText = document.getElementById("supplier-hint");

    const moneyFormatter = new Intl.NumberFormat("pt-BR", {style: "currency", currency: "BRL"});
    let nextRowId = 0;

    function addItemRow(focusNewRow) {
        const row = itemTemplate.content.firstElementChild.cloneNode(true);
        nextRowId++;

        // Liga cada <label> ao seu campo pelo id, para leitores de tela e para o clique no rótulo.
        for (const input of row.querySelectorAll("[data-field]")) {
            input.id = "item-" + nextRowId + "-" + input.dataset.field;
        }
        for (const label of row.querySelectorAll("[data-label]")) {
            label.htmlFor = "item-" + nextRowId + "-" + label.dataset.label;
        }

        itemRows.appendChild(row);
        renumberRows();
        updateTotals();

        if (focusNewRow) {
            row.querySelector('[data-field="productCode"]').focus();
        }
    }

    function removeItemRow(row) {
        row.remove();
        // A nota precisa de pelo menos uma linha na tela.
        if (itemRows.children.length === 0) {
            addItemRow(false);
        }
        renumberRows();
        updateTotals();
    }

    // Depois de adicionar ou remover, as linhas voltam a ser 0, 1, 2... na ordem da tela.
    function renumberRows() {
        const rows = itemRows.querySelectorAll(".item-row");
        rows.forEach((row, index) => {
            for (const input of row.querySelectorAll("[data-field]")) {
                input.name = "items[" + index + "]." + input.dataset.field;
            }
            for (const span of row.querySelectorAll("[data-error-field]")) {
                span.dataset.errorFor = "items[" + index + "]." + span.dataset.errorField;
            }
            row.querySelector(".remove-item").setAttribute("aria-label", "Remover item " + (index + 1));
            row.querySelector(".remove-item").disabled = rows.length === 1;
        });
    }

    function updateTotals() {
        let invoiceTotal = 0;
        for (const row of itemRows.querySelectorAll(".item-row")) {
            const quantity = Number(row.querySelector('[data-field="quantity"]').value) || 0;
            const unitPrice = Number(row.querySelector('[data-field="unitPrice"]').value) || 0;
            // Arredonda para centavos, para a prévia não mostrar 0,30000000000000004.
            const itemTotal = Math.round(quantity * unitPrice * 100) / 100;
            row.querySelector("[data-item-total]").textContent = moneyFormatter.format(itemTotal);
            invoiceTotal += itemTotal;
        }
        invoiceTotalText.textContent = moneyFormatter.format(invoiceTotal);
    }

    function updateSupplierHint() {
        const length = supplierCnpjInput.value.toUpperCase().replace(/[^A-Z0-9]/g, "").length;
        supplierHintText.textContent = length === 0
            ? "Com ou sem pontuação."
            : length + " de 14 caracteres";
    }

    addItemButton.addEventListener("click", () => addItemRow(true));

    itemRows.addEventListener("input", updateTotals);

    itemRows.addEventListener("click", event => {
        const removeButton = event.target.closest(".remove-item");
        if (removeButton) {
            removeItemRow(removeButton.closest(".item-row"));
        }
    });

    supplierCnpjInput.addEventListener("input", updateSupplierHint);

    // O "Limpar" do formulário volta para uma linha vazia e total zero.
    invoiceForm.addEventListener("reset", () => {
        // O reset do navegador roda depois deste evento; o setTimeout espera ele terminar.
        setTimeout(() => {
            itemRows.replaceChildren();
            addItemRow(false);
            updateSupplierHint();
            for (const span of invoiceForm.querySelectorAll("[data-error-for]")) {
                span.textContent = "";
            }
        });
    });

    addItemRow(false);
})();
