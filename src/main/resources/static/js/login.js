document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const passwordHash = document.getElementById('passwordHash').value;
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ username, passwordHash })
            });
            const result = await response.json();
            const msgDiv = document.getElementById('loginMessage');
            if (typeof window.storeJwtToken !== 'function') {
                window.storeJwtToken = function(token) {
                    localStorage.setItem('jwtToken', token);
                };
            }
            if (result.token) {
                window.storeJwtToken(result.token);
                console.log('JWT token stored:', result.token);
                msgDiv.innerHTML = `<span class='text-success'>${result.message}<br/>JWT Token: <code>${result.token}</code></span>`;
                window.location.href = '/dashboard';
            } else if (result.error) {
                msgDiv.innerHTML = `<span class='text-danger'>${result.error}</span>`;
            } else {
                msgDiv.innerHTML = `<span class='text-danger'>Unknown error occurred.</span>`;
            }
        });
    }
});
