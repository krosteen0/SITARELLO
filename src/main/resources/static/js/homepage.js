/* Homepage JavaScript - Sitarello */

document.addEventListener('DOMContentLoaded', function() {
    // Controllo messaggio di benvenuto
    checkWelcomeMessage();
    
    // Gestione errori immagini
    initImageErrorHandling();
    
    // Animazioni al caricamento eleganti
    initScrollAnimations();
    
    // Gestione ricerca con miglioramenti
    initSearchEnhancements();
    
    // Aggiunge un effetto di fade-in alla pagina
    document.body.style.opacity = '0';
    document.body.style.transition = 'opacity 0.6s ease';
    
    setTimeout(() => {
        document.body.style.opacity = '1';
    }, 100);
});

/**
 * Gestisce gli errori di caricamento delle immagini
 */
function initImageErrorHandling() {
    const images = document.querySelectorAll('img');
    
    images.forEach(img => {
        img.addEventListener('error', function() {
            // Sostituisce l'immagine con un placeholder
            this.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMzAwIiBoZWlnaHQ9IjIwMCIgdmlld0JveD0iMCAwIDMwMCAyMDAiIGZpbGw9Im5vbmUiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+CjxyZWN0IHdpZHRoPSIzMDAiIGhlaWdodD0iMjAwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik0xMzAgOTBIMTcwVjExMEgxMzBWOTBaIiBmaWxsPSIjOUNBM0FGIi8+CjxwYXRoIGQ9Ik0xNDAgMTAwSDEyMFYxMjBIMTQwVjEwMFoiIGZpbGw9IiM5Q0EzQUYiLz4KPHN2Zz4K';
            this.alt = 'Immagine non disponibile';
            this.style.backgroundColor = '#f3f4f6';
        });
        
        // Lazy loading per le immagini
        if ('loading' in HTMLImageElement.prototype) {
            img.loading = 'lazy';
        } else {
            // Fallback per browser che non supportano lazy loading nativo
            const imageObserver = new IntersectionObserver((entries, observer) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        const lazyImage = entry.target;
                        lazyImage.src = lazyImage.dataset.src;
                        observer.unobserve(lazyImage);
                    }
                });
            });
            
            if (img.dataset.src) {
                imageObserver.observe(img);
            }
        }
    });
}

/**
 * Inizializza le animazioni al scroll - versione elegante
 */
function initScrollAnimations() {
    // Controllo se l'API IntersectionObserver è supportata
    if ('IntersectionObserver' in window) {
        const animateElements = document.querySelectorAll('.product-card, .category-card, .welcome-card');
        
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.opacity = '1';
                    entry.target.style.transform = 'translateY(0)';
                    observer.unobserve(entry.target);
                }
            });
        }, {
            threshold: 0.15,
            rootMargin: '0px 0px -30px 0px'
        });
        
        // Imposta lo stato iniziale per l'animazione
        animateElements.forEach((el, index) => {
            el.style.opacity = '0';
            el.style.transform = 'translateY(30px)';
            el.style.transition = `opacity 0.8s cubic-bezier(0.4, 0, 0.2, 1) ${index * 0.1}s, transform 0.8s cubic-bezier(0.4, 0, 0.2, 1) ${index * 0.1}s`;
            observer.observe(el);
        });
    }
}

/**
 * Migliora la funzionalità di ricerca - versione elegante
 */
