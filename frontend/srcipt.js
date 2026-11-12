document.addEventListener('DOMContentLoaded', () => {
    const API_URL = 'http://localhost:8080/api/encomendas';

    // 1. Criar Encomenda (POST)
    const formCriar = document.getElementById('formCriar'); // Certifique-se de que este ID existe no <form>
    if (formCriar) {
        formCriar.addEventListener('submit', async (e) => {
            e.preventDefault();
            const payload = {
                idEncomenda: document.getElementById('idCriar').value,
                destinatario: document.getElementById('destinatario').value,
                enderecoEntrega: document.getElementById('endereco').value
            };

            try {
                const res = await fetch(API_URL, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                alert(await res.text());
            } catch (err) {
                console.error(err);
            }
        });
    }

    // 2. Atualizar Status (PUT)
    const formStatus = document.getElementById('formStatus'); // Certifique-se de que este ID existe no <form>
    if (formStatus) {
        formStatus.addEventListener('submit', async (e) => {
            e.preventDefault();
            const id = document.getElementById('idStatus').value;
            const payload = {
                novoStatus: document.getElementById('novoStatus').value,
                localizacaoAtual: document.getElementById('localizacaoAtual').value
            };

            try {
                const res = await fetch(`${API_URL}/${id}/status`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });
                alert(await res.text());
            } catch (err) {
                console.error(err);
            }
        });
    }

    // 3. Consultar Visão Geral e Linha do Tempo (GET)
    const btnConsultar = document.getElementById('btnConsultar');
    if (btnConsultar) {
        btnConsultar.addEventListener('click', async () => {
            const id = document.getElementById('inputBuscaId').value.trim();
            if (!id) {
                alert('Informe o ID da encomenda.');
                return;
            }

            // Exemplo de chamada GET para a Visão Geral
            try {
                const res = await fetch(`${API_URL}/${id}`);
                if (!res.ok) throw new Error('Encomenda não encontrada.');
                const dados = await res.json();
                console.log('Dados da Visão Geral:', dados);
                // Atualize o HTML do card de visão geral aqui...
            } catch (err) {
                alert(err.message);
            }
        });
    }
});