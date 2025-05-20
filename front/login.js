document.getElementById('loginForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    try {
        const response = await fetch('http://localhost:8081/api/users/authenticate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            let data;
            try {
                data = await response.json();
            } catch (e) {
                alert('Login failed: Invalid server response.');
                return;
            }
            localStorage.setItem('userId', data.id);
            localStorage.setItem('username', data.username);
            const normalizedRole = data.role.toLowerCase();
            if (normalizedRole === 'admin') {
                window.location.href = 'admin.html';
            } else if (normalizedRole === 'customer') {
                window.location.href = 'customer.html';
            } else if (normalizedRole === 'seller') {
                window.location.href = 'seller.html';
            } else {
                alert('Unknown role.');
            }
        } else {
            let errorData;
            try {
                errorData = await response.json();
                alert(`Error: ${errorData.message}`);
            } catch (e) {
                const text = await response.text();
                alert(`Error: ${text}`);
            }
        }
    } catch (error) {
        console.error('Error details:', error);
        alert('An error occurred while logging in.');
    }
});

document.getElementById('registerRedirect').addEventListener('click', function() {
    window.location.href = 'index.html';
});

document.addEventListener('DOMContentLoaded', function() {
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            window.location.href = 'login.html';
        });
    }
});
