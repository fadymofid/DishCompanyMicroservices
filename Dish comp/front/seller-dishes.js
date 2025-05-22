

document.addEventListener('DOMContentLoaded', async function() {
    const userId = localStorage.getItem('userId');
    const dishesContainer = document.getElementById('dishesContainer');

    if (!userId) {
        dishesContainer.innerHTML = '<div class="alert alert-danger">User not logged in.</div>';
        setTimeout(() => window.location.href = 'login.html', 1500);
        return;
    }

    try {
        const response = await fetch(`http://localhost:8087/dishes/api/dishes/seller/${userId}`);
        if (response.ok) {
            const dishes = await response.json();
            if (dishes.length === 0) {
                dishesContainer.innerHTML = '<div class="alert alert-info">No dishes found.</div>';
            } else {
                const list = document.createElement('ul');
                list.className = 'list-group';
                dishes.forEach(dish => {
                    const item = document.createElement('li');
                    item.className = 'list-group-item';
                    item.innerHTML = `
                        <strong>${dish.dishName.trim()}</strong> - $${dish.price} <br>
                        Description: ${dish.description}<br>
                        Stock: ${dish.stockQuantity}<br>
                        <button class="btn btn-warning btn-sm me-2 update-btn" data-dish='${JSON.stringify(dish)}'>Update</button>
                        <button class="btn btn-danger btn-sm delete-btn" data-id="${dish.dishId}">Delete</button>
                    `;
                    list.appendChild(item);
                });
                dishesContainer.appendChild(list);

                dishesContainer.querySelectorAll('.delete-btn').forEach(btn => {
                    btn.addEventListener('click', async function() {
                        const dishId = this.getAttribute('data-id');
                        if (confirm('Are you sure you want to delete this dish?')) {
                            try {
                                const resp = await fetch(`http://localhost:8087/dishes/api/dishes/${dishId}`, {
                                    method: 'DELETE'
                                });
                                if (resp.ok) {
                                    this.closest('li').remove();
                                } else {
                                    alert('Failed to delete dish.');
                                }
                            } catch (err) {
                                alert('Error deleting dish.');
                            }
                        }
                    });
                });

                dishesContainer.querySelectorAll('.update-btn').forEach(btn => {
                    btn.addEventListener('click', function() {
                        const dish = JSON.parse(this.getAttribute('data-dish'));
                        const updateFormHtml = `
                            <form class="update-dish-form mt-2">
                                <input type="text" class="form-control mb-1" name="dishName" value="${dish.dishName.trim()}" required />
                                <input type="text" class="form-control mb-1" name="description" value="${dish.description}" required />
                                <input type="text" class="form-control mb-1" name="price" value="${dish.price}" required pattern="^-?\\d*(\\.\\d+)?$" title="Enter a valid number" />
                                <input type="number" class="form-control mb-1" name="stockQuantity" value="${dish.stockQuantity}" required />
                                <button type="submit" class="btn btn-success btn-sm">Save</button>
                                <button type="button" class="btn btn-secondary btn-sm cancel-update">Cancel</button>
                            </form>
                        `;
                        const li = this.closest('li');
                        const originalHtml = li.innerHTML;
                        li.innerHTML = updateFormHtml;
                        li.querySelector('.cancel-update').addEventListener('click', function() {
                            li.innerHTML = originalHtml;
                        });
                        li.querySelector('.update-dish-form').addEventListener('submit', async function(e) {
                            e.preventDefault();
                            const formData = new FormData(this);
                            const updatedDish = {
                                dishId: dish.dishId,
                                sellerId: dish.sellerId,
                                dishName: formData.get('dishName'),
                                description: formData.get('description'),
                                price: parseFloat(formData.get('price')),
                                stockQuantity: parseInt(formData.get('stockQuantity'))
                            };
                            try {
                                const resp = await fetch('http://localhost:8087/dishes/api/dishes', {
                                    method: 'PUT',
                                    headers: { 'Content-Type': 'application/json' },
                                    body: JSON.stringify(updatedDish)
                                });
                                if (resp.ok) {
                                    li.innerHTML = `<strong>${updatedDish.dishName.trim()}</strong> - $${updatedDish.price} <br>Description: ${updatedDish.description}<br>Stock: ${updatedDish.stockQuantity}`;
                                } else {
                                    alert('Failed to update dish.');
                                }
                            } catch (err) {
                                alert('Error updating dish.');
                            }
                        });
                    });
                });
            }
        } else {
            dishesContainer.innerHTML = '<div class="alert alert-danger">Failed to fetch dishes.</div>';
        }
    } catch (error) {
        dishesContainer.innerHTML = '<div class="alert alert-danger">Error fetching dishes.</div>';
    }
});

document.addEventListener('DOMContentLoaded', function() {
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            window.location.href = 'login.html';
        });
    }
});
