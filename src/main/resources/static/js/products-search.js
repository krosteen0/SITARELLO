// Auto-submit del form quando si cambia ordinamento o altri filtri
document.getElementById('sortBy').addEventListener('change', function() {
    document.querySelector('.search-form').submit();
});

document.getElementById('ratingMin').addEventListener('change', function() {
    document.querySelector('.search-form').submit();
});
