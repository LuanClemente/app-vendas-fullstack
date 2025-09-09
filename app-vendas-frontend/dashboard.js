document.addEventListener('DOMContentLoaded', () => {
    
    const token = localStorage.getItem('jwt_token');
    const userId = localStorage.getItem('user_id');

    const logoutButton = document.getElementById('logout-button');
    const metaModal = document.getElementById('meta-modal');
    const definirMetaBtn = document.getElementById('definir-meta-btn');
    const closeMetaModalBtn = document.getElementById('close-meta-modal-button');
    const cancelMetaBtn = document.getElementById('cancel-meta-button');
    const metaForm = document.getElementById('meta-form');

    if (!token || !userId) {
        if (window.location.pathname.includes('dashboard.html')) {
            alert('Você precisa estar logado para acessar esta página.');
            window.location.href = 'index.html';
        }
        return;
    }

    if(logoutButton) {
        logoutButton.addEventListener('click', () => {
            localStorage.clear();
            alert('Você saiu da sua conta.');
            window.location.href = 'index.html';
        });
    }

    if(definirMetaBtn) {
        definirMetaBtn.addEventListener('click', () => metaModal.style.display = 'flex');
    }
    if(closeMetaModalBtn) {
        closeMetaModalBtn.addEventListener('click', () => metaModal.style.display = 'none');
    }
    if(cancelMetaBtn) {
        cancelMetaBtn.addEventListener('click', () => metaModal.style.display = 'none');
    }
    if(metaModal) {
        window.addEventListener('click', (event) => {
            if (event.target == metaModal) metaModal.style.display = 'none';
        });

        metaForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const hoje = new Date();
            const dadosMeta = {
                valor: document.getElementById('valorMeta').value,
                mes: hoje.getMonth() + 1,
                ano: hoje.getFullYear(),
                idUsuario: userId
            };
    
            try {
                const response = await fetch('http://localhost:8081/api/metas', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                    body: JSON.stringify(dadosMeta)
                });
    
                if (response.ok) {
                    metaModal.style.display = 'none';
                    fetchDashboardData();
                } else {
                    alert('Erro ao definir a meta.');
                }
            } catch (error) {
                console.error("Erro de rede ao definir meta:", error);
                alert("Não foi possível conectar ao servidor.");
            }
        });
    }

    async function fetchDashboardData() {
        try {
            const response = await fetch('http://localhost:8081/api/relatorios/dashboard/vendedor', {
                method: 'GET', headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const data = await response.json();
                updateUI(data);
            } else if (response.status === 403 || response.status === 401) {
                alert('Sua sessão expirou. Faça login novamente.');
                if(logoutButton) logoutButton.click();
            }
        } catch (error) { console.error('Erro de rede:', error); }
    }

    function updateUI(data) {
        const formatadorMoeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
        
        document.getElementById('meta-valor').textContent = formatadorMoeda.format(data.valorMeta);
        document.getElementById('vendido-valor').textContent = formatadorMoeda.format(data.totalVendido);
        document.getElementById('cortes-valor').textContent = formatadorMoeda.format(data.totalCortes);
        document.getElementById('faturado-valor').textContent = formatadorMoeda.format(data.totalFaturado);
        document.getElementById('faltante-valor').textContent = formatadorMoeda.format(data.valorFaltante);
        
        const porcentagemVendas = data.porcentagemAtingida;
        document.getElementById('porcentagem-valor').textContent = porcentagemVendas.toFixed(2);
        const progressBarVendas = document.getElementById('progresso-barra');
        progressBarVendas.classList.remove('red', 'yellow', 'green');
        if (porcentagemVendas < 25) {
            progressBarVendas.classList.add('red');
        } else if (porcentagemVendas < 75) {
            progressBarVendas.classList.add('yellow');
        } else {
            progressBarVendas.classList.add('green');
        }
        progressBarVendas.style.width = `${Math.min(porcentagemVendas, 100)}%`;

        const porcentagemFaturado = data.porcentagemFaturada;
        document.getElementById('porcentagem-faturado-valor').textContent = porcentagemFaturado.toFixed(2);
        const progressBarFaturado = document.getElementById('progresso-faturado-barra');
        progressBarFaturado.style.width = `${Math.min(porcentagemFaturado, 100)}%`;
    }
    
    // =======================================================
    //  FUNÇÕES PARA BUSCAR E EXIBIR ANIVERSARIANTES
    // =======================================================
    function calcularIdade(dataNascimento) {
        if (!dataNascimento) return '';
        const hoje = new Date();
        const nascimento = new Date(dataNascimento + 'T00:00:00');
        let idade = hoje.getFullYear() - nascimento.getFullYear();
        const m = hoje.getMonth() - nascimento.getMonth();
        if (m < 0 || (m === 0 && hoje.getDate() < nascimento.getDate())) {
            idade--;
        }
        return ` (${idade} anos)`;
    }

    async function fetchAniversariantes() {
        try {
            const response = await fetch('http://localhost:8081/api/clientes/aniversariantes', {
                method: 'GET',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const aniversariantes = await response.json();
                renderAniversariantes(aniversariantes);
            }
        } catch (error) {
            console.error('Erro ao buscar aniversariantes:', error);
        }
    }

    function renderAniversariantes(aniversariantes) {
        const lista = document.getElementById('lista-aniversariantes');
        lista.innerHTML = ''; 

        if (aniversariantes.length === 0) {
            lista.innerHTML = '<li>Nenhum aniversariante hoje. ✨</li>';
            return;
        }

        aniversariantes.forEach(cliente => {
            const item = document.createElement('li');
            const idade = calcularIdade(cliente.dataDeAniversario);

            item.innerHTML = `
                <div>
                    <strong>${cliente.nomeContato}</strong><small>${idade}</small>
                    <br>
                    <small>${cliente.nomeEmpresa || 'N/A'}</small>
                </div>
                <span>${cliente.telefone || 'Não informado'}</span>
            `;
            lista.appendChild(item);
        });
    }

    // --- INICIALIZAÇÃO ---
    if (document.getElementById('meta-valor')) {
        fetchDashboardData();
        fetchAniversariantes();
    }
});