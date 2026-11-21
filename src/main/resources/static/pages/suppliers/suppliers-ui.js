// Interatividade da tela de fornecedores (sem chamadas à API).
// Mostra, enquanto a pessoa digita, quantos caracteres o CNPJ terá depois de tirar a pontuação,
// que é a mesma regra do back (só letras e números, 14 caracteres).
// Tudo fica dentro desta função para não conflitar com as variáveis do seu JS da página.
(() => {

    const cnpjInput = document.getElementById("cnpj");
    const cnpjHint = document.getElementById("cnpj-hint");
    const createForm = document.getElementById("create-form");

    function updateCnpjHint() {
        const length = cnpjInput.value.toUpperCase().replace(/[^A-Z0-9]/g, "").length;
        cnpjHint.textContent = length + " de 14 caracteres";
    }

    cnpjInput.addEventListener("input", updateCnpjHint);

    // Depois do "Limpar", o contador volta para 0.
    createForm.addEventListener("reset", () => setTimeout(updateCnpjHint));
})();
