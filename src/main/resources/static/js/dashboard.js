document.addEventListener('DOMContentLoaded', function() {
    // Hide dashboard content initially
    const dashboardContent = document.querySelector('.container');
    if (dashboardContent) dashboardContent.style.display = 'none';
    // Show loading spinner
    const loadingDiv = document.createElement('div');
    loadingDiv.id = 'dashboardLoading';
    loadingDiv.className = 'text-center mt-5';
    loadingDiv.innerHTML = '<div class="spinner-border text-primary" role="status"></div><div>Validating session...</div>';
    document.body.appendChild(loadingDiv);

    // Store JWT token after login
    window.storeJwtToken = function(token) {
        localStorage.setItem('jwtToken', token);
    };

    // Remove JWT token on logout
    window.logout = function() {
        localStorage.removeItem('jwtToken');
        window.location.href = '/login';
    };

    // Check for JWT token in localStorage
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = '/login';
        return;
    }
    // Try to access a protected endpoint to verify JWT
    function onUserIdReady() {
        if (window.currentUserId) {
            // If chat tab is active, load contacts immediately
            const chatTabPane = document.getElementById('chat');
            if (chatTabPane && chatTabPane.classList.contains('show') && chatTabPane.classList.contains('active')) {
                if (typeof loadChatContacts === 'function') loadChatContacts();
            }
        }
    }
    fetch('/api/auth/validate', {
        method: 'GET',
        headers: {
            'Authorization': 'Bearer ' + token,
            'Accept': 'application/json'
        },
        credentials: 'include'
    }).then(res => {
        if (res.status === 200) {
            // Valid token, fetch user info
            fetch('/api/auth/me', {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Accept': 'application/json'
                }
            })
            .then(res => {
                if (!res.ok) throw new Error('Failed to fetch user info');
                return res.json();
            })
            .then(user => {
                window.currentUserId = user.userId;
                onUserIdReady();
                if (dashboardContent) dashboardContent.style.display = '';
                if (loadingDiv) document.body.removeChild(loadingDiv);
            })
            .catch(err => {
                if (loadingDiv) loadingDiv.innerHTML = '<div class="alert alert-danger">Session valid, but failed to load user info.</div>';
            });
        } else {
            if (loadingDiv) loadingDiv.innerHTML = '<div class="alert alert-danger">Session invalid. Please log in again.</div>';
            setTimeout(() => { window.location.href = '/login'; }, 2000);
        }
    }).catch(() => {
        if (loadingDiv) loadingDiv.innerHTML = '<div class="alert alert-danger">Could not validate session. Please log in again.</div>';
        setTimeout(() => { window.location.href = '/login'; }, 2000);
    });
    // --- Friend Requests Dynamic Loading ---
    function loadFriendRequests(userId) {
        const token = localStorage.getItem('jwtToken');
        fetch(`/api/friend-requests/${userId}`, {
            headers: {
                'Authorization': 'Bearer ' + token,
                'Accept': 'application/json'
            }
        })
        .then(res => res.json())
        .then(requests => {
            const tbody = document.getElementById('friend-requests-list');
            tbody.innerHTML = '';
            if (requests.length === 0) {
                tbody.innerHTML = '<tr><td colspan="3" class="text-center">No friend requests yet.</td></tr>';
                return;
            }
            requests.forEach(req => {
                console.log('FriendRequest status:', req.status);
                const senderName = req.senderUsername || (req.sender && (req.sender.username || req.sender.email || req.sender.userId)) || req.senderId || 'Unknown';
                let isPending = false;
                if (typeof req.status === 'string') {
                    isPending = req.status.includes('PENDING');
                } else if (req.status && typeof req.status === 'object' && req.status.name) {
                    isPending = req.status.name === 'PENDING';
                }
                const actions = isPending
                    ? `<button class="btn btn-success btn-sm" onclick="acceptFriendRequest(${req.requestId})">Accept</button>
                       <button class="btn btn-danger btn-sm" onclick="rejectFriendRequest(${req.requestId})">Reject</button>`
                    : '';
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${senderName}</td>
                    <td>${typeof req.status === 'object' && req.status.name ? req.status.name : req.status}</td>
                    <td>${actions}</td>
                `;
                tbody.appendChild(row);
            });
        });
    }

    // Accept/Reject actions
    window.acceptFriendRequest = function(requestId) {
        const token = localStorage.getItem('jwtToken');
        fetch('/api/friend-requests/accept', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `requestId=${requestId}`
        }).then(() => {
            loadFriendRequests(window.currentUserId);
        });
    };
    window.rejectFriendRequest = function(requestId) {
        const token = localStorage.getItem('jwtToken');
        fetch('/api/friend-requests/reject', {
            method: 'POST',
            headers: {
                'Authorization': 'Bearer ' + token,
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `requestId=${requestId}`
        }).then(() => {
            loadFriendRequests(window.currentUserId);
        });
    };

    // Detect tab switch and load friend requests
    const friendTab = document.getElementById('friend-tab');
    if (friendTab) {
        friendTab.addEventListener('shown.bs.tab', function () {
            // You need to set window.currentUserId after login/session validation
            if (window.currentUserId) {
                loadFriendRequests(window.currentUserId);
            }
        });
    }
    // --- Send Friend Request Form Logic ---
    const sendFriendForm = document.getElementById('send-friend-form');
    if (sendFriendForm) {
        sendFriendForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const usernameOrEmail = document.getElementById('friend-username').value.trim();
            const feedbackDiv = document.getElementById('send-friend-feedback');
            feedbackDiv.innerHTML = '';
            if (!usernameOrEmail) {
                feedbackDiv.innerHTML = '<div class="alert alert-danger">Please enter a username or email.</div>';
                return;
            }
            const token = localStorage.getItem('jwtToken');
            // Find user by username/email
            fetch(`/api/users/find?query=${encodeURIComponent(usernameOrEmail)}`, {
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Accept': 'application/json'
                }
            })
            .then(res => res.json())
            .then(user => {
                if (!user || !user.userId) {
                    feedbackDiv.innerHTML = '<div class="alert alert-danger">User not found.</div>';
                    return;
                }
                // Send friend request
                fetch('/api/friend-requests/send', {
                    method: 'POST',
                    headers: {
                        'Authorization': 'Bearer ' + token,
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: `fromUserId=${window.currentUserId}&toUserId=${user.userId}`
                })
                .then(res => res.json())
                .then(success => {
                    if (success) {
                        feedbackDiv.innerHTML = '<div class="alert alert-success">Friend request sent!</div>';
                    } else {
                        feedbackDiv.innerHTML = '<div class="alert alert-warning">Friend request already sent or failed.</div>';
                    }
                });
            });
        });
    }
    // --- Chat Tab Logic ---
    let selectedContactId = null;
    function loadChatContacts() {
        const token = localStorage.getItem('jwtToken');
        fetch(`/api/friends/list/${window.currentUserId}`, {
            headers: {
                'Authorization': 'Bearer ' + token,
                'Accept': 'application/json'
            }
        })
        .then(res => res.json())
        .then(contacts => {
            const contactsList = document.getElementById('chat-contacts-list');
            contactsList.innerHTML = '';
            if (!contacts.length) {
                contactsList.innerHTML = '<li class="list-group-item">No contacts yet.</li>';
                return;
            }
            contacts.forEach(contact => {
                const li = document.createElement('li');
                li.className = 'list-group-item list-group-item-action';
                li.textContent = contact.username || contact.email || contact.userId;
                li.onclick = function() {
                    selectedContactId = contact.userId;
                    document.getElementById('chat-with-label').textContent = `Chat with ${contact.username || contact.email || contact.userId}`;
                    document.getElementById('chat-form').style.display = '';
                    loadChatMessages(selectedContactId);
                };
                contactsList.appendChild(li);
            });
        });
    }

    function loadChatMessages(contactId) {
        const token = localStorage.getItem('jwtToken');
        fetch(`/api/messages/${window.currentUserId}/${contactId}`, {
            headers: {
                'Authorization': 'Bearer ' + token,
                'Accept': 'application/json'
            }
        })
        .then(res => res.json())
        .then(messages => {
            const chatMessages = document.getElementById('chat-messages');
            chatMessages.innerHTML = '';
            if (!messages.length) {
                chatMessages.innerHTML = '<div class="text-center text-muted">No messages yet.</div>';
                return;
            }
            messages.forEach(msg => {
                const div = document.createElement('div');
                div.className = msg.senderId === window.currentUserId ? 'text-end mb-2' : 'text-start mb-2';
                const date = msg.createdAt || msg.createdDate;
                div.innerHTML = `
                    <span class="badge bg-${msg.senderId === window.currentUserId ? 'primary' : 'secondary'}">${msg.messageText}</span><br>
                    <small class="text-muted">${date ? new Date(date).toLocaleString() : ''}</small>
                `;
                chatMessages.appendChild(div);
            });
            chatMessages.scrollTop = chatMessages.scrollHeight;
        });
    }

    const chatTab = document.getElementById('chat-tab');
    if (chatTab) {
        let chatTabInitialized = false;
        chatTab.addEventListener('shown.bs.tab', function () {
            function tryLoadChatContacts() {
                if (window.currentUserId) {
                    loadChatContacts();
                    document.getElementById('chat-form').style.display = 'none';
                    document.getElementById('chat-with-label').textContent = 'Select a contact to chat';
                    document.getElementById('chat-messages').innerHTML = '';
                    chatTabInitialized = true;
                } else {
                    setTimeout(tryLoadChatContacts, 100);
                }
            }
            if (!chatTabInitialized) {
                tryLoadChatContacts();
            } else {
                loadChatContacts();
                document.getElementById('chat-form').style.display = 'none';
                document.getElementById('chat-with-label').textContent = 'Select a contact to chat';
                document.getElementById('chat-messages').innerHTML = '';
            }
        });
    }

    const chatForm = document.getElementById('chat-form');
    if (chatForm) {
        chatForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const input = document.getElementById('chat-input');
            const message = input.value.trim();
            if (!message || !selectedContactId) return;
            const token = localStorage.getItem('jwtToken');
            fetch('/api/messages/send', {
                method: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + token,
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `fromUserId=${window.currentUserId}&toUserId=${selectedContactId}&content=${encodeURIComponent(message)}`
            }).then(() => {
                input.value = '';
                loadChatMessages(selectedContactId);
            });
        });
    }
    // Example: Use JWT token for protected API call
    // fetch('/api/protected-endpoint', {
    //     headers: { 'Authorization': 'Bearer ' + localStorage.getItem('jwtToken') }
    // })
    // .then(res => res.json())
    // .then(data => { /* handle data */ });
});
