function validateImages(input) {
    const files = input.files;
    const imageCount = document.getElementById('imageCount');
    const errorMessage = document.getElementById('errorMessage');
    const submitButton = document.getElementById('submitButton');
    const preview = document.getElementById('newImagePreview');
    
    // Clear previous preview
    preview.innerHTML = '';
    errorMessage.textContent = '';
    
    imageCount.textContent = 'Immagini selezionate: ' + files.length;
    
    if (files.length < 2 || files.length > 10) {
        errorMessage.textContent = 'Seleziona da 2 a 10 immagini.';
        submitButton.disabled = true;
        return;
    }
    
    // Validate file types
    for (let i = 0; i < files.length; i++) {
        if (!files[i].type.startsWith('image/')) {
            errorMessage.textContent = 'Tutti i file devono essere immagini.';
            submitButton.disabled = true;
            return;
        }
    }
    
    // Show preview
    for (let i = 0; i < files.length; i++) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            img.style.maxWidth = '200px';
            img.style.maxHeight = '200px';
            img.style.margin = '5px';
            preview.appendChild(img);
        }
        reader.readAsDataURL(files[i]);
    }
    
    submitButton.disabled = false;
}
function validateEditImages() {
    const mainImageInput = document.getElementById('mainImage');
    const extraImagesInput = document.getElementById('extraImages');
    const imageCount = document.getElementById('imageCount');
    const errorMessage = document.getElementById('errorMessage');
    const submitButton = document.getElementById('submitButton');
    const preview = document.getElementById('newImagePreview');

    // Clear previous preview
    preview.innerHTML = '';
    errorMessage.textContent = '';

    let total = 0;
    if (mainImageInput && mainImageInput.files.length > 0) total += 1;
    if (extraImagesInput && extraImagesInput.files.length > 0) total += extraImagesInput.files.length;
    imageCount.textContent = 'Immagini selezionate: ' + total;

    // Validazione
    if (!mainImageInput || mainImageInput.files.length === 0) {
        errorMessage.textContent = 'Carica un\'immagine principale.';
        submitButton.disabled = true;
        return;
    }
    if (extraImagesInput && extraImagesInput.files.length > 4) {
        errorMessage.textContent = 'Puoi caricare al massimo 4 immagini extra.';
        submitButton.disabled = true;
        return;
    }
    // Validate file types
    if (mainImageInput.files.length > 0 && !mainImageInput.files[0].type.startsWith('image/')) {
        errorMessage.textContent = 'L\'immagine principale deve essere un file immagine.';
        submitButton.disabled = true;
        return;
    }
    if (extraImagesInput && extraImagesInput.files.length > 0) {
        for (let i = 0; i < extraImagesInput.files.length; i++) {
            if (!extraImagesInput.files[i].type.startsWith('image/')) {
                errorMessage.textContent = 'Tutti i file extra devono essere immagini.';
                submitButton.disabled = true;
                return;
            }
        }
    }
    // Show preview
    if (mainImageInput.files.length > 0) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            img.style.maxWidth = '200px';
            img.style.maxHeight = '200px';
            img.style.margin = '5px';
            preview.appendChild(img);
        }
        reader.readAsDataURL(mainImageInput.files[0]);
    }
    if (extraImagesInput && extraImagesInput.files.length > 0) {
        for (let i = 0; i < extraImagesInput.files.length; i++) {
            const reader = new FileReader();
            reader.onload = function(e) {
                const img = document.createElement('img');
                img.src = e.target.result;
                img.style.maxWidth = '200px';
                img.style.maxHeight = '200px';
                img.style.margin = '5px';
                preview.appendChild(img);
            }
            reader.readAsDataURL(extraImagesInput.files[i]);
        }
    }
    submitButton.disabled = false;
}
