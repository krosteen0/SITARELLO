// Auto-submit del form quando si cambia ordinamento o altri filtri
document.getElementById('sortBy').addEventListener('change', function() {
    document.querySelector('.search-form').submit();
});

document.getElementById('ratingMin').addEventListener('change', function() {
    document.querySelector('.search-form').submit();
});

// Funzione per toggle dei filtri avanzati
function toggleAdvancedFilters() {
    const content = document.getElementById('advancedFiltersContent');
    const toggle = document.getElementById('advancedToggle');
    
    if (content.classList.contains('collapsed')) {
        content.classList.remove('collapsed');
        toggle.classList.remove('rotated');
        toggle.textContent = '▼';
    } else {
        content.classList.add('collapsed');
        toggle.classList.add('rotated');
        toggle.textContent = '▲';
    }
}

// Inizializzazione della pagina
document.addEventListener('DOMContentLoaded', function() {
    // Controlla se ci sono filtri avanzati attivi per decidere se mostrare la sezione
    const hasAdvancedFilters = document.getElementById('prezzoMin').value || 
                               document.getElementById('prezzoMax').value || 
                               document.getElementById('ratingMin').value;
    
    if (!hasAdvancedFilters) {
        // Se non ci sono filtri avanzati, inizia con la sezione chiusa
        toggleAdvancedFilters();
    }
    
    // Aggiungere animazioni fade-in alle card dei prodotti
    const productCards = document.querySelectorAll('.product-card');
    productCards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(30px)';
        
        setTimeout(() => {
            card.style.transition = 'opacity 0.6s ease, transform 0.6s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
    });
});
