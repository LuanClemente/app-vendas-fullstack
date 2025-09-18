document.addEventListener('DOMContentLoaded', () => {

    const token = localStorage.getItem('jwt_token');
    const userId = localStorage.getItem('user_id');

    if (!token || !userId) {
        window.location.href = 'index.html';
        return;
    }

    // --- ELEMENTOS DO DOM ---
    const metaForm = document.getElementById('meta-form');
    const mesSelect = document.getElementById('mes-meta');
    const anoInput = document.getElementById('ano-meta');
    const valorInput = document.getElementById('valor-meta');
    const messageAreaMeta = document.getElementById('message-area-meta');
    const metasTableBody = document.getElementById('metas-table-body');
    
    const metaClienteForm = document.getElementById('meta-cliente-form');
    const clienteSearchInput = document.getElementById('cliente-search-meta');
    const clienteIdInput = document.getElementById('cliente-id-meta');
    const clienteResultsDiv = document.getElementById('cliente-results-meta');
    const valorMetaClienteInput = document.getElementById('valor-meta-cliente');
    const messageAreaMetaCliente = document.getElementById('message-area-meta-cliente');
    const metasClientesList = document.getElementById('metas-clientes-list');

    function init() {
        populateFormDefaults();
        fetchMetas();
        fetchProgressoMetasClientes();
    }

    function populateFormDefaults() {
        const hoje = new Date();
        const anoAtual = hoje.getFullYear();
        const mesAtual = hoje.getMonth() + 1;
        anoInput.value = anoAtual;
        const meses = ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"];
        mesSelect.innerHTML = '';
        for (let i = 0; i < meses.length; i++) {
            const option = document.createElement('option');
            option.value = i + 1;
            option.textContent = meses[i];
            mesSelect.appendChild(option);
        }
        mesSelect.value = mesAtual;
    }

    mesSelect.addEventListener('change', fetchProgressoMetasClientes);
    anoInput.addEventListener('change', fetchProgressoMetasClientes);

    async function fetchMetas() {
        try {
            const response = await fetch('http://localhost:8081/api/metas', {
                method: 'GET', headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const metas = await response.json();
                renderMetasTable(metas);
            }
        } catch (error) { console.error("Erro de rede ao buscar metas:", error); }
    }

    function renderMetasTable(metas) {
        metasTableBody.innerHTML = '';
        if (metas.length === 0) {
            metasTableBody.innerHTML = '<tr><td colspan="2">Nenhuma meta definida.</td></tr>';
            return;
        }
        const formatadorMoeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
        const meses = ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"];
        metas.forEach(meta => {
            const row = document.createElement('tr');
            row.innerHTML = `<td>${meses[meta.mes - 1]} de ${meta.ano}</td><td>${formatadorMoeda.format(meta.valor)}</td>`;
            metasTableBody.appendChild(row);
        });
    }

    metaForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const dadosMeta = { valor: valorInput.value, mes: mesSelect.value, ano: anoInput.value, idUsuario: userId };
        try {
            const response = await fetch('http://localhost:8081/api/metas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify(dadosMeta)
            });
            if (response.ok) {
                messageAreaMeta.textContent = 'Meta salva com sucesso!';
                messageAreaMeta.classList.add('success');
                fetchMetas();
            } else {
                const errorData = await response.json();
                messageAreaMeta.textContent = `Erro: ${errorData.mensagem || 'Não foi possível salvar a meta.'}`;
                messageAreaMeta.classList.add('error');
            }
        } catch (error) { console.error("Erro de rede:", error); }
    });

    let searchTimeout;
    clienteSearchInput.addEventListener('input', () => {
        clearTimeout(searchTimeout);
        const termo = clienteSearchInput.value;
        if (termo.length < 2) {
            clienteResultsDiv.style.display = 'none';
            return;
        }
        searchTimeout = setTimeout(async () => {
            try {
                const response = await fetch(`window.process.env.API_URL?busca=${termo}`, { headers: { 'Authorization': `Bearer ${token}` } });
                if (!response.ok) throw new Error('Falha na busca');
                const clientes = await response.json();
                renderClienteResults(clientes);
            } catch (error) { console.error("Erro ao buscar clientes:", error); }
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

    metaClienteForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        const dadosMeta = {
            valor: valorMetaClienteInput.value,
            mes: mesSelect.value,
            ano: anoInput.value,
            idCliente: clienteIdInput.value,
            idUsuario: userId
        };
        if (!dadosMeta.idCliente) {
            alert('Por favor, busque e selecione um cliente.');
            return;
        }
        try {
            const response = await fetch('http://localhost:8081/api/metas-cliente', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify(dadosMeta)
            });
            if (response.ok) {
                messageAreaMetaCliente.textContent = 'Meta do cliente salva com sucesso!';
                messageAreaMetaCliente.classList.add('success');
                metaClienteForm.reset();
                clienteSearchInput.value = '';
                fetchProgressoMetasClientes();
            } else {
                const errorData = await response.json();
                messageAreaMetaCliente.textContent = `Erro: ${errorData.mensagem || 'Não foi possível salvar a meta.'}`;
                messageAreaMetaCliente.classList.add('error');
            }
        } catch (error) { console.error('Erro de rede:', error); }
    });

    async function fetchProgressoMetasClientes() {
        const mes = mesSelect.value;
        const ano = anoInput.value;
        if (!mes || !ano) return;
        try {
            const response = await fetch(`http://localhost:8081/api/relatorios/progresso-metas-cliente?mes=${mes}&ano=${ano}`, {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const progressoMetas = await response.json();
                renderProgressoMetasClientes(progressoMetas);
            }
        } catch (error) { console.error('Erro ao buscar progresso de metas:', error); }
    }

    function renderProgressoMetasClientes(progressoMetas) {
        metasClientesList.innerHTML = '';
        if (progressoMetas.length === 0) {
            metasClientesList.innerHTML = '<li>Nenhuma meta definida para clientes neste mês.</li>';
            return;
        }
        const formatadorMoeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
        progressoMetas.forEach(progresso => {
            const item = document.createElement('li');
            const percent = Math.min(progresso.porcentagemAtingida, 100);
            item.innerHTML = `
                <div class="client-goal-item">
                    <div class="client-goal-info">
                        <strong>${progresso.nomeCliente}</strong> (${progresso.nomeEmpresa || 'N/A'})
                        <small>${formatadorMoeda.format(progresso.totalVendido)} / ${formatadorMoeda.format(progresso.valorMeta)}</small>
                        <div class="progress-container">
                            <div class="progress-bar green" style="width: ${percent}%;"></div>
                        </div>
                    </div>
                    <div class="client-goal-actions">
                        <button class="action-btn edit" data-id="${progresso.idMeta}" data-valor="${progresso.valorMeta}">Editar</button>
                        <button class="action-btn delete" data-id="${progresso.idMeta}">Excluir</button>
                    </div>
                </div>
            `;
            metasClientesList.appendChild(item);
        });
        addEventListenersToMetaClienteButtons();
    }

    function addEventListenersToMetaClienteButtons() {
        document.querySelectorAll('.client-goal-actions .delete').forEach(button => {
            button.addEventListener('click', (event) => {
                const metaId = event.target.dataset.id;
                if (confirm(`Tem certeza que deseja excluir esta meta?`)) {
                    deleteMetaCliente(metaId);
                }
            });
        });
        document.querySelectorAll('.client-goal-actions .edit').forEach(button => {
            button.addEventListener('click', (event) => {
                const metaId = event.target.dataset.id;
                const valorAtual = event.target.dataset.valor;
                handleEditMetaCliente(metaId, valorAtual);
            });
        });
    }

    async function deleteMetaCliente(id) {
        try {
            const response = await fetch(`http://localhost:8081/api/metas-cliente/${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                fetchProgressoMetasClientes();
            } else {
                alert("Erro ao excluir a meta.");
            }
        } catch (error) { console.error("Erro de rede ao excluir meta:", error); }
    }

    function handleEditMetaCliente(id, valorAtual) {
        const novoValor = prompt("Digite o novo valor para a meta:", valorAtual);
        if (novoValor && !isNaN(novoValor) && novoValor.trim() !== '') {
            updateMetaCliente(id, novoValor);
        }
    }

    async function updateMetaCliente(id, valor) {
        try {
            const response = await fetch(`http://localhost:8081/api/metas-cliente/${id}`, {
                method: 'PUT',
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}` 
                },
                body: valor
            });
            if (response.ok) {
                fetchProgressoMetasClientes();
            } else {
                alert("Erro ao atualizar a meta.");
            }
        } catch (error) { console.error("Erro de rede ao atualizar meta:", error); }
    }
    
    init();
});
