
document.getElementById('createSellerForm').addEventListener('submit', async function(event) {
    event.preventDefault();

    const companyName = document.getElementById('companyName').value;
    const role = 'SELLER';

    try {
        const response = await fetch('http://localhost:8081/api/users/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username: companyName, password: '', role })
        });

        if (response.ok) {
            alert('Seller account created successfully!');
        } else {
            const errorData = await response.json();
            alert(`Error: ${errorData.message}`);
        }
    } catch (error) {
        alert('An error occurred while creating the seller account.');
    }
});

document.getElementById('fetchSellers').addEventListener('click', async function() {
    try {
        const response = await fetch('http://localhost:8081/api/users/sellers');
        if (response.ok) {
            const sellers = await response.json();
            const sellersList = document.getElementById('sellersList');
            sellersList.innerHTML = '';
            sellers.forEach(seller => {
                const li = document.createElement('li');
                li.textContent = seller.username;
                sellersList.appendChild(li);
            });
        } else {
            alert('Failed to fetch sellers.');
        }
    } catch (error) {
        alert('An error occurred while fetching sellers.');
    }
});

document.getElementById('fetchCustomers').addEventListener('click', async function() {
    try {
        const response = await fetch('http://localhost:8081/api/users/customers');
        if (response.ok) {
            const customers = await response.json();
            const customersList = document.getElementById('customersList');
            customersList.innerHTML = '';
            customers.forEach(customer => {
                const li = document.createElement('li');
                li.textContent = customer.username;
                customersList.appendChild(li);
            });
        } else {
            alert('Failed to fetch customers.');
        }
    } catch (error) {
        alert('An error occurred while fetching customers.');
    }
});

const createShippingCompanyForm = document.getElementById('createShippingCompanyForm');
const regionsContainer = document.getElementById('regionsContainer');
const addRegionBtn = document.getElementById('addRegionBtn');

if (addRegionBtn) {
    addRegionBtn.addEventListener('click', function() {
        const regionGroup = document.createElement('div');
        regionGroup.className = 'region-group mb-3';
        regionGroup.innerHTML = `
            <label class="form-label">Region Name:</label>
            <input type="text" name="regionName" class="form-control mb-1 region-name" placeholder="Enter region name" required>
            <label class="form-label">Region Fee:</label>
            <input type="number" name="regionFee" class="form-control region-fee" placeholder="Enter region fee" required>
            <button type="button" class="btn btn-danger btn-sm remove-region-btn mt-2">Remove</button>
        `;
        regionsContainer.appendChild(regionGroup);
        regionGroup.querySelector('.remove-region-btn').addEventListener('click', function() {
            regionGroup.remove();
        });
    });
}

if (createShippingCompanyForm) {
    createShippingCompanyForm.addEventListener('submit', async function(event) {
        event.preventDefault();
        const companyName = document.getElementById('shippingCompanyName').value.trim();
        if (!companyName) {
            alert('Please enter the company name.');
            return;
        }
        const regionGroups = regionsContainer.querySelectorAll('.region-group');
        const regions = [];
        for (const group of regionGroups) {
            const name = group.querySelector('.region-name').value.trim();
            const fee = group.querySelector('.region-fee').value.trim();
            if (!name || fee === '') {
                alert('Please enter both region name and fee for all regions.');
                return;
            }
            regions.push({ regionName: name, fee: parseFloat(fee) });
        }
        try {
            // 1. Create the company
            const companyResp = await fetch('http://localhost:8082/shipping/company', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name: companyName })
            });
            if (!companyResp.ok) {
                alert('Failed to create shipping company.');
                return;
            }
            const company = await companyResp.json();
            const companyId = company.id;
            // 2. Add regions
            for (const region of regions) {
                await fetch(`http://localhost:8082/shipping/company/${companyId}/region`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(region)
                });
            }
            alert('Shipping company and regions created successfully!');
            createShippingCompanyForm.reset();
            // Remove all but the first region group
            while (regionsContainer.children.length > 1) {
                regionsContainer.lastChild.remove();
            }
        } catch (err) {
            alert('Error creating shipping company or regions.');
        }
    });
}

document.getElementById('fetchShippingCompanies').addEventListener('click', async function() {
    try {
        const response = await fetch('http://localhost:8082/shipping/companies');
        if (response.ok) {
            const companies = await response.json();
            const companiesList = document.getElementById('shippingCompaniesList');
            companiesList.innerHTML = '';
            companies.forEach(company => {
                const li = document.createElement('li');
                li.textContent = `${company.name} (ID: ${company.id})`;
                companiesList.appendChild(li);
            });
        } else {
            alert('Failed to fetch shipping companies.');
        }
    } catch (error) {
        alert('An error occurred while fetching shipping companies.');
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