function initSearchEnhancements() {
    const searchInput = document.querySelector('.search-input');
    const searchForm = document.querySelector('.search-form');
    
    if (searchInput && searchForm) {
        // Gestione del placeholder semplice
        const placeholders = [
            'Cerca prodotti, categorie, brand...',
            'Prova "elettronica", "moda", "libri"...',
            'Scopri prodotti unici su Sitarello...'
        ];
        
        let currentPlaceholder = 0;
        
        // Cambia placeholder ogni 4 secondi se l'input non è focalizzato
        const placeholderInterval = setInterval(() => {
            if (document.activeElement !== searchInput && searchInput.value === '') {
                currentPlaceholder = (currentPlaceholder + 1) % placeholders.length;
                searchInput.placeholder = placeholders[currentPlaceholder];
            }
        }, 4000);
        
        // Gestione focus elegante
        searchInput.addEventListener('focus', function() {
            this.placeholder = 'Inserisci termini di ricerca...';
            this.style.transition = 'all 0.3s ease';
        });
        
        searchInput.addEventListener('blur', function() {
            if (this.value === '') {
                this.placeholder = placeholders[currentPlaceholder];
            }
        });
        
        // Validazione form elegante
        searchForm.addEventListener('submit', function(e) {
            if (searchInput.value.trim() === '') {
                e.preventDefault();
                searchInput.focus();
                searchInput.style.borderColor = 'rgba(239, 68, 68, 0.5)';
                searchInput.style.boxShadow = '0 0 0 3px rgba(239, 68, 68, 0.1)';
                
                setTimeout(() => {
                    searchInput.style.borderColor = 'rgba(255, 255, 255, 0.08)';
                    searchInput.style.boxShadow = 'none';
                }, 2000);
            }
        });
        
        // Cleanup interval quando la pagina viene scaricata
        window.addEventListener('beforeunload', () => {
            clearInterval(placeholderInterval);
        });
    }
}

/**
 * Gestione della chiusura del messaggio di benvenuto - versione elegante
 */
function closeWelcomeMessage() {
    const welcomeSection = document.querySelector('.welcome-section');
    if (welcomeSection) {
        welcomeSection.style.transition = 'all 0.4s cubic-bezier(0.4, 0, 0.2, 1)';
        welcomeSection.style.opacity = '0';
        welcomeSection.style.transform = 'translateY(-30px) scale(0.95)';
        
        setTimeout(() => {
            welcomeSection.style.display = 'none';
            // Salva lo stato di chiusura nel localStorage
            localStorage.setItem('welcomeMessageClosed', 'true');
        }, 400);
    }
}

/**
 * Controllo se mostrare il messaggio di benvenuto
 */
function checkWelcomeMessage() {
    const welcomeSection = document.querySelector('.welcome-section');
    const wasClosed = localStorage.getItem('welcomeMessageClosed');
    
    if (welcomeSection && wasClosed === 'true') {
        welcomeSection.style.display = 'none';
    }
}

/**
 * Funzione per il feedback di caricamento elegante
 */
function showLoading(element) {
    if (element) {
        element.style.transition = 'all 0.3s ease';
        element.style.opacity = '0.6';
        element.style.pointerEvents = 'none';
        element.style.filter = 'blur(1px)';
        
        // Aggiunge un indicatore di caricamento elegante
        const loader = document.createElement('div');
        loader.className = 'loading-indicator';
        loader.innerHTML = '<div class="elegant-spinner"></div>';
        loader.style.cssText = `
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            z-index: 1000;
            background: rgba(255, 255, 255, 0.05);
            backdrop-filter: blur(10px);
            border-radius: 12px;
            padding: 20px;
            border: 1px solid rgba(255, 255, 255, 0.1);
        `;
        
        // Aggiungi stili per lo spinner
        const style = document.createElement('style');
        style.textContent = `
            .elegant-spinner {
                width: 20px;
                height: 20px;
                border: 2px solid rgba(99, 102, 241, 0.3);
                border-top: 2px solid #6366f1;
                border-radius: 50%;
                animation: spin 1s linear infinite;
            }
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
        `;
        document.head.appendChild(style);
        
        element.style.position = 'relative';
        element.appendChild(loader);
    }
}

/**
 * Rimuove l'indicatore di caricamento
 */
function hideLoading(element) {
    if (element) {
        element.style.opacity = '1';
        element.style.pointerEvents = 'auto';
        element.style.filter = 'none';
        
        const loader = element.querySelector('.loading-indicator');
        if (loader) {
            loader.remove();
        }
    }
}

// Esporta le funzioni per uso globale se necessario
window.SitarelloHomepage = {
    closeWelcomeMessage,
    checkWelcomeMessage,
    showLoading,
    hideLoading
};

// Rendi disponibile la funzione anche globalmente per facilità d'uso
window.closeWelcomeMessage = closeWelcomeMessage;
