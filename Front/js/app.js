// Example: Fetch dishes from Seller service
fetch('http://localhost:8083/api/seller/dishes')
  .then(res => res.json())
  .then(data => {
    console.log('Seller dishes:', data);
    // handle data
  })
  .catch(err => console.error('Error fetching seller dishes:', err));

// Example: Create seller via Identify_Admin_Services
fetch('http://localhost:8081/admin/sellers', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ /* your seller data here */ })
})
  .then(res => res.json())
  .then(data => {
    console.log('Admin create seller response:', data);
    // handle data
  })
  .catch(err => console.error('Error creating seller:', err));

// Example: Place order via Order_shipping_Service
fetch('http://localhost:8082/orders', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ /* your order data here */ })
})
  .then(res => res.json())
  .then(data => {
    console.log('Order shipping response:', data);
    // handle data
  })
  .catch(err => console.error('Error placing order:', err));
