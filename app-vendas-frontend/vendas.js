document.addEventListener('DOMContentLoaded', () => {

    const token = localStorage.getItem('jwt_token');
    const userId = localStorage.getItem('user_id');

    if (!token || !userId) {
        window.location.href = 'index.html';
        return;
    }

    // --- ELEMENTOS DO DOM ---
    const vendaForm = document.getElementById('venda-form');
    const valorInput = document.getElementById('valor-venda');
    const descricaoInput = document.getElementById('descricao-venda');
    const messageArea = document.getElementById('message-area');
    const vendasTableBody = document.getElementById('vendas-table-body');
    const topClientesList = document.getElementById('top-clientes-list');
    const clienteSearchInput = document.getElementById('cliente-search');
    const clienteIdInput = document.getElementById('cliente-id');
    const clienteResultsDiv = document.getElementById('cliente-results');
    const paginationControls = document.getElementById('pagination-controls');
    const prevButton = document.getElementById('prev-page');
    const nextButton = document.getElementById('next-page');
    const pageInfo = document.getElementById('page-info');

    let currentPage = 0;
    let totalPages = 0;

    // --- LÓGICA DO AUTOCOMPLETAR DE CLIENTES ---
    let searchTimeout;
    clienteSearchInput.addEventListener('input', () => {
        clearTimeout(searchTimeout);
        const termo = clienteSearchInput.value;
        clienteIdInput.value = '';
        if (termo.length < 2) {
            clienteResultsDiv.style.display = 'none';
            return;
        }
        searchTimeout = setTimeout(async () => {
            try {
                const response = await fetch(`https://app-vendas-front-production.up.railway.app/?busca=${termo}`, {
                    headers: { 'Authorization': `Bearer ${token}` }
                });
                if (!response.ok) throw new Error('Falha na busca');
                const clientes = await response.json();
                renderClienteResults(clientes);
            } catch (error) {
                console.error("Erro ao buscar clientes:", error);
            }
        }, 300);
    });

    function renderClienteResults(clientes) {
        clienteResultsDiv.innerHTML = '';
        if (clientes.length === 0) {
            clienteResultsDiv.innerHTML = '<div class="autocomplete-item">Nenhum cliente encontrado.</div>';
        } else {
            clientes.forEach(cliente => {
                const resultItem = document.createElement('div');
                resultItem.className = 'autocomplete-item';
                resultItem.textContent = `${cliente.nomeContato} (${cliente.nomeEmpresa || 'N/A'})`;
                resultItem.addEventListener('click', () => {
                    clienteSearchInput.value = resultItem.textContent;
                    clienteIdInput.value = cliente.id;
                    clienteResultsDiv.style.display = 'none';
                });
                clienteResultsDiv.appendChild(resultItem);
            });
        }
        clienteResultsDiv.style.display = 'block';
    }

    // --- LÓGICA DO FORMULÁRIO DE VENDA ---
    vendaForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const dadosVenda = {
            valor: valorInput.value,
            descricao: descricaoInput.value,
            idCliente: clienteIdInput.value,
            idUsuario: userId
        };
        if (!dadosVenda.idCliente) {
            alert('Por favor, busque e selecione um cliente da lista.');
            return;
        }
        try {
            const response = await fetch('http://localhost:8081/api/vendas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify(dadosVenda)
            });
            if (response.status === 201) {
                messageArea.textContent = 'Venda registrada com sucesso!';
                messageArea.classList.add('success');
                vendaForm.reset();
                clienteSearchInput.value = '';
                fetchVendas();
                fetchTopClientes();
            } else {
                const errorData = await response.json();
                messageArea.textContent = `Erro: ${errorData.mensagem || 'Não foi possível registrar a venda.'}`;
                messageArea.classList.add('error');
            }
        } catch (error) { console.error('Erro de rede:', error); }
    });

    // --- LÓGICA DA TABELA DE VENDAS (COM PAGINAÇÃO) ---
    async function fetchVendas(page = 0) {
        try {
            const response = await fetch(`http://localhost:8081/api/vendas?pagina=${page}`, {
                method: 'GET', headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const pageData = await response.json();
                renderVendasTable(pageData.content);
                updatePaginationControls(pageData);
            }
        } catch (error) { console.error('Erro de rede ao buscar vendas:', error); }
    }

    function renderVendasTable(vendas) {
        vendasTableBody.innerHTML = '';
        if (vendas.length === 0 && currentPage === 0) {
            vendasTableBody.innerHTML = '<tr><td colspan="8">Nenhuma venda registrada.</td></tr>';
            return;
        }
        const formatadorMoeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
        vendas.forEach(venda => {
            const row = document.createElement('tr');
            if (venda.valor >= 0) { row.classList.add('venda-positiva'); } 
            else { row.classList.add('venda-negativa'); }
            const dataFormatada = new Date(venda.dataDaVenda).toLocaleDateString('pt-BR', {timeZone: 'UTC'});
            const statusFaturado = venda.faturada ? '<span class="status-badge status-ativo">Sim</span>' : '<span class="status-badge status-inativo">Não</span>';
            const botaoAcao = venda.faturada ? '' : `<button class="action-btn faturar" data-id="${venda.id}">Faturar</button>`;
            row.innerHTML = `<td>${venda.id}</td><td>${venda.nomeCliente}</td><td>${venda.nomeVendedor}</td><td>${formatadorMoeda.format(venda.valor)}</td><td>${dataFormatada}</td><td>${venda.descricao || ''}</td><td>${statusFaturado}</td><td class="actions-cell">${botaoAcao}<button class="action-btn delete" data-id="${venda.id}">Excluir</button></td>`;
            vendasTableBody.appendChild(row);
        });
        addEventListenersToButtons();
    }

    function updatePaginationControls(pageData) {
        currentPage = pageData.number;
        totalPages = pageData.totalPages;
        pageInfo.textContent = `Página ${currentPage + 1} de ${totalPages}`;
        prevButton.disabled = pageData.first;
        nextButton.disabled = pageData.last;
        paginationControls.style.display = totalPages > 1 ? 'flex' : 'none';
    }

    prevButton.addEventListener('click', () => { if (currentPage > 0) fetchVendas(currentPage - 1); });
    nextButton.addEventListener('click', () => { if (currentPage < totalPages - 1) fetchVendas(currentPage + 1); });

    function addEventListenersToButtons() {
        document.querySelectorAll('.action-btn.delete').forEach(button => {
            button.addEventListener('click', (event) => {
                const vendaId = event.target.dataset.id;
                if (confirm(`Tem certeza que deseja excluir a venda com ID ${vendaId}?`)) {
                    deleteVenda(vendaId);
                }
            });
        });
        document.querySelectorAll('.action-btn.faturar').forEach(button => {
            button.addEventListener('click', (event) => {
                const vendaId = event.target.dataset.id;
                if (confirm(`Tem certeza que deseja faturar a venda com ID ${vendaId}?`)) {
                    faturarVenda(vendaId);
                }
            });
        });
    }

    async function deleteVenda(id) {
        try {
            const response = await fetch(`http://localhost:8081/api/vendas/${id}`, {
                method: 'DELETE', headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) { 
                fetchVendas(currentPage);
                fetchTopClientes(); 
            } else { alert('Erro ao excluir a venda.'); }
        } catch (error) { console.error('Erro de rede:', error); }
    }

    async function faturarVenda(id) {
        try {
            const response = await fetch(`http://localhost:8081/api/vendas/${id}/faturar`, {
                method: 'POST', headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) { 
                fetchVendas(currentPage);
                fetchTopClientes(); 
            } else { alert('Erro ao faturar a venda.'); }
        } catch (error) { console.error('Erro de rede:', error); }
    }

    async function fetchTopClientes() {
        try {
            const response = await fetch(`http://localhost:8081/api/relatorios/top-clientes`, {
                method: 'GET', headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const topClientes = await response.json();
                renderTopClientes(topClientes);
            }
        } catch (error) { console.error('Erro ao buscar top clientes:', error); }
    }

    function renderTopClientes(clientes) {
        topClientesList.innerHTML = '';
        if (clientes.length === 0) {
            topClientesList.innerHTML = '<li>Nenhuma venda no mês ainda.</li>';
            return;
        }
        const formatadorMoeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
        clientes.forEach(cliente => {
            const item = document.createElement('li');
            item.innerHTML = `<span>${cliente.nomeEmpresa || 'Cliente sem empresa'}</span> <strong>${formatadorMoeda.format(cliente.totalComprado)}</strong>`;
            topClientesList.appendChild(item);
        });
    }

    // --- INICIALIZAÇÃO ---
    fetchVendas();
    fetchTopClientes();
});
