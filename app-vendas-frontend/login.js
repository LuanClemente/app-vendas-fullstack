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
            // 5. Fazendo a requisição para a API com a função fetch
            const response = await fetch('http://localhost:8081/api/login', {
                method: 'POST',
                headers: {
                    // Informando ao back-end que estamos enviando dados em formato JSON
                    'Content-Type': 'application/json'
                },
                // Convertendo nosso objeto JavaScript para uma string JSON
                body: JSON.stringify(dadosLogin)
            });
             if (response.ok) {
            const data = await response.json();

            // Guardamos o token e também os dados do usuário
            localStorage.setItem('jwt_token', data.token);
            localStorage.setItem('user_id', data.id);
            localStorage.setItem('user_name', data.nome);
            localStorage.setItem('user_perfil', data.perfil);

            window.location.href = 'dashboard.html';
        } 

            // 6. Analisando a resposta do back-end
            if (response.ok) {
                // Se a resposta for SUCESSO (status 200-299)...
                const data = await response.json(); // Pega o corpo da resposta (que contém o token)

                // Guardamos o token no localStorage do navegador.
                // É um "cofre" que persiste os dados mesmo se fecharmos a aba.
                localStorage.setItem('jwt_token', data.token);
                // Redireciona o usuário para a página principal da aplicação.
                // Vamos criar este arquivo em breve!
                window.location.href = 'dashboard.html';

            } else {
                // Se a resposta for ERRO...
                // O back-end pode nos dar uma mensagem de erro específica, mas por enquanto vamos usar uma genérica.
                errorMessage.textContent = 'E-mail ou senha inválidos. Tente novamente.';
            }

        } catch (error) {
            // Se houver um erro de rede (ex: back-end offline)
            console.error('Erro ao tentar fazer login:', error);
            errorMessage.textContent = 'Não foi possível conectar ao servidor. Verifique sua conexão.';
        }
    });
});