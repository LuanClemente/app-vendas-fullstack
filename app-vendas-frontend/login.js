document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('password');
    const errorMessage = document.getElementById('error-message');

    // Só adiciona o event listener se o formulário existir
    if (loginForm) {
        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const email = emailInput.value;
            const password = passwordInput.value;
            errorMessage.textContent = '';
            const dadosLogin = {
                email: email,
                senha: password // No back-end, o DTO espera o campo "senha"
            };
            try {
                const response = await fetch(window.process.env.API_URL + 'api/login', {
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
    }
});