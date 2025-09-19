document.addEventListener('DOMContentLoaded', () => {

    const token = localStorage.getItem('jwt_token');
    const userPerfil = localStorage.getItem('user_perfil');

    if (!token || (userPerfil !== 'SUPERVISOR' && userPerfil !== 'ADMIN')) {
        alert('Você não tem permissão para acessar esta página.');
        window.location.href = 'dashboard.html';
        return;
    }

    // --- ELEMENTOS DO DOM ---
    const tableBody = document.getElementById('users-table-body');
    const modal = document.getElementById('user-modal');
    const modalTitle = document.getElementById('modal-title');
    const addButton = document.getElementById('add-user-button');
    const closeModalButton = document.getElementById('close-modal-button');
    const cancelButton = document.getElementById('cancel-button');
    const userForm = document.getElementById('user-form');
    const desempenhoList = document.getElementById('desempenho-equipe-list');
    const metaGeralForm = document.getElementById('meta-geral-form');
    const mesMetaGeralSelect = document.getElementById('mes-meta-geral');
    const anoMetaGeralInput = document.getElementById('ano-meta-geral');
    const valorMetaGeralInput = document.getElementById('valor-meta-geral');
    const messageAreaMetaGeral = document.getElementById('message-area-meta-geral');
    
    let editingUserId = null;

    // --- LÓGICA DO CRUD DE USUÁRIOS ---
    const openModalForCreate = () => { /* ... */ };
    const openModalForEdit = (user) => { /* ... */ };
    const closeModal = () => { /* ... */ };
    addButton.addEventListener('click', openModalForCreate);
    closeModalButton.addEventListener('click', closeModal);
    cancelButton.addEventListener('click', closeModal);
    window.addEventListener('click', (event) => { if (event.target == modal) closeModal(); });
    userForm.addEventListener('submit', async (event) => { /* ... */ });
    async function fetchUsers() { /* ... */ }
    function renderUsersTable(users) { /* ... */ }
    function addEventListenersToButtons() { /* ... */ }
    async function deleteUser(id) { /* ... */ }

    // --- LÓGICA DO FORMULÁRIO DE META GERAL ---
    function populateMetaGeralForm() { /* ... */ }
    metaGeralForm.addEventListener('submit', async (event) => { /* ... */ });

    // --- LÓGICA DO PAINEL DE DESEMPENHO DA EQUIPE ---
    async function fetchDesempenhoEquipe() { /* ... */ }
    function renderDesempenhoEquipe(desempenho) { /* ... */ }
    
    // =======================================================
    //  NOVA LÓGICA PARA O DASHBOARD DO SUPERVISOR
    // =======================================================
    async function fetchSupervisorDashboard() {
        try {
            const response = await fetch('https://app-vendas-fullstack-production.up.railway.app//api/relatorios/dashboard-supervisor', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                renderSupervisorDashboard(data);
            }
        } catch (error) {
            console.error("Erro ao buscar dados do dashboard do supervisor:", error);
        }
    }

    function renderSupervisorDashboard(data) {
        const formatadorMoeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
        document.getElementById('geral-meta').textContent = formatadorMoeda.format(data.metaGeral);
        document.getElementById('geral-vendido').textContent = formatadorMoeda.format(data.totalVendidoGeral);
        document.getElementById('geral-faturado').textContent = formatadorMoeda.format(data.totalFaturadoGeral);
        document.getElementById('geral-cortes').textContent = formatadorMoeda.format(data.totalCortesGeral);
        document.getElementById('geral-falta-faturar').textContent = formatadorMoeda.format(data.faltaParaFaturarGeral);

        const percVendas = data.porcentagemGeralAtingida;
        document.getElementById('geral-perc-vendas').textContent = percVendas.toFixed(2);
        const progressBarVendas = document.getElementById('geral-progresso-vendas');
        progressBarVendas.style.width = `${Math.min(percVendas, 100)}%`;

        const percFaturado = data.porcentagemGeralFaturada;
        document.getElementById('geral-perc-faturado').textContent = percFaturado.toFixed(2);
        const progressBarFaturado = document.getElementById('geral-progresso-faturado');
        progressBarFaturado.style.width = `${Math.min(percFaturado, 100)}%`;
    }

    // --- INICIALIZAÇÃO ---
    populateMetaGeralForm();
    fetchUsers();
    fetchDesempenhoEquipe();
    fetchSupervisorDashboard(); // Chamada da nova função
});

