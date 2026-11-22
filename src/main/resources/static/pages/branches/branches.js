const buttonSubmit = document.getElementById("submit-button");
const buttonToUpdate = document.getElementById("get-branches");

buttonSubmit.addEventListener("click", createBranch)
buttonToUpdate.addEventListener("click", getBranches)

async function getBranches() {
    const listMessage = document.getElementById("message");
    listMessage.hidden = true;

    const response = await fetch("/branches");
    if (!response.ok) {
        listMessage.hidden = false;
        listMessage.className = "message message-error";
        listMessage.textContent = "Não foi possível carregar as filiais.";
        return;
    }

    const branches = await response.json();
    const listBranches = document.getElementById("branch-list");
    if (branches.length > 0) {
        listBranches.textContent = "";
    }
    for (let i = 0; i < branches.length; i++) {
        const divBranches = document.createElement("div");

        const pCode = document.createElement("p");
        const pName = document.createElement("p");

        const strongCode = document.createElement("strong");
        const strongName = document.createElement("strong");

        strongCode.textContent = "Código: ";
        strongName.textContent = "Nome: ";

        pCode.appendChild(strongCode);
        pCode.append(branches[i].code);

        pName.appendChild(strongName);
        pName.append(branches[i].name);

        divBranches.appendChild(pCode);
        divBranches.appendChild(pName);

        listBranches.appendChild(divBranches);
    }
}

async function createBranch() {
    const createMessage = document.getElementById("create-message");
    createMessage.hidden = true;
    const dataErrors = document.querySelectorAll("[data-error-for]");
    for (const errorsClear of dataErrors) {
        errorsClear.textContent = "";
    }

    const code = document.getElementById("code").value;
    const name = document.getElementById("name").value;

    const branch = {
        "code": code,
        "name": name
    }

    try {
        const response = await fetch("/branches",
            {
                method: "POST",
                body: JSON.stringify(branch),
                headers: {
                    "Content-Type": "application/json"
                }
            })

        if (response.ok) {
            const savedBranch = await response.json();
            createMessage.hidden = false;
            createMessage.className = "message message-success";
            createMessage.textContent = "Filial " + savedBranch.code + " cadastrada com sucesso!";
            document.getElementById("branch-form").reset();
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
        if (response.status === 500) {
            createMessage.hidden = false;
            createMessage.className = "message message-error";
            createMessage.textContent = "Erro inesperado no servidor. Tente novamente em alguns minutos.";
        }
    } catch (error) {
        createMessage.hidden = false;
        createMessage.className = "message message-error";
        createMessage.textContent = "Não foi possível conectar à API. Verifique se a aplicação está rodando.";
    }
}
