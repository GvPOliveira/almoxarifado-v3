// Menu lateral e barra superior compartilhados por todas as telas.
//
// Cada página só precisa ter, no <body class="app">:
//   <aside class="sidebar" data-app-sidebar></aside>
//   <div class="app-main"> <header class="topbar" data-app-topbar></header> ... </div>
// e carregar este arquivo no <head> com: <script src="/layout.js" defer></script>
//
// Para adicionar uma tela nova, inclua um item no MENU abaixo.
// Item sem "href" aparece no menu como "em breve" (não é clicável).
//
// Este arquivo não cria nenhum id que as páginas usam: tudo aqui é por classe ou data-*.

(() => {
    const MENU = [
        {
            items: [{label: "Início", href: "/"}]
        },
        {
            title: "Cadastros",
            items: [
                {label: "Produtos", href: "/pages/products/products.html"},
                {label: "Fornecedores", href: "/pages/suppliers/suppliers.html"},
                {label: "Filiais", href: "/pages/branches/branches.html"}
            ]
        },
        {
            title: "Estoque",
            items: [
                {label: "Produtos da filial", href: "/pages/branch-products/branch-products.html"},
                {label: "Movimentações"}
            ]
        },
        {
            title: "Entradas",
            items: [
                {label: "Notas fiscais", href: "/pages/invoices/invoices.html"},
                {label: "Processar notas"}
            ]
        },
        {
            title: "Saídas",
            items: [
                {label: "Requisições"}
            ]
        }
    ];

    const sidebar = document.querySelector("[data-app-sidebar]");
    const topbar = document.querySelector("[data-app-topbar]");
    if (!sidebar || !topbar) {
        return;
    }

    const currentPath = location.pathname === "/index.html" ? "/" : location.pathname;
    let currentGroup = null;
    let currentItem = null;

    // ===== Menu lateral =====

    sidebar.id = "app-sidebar";
    sidebar.setAttribute("aria-label", "Menu principal");

    const brand = document.createElement("a");
    brand.href = "/";
    brand.className = "logo sidebar-logo";
    const brandIcon = document.createElement("span");
    brandIcon.className = "logo-icon";
    brandIcon.setAttribute("aria-hidden", "true");
    brandIcon.textContent = "A";
    const brandText = document.createElement("span");
    brandText.textContent = "Almoxarifado";
    const brandVersion = document.createElement("span");
    brandVersion.className = "logo-version";
    brandVersion.textContent = "V3";
    brandText.append(brandVersion);
    brand.append(brandIcon, brandText);

    const nav = document.createElement("nav");
    nav.className = "sidebar-nav";
    nav.setAttribute("aria-label", "Módulos");

    for (const group of MENU) {
        const groupElement = document.createElement("div");
        groupElement.className = "nav-group";

        if (group.title) {
            const title = document.createElement("p");
            title.className = "nav-group-title";
            title.textContent = group.title;
            groupElement.append(title);
        }

        const list = document.createElement("ul");
        list.className = "nav-list";

        for (const item of group.items) {
            const li = document.createElement("li");

            if (item.href) {
                const link = document.createElement("a");
                link.className = "nav-item";
                link.href = item.href;
                link.textContent = item.label;
                if (item.href === currentPath) {
                    link.setAttribute("aria-current", "page");
                    currentGroup = group;
                    currentItem = item;
                }
                li.append(link);
            } else {
                const soon = document.createElement("span");
                soon.className = "nav-item nav-item-soon";
                soon.textContent = item.label;
                const tag = document.createElement("span");
                tag.className = "nav-soon";
                tag.textContent = "em breve";
                soon.append(tag);
                li.append(soon);
            }

            list.append(li);
        }

        groupElement.append(list);
        nav.append(groupElement);
    }

    sidebar.replaceChildren(brand, nav);

    // ===== Barra superior: botão do menu (celular) + "você está em" =====

    const menuButton = document.createElement("button");
    menuButton.type = "button";
    menuButton.className = "menu-button";
    menuButton.setAttribute("aria-controls", "app-sidebar");
    menuButton.setAttribute("aria-expanded", "false");
    menuButton.setAttribute("aria-label", "Abrir menu");
    for (let i = 0; i < 3; i++) {
        menuButton.append(document.createElement("span"));
    }

    const breadcrumbNav = document.createElement("nav");
    breadcrumbNav.setAttribute("aria-label", "Você está em");
    const breadcrumb = document.createElement("ol");
    breadcrumb.className = "breadcrumb";

    const crumbs = [];
    if (currentGroup && currentGroup.title) {
        crumbs.push(currentGroup.title);
    }
    crumbs.push(currentItem ? currentItem.label : document.title.split("|")[0].trim());

    crumbs.forEach((text, index) => {
        const li = document.createElement("li");
        li.textContent = text;
        if (index === crumbs.length - 1) {
            li.setAttribute("aria-current", "page");
        }
        breadcrumb.append(li);
    });
    breadcrumbNav.append(breadcrumb);

    // Espaço reservado para o usuário logado e o botão "Sair" (quando chegar a autenticação).
    const topbarEnd = document.createElement("div");
    topbarEnd.className = "topbar-end";

    topbar.replaceChildren(menuButton, breadcrumbNav, topbarEnd);

    // ===== Abrir e fechar o menu no celular =====

    const backdrop = document.createElement("div");
    backdrop.className = "sidebar-backdrop";
    document.body.append(backdrop);

    function setMenuOpen(open) {
        document.body.classList.toggle("sidebar-open", open);
        menuButton.setAttribute("aria-expanded", String(open));
        menuButton.setAttribute("aria-label", open ? "Fechar menu" : "Abrir menu");
    }

    menuButton.addEventListener("click", () => {
        setMenuOpen(!document.body.classList.contains("sidebar-open"));
    });
    backdrop.addEventListener("click", () => setMenuOpen(false));
    document.addEventListener("keydown", event => {
        if (event.key === "Escape" && document.body.classList.contains("sidebar-open")) {
            setMenuOpen(false);
            menuButton.focus();
        }
    });
})();