// Para garantir, segue o código completo das funções que não foram alteradas
document.addEventListener('DOMContentLoaded', () => {

    const token = localStorage.getItem('jwt_token');
    const userPerfil = localStorage.getItem('user_perfil');

    if (!token || (userPerfil !== 'SUPERVISOR' && userPerfil !== 'ADMIN')) {
        alert('Você não tem permissão para acessar esta página.');
        window.location.href = 'dashboard.html';
        return;
    }

    // --- ELEMENTOS DO DOM ---
    const tableBody = document.getElementById('users-table-body');
    const modal = document.getElementById('user-modal');
    const modalTitle = document.getElementById('modal-title');
    const addButton = document.getElementById('add-user-button');
    const closeModalButton = document.getElementById('close-modal-button');
    const cancelButton = document.getElementById('cancel-button');
    const userForm = document.getElementById('user-form');
    const desempenhoList = document.getElementById('desempenho-equipe-list');
    const metaGeralForm = document.getElementById('meta-geral-form');
    const mesMetaGeralSelect = document.getElementById('mes-meta-geral');
    const anoMetaGeralInput = document.getElementById('ano-meta-geral');
    const valorMetaGeralInput = document.getElementById('valor-meta-geral');
    const messageAreaMetaGeral = document.getElementById('message-area-meta-geral');
    
    let editingUserId = null;

    const openModalForCreate = () => {
        editingUserId = null;
        modalTitle.textContent = 'Adicionar Novo Usuário';
        userForm.reset();
        modal.style.display = 'flex';
    };

    const openModalForEdit = (user) => {
        editingUserId = user.id;
        modalTitle.textContent = 'Editar Usuário';
        userForm.reset();
        document.getElementById('nome').value = user.nome;
        document.getElementById('email').value = user.email;
        document.getElementById('perfil').value = user.perfil;
        modal.style.display = 'flex';
    };

    const closeModal = () => {
        modal.style.display = 'none';
    };

    addButton.addEventListener('click', openModalForCreate);
    closeModalButton.addEventListener('click', closeModal);
    cancelButton.addEventListener('click', closeModal);
    window.addEventListener('click', (event) => { if (event.target == modal) closeModal(); });

    userForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const userData = {
            nome: document.getElementById('nome').value,
            email: document.getElementById('email').value,
            senha: document.getElementById('senha').value,
            perfil: document.getElementById('perfil').value
        };
        if (editingUserId && !userData.senha) {
            delete userData.senha;
        }
        const isEditing = editingUserId !== null;
        const method = isEditing ? 'PUT' : 'POST';
        const url = isEditing ? `https://app-vendas-fullstack-production.up.railway.app//api/usuarios/${editingUserId}` : 'https://app-vendas-fullstack-production.up.railway.app//api/usuarios';

        try {
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify(userData)
            });
            if (response.ok) {
                closeModal();
                fetchUsers();
                fetchDesempenhoEquipe();
                fetchSupervisorDashboard();
            } else {
                const errorData = await response.json();
                alert(`Erro: ${errorData.mensagem || 'Não foi possível salvar o usuário.'}`);
            }
        } catch (error) { console.error('Erro de rede:', error); }
    });

    async function fetchUsers() {
        try {
            const response = await fetch('https://app-vendas-fullstack-production.up.railway.app//api/usuarios', { headers: { 'Authorization': `Bearer ${token}` } });
            if (response.ok) {
                const users = await response.json();
                renderUsersTable(users);
            }
        } catch (error) { console.error('Erro de rede ao buscar usuários:', error); }
    }

    function renderUsersTable(users) {
        tableBody.innerHTML = '';
        if (users.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="5">Nenhum usuário encontrado.</td></tr>';
            return;
        }
        users.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `<td>${user.id}</td><td>${user.nome}</td><td>${user.email}</td><td>${user.perfil}</td><td class="actions-cell"><button class="action-btn edit" data-id="${user.id}">Editar</button><button class="action-btn delete" data-id="${user.id}">Excluir</button></td>`;
            tableBody.appendChild(row);
        });
        addEventListenersToButtons();
    }

    function addEventListenersToButtons() {
        document.querySelectorAll('.action-btn.delete').forEach(button => {
            button.addEventListener('click', (event) => {
                const userId = event.target.dataset.id;
                if (confirm(`Tem certeza que deseja excluir o usuário com ID ${userId}?`)) {
                    deleteUser(userId);
                }
            });
        });
        document.querySelectorAll('.action-btn.edit').forEach(button => {
            button.addEventListener('click', async (event) => {
                const userId = event.target.dataset.id;
                try {
                    const response = await fetch(`https://app-vendas-fullstack-production.up.railway.app//api/usuarios/${userId}`, { headers: { 'Authorization': `Bearer ${token}` } });
                    if (response.ok) {
                        const user = await response.json();
                        openModalForEdit(user);
                    } else { alert("Não foi possível buscar os dados do usuário."); }
                } catch (error) { console.error("Erro de rede:", error); }
            });
        });
    }

    async function deleteUser(id) {
        try {
            const response = await fetch(`https://app-vendas-fullstack-production.up.railway.app//api/usuarios/${id}`, {
                method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                fetchUsers();
                fetchDesempenhoEquipe();
                fetchSupervisorDashboard();
            } else { alert("Erro ao excluir usuário."); }
        } catch (error) { console.error('Erro de rede:', error); }
    }

    function populateMetaGeralForm() {
        const hoje = new Date();
        anoMetaGeralInput.value = hoje.getFullYear();
        const meses = ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"];
        mesMetaGeralSelect.innerHTML = '';
        for (let i = 0; i < meses.length; i++) {
            const option = document.createElement('option');
            option.value = i + 1;
            option.textContent = meses[i];
            mesMetaGeralSelect.appendChild(option);
        }
        mesMetaGeralSelect.value = hoje.getMonth() + 1;
    }

    metaGeralForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const dadosMeta = {
            mes: mesMetaGeralSelect.value,
            ano: anoMetaGeralInput.value,
            valor: valorMetaGeralInput.value
        };
        try {
            const response = await fetch('https://app-vendas-fullstack-production.up.railway.app//api/metas-gerais', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify(dadosMeta)
            });
            if (response.ok) {
                messageAreaMetaGeral.textContent = 'Meta da equipe salva com sucesso!';
                messageAreaMetaGeral.classList.add('success');
                fetchDesempenhoEquipe();
                fetchSupervisorDashboard();
            } else {
                messageAreaMetaGeral.textContent = 'Erro ao salvar a meta da equipe.';
                messageAreaMetaGeral.classList.add('error');
            }
        } catch (error) { console.error("Erro de rede:", error); }
    });

    async function fetchDesempenhoEquipe() {
        try {
            const response = await fetch('https://app-vendas-fullstack-production.up.railway.app//api/relatorios/desempenho-equipe', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const desempenho = await response.json();
                renderDesempenhoEquipe(desempenho);
            }
        } catch (error) { console.error("Erro ao buscar desempenho da equipe:", error); }
    }

    function renderDesempenhoEquipe(desempenho) {
        desempenhoList.innerHTML = '';
        if (desempenho.length === 0) {
            desempenhoList.innerHTML = '<p>Nenhum usuário encontrado ou sem metas/vendas no mês.</p>';
            return;
        }
        const formatadorMoeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
        desempenho.forEach(vendedor => {
            const card = document.createElement('div');
            card.className = 'performance-card';
            const percent = Math.min(vendedor.porcentagemAtingida, 100);
            card.innerHTML = `
                <h3>${vendedor.nomeVendedor}</h3>
                <div class="info-line"><span>Meta Equipe:</span><strong>${formatadorMoeda.format(vendedor.valorMeta)}</strong></div>
                <div class="info-line"><span>Vendido:</span><strong>${formatadorMoeda.format(vendedor.totalVendido)}</strong></div>
                <div class="progress-container"><div class="progress-bar green" style="width: ${percent}%;"></div></div>
                <small>${vendedor.porcentagemAtingida.toFixed(2)}% atingido</small>
            `;
            desempenhoList.appendChild(card);
        });
    }

    async function fetchSupervisorDashboard() {
        try {
            const response = await fetch('https://app-vendas-fullstack-production.up.railway.app//api/relatorios/dashboard-supervisor', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                renderSupervisorDashboard(data);
            }
        } catch (error) {
            console.error("Erro ao buscar dados do dashboard do supervisor:", error);
        }
    }

    function renderSupervisorDashboard(data) {
        const formatadorMoeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
        document.getElementById('geral-meta').textContent = formatadorMoeda.format(data.metaGeral);
        document.getElementById('geral-vendido').textContent = formatadorMoeda.format(data.totalVendidoGeral);
        document.getElementById('geral-faturado').textContent = formatadorMoeda.format(data.totalFaturadoGeral);
        document.getElementById('geral-cortes').textContent = formatadorMoeda.format(data.totalCortesGeral);
        document.getElementById('geral-falta-faturar').textContent = formatadorMoeda.format(data.faltaParaFaturarGeral);
        const percVendas = data.porcentagemGeralAtingida;
        document.getElementById('geral-falta-meta').textContent = formatadorMoeda.format(data.faltaParaMetaGeral);
        document.getElementById('geral-perc-vendas').textContent = percVendas.toFixed(2);
        const progressBarVendas = document.getElementById('geral-progresso-vendas');
        progressBarVendas.style.width = `${Math.min(percVendas, 100)}%`;
        const percFaturado = data.porcentagemGeralFaturada;
        document.getElementById('geral-perc-faturado').textContent = percFaturado.toFixed(2);
        const progressBarFaturado = document.getElementById('geral-progresso-faturado');
        progressBarFaturado.style.width = `${Math.min(percFaturado, 100)}%`;
    }

    populateMetaGeralForm();
    fetchUsers();
    fetchDesempenhoEquipe();
    fetchSupervisorDashboard();
});
