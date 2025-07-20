// Logica JS per sidebar carrello
function toggleCartSidebar() {
    const sidebar = document.getElementById('cart-sidebar');
    const overlay = document.getElementById('cart-sidebar-overlay');
    if (!sidebar) {
        console.error('Sidebar non trovata!');
        return;
    }
    if (sidebar.classList.contains('open')) {
        sidebar.classList.remove('open');
        if (overlay) overlay.style.display = 'none';
        console.log('Sidebar chiusa');
    } else {
        loadCartItems();
        sidebar.classList.add('open');
        if (overlay) overlay.style.display = 'block';
        console.log('Sidebar aperta, classi:', sidebar.className, 'display:', getComputedStyle(sidebar).display, 'right:', getComputedStyle(sidebar).right);
    }
}

function loadCartItems() {
    fetch('/cart/items')
        .then(response => response.json())
        .then(data => renderCartItems({items: data}))
        .catch(() => renderCartItems({items: []}));
}

function renderCartItems(cartOrArray) {
    const list = document.getElementById('cart-items-list');
    list.innerHTML = '';
    // Support both {items: [...]} and plain array
    const items = Array.isArray(cartOrArray) ? cartOrArray : cartOrArray.items;
    const cartCount = document.getElementById('cart-count');
    const cartTotal = document.getElementById('cart-sidebar-total');
    if (!items || items.length === 0) {
        list.innerHTML = '<div class="empty-cart">Il carrello è vuoto.</div>';
        if (cartCount) cartCount.style.display = 'none';
        if (cartTotal) cartTotal.textContent = 'Totale: €0.00';
        return;
    }
    let total = 0;
    items.forEach(item => {
        // item structure from backend: id, productId, name, price, quantity, imageId
        total += item.price * item.quantity;
        const div = document.createElement('div');
        div.className = 'cart-item';
        div.innerHTML = `
            <div class="cart-item-info">
                <span class="cart-item-name">${item.name}</span>
                <span class="cart-item-qty">x${item.quantity}</span>
                <span class="cart-item-price">€${item.price.toFixed(2)}</span>
            </div>
            <button class="remove-cart-item" onclick="removeFromCart(${item.productId})">&times;</button>
        `;
        list.appendChild(div);
    });
    if (cartCount) {
        cartCount.textContent = items.length;
        cartCount.style.display = 'inline-block';
    }
    if (cartTotal) cartTotal.textContent = 'Totale: €' + total.toFixed(2);
}


function getCsrfHeaders() {
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    return { [header]: token };
}

function addToCart(productId) {
    fetch('/cart/add?productId=' + productId + '&quantity=1', {
        method: 'POST',
        headers: getCsrfHeaders()
    })
    .then(response => {
        if (!response.ok) throw new Error('Errore nell\'aggiunta al carrello');
        loadCartItems();
        document.getElementById('cart-count').style.display = 'inline-block';
        // Apri la sidebar del carrello
        setTimeout(() => {
            if (typeof toggleCartSidebar === 'function') {
                const sidebar = document.getElementById('cart-sidebar');
                if (!sidebar.classList.contains('open')) toggleCartSidebar();
            }
        }, 100);
    })
    .catch(err => {
        alert(err.message || 'Errore nell\'aggiunta al carrello');
    });
}
window.addToCart = addToCart;

function removeFromCart(productId) {
    fetch('/cart/remove?productId=' + productId, {
        method: 'POST',
        headers: getCsrfHeaders()
    })
    .then(() => loadCartItems());
}
window.removeFromCart = removeFromCart;

document.addEventListener('DOMContentLoaded', function() {
    const cartBtn = document.getElementById('cart-btn');
    const overlay = document.getElementById('cart-sidebar-overlay');
    if (cartBtn) {
        cartBtn.addEventListener('click', function() {
            console.log('Cart button clicked');
            toggleCartSidebar();
        });
    } else {
        console.warn('cart-btn non trovato nel DOM');
    }
    if (overlay) {
        overlay.addEventListener('click', function() {
            console.log('Overlay clicked');
            toggleCartSidebar();
        });
    } else {
        console.warn('cart-sidebar-overlay non trovato nel DOM');
    }
    // Chiudi la sidebar se clicchi fuori dalla sidebar e dall'overlay
    document.addEventListener('mousedown', function(event) {
        const sidebar = document.getElementById('cart-sidebar');
        const overlay = document.getElementById('cart-sidebar-overlay');
        if (sidebar && sidebar.classList.contains('open')) {
            if (
                !sidebar.contains(event.target) &&
                (!overlay || !overlay.contains(event.target))
            ) {
                sidebar.classList.remove('open');
                if (overlay) overlay.style.display = 'none';
                console.log('Sidebar chiusa cliccando fuori');
            }
        }
    });
    loadCartItems();
});
