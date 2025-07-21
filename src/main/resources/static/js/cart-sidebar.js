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
    console.log('Caricamento items del carrello...');
    const list = document.getElementById('cart-items-list');
    
    // Mostra stato di loading
    if (list) {
        list.innerHTML = '<div class="cart-loading"><i class="fas fa-spinner fa-spin"></i> Caricamento...</div>';
    }
    
    fetch('/cart/items')
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Dati carrello ricevuti:', data);
            renderCartItems({items: data});
        })
        .catch(error => {
            console.error('Errore nel caricamento del carrello:', error);
            // Mostra messaggio di errore user-friendly
            if (list) {
                list.innerHTML = '<div class="empty-cart"><i class="fas fa-exclamation-triangle"></i>Errore nel caricamento del carrello.</div>';
            }
            // Reset contatori in caso di errore
            const cartCount = document.getElementById('cart-count');
            const cartTotal = document.getElementById('cart-total');
            if (cartCount) {
                cartCount.style.display = 'none';
                cartCount.textContent = '0';
            }
            if (cartTotal) {
                cartTotal.textContent = '0,00 €';
            }
        });
}

function renderCartItems(cartOrArray) {
    const list = document.getElementById('cart-items-list');
    list.innerHTML = '';
    // Support both {items: [...]} and plain array
    const items = Array.isArray(cartOrArray) ? cartOrArray : cartOrArray.items;
    const cartCount = document.getElementById('cart-count');
    const cartTotal = document.getElementById('cart-total'); // Corretto da cart-sidebar-total a cart-total
    
    if (!items || items.length === 0) {
        list.innerHTML = '<div class="empty-cart"><i class="fas fa-shopping-cart"></i>Il carrello è vuoto.</div>';
        if (cartCount) {
            cartCount.style.display = 'none';
            cartCount.textContent = '0';
        }
        if (cartTotal) cartTotal.textContent = '0,00 €';
        return;
    }
    
    let total = 0;
    let totalItems = 0;
    
    items.forEach(item => {
        // item structure from backend: id, productId, name, price, quantity, imageId
        const itemTotal = item.price * item.quantity;
        total += itemTotal;
        totalItems += item.quantity;
        
        const div = document.createElement('div');
        div.className = 'cart-item';
        div.innerHTML = `
            <div class="cart-item-info">
                <span class="cart-item-name">${escapeHtml(item.name)}</span>
                <span class="cart-item-qty">Quantità: ${item.quantity}</span>
            </div>
            <span class="cart-item-price">€${itemTotal.toFixed(2)}</span>
            <button class="remove-cart-item" onclick="removeFromCart(${item.productId})" title="Rimuovi dal carrello">
                <i class="fas fa-times"></i>
            </button>
        `;
        list.appendChild(div);
    });
    
    // Aggiorna il contatore del carrello
    if (cartCount) {
        cartCount.textContent = totalItems;
        cartCount.style.display = totalItems > 0 ? 'inline-block' : 'none';
    }
    
    // Aggiorna il totale
    if (cartTotal) {
        cartTotal.textContent = total.toFixed(2) + ' €';
    }
    
    console.log(`Carrello aggiornato: ${totalItems} articoli, totale: €${total.toFixed(2)}`);
}

// Funzione helper per escape HTML e prevenire XSS
function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, function(m) { return map[m]; });
}


function getCsrfHeaders() {
    const tokenMeta = document.querySelector('meta[name="_csrf"]');
    const headerMeta = document.querySelector('meta[name="_csrf_header"]');
    
    if (!tokenMeta || !headerMeta) {
        console.error('Meta tag CSRF non trovati nella pagina');
        return {};
    }
    
    const token = tokenMeta.getAttribute('content');
    const header = headerMeta.getAttribute('content');
    
    if (!token || !header) {
        console.error('Token o header CSRF vuoti');
        return {};
    }
    
    console.log('CSRF Headers:', { [header]: token });
    return { [header]: token };
}

function addToCart(productId) {
    console.log('Aggiunta prodotto al carrello:', productId);
    
    // Ottieni gli header CSRF
    const csrfHeaders = getCsrfHeaders();
    if (Object.keys(csrfHeaders).length === 0) {
        alert('Errore: token di sicurezza non trovato. Ricarica la pagina e riprova.');
        return;
    }
    
    fetch('/cart/add?productId=' + productId + '&quantity=1', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            ...csrfHeaders
        }
    })
    .then(response => {
        if (!response.ok) throw new Error(`Errore HTTP: ${response.status}`);
        console.log('Prodotto aggiunto al carrello con successo');
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
        console.error('Errore nell\'aggiunta al carrello:', err);
        alert(err.message || 'Errore nell\'aggiunta al carrello');
    });
}
window.addToCart = addToCart;

function removeFromCart(productId) {
    console.log('Rimozione prodotto dal carrello:', productId);
    
    // Ottieni gli header CSRF
    const csrfHeaders = getCsrfHeaders();
    if (Object.keys(csrfHeaders).length === 0) {
        alert('Errore: token di sicurezza non trovato. Ricarica la pagina e riprova.');
        return;
    }
    
    // Aggiungi feedback visivo immediato
    const cartItems = document.querySelectorAll('.cart-item');
    cartItems.forEach(item => {
        const button = item.querySelector(`button[onclick*="${productId}"]`);
        if (button) {
            item.style.opacity = '0.5';
            button.disabled = true;
        }
    });
    
    fetch('/cart/remove?productId=' + productId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            ...csrfHeaders
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`Errore HTTP: ${response.status}`);
        }
        console.log('Prodotto rimosso con successo');
        loadCartItems(); // Ricarica la lista del carrello
    })
    .catch(error => {
        console.error('Errore:', error);
        // Ripristina lo stato visivo in caso di errore
        const cartItems = document.querySelectorAll('.cart-item');
        cartItems.forEach(item => {
            const button = item.querySelector(`button[onclick*="${productId}"]`);
            if (button) {
                item.style.opacity = '1';
                button.disabled = false;
            }
        });
        alert('Errore nella rimozione del prodotto dal carrello: ' + error.message);
    });
}
window.removeFromCart = removeFromCart;

document.addEventListener('DOMContentLoaded', function() {
    // Debug: Verifica che i meta tag CSRF siano presenti
    const tokenMeta = document.querySelector('meta[name="_csrf"]');
    const headerMeta = document.querySelector('meta[name="_csrf_header"]');
    
    if (tokenMeta && headerMeta) {
        console.log('Meta tag CSRF trovati correttamente');
        console.log('Token:', tokenMeta.getAttribute('content'));
        console.log('Header:', headerMeta.getAttribute('content'));
    } else {
        console.error('Meta tag CSRF mancanti!', { tokenMeta, headerMeta });
    }
    
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
