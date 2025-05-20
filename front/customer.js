document.addEventListener('DOMContentLoaded', async function() {
    const userId = localStorage.getItem('userId');
    const username = localStorage.getItem('username');
    const welcome = document.getElementById('customerWelcome');
    if (welcome && username) {
        welcome.textContent = `Welcome, ${username}!`;
    }
    if (userId) {
        try {
            const resp = await fetch(`http://localhost:8081/api/users/id/${userId}`);
            if (resp.ok) {
                const user = await resp.json();
                const welcome = document.getElementById('customerWelcome');
                if (welcome) {
                    welcome.textContent = `Welcome, ${user.username}!`;
                }
            }
        } catch (e) {
            //handle error
        }
    }

    const ordersList = document.getElementById('ordersList');
    const fetchOrdersBtn = document.getElementById('fetchOrders');
    const allDishesList = document.getElementById('allDishesList');
    const fetchAllDishesBtn = document.getElementById('fetchAllDishes');
    const logoutBtn = document.getElementById('logoutBtn');
    const fetchCurrentOrdersBtn = document.getElementById('fetchCurrentOrders');
    const currentOrdersList = document.getElementById('currentOrdersList');
    const fetchPastOrdersBtn = document.getElementById('fetchPastOrders');
    const pastOrdersList = document.getElementById('pastOrdersList');

    if (fetchOrdersBtn) {
        fetchOrdersBtn.addEventListener('click', async function() {
            try {
                const response = await fetch('http://localhost:8080/dishes/api/dishes', {
                    method: 'GET',
                    headers: {
                        'Accept': 'application/json'
                    }
                });
                if (response.ok) {
                    const dishes = await response.json();
                    displayDishes(dishes);
                } else {
                    ordersList.innerHTML = '<li class="list-group-item text-danger">Failed to fetch dishes.</li>';
                }
            } catch (error) {
                ordersList.innerHTML = '<li class="list-group-item text-danger">Error fetching dishes.</li>';
            }
        });
    }

    if (fetchAllDishesBtn) {
        fetchAllDishesBtn.addEventListener('click', async function() {
            try {
                const response = await fetch('http://localhost:8080/dishes/api/dishes/all', {
                    method: 'GET',
                    headers: {
                        'Accept': 'application/json'
                    }
                });
                if (response.ok) {
                    const dishes = await response.json();
                    displayAllDishes(dishes);
                } else {
                    allDishesList.innerHTML = '<li class="list-group-item text-danger">Failed to fetch dishes.</li>';
                }
            } catch (error) {
                allDishesList.innerHTML = '<li class="list-group-item text-danger">Error fetching dishes.</li>';
            }
        });
    }

    if (fetchCurrentOrdersBtn && userId) {
        fetchCurrentOrdersBtn.addEventListener('click', async function() {
            try {
                const response = await fetch(`http://localhost:8082/orders/customer/${userId}`);
                if (response.ok) {
                    const orders = await response.json();
                    displayCurrentOrders(orders);
                } else {
                    currentOrdersList.innerHTML = '<li class="list-group-item text-danger">Failed to fetch current orders.</li>';
                }
            } catch (error) {
                currentOrdersList.innerHTML = '<li class="list-group-item text-danger">Error fetching current orders.</li>';
            }
        });
    }

    if (fetchPastOrdersBtn && userId) {
        fetchPastOrdersBtn.addEventListener('click', async function() {
            try {
                const response = await fetch(`http://localhost:8082/orders/customer/${userId}`);
                if (response.ok) {
                    const orders = await response.json();
                    displayPastOrders(orders);
                } else {
                    pastOrdersList.innerHTML = '<li class="list-group-item text-danger">Failed to fetch past orders.</li>';
                }
            } catch (error) {
                pastOrdersList.innerHTML = '<li class="list-group-item text-danger">Error fetching past orders.</li>';
            }
        });
    }

    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            window.location.href = 'login.html';
        });
    }

    function displayDishes(dishes) {
        ordersList.innerHTML = '';
        if (!dishes || dishes.length === 0) {
            ordersList.innerHTML = '<li class="list-group-item">No dishes found.</li>';
            return;
        }
        dishes.forEach(dish => {
            const li = document.createElement('li');
            li.className = 'list-group-item';
            li.innerHTML = `<strong>${dish.dishName}</strong> - $${dish.price} <br>Description: ${dish.description} <br>Stock: ${dish.stockQuantity}`;
            ordersList.appendChild(li);
        });
    }

    function displayAllDishes(dishes) {
        allDishesList.innerHTML = '';
        if (!dishes || dishes.length === 0) {
            allDishesList.innerHTML = '<li class="list-group-item">No dishes found.</li>';
            return;
        }
        dishes.forEach(dish => {
            const li = document.createElement('li');
            li.className = 'list-group-item';
            li.innerHTML = `<strong>${dish.dishName}</strong> - $${dish.price} <br>Description: ${dish.description} <br>Stock: ${dish.stockQuantity}`;
            allDishesList.appendChild(li);
        });
    }

    async function getDishNameById(dishId) {
        try {
            const resp = await fetch(`http://localhost:8080/dishes/api/dishes/${dishId}`);
            if (resp.ok) {
                const dish = await resp.json();
                return dish.dishName || dishId;
            }
        } catch (e) {}
        return dishId;
    }

    async function displayCurrentOrders(orders) {
        currentOrdersList.innerHTML = '';
        if (!orders || orders.length === 0) {
            currentOrdersList.innerHTML = '<li class="list-group-item">No current orders found.</li>';
            return;
        }
        let hasCurrent = false;
        for (const order of orders) {
            if (order.status && order.status.toLowerCase() !== 'delivered') {
                hasCurrent = true;
                let subtotal = 0;
                if (order.items && Array.isArray(order.items)) {
                    subtotal = order.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
                }
                // Fetch shipping fee using the provided API
                let shippingFee = '...';
                try {
                    const shippingResp = await fetch(`http://localhost:8082/orders/shipping-order/${order.id}`);
                    if (shippingResp.ok) {
                        const shippingOrder = await shippingResp.json();
                        if (shippingOrder && shippingOrder.shippingFee != null) {
                            shippingFee = shippingOrder.shippingFee;
                        }
                    }
                } catch (e) {}
                const li = document.createElement('li');
                li.className = 'list-group-item';
                let itemsHtml = '';
                for (const item of order.items) {
                    const dishName = await getDishNameById(item.dishId);
                    itemsHtml += `<li>Dish: ${dishName}, Quantity: ${item.quantity}, Price: $${item.price}</li>`;
                }
                let totalPriceWithShipping = parseFloat(order.totalPrice) + parseFloat(shippingFee);
                li.innerHTML = `
                    <strong>Status:</strong> ${order.status} <br>
                    <strong>Created At:</strong> ${order.createdAt} <br>
                    <strong>Items:</strong> <ul>${itemsHtml}</ul>
                    <strong>Subtotal:</strong> $${subtotal.toFixed(2)} <br>
                    <strong>Shipping Fee:</strong> $${shippingFee} <br>
                    <strong>Total Price:</strong> $${totalPriceWithShipping.toFixed(2)} <br>`;
                currentOrdersList.appendChild(li);
            }
        }
        if (!hasCurrent) {
            currentOrdersList.innerHTML = '<li class="list-group-item">No current orders found.</li>';
        }
    }

    async function displayPastOrders(orders) {
        pastOrdersList.innerHTML = '';
        if (!orders || orders.length === 0) {
            pastOrdersList.innerHTML = '<li class="list-group-item">No past orders found.</li>';
            return;
        }
        let hasPast = false;
        for (const order of orders) {
            if (order.status && order.status.toLowerCase() === 'delivered') {
                hasPast = true;
                let subtotal = 0;
                if (order.items && Array.isArray(order.items)) {
                    subtotal = order.items.reduce((sum, item) => sum + (item.price * item.quantity), 0);
                }
                // Fetch shipping fee using the provided API
                let shippingFee = '...';
                try {
                    const shippingResp = await fetch(`http://localhost:8082/orders/shipping-order/${order.id}`);
                    if (shippingResp.ok) {
                        const shippingOrder = await shippingResp.json();
                        if (shippingOrder && shippingOrder.shippingFee != null) {
                            shippingFee = shippingOrder.shippingFee;
                        }
                    }
                } catch (e) {}
                const li = document.createElement('li');
                li.className = 'list-group-item';
                let itemsHtml = '';
                for (const item of order.items) {
                    const dishName = await getDishNameById(item.dishId);
                    itemsHtml += `<li>Dish: ${dishName}, Quantity: ${item.quantity}, Price: $${item.price}</li>`;
                }
                let totalPriceWithShipping = parseFloat(order.totalPrice) + parseFloat(shippingFee);
                li.innerHTML = `
                    <strong>Status:</strong> ${order.status} <br>
                    <strong>Created At:</strong> ${order.createdAt} <br>
                    <strong>Items:</strong> <ul>${itemsHtml}</ul>
                    <strong>Subtotal:</strong> $${subtotal.toFixed(2)} <br>
                    <strong>Shipping Fee:</strong> $${shippingFee} <br>
                    <strong>Total Price:</strong> $${totalPriceWithShipping.toFixed(2)} <br>`;
                pastOrdersList.appendChild(li);
            }
        }
        if (!hasPast) {
            pastOrdersList.innerHTML = '<li class="list-group-item">No past orders found.</li>';
        }
    }

    const orderItemsContainer = document.getElementById('orderItemsContainer');
    const addOrderItemBtn = document.getElementById('addOrderItemBtn');
    const makeOrderForm = document.getElementById('makeOrderForm');
    const shippingLocationInput = document.getElementById('shippingLocation');
    const shippingCompanySelect = document.getElementById('shippingCompany');

    let sellers = [];
    let orderItemIndex = 0;

    async function fetchSellers() {
        try {
            const resp = await fetch('http://localhost:8081/api/users/sellers');
            if (resp.ok) {
                sellers = await resp.json();
            } else {
                sellers = [];
            }
        } catch (e) {
            sellers = [];
        }
    }

    async function fetchDishesBySeller(sellerId) {
        try {
            const resp = await fetch(`http://localhost:8080/dishes/api/dishes/seller/${sellerId}`);
            if (resp.ok) {
                return await resp.json();
            }
        } catch (e) {}
        return [];
    }

    async function fetchShippingCompanies(region) {
        try {
            const resp = await fetch(`http://localhost:8082/shipping/companies/region/${encodeURIComponent(region)}`);
            if (resp.ok) {
                return await resp.json();
            }
        } catch (e) {}
        return [];
    }

    async function addOrderItemRow() {
        const idx = orderItemIndex++;
        const row = document.createElement('div');
        row.className = 'order-item-row row mb-3';
        row.dataset.idx = idx;
        row.innerHTML = `
            <div class="col-md-3 mb-2">
                <select class="form-select seller-select" required>
                    <option value="">Select seller</option>
                    ${sellers.map(s => `<option value="${s.id}">${s.username || s.name || s.fullName || 'Seller ' + s.id}</option>`).join('')}
                </select>
            </div>
            <div class="col-md-4 mb-2">
                <select class="form-select dish-select" required disabled>
                    <option value="">Select dish</option>
                </select>
            </div>
            <div class="col-md-3 mb-2">
                <input type="number" class="form-control quantity-input" min="1" placeholder="Quantity" required disabled />
            </div>
            <div class="col-md-2 mb-2 d-flex align-items-center">
                <button type="button" class="btn btn-danger remove-order-item-btn">Remove</button>
            </div>
        `;
        orderItemsContainer.appendChild(row);

        const sellerSelect = row.querySelector('.seller-select');
        const dishSelect = row.querySelector('.dish-select');
        const quantityInput = row.querySelector('.quantity-input');
        sellerSelect.addEventListener('change', async function() {
            dishSelect.innerHTML = '<option value="">Select dish</option>';
            dishSelect.disabled = true;
            quantityInput.value = '';
            quantityInput.disabled = true;
            if (this.value) {
                const dishes = await fetchDishesBySeller(this.value);
                if (dishes.length > 0) {
                    dishSelect.innerHTML += dishes.map(d => `<option value="${d.dishId}" data-price="${d.price}">${d.dishName} ($${d.price})</option>`).join('');
                    dishSelect.disabled = false;
                } else {
                    dishSelect.innerHTML = '<option value="">No dishes</option>';
                }
            }
        });
        dishSelect.addEventListener('change', function() {
            quantityInput.value = '';
            quantityInput.disabled = !this.value;
        });
        row.querySelector('.remove-order-item-btn').addEventListener('click', function() {
            row.remove();
        });
    }

    await fetchSellers();
    addOrderItemRow();

    if (addOrderItemBtn) {
        addOrderItemBtn.addEventListener('click', addOrderItemRow);
    }

    if (shippingLocationInput) {
        let lastRegion = '';
        shippingLocationInput.addEventListener('input', async function() {
            const region = this.value.trim();
            if (region && region !== lastRegion) {
                lastRegion = region;
                shippingCompanySelect.innerHTML = '<option value="">Loading...</option>';
                const companies = await fetchShippingCompanies(region);
                if (companies.length > 0) {
                    shippingCompanySelect.innerHTML = '<option value="">Select shipping company</option>' +
                        companies.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
                } else {
                    shippingCompanySelect.innerHTML = '<option value="">No companies for this region</option>';
                }
            } else if (!region) {
                shippingCompanySelect.innerHTML = '<option value="">Select shipping company</option>';
            }
        });
    }

    if (makeOrderForm) {
        makeOrderForm.addEventListener('submit', async function(event) {
            event.preventDefault();
            const userId = localStorage.getItem('userId');
            if (!userId) {
                showToast('User not logged in.', 'danger');
                return;
            }
            const orderItems = [];
            const rows = orderItemsContainer.querySelectorAll('.order-item-row');
            for (const row of rows) {
                const sellerId = row.querySelector('.seller-select').value;
                const dishSelect = row.querySelector('.dish-select');
                const dishId = dishSelect.value;
                const quantity = row.querySelector('.quantity-input').value;
                const price = dishSelect.options[dishSelect.selectedIndex]?.getAttribute('data-price');
                if (!sellerId || !dishId || !quantity || quantity <= 0) {
                    showToast('Please fill all order item fields.', 'danger');
                    return;
                }
                orderItems.push({ dishId: parseInt(dishId), quantity: parseInt(quantity), price: parseFloat(price) });
            }
            if (orderItems.length === 0) {
                showToast('Add at least one dish to your order.', 'danger');
                return;
            }
            const region = shippingLocationInput.value.trim();
            const shippingCompanyId = shippingCompanySelect.value;
            if (!region || !shippingCompanyId) {
                showToast('Please enter shipping location and select a shipping company.', 'danger');
                return;
            }
            let shippingFee = 0;
            try {
                const regionsResp = await fetch(`http://localhost:8082/shipping/company/${shippingCompanyId}/regions`);
                if (regionsResp.ok) {
                    const regions = await regionsResp.json();
                    const regionObj = regions.find(r => {
                        if (!r.name) return false;
                        return r.name.trim().toLowerCase() === region.trim().toLowerCase();
                    });
                    if (regionObj && regionObj.fee != null) {
                        shippingFee = parseFloat(regionObj.fee);
                    } else {
                        showToast('Selected shipping company does not deliver to this region or fee not found.', 'danger');
                        return;
                    }
                } else {
                    showToast('Failed to fetch shipping company regions.', 'danger');
                    return;
                }
            } catch (e) {
                showToast('Error fetching shipping fee.', 'danger');
                return;
            }
            try {
                const resp = await fetch('http://localhost:8082/orders/create', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        customerId: parseInt(userId),
                        items: orderItems,
                        shippingFee: shippingFee
                    })
                });
                const text = await resp.text();
                if (resp.ok) {
                    let result;
                    try { result = JSON.parse(text); } catch (e) { result = text; }
                    showToast('Order placed successfully!', 'success');
                    let orderId = null;
                    if (typeof result === 'object' && result.id) {
                        orderId = result.id;
                    } else if (typeof result === 'string') {
                        const match = result.match(/order\s*id\s*[:=]\s*(\d+)/i);
                        if (match) orderId = parseInt(match[1]);
                    }
                    if (!orderId) {
                        try {
                            const ordersResp = await fetch(`http://localhost:8082/orders/customer/${userId}`);
                            if (ordersResp.ok) {
                                const orders = await ordersResp.json();
                                if (Array.isArray(orders) && orders.length > 0) {
                                    orderId = orders[orders.length - 1].id;
                                }
                            }
                        } catch (e) {}
                    }
                    if (orderId) {
                        const shippingOrderPayload = {
                            order: { id: orderId },
                            company: { id: parseInt(shippingCompanyId) },
                            region: region,
                            status: "PENDING",
                            shippingFee: shippingFee
                        };
                        try {
                            const shippingResp = await fetch('http://localhost:8082/shipping/order', {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify(shippingOrderPayload)
                            });
                            if (shippingResp.ok) {
                                showToast('Shipping order created!', 'success');
                                let orderDetails = { items: orderItems, shippingFee, totalPrice: (orderItems.reduce((sum, i) => sum + i.price * i.quantity, 0) + shippingFee) };
                                let userBalance = 0;
                                try {
                                    const balResp = await fetch(`http://localhost:8081/api/users/balance/${userId}`);
                                    if (balResp.ok) userBalance = await balResp.json();
                                } catch (e) {}
                                let modal = document.getElementById('paymentModal');
                                if (modal) modal.remove();
                                modal = document.createElement('div');
                                modal.id = 'paymentModal';
                                modal.style.position = 'fixed';
                                modal.style.top = '0';
                                modal.style.left = '0';
                                modal.style.width = '100vw';
                                modal.style.height = '100vh';
                                modal.style.background = 'rgba(0,0,0,0.5)';
                                modal.style.display = 'flex';
                                modal.style.alignItems = 'center';
                                modal.style.justifyContent = 'center';
                                modal.style.zIndex = 10000;
                                modal.innerHTML = `
                                    <div style="background:#fff;padding:32px 24px;border-radius:12px;min-width:340px;max-width:95vw;box-shadow:0 2px 16px rgba(0,0,0,0.18);">
                                        <h4 class="mb-3">Payment Confirmation</h4>
                                        <div><strong>Order Items:</strong><ul>${orderItems.map(i => `<li>Dish ID: ${i.dishId}, Quantity: ${i.quantity}, Price: $${i.price}</li>`).join('')}</ul></div>
                                        <div><strong>Shipping Fee:</strong> $${shippingFee.toFixed(2)}</div>
                                        <div><strong>Total Price:</strong> $${orderDetails.totalPrice.toFixed(2)}</div>
                                        <div><strong>Your Balance:</strong> $${userBalance.toFixed(2)}</div>
                                        <div class="mt-4 d-flex justify-content-between">
                                            <button id="confirmPaymentBtn" class="btn btn-success">Confirm Payment</button>
                                            <button id="cancelOrderBtn" class="btn btn-danger">Cancel Order</button>
                                        </div>
                                    </div>
                                `;
                                document.body.appendChild(modal);
                                document.getElementById('confirmPaymentBtn').onclick = async function() {
                                    if (userBalance >= orderDetails.totalPrice) {
                                        try {
                                            const deductResp = await fetch(`http://localhost:8081/api/users/balance/${userId}/deduct`, {
                                                method: 'POST',
                                                headers: { 'Content-Type': 'application/json' },
                                                body: JSON.stringify({ amount: orderDetails.totalPrice })
                                            });
                                            if (deductResp.ok) {
                                                for (const item of orderItems) {
                                                    try {
                                                        await fetch('http://localhost:8080/dishes/api/dishes/deduct-stock', {
                                                            method: 'POST',
                                                            headers: { 'Content-Type': 'application/json' },
                                                            body: JSON.stringify({ dishId: item.dishId, quantity: item.quantity })
                                                        });
                                                        await fetch('http://localhost:8080/dishes/api/sold-dishes', {
                                                            method: 'POST',
                                                            headers: { 'Content-Type': 'application/json' },
                                                            body: JSON.stringify({ dishId: item.dishId, customerId: parseInt(userId) })
                                                        });
                                                    } catch (e) {
                                                        showToast('Error updating dish stock or sold dishes for dish ' + item.dishId, 'danger');
                                                    }
                                                }
                                                showToast('Payment successful!', 'success');
                                                modal.remove();
                                            } else {
                                                showToast('Failed to deduct balance.', 'danger');
                                            }
                                        } catch (e) {
                                            showToast('Error during payment.', 'danger');
                                        }
                                    } else {
                                        showToast('Insufficient balance. Order will be cancelled.', 'danger');
                                        await fetch(`http://localhost:8082/orders/cancel/${orderId}`, { method: 'POST' });
                                        await fetch(`http://localhost:8082/shipping/order/cancel-by-order-id/${orderId}`, { method: 'POST' });
                                        modal.remove();
                                    }
                                };
                                document.getElementById('cancelOrderBtn').onclick = async function() {
                                    await fetch(`http://localhost:8082/orders/cancel/${orderId}`, { method: 'POST' });
                                    await fetch(`http://localhost:8082/shipping/order/cancel-by-order-id/${orderId}`, { method: 'POST' });
                                    showToast('Order cancelled.', 'danger');
                                    modal.remove();
                                };
                            } else {
                                showToast('Failed to create shipping order.', 'danger');
                            }
                        } catch (e) {
                            showToast('Error creating shipping order.', 'danger');
                        }
                    } else {
                        showToast('Order placed but could not determine order ID for shipping.', 'danger');
                    }
                } else {
                    showToast('Failed to place order. Server says: ' + text, 'danger');
                }
            } catch (e) {
                showToast('Error submitting order. Please try again.', 'danger');
            }
        });
    }


    let toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toastContainer';
        toastContainer.style.position = 'fixed';
        toastContainer.style.top = '24px';
        toastContainer.style.right = '24px';
        toastContainer.style.zIndex = 9999;
        toastContainer.style.display = 'flex';
        toastContainer.style.flexDirection = 'column';
        toastContainer.style.alignItems = 'flex-end';
        document.body.appendChild(toastContainer);
    }

    function showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast align-items-center text-bg-${type} border-0 show`;
        toast.style.minWidth = '260px';
        toast.style.marginBottom = '12px';
        toast.style.background = type === 'info' ? '#0dcaf0' : (type === 'success' ? '#198754' : (type === 'danger' ? '#dc3545' : '#6c757d'));
        toast.style.color = '#fff';
        toast.style.padding = '16px 24px';
        toast.style.borderRadius = '8px';
        toast.style.boxShadow = '0 2px 12px rgba(0,0,0,0.12)';
        toast.innerHTML = `<span>${message}</span>`;
        toastContainer.appendChild(toast);
        setTimeout(() => {
            toast.classList.remove('show');
            toast.classList.add('hide');
            setTimeout(() => toast.remove(), 400);
        }, 4000);
    }

    async function pollNotifications() {
        if (!userId) return;
        try {
            const resp = await fetch(`http://localhost:8082/orders/notifications/${userId}`);
            if (resp.ok) {
                const notifications = await resp.json();
                if (Array.isArray(notifications)) {
                    notifications.forEach(n => {
                        let type = 'info';
                        if (n.status && n.status.toLowerCase() === 'confirmed') type = 'success';
                        if (n.status && n.status.toLowerCase() === 'rejected') type = 'danger';
                        showToast(n.message || 'Notification', type);
                    });
                }
            }
        } catch (e) {
            //handle error
        }
    }
    setInterval(() => {
        pollNotifications();
    }, 3000);
    pollNotifications();
});