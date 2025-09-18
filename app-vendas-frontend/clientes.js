document.addEventListener('DOMContentLoaded', () => {
    
    const token = localStorage.getItem('jwt_token');

    if (!token) {
        window.location.href = 'index.html';
        return;
    }

    const tableBody = document.getElementById('clients-table-body');
    const modal = document.getElementById('client-modal');
    const modalTitle = document.querySelector('.modal-header h2');
    const addButton = document.getElementById('add-client-button');
    const closeModalButton = document.getElementById('close-modal-button');
    const cancelButton = document.getElementById('cancel-button');
    const clientForm = document.getElementById('client-form');
    
    let editingClientId = null;

    const openModalForCreate = () => {
        editingClientId = null;
        modalTitle.textContent = 'Adicionar Novo Cliente';
        clientForm.reset();
        modal.style.display = 'flex';
    };

    const openModalForEdit = (cliente) => {
        editingClientId = cliente.id;
        modalTitle.textContent = 'Editar Cliente';
        
        document.getElementById('nomeContato').value = cliente.nomeContato;
        document.getElementById('nomeEmpresa').value = cliente.nomeEmpresa || '';
        document.getElementById('email').value = cliente.email;
        document.getElementById('telefone').value = cliente.telefone || '';
        document.getElementById('status').value = cliente.status;
        // Preenche o novo campo de aniversário
        document.getElementById('dataAniversario').value = cliente.dataDeAniversario;

        modal.style.display = 'flex';
    };
    
    const closeModal = () => {
        modal.style.display = 'none';
        clientForm.reset();
        editingClientId = null;
    };

    addButton.addEventListener('click', openModalForCreate);
    closeModalButton.addEventListener('click', closeModal);
    cancelButton.addEventListener('click', closeModal);
    window.addEventListener('click', (event) => {
        if (event.target == modal) closeModal();
    });

    clientForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        // Pega os dados do formulário, incluindo o novo campo
        const clientData = {
            nomeContato: document.getElementById('nomeContato').value,
            nomeEmpresa: document.getElementById('nomeEmpresa').value,
            email: document.getElementById('email').value,
            telefone: document.getElementById('telefone').value,
            status: document.getElementById('status').value,
            dataDeAniversario: document.getElementById('dataAniversario').value
        };

        if (!clientData.dataDeAniversario) {
            clientData.dataDeAniversario = null;
        }

        const isEditing = editingClientId !== null;
        const method = isEditing ? 'PUT' : 'POST';
        const url = isEditing 
            ? `https://app-vendas-front-production.up.railway.app//${editingClientId}` 
            : 'https://app-vendas-front-production.up.railway.app/';

        try {
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
                body: JSON.stringify(clientData)
            });

            if (response.ok) {
                closeModal();
                fetchClientes();
            } else {
                const errorData = await response.json();
                alert(`Erro: ${errorData.mensagem || 'Não foi possível salvar o cliente.'}`);
            }
        } catch (error) {
            console.error('Erro de rede:', error);
            alert('Não foi possível conectar ao servidor.');
        }
    });

    async function fetchClientes() {
        try {
            const response = await fetch('https://app-vendas-front-production.up.railway.app/', {
                method: 'GET',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const clientes = await response.json();
                renderTable(clientes);
            }
        } catch (error) { console.error('Erro de rede:', error); }
    }
    
    function renderTable(clientes) {
        tableBody.innerHTML = '';
        if (clientes.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="7">Nenhum cliente cadastrado.</td></tr>';
            return;
        }

        const getStatusClass = (status) => {
            if (status === 'Cliente Ativo') return 'status-ativo';
            if (status === 'Inativo') return 'status-inativo';
            if (status === 'Em Prospecção') return 'status-prospeccao';
            return '';
        };

        clientes.forEach(cliente => {
            const row = document.createElement('tr');
            row.innerHTML = `<td>${cliente.id}</td><td>${cliente.nomeContato}</td><td>${cliente.nomeEmpresa || ''}</td><td>${cliente.email}</td><td>${cliente.telefone || ''}</td><td><span class="status-badge ${getStatusClass(cliente.status)}">${cliente.status}</span></td><td class="actions-cell"><button class="action-btn edit" data-id="${cliente.id}">Editar</button><button class="action-btn delete" data-id="${cliente.id}">Excluir</button></td>`;
            tableBody.appendChild(row);
        });
        addEventListenersToButtons();
    }

    function addEventListenersToButtons() {
        document.querySelectorAll('.action-btn.delete').forEach(button => {
            button.addEventListener('click', (event) => {
                const clienteId = event.target.dataset.id;
                if (confirm(`Tem certeza que deseja excluir o cliente com ID ${clienteId}?`)) {
                    deleteCliente(clienteId);
                }
            });
        });

        document.querySelectorAll('.action-btn.edit').forEach(button => {
            button.addEventListener('click', (event) => {
                const clienteId = event.target.dataset.id;
                fetchClientByIdAndOpenModal(clienteId);
            });
        });
    }

    async function deleteCliente(id) {
        try {
            const response = await fetch(`https://app-vendas-front-production.up.railway.app//${id}`, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) { fetchClientes(); } 
            else { alert('Erro ao excluir cliente.'); }
        } catch (error) { console.error('Erro de rede:', error); }
    }
    
    async function fetchClientByIdAndOpenModal(id) {
        try {
            const response = await fetch(`https://app-vendas-front-production.up.railway.app//${id}`, {
                method: 'GET',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) {
                const cliente = await response.json();
                openModalForEdit(cliente);
            } else {
                alert('Não foi possível buscar os dados do cliente para edição.');
            }
        } catch (error) {
            console.error('Erro de rede:', error);
        }
    }

    fetchClientes();
});
