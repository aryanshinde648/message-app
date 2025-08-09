document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const passwordHash = document.getElementById('passwordHash').value;
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, email, passwordHash })
            });
            const result = await response.json();
            const msgDiv = document.getElementById('registerMessage');
            if (result.message) {
                msgDiv.innerHTML = `<span class='text-success'>${result.message}</span>`;
            } else if (result.error) {
                msgDiv.innerHTML = `<span class='text-danger'>${result.error}</span>`;
            } else {
                msgDiv.innerHTML = `<span class='text-danger'>Unknown error occurred.</span>`;
            }
        });
    }
});

