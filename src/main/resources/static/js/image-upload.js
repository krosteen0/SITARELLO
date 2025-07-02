function validateImages(input) {
    const files = input.files;
    const count = files.length;
    const errorMessage = document.getElementById('errorMessage');
    const imageCount = document.getElementById('imageCount');
    const forwardButton = document.getElementById('forwardButton');
    const imagePreview = document.getElementById('imagePreview');

    imageCount.textContent = `Immagini selezionate: ${count}`;
    imagePreview.innerHTML = '';

    if (count < 2) {
        errorMessage.textContent = 'Seleziona almeno 2 immagini.';
        forwardButton.disabled = true;
    } else if (count > 10) {
        errorMessage.textContent = 'Puoi selezionare un massimo di 10 immagini.';
        forwardButton.disabled = true;
    } else {
        errorMessage.textContent = '';
        forwardButton.disabled = false;
    }

    for (let i = 0; i < count; i++) {
        const img = document.createElement('img');
        img.src = URL.createObjectURL(files[i]);
        imagePreview.appendChild(img);
    }
}
