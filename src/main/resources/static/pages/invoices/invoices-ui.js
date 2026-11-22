// Interatividade da tela de notas fiscais (sem chamadas à API).
// - Adiciona e remove linhas de item a partir do <template id="item-row-template">.
// - Mantém os nomes dos campos no formato que o back usa nos erros: items[0].quantity, items[1].unitPrice...
//   Assim, no seu invoices.js, o erro "items[0].quantity" do 400 acha o span com
//   document.querySelector('[data-error-for="items[0].quantity"]').
// - Mostra a prévia do total de cada item e da nota (o total oficial é calculado pelo back).
// - Avisa o seu invoices.js sempre que uma linha nova é criada, com o evento "item-row-added".
//
// Para ler os itens no seu JS: percorra document.querySelectorAll(".item-row") e, em cada linha,
// use row.querySelector('[data-field="quantity"]').value (o mesmo para destination, productCode e unitPrice).
//
// Para encher o select de produto de cada linha nova, escute o evento no #item-rows:
//   document.getElementById("item-rows").addEventListener("item-row-added", event => {
//       const row = event.detail.row;   // a linha que acabou de ser criada
//   });
// Escute antes da primeira linha ser criada: este arquivo cria a primeira linha depois que a página
// termina de carregar (DOMContentLoaded), então o seu listener registrado no topo do invoices.js já pega ela.

// Tudo fica dentro desta função para não conflitar com as variáveis do seu JS da página.
(() => {
    const invoiceForm = document.getElementById("invoice-form");
    const itemRows = document.getElementById("item-rows");
    const itemTemplate = document.getElementById("item-row-template");
    const invoiceTotalText = document.getElementById("invoice-total");
    const addItemButton = document.getElementById("add-item");

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

        // Avisa quem estiver ouvindo (o seu invoices.js) que esta linha precisa da lista de produtos.
        itemRows.dispatchEvent(new CustomEvent("item-row-added", {detail: {row}}));

        if (focusNewRow) {
            row.querySelector('[data-field="destination"]').focus();
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
            const removeButton = row.querySelector(".remove-item");
            removeButton.setAttribute("aria-label", "Remover item " + (index + 1));
            removeButton.disabled = rows.length === 1;
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

    addItemButton.addEventListener("click", () => addItemRow(true));

    itemRows.addEventListener("input", updateTotals);

    itemRows.addEventListener("click", event => {
        const removeButton = event.target.closest(".remove-item");
        if (removeButton) {
            removeItemRow(removeButton.closest(".item-row"));
        }
    });

    // O "Limpar" do formulário volta para uma linha vazia e total zero.
    invoiceForm.addEventListener("reset", () => {
        // O reset do navegador roda depois deste evento; o setTimeout espera ele terminar.
        setTimeout(() => {
            itemRows.replaceChildren();
            addItemRow(false);
            for (const span of invoiceForm.querySelectorAll("[data-error-for]")) {
                span.textContent = "";
            }
        });
    });

    // A primeira linha só é criada quando a página termina de carregar, para dar tempo do seu
    // invoices.js registrar o listener de "item-row-added" e encher o select dela também.
    document.addEventListener("DOMContentLoaded", () => addItemRow(false));
})();
