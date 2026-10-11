const buttonSubmit = document.getElementById("submit-button");
const buttonToUpdate = document.getElementById("get-branches");

buttonSubmit.addEventListener("click", createBranch)
buttonToUpdate.addEventListener("click", getBranches)

async function getBranches() {

    const response = await fetch("/branches", {
        method: "GET"
    })

    const branches = await response.json();
    const listBranches = document.getElementById("branch-list");
    listBranches.textContent = "";
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
    const code = document.getElementById("code").value;
    const name = document.getElementById("name").value;


    const branch = {
        "code": code,
        "name": name
    }

    const errorCode = document.getElementById("errorCode");
    const errorName = document.getElementById("errorName");
    errorCode.textContent = "";
    errorName.textContent = "";
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
            alert("Filial criada com sucesso!");
            const reset = document.getElementById("reset");
            reset.click();

        } else if (response.status === 400) {
            const result = await response.json();
            for (const [field, message] of Object.entries(result.errors)) {
                if (field === "code") {
                    errorCode.textContent = message;
                }
                if (field === "name") {
                    errorName.textContent = message;
                }
            }
        } else if (response.status === 409) {
            const error = await response.json();
            alert(error.message)
        } else if (response.status === 500) {
            alert("Erro inesperado no servidor, status: " + response.status)
        }
    } catch (error) {
        alert("Houve um erro inesperado, tente novamente em alguns minutos.")
    }
}