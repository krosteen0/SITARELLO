// Product Creation JavaScript - Sitarello
console.log('Product create JS file loaded'); // Debug

class ProductCreator {
    constructor() {
        this.currentStep = 1;
        this.totalSteps = 3;
        this.images = [];
        this.maxImages = 5;
        this.maxFileSize = 5 * 1024 * 1024; // 5MB
        this.allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'];
        
        this.init();
    }
    
    init() {
        console.log('ProductCreator initializing...'); // Debug
        
        // Wait for DOM to be fully ready
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => {
                this.setupAll();
            });
        } else {
            this.setupAll();
        }
    }
    
    setupAll() {
        console.log('Setting up all components...'); // Debug
        this.setupEventListeners();
        this.updateProgressBar();
        this.showStep(this.currentStep);
        this.setupImageUpload();
        this.setupFormValidation();
        this.setupPreview();
        console.log('ProductCreator initialized successfully'); // Debug
    }
    
    setupEventListeners() {
        // Navigation buttons
        document.getElementById('nextBtn')?.addEventListener('click', () => this.nextStep());
        document.getElementById('prevBtn')?.addEventListener('click', () => this.prevStep());
        document.getElementById('submitBtn')?.addEventListener('click', (e) => this.submitForm(e));
        
        // Form inputs for live preview
        const inputs = ['productName', 'productPrice', 'productDescription', 'productCategory'];
        inputs.forEach(inputId => {
            const element = document.getElementById(inputId);
            if (element) {
                element.addEventListener('input', () => this.updatePreview());
                element.addEventListener('blur', () => this.validateField(inputId));
            }
        });
        
        // Character counters
        this.setupCharacterCounters();
    }
    
    setupImageUpload() {
        const uploadArea = document.querySelector('.image-upload-area') || document.getElementById('imageUploadArea');
        const fileInput = document.getElementById('imageInput');
        
        console.log('Setting up image upload...'); // Debug
        console.log('Upload area:', uploadArea);
        console.log('File input:', fileInput);
        
        if (!uploadArea || !fileInput) {
            console.error('Upload elements not found!');
            return;
        }

        // Ensure the file input is properly configured
        fileInput.setAttribute('accept', 'image/jpeg,image/jpg,image/png,image/webp');
        fileInput.setAttribute('multiple', 'true');
        
        // Click to upload - with better event handling
        uploadArea.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            console.log('Upload area clicked via addEventListener'); // Debug
            
            // Force focus and click on file input
            fileInput.focus();
            fileInput.click();
            
            console.log('File input click triggered'); // Debug
        });
        
        // Alternative event listener for debugging
        uploadArea.addEventListener('mousedown', (e) => {
            console.log('Upload area mousedown detected'); // Debug
        });
        
        uploadArea.addEventListener('mouseup', (e) => {
            console.log('Upload area mouseup detected'); // Debug
        });
        
        // Also handle clicks on child elements
        uploadArea.addEventListener('mousedown', (e) => {
            e.preventDefault();
        });
        
        // File input change
        fileInput.addEventListener('change', (e) => {
            console.log('Files selected via input:', e.target.files); // Debug
            this.handleFileSelect(e.target.files);
            // Reset the input to allow selecting the same file again
            e.target.value = '';
        });
        
        // Test the file input directly
        console.log('File input ready, testing programmatic click...');
        setTimeout(() => {
            console.log('Attempting programmatic click on file input...');
            // This should work even if the upload area click doesn't
            // fileInput.click(); // Uncomment to test
        }, 1000);
        
        // Drag and drop
        uploadArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            e.stopPropagation();
            uploadArea.classList.add('dragover');
            console.log('Drag over'); // Debug
        });
        
        uploadArea.addEventListener('dragenter', (e) => {
            e.preventDefault();
            e.stopPropagation();
            uploadArea.classList.add('dragover');
            console.log('Drag enter'); // Debug
        });
        
        uploadArea.addEventListener('dragleave', (e) => {
            e.preventDefault();
            e.stopPropagation();
            // Only remove dragover if leaving the main area, not child elements
            if (!uploadArea.contains(e.relatedTarget)) {
                uploadArea.classList.remove('dragover');
                console.log('Drag leave'); // Debug
            }
        });
        
        uploadArea.addEventListener('drop', (e) => {
            e.preventDefault();
            e.stopPropagation();
            uploadArea.classList.remove('dragover');
            console.log('Files dropped:', e.dataTransfer.files); // Debug
            this.handleFileSelect(e.dataTransfer.files);
        });
        
        // Prevent default drag behaviors on document
        document.addEventListener('dragover', (e) => e.preventDefault());
        document.addEventListener('drop', (e) => e.preventDefault());
        
        console.log('Image upload setup completed'); // Debug
    }
    
    handleFileSelect(files) {
        console.log('handleFileSelect called with files:', files); // Debug
        
        if (!files || files.length === 0) {
            console.log('No files provided'); // Debug
            return;
        }
        
        Array.from(files).forEach((file, index) => {
            console.log(`Processing file ${index + 1}:`, file.name, file.type, file.size); // Debug
            
            if (this.images.length >= this.maxImages) {
                this.showNotification('Puoi caricare massimo ' + this.maxImages + ' immagini', 'warning');
                return;
            }
            
            if (!this.allowedTypes.includes(file.type)) {
                this.showNotification('Formato file non supportato: ' + file.name, 'error');
                console.log('Invalid file type:', file.type); // Debug
                return;
            }
            
            if (file.size > this.maxFileSize) {
                this.showNotification('File troppo grande: ' + file.name + ' (max 5MB)', 'error');
                console.log('File too large:', file.size); // Debug
                return;
            }
            
            console.log('Adding image:', file.name); // Debug
            this.addImage(file);
        });
    }
    
    addImage(file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            const imageData = {
                file: file,
                dataUrl: e.target.result,
                id: Date.now() + Math.random()
            };
            
            this.images.push(imageData);
            this.renderImagePreviews();
            this.updatePreview();
            this.validateStep1();
        };
        reader.readAsDataURL(file);
    }
    
    removeImage(imageId) {
        this.images = this.images.filter(img => img.id !== imageId);
        this.renderImagePreviews();
        this.updatePreview();
        this.validateStep1();
    }
    
    renderImagePreviews() {
        const container = document.getElementById('imagePreviewContainer') || document.getElementById('imagePreviewGrid');
        if (!container) return;
        
        container.innerHTML = '';
        
        this.images.forEach(image => {
            const item = document.createElement('div');
            item.className = 'image-preview-item';
            item.innerHTML = `
                <img src="${image.dataUrl}" alt="Preview">
                <button type="button" class="image-remove-btn" onclick="productCreator.removeImage(${image.id})">
                    ×
                </button>
            `;
            container.appendChild(item);
        });
        
        // Update upload area visibility
        const uploadArea = document.querySelector('.image-upload-area') || document.getElementById('imageUploadArea');
        if (uploadArea) {
            uploadArea.style.display = this.images.length >= this.maxImages ? 'none' : 'block';
        }
        
        // Update image counter
        const imageCounter = document.getElementById('imageCount');
        if (imageCounter) {
            imageCounter.textContent = this.images.length;
        }
    }
    
    setupCharacterCounters() {
        const fields = [
            { id: 'productName', max: 100 },
            { id: 'productDescription', max: 500 }
        ];
        
        fields.forEach(field => {
            const element = document.getElementById(field.id);
            const counter = document.getElementById(field.id + 'Counter');
            
            if (element && counter) {
                element.addEventListener('input', () => {
                    const length = element.value.length;
                    counter.textContent = `${length}/${field.max}`;
                    
                    counter.className = 'character-counter';
                    if (length > field.max * 0.9) {
                        counter.classList.add('warning');
                    }
                    if (length > field.max) {
                        counter.classList.add('error');
                    }
                });
            }
        });
    }
    
    setupFormValidation() {
        // Real-time validation
        this.validationRules = {
            productName: {
                required: true,
                minLength: 3,
                maxLength: 100
            },
            productPrice: {
                required: true,
                min: 0.01
            },
            productDescription: {
                required: true,
                minLength: 10,
                maxLength: 500
            },
            productCategory: {
                required: true
            }
        };
    }
    
    validateField(fieldId) {
        const field = document.getElementById(fieldId);
        const rules = this.validationRules[fieldId];
        
        if (!field || !rules) return true;
        
        const value = field.value.trim();
        let isValid = true;
        let errorMessage = '';
        
        // Required validation
        if (rules.required && !value) {
            isValid = false;
            errorMessage = 'Questo campo è obbligatorio';
        }
        
        // Length validation
        if (value && rules.minLength && value.length < rules.minLength) {
            isValid = false;
            errorMessage = `Minimo ${rules.minLength} caratteri`;
        }
        
        if (value && rules.maxLength && value.length > rules.maxLength) {
            isValid = false;
            errorMessage = `Massimo ${rules.maxLength} caratteri`;
        }
        
        // Number validation
        if (value && rules.min && parseFloat(value) < rules.min) {
            isValid = false;
            errorMessage = `Valore minimo: ${rules.min}`;
        }
        
        this.showFieldError(fieldId, isValid, errorMessage);
        return isValid;
    }
    
    showFieldError(fieldId, isValid, message) {
        const field = document.getElementById(fieldId);
        let errorElement = document.getElementById(fieldId + 'Error');
        
        if (!field) return;
        
        if (isValid) {
            field.classList.remove('form-error');
            if (errorElement) {
                errorElement.remove();
            }
        } else {
            field.classList.add('form-error');
            
            if (!errorElement) {
                errorElement = document.createElement('div');
                errorElement.id = fieldId + 'Error';
                errorElement.className = 'error-message';
                field.parentNode.appendChild(errorElement);
            }
            
            errorElement.innerHTML = `<span>⚠</span> ${message}`;
        }
    }
    
    validateStep1() {
        const isValid = this.images.length > 0;
        this.updateStepValidation(1, isValid);
        return isValid;
    }
    
    validateStep2() {
        const fields = ['productName', 'productPrice', 'productDescription', 'productCategory'];
        let allValid = true;
        
        fields.forEach(fieldId => {
            if (!this.validateField(fieldId)) {
                allValid = false;
            }
        });
        
        this.updateStepValidation(2, allValid);
        return allValid;
    }
    
    updateStepValidation(step, isValid) {
        const nextBtn = document.getElementById('nextBtn');
        const submitBtn = document.getElementById('submitBtn');
        
        if (step === 1 && this.currentStep === 1) {
            if (nextBtn) nextBtn.disabled = !isValid;
        }
        
        if (step === 2 && this.currentStep === 2) {
            if (nextBtn) nextBtn.disabled = !isValid;
        }
        
        if (step === 2 && this.currentStep === 3) {
            if (submitBtn) submitBtn.disabled = !isValid;
        }
    }
    
    setupPreview() {
        this.updatePreview();
    }
    
    updatePreview() {
        const previewElements = {
            image: document.getElementById('previewImage'),
            imageContainer: document.getElementById('previewImageContainer'),
            placeholder: document.getElementById('previewPlaceholder'),
            title: document.getElementById('previewTitle'),
            price: document.getElementById('previewPrice'),
            description: document.getElementById('previewDescription'),
            category: document.getElementById('previewCategory')
        };
        
        // Update image
        if (this.images.length > 0 && previewElements.image) {
            previewElements.image.src = this.images[0].dataUrl;
            previewElements.image.style.display = 'block';
            if (previewElements.placeholder) previewElements.placeholder.style.display = 'none';
        } else {
            if (previewElements.image) previewElements.image.style.display = 'none';
            if (previewElements.placeholder) previewElements.placeholder.style.display = 'block';
        }
        
        // Update text content
        const name = document.getElementById('productName')?.value || 'Nome prodotto';
        const price = document.getElementById('productPrice')?.value || '0';
        const description = document.getElementById('productDescription')?.value || 'Descrizione prodotto';
        const categorySelect = document.getElementById('productCategory');
        const category = categorySelect?.options[categorySelect.selectedIndex]?.text || 'Categoria';
        
        if (previewElements.title) previewElements.title.textContent = name;
        if (previewElements.price) previewElements.price.textContent = `€${parseFloat(price || 0).toFixed(2)}`;
        if (previewElements.description) previewElements.description.textContent = description;
        if (previewElements.category) previewElements.category.textContent = category;
    }
    
    nextStep() {
        if (this.currentStep === 1 && !this.validateStep1()) {
            this.showNotification('Carica almeno un\'immagine per continuare', 'warning');
            return;
        }
        
        if (this.currentStep === 2 && !this.validateStep2()) {
            this.showNotification('Completa tutti i campi obbligatori', 'warning');
            return;
        }
        
        if (this.currentStep < this.totalSteps) {
            this.currentStep++;
            this.showStep(this.currentStep);
            this.updateProgressBar();
            
            if (this.currentStep === 3) {
                this.updatePreview();
            }
        }
    }
    
    prevStep() {
        if (this.currentStep > 1) {
            this.currentStep--;
            this.showStep(this.currentStep);
            this.updateProgressBar();
        }
    }
    
    showStep(step) {
        // Hide all steps
        for (let i = 1; i <= this.totalSteps; i++) {
            const stepElement = document.getElementById(`step${i}`);
            if (stepElement) {
                stepElement.classList.remove('active');
            }
        }
        
        // Show current step
        const currentStepElement = document.getElementById(`step${step}`);
        if (currentStepElement) {
            currentStepElement.classList.add('active');
        }
        
        // Update navigation buttons
        this.updateNavigationButtons();
    }
    
    updateProgressBar() {
        const progressLine = document.querySelector('.progress-line');
        const steps = document.querySelectorAll('.step');
        
        if (progressLine) {
            const progress = ((this.currentStep - 1) / (this.totalSteps - 1)) * 100;
            progressLine.style.width = `${progress}%`;
        }
        
        steps.forEach((step, index) => {
            const stepNumber = index + 1;
            step.classList.remove('active', 'completed');
            
            if (stepNumber < this.currentStep) {
                step.classList.add('completed');
            } else if (stepNumber === this.currentStep) {
                step.classList.add('active');
            }
        });
    }
    
    updateNavigationButtons() {
        const prevBtn = document.getElementById('prevBtn');
        const nextBtn = document.getElementById('nextBtn');
        const submitBtn = document.getElementById('submitBtn');
        
        if (prevBtn) {
            prevBtn.style.display = this.currentStep === 1 ? 'none' : 'inline-flex';
        }
        
        if (nextBtn) {
            nextBtn.style.display = this.currentStep === this.totalSteps ? 'none' : 'inline-flex';
        }
        
        if (submitBtn) {
            submitBtn.style.display = this.currentStep === this.totalSteps ? 'inline-flex' : 'none';
        }
        
        // Update validation
        if (this.currentStep === 1) {
            this.validateStep1();
        } else if (this.currentStep === 2) {
            this.validateStep2();
        }
    }
    
    async submitForm(e) {
        e.preventDefault();
        
        if (!this.validateStep2()) {
            this.showNotification('Correggi gli errori nel modulo', 'error');
            return;
        }
        
        if (this.images.length === 0) {
            this.showNotification('Carica almeno un\'immagine', 'error');
            return;
        }
        
        const submitBtn = document.getElementById('submitBtn');
        if (submitBtn) {
            submitBtn.classList.add('loading');
            submitBtn.disabled = true;
        }
        
        try {
            await this.prepareAndSubmitForm();
        } catch (error) {
            console.error('Error submitting form:', error);
            this.showNotification('Errore durante la creazione del prodotto', 'error');
        } finally {
            if (submitBtn) {
                submitBtn.classList.remove('loading');
                submitBtn.disabled = false;
            }
        }
    }
    
    async prepareAndSubmitForm() {
        const formData = new FormData();
        
        // Add product data
        formData.append('name', document.getElementById('productName').value);
        formData.append('price', document.getElementById('productPrice').value);
        formData.append('description', document.getElementById('productDescription').value);
        formData.append('category', document.getElementById('productCategory').value);
        
        // Add images
        this.images.forEach((image, index) => {
            formData.append('images', image.file);
        });
        
        // Submit to server
        const response = await fetch('/product/create', {
            method: 'POST',
            body: formData
        });
        
        const responseText = await response.text();
        let result;
        
        try {
            result = JSON.parse(responseText);
        } catch (e) {
            // Se la risposta non è JSON, probabilmente è un errore del server
            throw new Error('Errore del server');
        }
        
        if (result.success) {
            this.showSuccessAndRedirect(result.productId);
        } else if (result.error) {
            throw new Error(result.error);
        } else {
            throw new Error('Risposta del server non valida');
        }
    }
    
    showSuccessAndRedirect(productId) {
        // Show success message
        const container = document.querySelector('.product-create-container');
        if (container) {
            container.innerHTML = `
                <div style="text-align: center; padding: 2rem;">
                    <div class="success-checkmark">✓</div>
                    <h2 style="color: #10b981; margin-bottom: 1rem;">Prodotto creato con successo!</h2>
                    <p style="color: #6b7280; margin-bottom: 2rem;">Il tuo prodotto è stato pubblicato e sarà visibile a tutti gli utenti.</p>
                    <a href="/product/${productId}" class="btn btn-primary" style="margin-right: 1rem;">Visualizza Prodotto</a>
                    <a href="/users/products" class="btn btn-secondary">I Miei Prodotti</a>
                </div>
            `;
        }
        
        // Redirect after delay
        setTimeout(() => {
            window.location.href = `/product/${productId}`;
        }, 3000);
    }
    
    showNotification(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 1rem 1.5rem;
            border-radius: 8px;
            color: white;
            font-weight: 500;
            z-index: 9999;
            animation: slideIn 0.3s ease;
        `;
        
        // Set background color based on type
        const colors = {
            info: '#3b82f6',
            success: '#10b981',
            warning: '#f59e0b',
            error: '#ef4444'
        };
        notification.style.backgroundColor = colors[type] || colors.info;
        
        notification.textContent = message;
        document.body.appendChild(notification);
        
        // Auto remove after 5 seconds
        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, 5000);
    }
}

// Add CSS for notifications
const style = document.createElement('style');
style.textContent = `
    @keyframes slideIn {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    
    @keyframes slideOut {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
`;
document.head.appendChild(style);

// Global functions for HTML onclick handlers
function triggerFileInput() {
    console.log('triggerFileInput called');
    const fileInput = document.getElementById('imageInput');
    if (fileInput) {
        fileInput.click();
    } else {
        console.error('File input not found');
    }
}

function handleImageUpload(files) {
    console.log('handleImageUpload called with files:', files);
    if (window.productCreator) {
        window.productCreator.handleFileSelect(files);
    } else {
        console.error('ProductCreator not initialized');
    }
}

function changeStep(direction) {
    console.log('changeStep called with direction:', direction);
    if (window.productCreator) {
        if (direction > 0) {
            window.productCreator.nextStep();
        } else {
            window.productCreator.prevStep();
        }
    } else {
        console.error('ProductCreator not initialized');
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    console.log('DOM loaded, initializing ProductCreator...'); // Debug
    
    // Test if elements exist
    const uploadArea = document.querySelector('.image-upload-area') || document.getElementById('imageUploadArea');
    const fileInput = document.getElementById('imageInput');
    console.log('Found upload area:', uploadArea);
    console.log('Found file input:', fileInput);
    
    window.productCreator = new ProductCreator();
    console.log('ProductCreator assigned to window:', window.productCreator); // Debug
});

// Prevent form submission on Enter key in text inputs
document.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' && e.target.tagName === 'INPUT' && e.target.type === 'text') {
        e.preventDefault();
    }
});
