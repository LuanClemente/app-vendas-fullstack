// Este evento garante que nosso script só vai rodar depois que
// toda a estrutura da página (HTML) for completamente carregada.
document.addEventListener('DOMContentLoaded', () => {

    // 1. Pegando os elementos da página com os quais vamos interagir
    const loginForm = document.getElementById('login-form');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const errorMessage = document.getElementById('error-message');

    // 2. Adicionando o "espião" ao formulário.
    // Usamos 'async' para poder usar 'await' e deixar o código mais limpo.
    loginForm.addEventListener('submit', async (event) => {
        
        // event.preventDefault() é crucial! Ele impede o comportamento padrão do
        // formulário, que é recarregar a página. Nós queremos controlar o que acontece.
        event.preventDefault();

        // 3. Pegando os valores digitados pelo usuário
        const email = emailInput.value;
        const password = passwordInput.value;

        // Limpa mensagens de erro antigas
        errorMessage.textContent = '';

        // 4. Montando o corpo (body) da requisição, exatamente como no Thunder Client
        const dadosLogin = {
            email: email,
            senha: password // No back-end, o DTO espera o campo "senha"
        };

        try {
            // Monta a URL corretamente, evitando barras duplicadas
            let apiUrl = window.process.env.API_URL;
            if (!apiUrl.endsWith('/')) apiUrl += '/';
            const response = await fetch(apiUrl + 'api/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(dadosLogin)
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('jwt_token', data.token);
                localStorage.setItem('user_id', data.id);
                localStorage.setItem('user_name', data.nome);
                localStorage.setItem('user_perfil', data.perfil);
                window.location.href = 'dashboard.html';
            } else {
                errorMessage.textContent = 'E-mail ou senha inválidos. Tente novamente.';
            }
        } catch (error) {
            console.error('Erro ao tentar fazer login:', error);
            errorMessage.textContent = 'Não foi possível conectar ao servidor. Verifique sua conexão.';
        }
    });
});
