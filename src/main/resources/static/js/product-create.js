// SOLUZIONE SEMPLICE: File input sovrapposto
console.log('=== PRODUCT CREATE JS LOADED ===');

document.addEventListener('DOMContentLoaded', function() {
    console.log('=== DOM READY ===');
    
    const fileInput = document.getElementById('imageInput');
    
    if (fileInput) {
        console.log('✅ File input trovato:', fileInput);
        
        // Listener per il cambio file
        fileInput.addEventListener('change', function(e) {
            console.log('🎉 FILE SELEZIONATI! 🎉');
            console.log('Files:', e.target.files);
            
            if (e.target.files && e.target.files.length > 0) {
                handleFileSelection(e.target.files);
            }
        });
        
        // Test del drag and drop sull'area
        const uploadArea = document.getElementById('imageUploadArea');
        if (uploadArea) {
            uploadArea.addEventListener('dragover', function(e) {
                e.preventDefault();
                uploadArea.style.backgroundColor = '#f0f8ff';
            });
            
            uploadArea.addEventListener('dragleave', function(e) {
                e.preventDefault();
                uploadArea.style.backgroundColor = '';
            });
            
            uploadArea.addEventListener('drop', function(e) {
                e.preventDefault();
                uploadArea.style.backgroundColor = '';
                
                const files = e.dataTransfer.files;
                if (files && files.length > 0) {
                    console.log('🎯 Files dropped:', files);
                    handleFileSelection(files);
                }
            });
        }
        
        console.log('✅ Event listeners configurati');
    } else {
        console.error('❌ File input non trovato!');
    }
});

function handleFileSelection(files) {
    console.log('Gestione file selezionati:', files.length);
    
    // Mostra alert di test
    alert(`${files.length} file selezionato(i):\n${Array.from(files).map(f => f.name).join('\n')}`);
    
    // Qui andrà la logica per mostrare le anteprime
    const previewContainer = document.getElementById('imagePreviewGrid');
    if (previewContainer) {
        previewContainer.innerHTML = '';
        
        Array.from(files).forEach((file, index) => {
            if (file.type.startsWith('image/')) {
                const reader = new FileReader();
                reader.onload = function(e) {
                    const div = document.createElement('div');
                    div.innerHTML = `
                        <div style="display: inline-block; margin: 5px; border: 1px solid #ccc; padding: 5px;">
                            <img src="${e.target.result}" style="width: 100px; height: 100px; object-fit: cover;">
                            <div>${file.name}</div>
                        </div>
                    `;
                    previewContainer.appendChild(div);
                };
                reader.readAsDataURL(file);
            }
        });
    }
}

// Enhanced Product Creation JavaScript - Sitarello
console.log('Enhanced Product Create JS loaded');

class EnhancedProductCreator {
    constructor() {
        // Configuration
        this.currentStep = 1;
        this.totalSteps = 3;
        this.images = [];
        this.maxImages = 10;
        this.maxFileSize = 5 * 1024 * 1024; // 5MB
        this.allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'];
        
        // Auto-save
        this.autoSaveTimer = null;
        this.formData = {};
        this.hasUnsavedChanges = false;
        
        // Validation
        this.validationRules = {
            productName: { required: true, minLength: 3, maxLength: 100 },
            productPrice: { required: true, min: 0.01 },
            productDescription: { required: true, minLength: 10, maxLength: 2000 },
            productCategory: { required: true },
            condition: { required: true }
        };
        
        // Price suggestions data (mock)
        this.priceSuggestions = {};
        
        // Category suggestions
        this.categorySuggestions = {
            'iphone': ['Smartphone', 'Elettronica'],
            'samsung': ['Smartphone', 'Elettronica'],
            'laptop': ['Computer', 'Elettronica'],
            'macbook': ['Computer', 'Elettronica'],
            'nike': ['Scarpe', 'Abbigliamento'],
            'adidas': ['Scarpe', 'Abbigliamento']
        };
        
        this.init();
    }
    
    init() {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.setupAll());
        } else {
            this.setupAll();
        }
    }
    
    setupAll() {
        console.log('Setting up enhanced product creator...');
        
        this.setupEventListeners();
        this.setupImageUpload();
        this.setupFormValidation();
        this.setupPreview();
        this.setupAutoSave();
        this.setupSmartFeatures();
        this.updateProgressBar();
        this.showStep(this.currentStep);
        
        console.log('Enhanced product creator initialized');
    }
    
    setupEventListeners() {
        // Navigation buttons
        const nextBtn = document.getElementById('nextBtn');
        const prevBtn = document.getElementById('prevBtn');
        const submitBtn = document.getElementById('submitBtn');
        
        if (nextBtn) nextBtn.addEventListener('click', () => this.nextStep());
        if (prevBtn) prevBtn.addEventListener('click', () => this.prevStep());
        if (submitBtn) submitBtn.addEventListener('click', (e) => this.submitForm(e));
        
        // Form inputs for live preview and auto-save
        const inputs = ['productName', 'productPrice', 'productDescription', 'productCategory'];
        inputs.forEach(inputId => {
            const element = document.getElementById(inputId);
            if (element) {
                element.addEventListener('input', () => {
                    this.updatePreview();
                    this.markUnsavedChanges();
                    this.scheduleAutoSave();
                });
                element.addEventListener('blur', () => this.validateField(inputId));
            }
        });
        
        // Condition radio buttons
        document.querySelectorAll('input[name="condition"]').forEach(radio => {
            radio.addEventListener('change', () => {
                this.updatePreview();
                this.markUnsavedChanges();
                this.scheduleAutoSave();
            });
        });
        
        // Character counters
        this.setupCharacterCounters();
        
        // Tips toggle
        this.setupTipsToggle();
        
        // Format buttons (if present)
        this.setupFormatButtons();
    }
    
    setupImageUpload() {
        const uploadArea = document.getElementById('imageUploadArea');
        const fileInput = document.getElementById('imageInput');
        
        if (!uploadArea || !fileInput) {
            console.error('Upload elements not found');
            return;
        }
        
        // File input configuration
        fileInput.setAttribute('accept', 'image/jpeg,image/jpg,image/png,image/webp');
        fileInput.setAttribute('multiple', 'true');
        
        // Click to upload - clean implementation
        uploadArea.addEventListener('click', (e) => {
            // Don't trigger if clicking on the upload button
            if (e.target.closest('.upload-btn')) {
                return;
            }
            
            e.preventDefault();
            fileInput.click();
        });
        
        // File input change
        fileInput.addEventListener('change', (e) => {
            if (e.target.files.length > 0) {
                console.log(`${e.target.files.length} file(s) selected`);
                this.handleFileSelect(e.target.files);
                e.target.value = ''; // Reset input
            }
        });
        
        // Drag and drop
        uploadArea.addEventListener('dragover', (e) => {
            e.preventDefault();
            uploadArea.classList.add('dragover');
        });
        
        uploadArea.addEventListener('dragenter', (e) => {
            e.preventDefault();
            uploadArea.classList.add('dragover');
        });
        
        uploadArea.addEventListener('dragleave', (e) => {
            e.preventDefault();
            if (!uploadArea.contains(e.relatedTarget)) {
                uploadArea.classList.remove('dragover');
            }
        });
        
        uploadArea.addEventListener('drop', (e) => {
            e.preventDefault();
            uploadArea.classList.remove('dragover');
            this.handleFileSelect(e.dataTransfer.files);
        });
        
        // Prevent default drag behaviors
        document.addEventListener('dragover', (e) => e.preventDefault());
        document.addEventListener('drop', (e) => e.preventDefault());
    }
    
    handleFileSelect(files) {
        if (!files || files.length === 0) return;
        
        const remainingSlots = this.maxImages - this.images.length;
        
        Array.from(files).slice(0, remainingSlots).forEach((file, index) => {
            if (!this.allowedTypes.includes(file.type)) {
                this.showNotification(`Formato non supportato: ${file.name}`, 'error');
                return;
            }
            
            if (file.size > this.maxFileSize) {
                this.showNotification(`File troppo grande: ${file.name} (max 5MB)`, 'error');
                return;
            }
            
            this.addImage(file);
        });
        
        if (files.length > remainingSlots) {
            this.showNotification(`Puoi caricare solo ${remainingSlots} immagini aggiuntive`, 'warning');
        }
    }
    
    addImage(file) {
        const reader = new FileReader();
        reader.onload = (e) => {
            const imageData = {
                file: file,
                dataUrl: e.target.result,
                id: Date.now() + Math.random(),
                size: file.size
            };
            
            this.images.push(imageData);
            this.renderImagePreviews();
            this.updateUploadInfo();
            this.updatePreview();
            this.validateStep1();
            this.markUnsavedChanges();
            this.scheduleAutoSave();
        };
        reader.readAsDataURL(file);
    }
    
    removeImage(imageId) {
        this.images = this.images.filter(img => img.id !== imageId);
        this.renderImagePreviews();
        this.updateUploadInfo();
        this.updatePreview();
        this.validateStep1();
        this.markUnsavedChanges();
        this.scheduleAutoSave();
    }
    
    renderImagePreviews() {
        const container = document.getElementById('imagePreviewGrid');
        if (!container) return;
        
        container.innerHTML = '';
        
        this.images.forEach((image, index) => {
            const item = document.createElement('div');
            item.className = 'image-preview-item';
            item.innerHTML = `
                <img src="${image.dataUrl}" alt="Preview ${index + 1}">
                <button type="button" class="image-remove-btn" onclick="productCreator.removeImage(${image.id})" title="Rimuovi immagine">
                    <i class="fas fa-times"></i>
                </button>
            `;
            container.appendChild(item);
        });
        
        // Hide upload area if max images reached
        const uploadArea = document.getElementById('imageUploadArea');
        if (uploadArea) {
            uploadArea.style.display = this.images.length >= this.maxImages ? 'none' : 'block';
        }
    }
    
    updateUploadInfo() {
        const imageCount = document.getElementById('imageCount');
        const totalSize = document.getElementById('totalSize');
        
        if (imageCount) {
            imageCount.textContent = this.images.length;
        }
        
        if (totalSize) {
            const totalSizeBytes = this.images.reduce((sum, img) => sum + img.size, 0);
            const totalSizeMB = (totalSizeBytes / (1024 * 1024)).toFixed(1);
            totalSize.textContent = `${totalSizeMB} MB`;
        }
    }
    
    setupCharacterCounters() {
        const fields = [
            { id: 'productName', max: 100 },
            { id: 'productDescription', max: 2000 }
        ];
        
        fields.forEach(field => {
            const element = document.getElementById(field.id);
            const counter = document.getElementById(field.id + 'Counter');
            
            if (element && counter) {
                const updateCounter = () => {
                    const length = element.value.length;
                    counter.textContent = length;
                    
                    counter.className = 'input-counter';
                    if (length > field.max * 0.8) counter.classList.add('warning');
                    if (length > field.max) counter.classList.add('error');
                };
                
                element.addEventListener('input', updateCounter);
                updateCounter(); // Initialize
            }
        });
    }
    
    setupTipsToggle() {
        const tipsHeader = document.querySelector('.tips-header');
        const tipsContent = document.querySelector('.tips-content');
        
        if (tipsHeader && tipsContent) {
            tipsHeader.addEventListener('click', () => {
                tipsHeader.classList.toggle('expanded');
                tipsContent.classList.toggle('expanded');
            });
        }
    }
    
    setupFormatButtons() {
        document.querySelectorAll('.format-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();
                const format = btn.dataset.format;
                const textarea = document.getElementById('productDescription');
                
                if (textarea && format) {
                    this.applyTextFormat(textarea, format);
                }
            });
        });
    }
    
    applyTextFormat(textarea, format) {
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;
        const selectedText = textarea.value.substring(start, end);
        
        let formattedText = '';
        switch (format) {
            case 'bold':
                formattedText = `**${selectedText || 'testo in grassetto'}**`;
                break;
            case 'italic':
                formattedText = `*${selectedText || 'testo in corsivo'}*`;
                break;
            case 'list':
                formattedText = selectedText ? selectedText.split('\n').map(line => `• ${line}`).join('\n') : '• Elemento lista';
                break;
        }
        
        const newValue = textarea.value.substring(0, start) + formattedText + textarea.value.substring(end);
        textarea.value = newValue;
        textarea.focus();
        
        // Update counter and preview
        textarea.dispatchEvent(new Event('input'));
    }
    
    setupFormValidation() {
        // Setup real-time validation for all fields
        Object.keys(this.validationRules).forEach(fieldId => {
            const field = document.getElementById(fieldId) || document.querySelector(`input[name="${fieldId}"]`);
            if (field) {
                field.addEventListener('blur', () => this.validateField(fieldId));
                field.addEventListener('input', () => this.clearFieldError(fieldId));
            }
        });
    }
    
    validateField(fieldId) {
        const field = document.getElementById(fieldId) || document.querySelector(`input[name="${fieldId}"]:checked`);
        const rules = this.validationRules[fieldId];
        
        if (!field || !rules) return true;
        
        const value = field.value ? field.value.trim() : '';
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
        if (value && rules.min !== undefined && parseFloat(value) < rules.min) {
            isValid = false;
            errorMessage = `Valore minimo: ${rules.min}`;
        }
        
        this.showFieldError(fieldId, isValid, errorMessage);
        return isValid;
    }
    
    showFieldError(fieldId, isValid, message) {
        const field = document.getElementById(fieldId) || document.querySelector(`input[name="${fieldId}"]`);
        if (!field) return;
        
        const formGroup = field.closest('.form-group');
        if (!formGroup) return;
        
        let errorElement = formGroup.querySelector('.error-message');
        
        if (isValid) {
            field.classList.remove('form-error');
            if (errorElement) {
                errorElement.classList.remove('show');
                setTimeout(() => errorElement.remove(), 200);
            }
        } else {
            field.classList.add('form-error');
            
            if (!errorElement) {
                errorElement = document.createElement('div');
                errorElement.className = 'error-message';
                formGroup.appendChild(errorElement);
            }
            
            errorElement.innerHTML = `<i class="fas fa-exclamation-triangle"></i> ${message}`;
            errorElement.classList.add('show');
        }
    }
    
    clearFieldError(fieldId) {
        const field = document.getElementById(fieldId);
        if (field) {
            field.classList.remove('form-error');
        }
    }
    
    validateStep1() {
        const isValid = this.images.length > 0;
        this.updateStepValidation(1, isValid);
        return isValid;
    }
    
    validateStep2() {
        const fields = ['productName', 'productPrice', 'productDescription', 'productCategory', 'condition'];
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
        
        if (step === this.currentStep) {
            if (this.currentStep < this.totalSteps && nextBtn) {
                nextBtn.disabled = !isValid;
            }
            if (this.currentStep === this.totalSteps && submitBtn) {
                submitBtn.disabled = !isValid;
            }
        }
        
        // Update quality check
        this.updateQualityCheck();
    }
    
    setupPreview() {
        this.updatePreview();
    }
    
    updatePreview() {
        // Update preview title
        const titleElement = document.getElementById('previewTitle');
        const nameInput = document.getElementById('productName');
        if (titleElement && nameInput) {
            titleElement.textContent = nameInput.value || 'Nome del prodotto';
        }
        
        // Update preview price
        const priceElement = document.getElementById('previewPrice');
        const priceInput = document.getElementById('productPrice');
        if (priceElement && priceInput) {
            const price = parseFloat(priceInput.value) || 0;
            priceElement.textContent = `€ ${price.toFixed(2)}`;
        }
        
        // Update preview category
        const categoryElement = document.getElementById('previewCategory');
        const categorySelect = document.getElementById('productCategory');
        if (categoryElement && categorySelect) {
            const selectedOption = categorySelect.options[categorySelect.selectedIndex];
            categoryElement.textContent = selectedOption ? selectedOption.text : 'Categoria';
        }
        
        // Update preview description
        const descElement = document.getElementById('previewDescription');
        const descInput = document.getElementById('productDescription');
        if (descElement && descInput) {
            descElement.textContent = descInput.value || 'Descrizione del prodotto...';
        }
        
        // Update preview images
        this.updatePreviewImages();
        
        // Update condition badge
        this.updateConditionBadge();
    }
    
    updatePreviewImages() {
        const mainImageContainer = document.getElementById('previewMainImageContainer');
        const thumbnailsContainer = document.getElementById('previewThumbnails');
        const placeholder = document.getElementById('previewPlaceholder');
        const mainImage = document.getElementById('previewMainImage');
        
        if (this.images.length > 0) {
            // Show main image
            if (mainImage && placeholder) {
                mainImage.src = this.images[0].dataUrl;
                mainImage.style.display = 'block';
                placeholder.style.display = 'none';
            }
            
            // Show thumbnails
            if (thumbnailsContainer) {
                thumbnailsContainer.innerHTML = '';
                this.images.forEach((image, index) => {
                    const thumb = document.createElement('div');
                    thumb.className = `preview-thumbnail ${index === 0 ? 'active' : ''}`;
                    thumb.innerHTML = `<img src="${image.dataUrl}" alt="Thumbnail ${index + 1}">`;
                    thumb.addEventListener('click', () => this.switchMainImage(index));
                    thumbnailsContainer.appendChild(thumb);
                });
            }
        } else {
            // Show placeholder
            if (mainImage && placeholder) {
                mainImage.style.display = 'none';
                placeholder.style.display = 'flex';
            }
            
            if (thumbnailsContainer) {
                thumbnailsContainer.innerHTML = '';
            }
        }
    }
    
    switchMainImage(index) {
        if (index < 0 || index >= this.images.length) return;
        
        const mainImage = document.getElementById('previewMainImage');
        if (mainImage) {
            mainImage.src = this.images[index].dataUrl;
        }
        
        // Update thumbnail active state
        document.querySelectorAll('.preview-thumbnail').forEach((thumb, i) => {
            thumb.classList.toggle('active', i === index);
        });
    }
    
    updateConditionBadge() {
        const badge = document.getElementById('conditionBadge');
        const conditionInput = document.querySelector('input[name="condition"]:checked');
        
        if (badge && conditionInput) {
            const conditionValue = conditionInput.value;
            const conditionText = conditionInput.closest('label').querySelector('.condition-title').textContent;
            
            badge.innerHTML = `<i class="fas fa-star"></i><span>${conditionText}</span>`;
            badge.style.display = 'flex';
            
            // Update badge color based on condition
            badge.className = 'badge';
            if (conditionValue === 'nuovo') badge.classList.add('new-badge');
            else if (conditionValue === 'eccellente') badge.classList.add('excellent-badge');
            else badge.classList.add('good-badge');
        } else if (badge) {
            badge.style.display = 'none';
        }
    }
    
    updateQualityCheck() {
        const qualityItems = {
            'qualityImages': this.images.length > 0,
            'qualityTitle': document.getElementById('productName')?.value?.trim().length >= 3,
            'qualityPrice': parseFloat(document.getElementById('productPrice')?.value || 0) > 0,
            'qualityDescription': document.getElementById('productDescription')?.value?.trim().length >= 10,
            'qualityCategory': document.getElementById('productCategory')?.value?.length > 0
        };
        
        let completedCount = 0;
        const totalCount = Object.keys(qualityItems).length;
        
        Object.entries(qualityItems).forEach(([itemId, isComplete]) => {
            const item = document.getElementById(itemId);
            if (item) {
                const icon = item.querySelector('i');
                if (isComplete) {
                    icon.className = 'fas fa-check-circle';
                    item.classList.add('completed');
                    completedCount++;
                } else {
                    icon.className = 'fas fa-times-circle';
                    item.classList.remove('completed');
                }
            }
        });
        
        // Update quality score
        const scorePercentage = Math.round((completedCount / totalCount) * 100);
        const scoreFill = document.getElementById('qualityScoreFill');
        const scoreText = document.getElementById('qualityScoreText');
        
        if (scoreFill) scoreFill.style.width = `${scorePercentage}%`;
        if (scoreText) scoreText.textContent = `${scorePercentage}%`;
    }
    
    setupAutoSave() {
        // Load saved data on page load
        this.loadAutoSavedData();
        
        // Save data before page unload
        window.addEventListener('beforeunload', (e) => {
            if (this.hasUnsavedChanges) {
                this.autoSave();
                e.preventDefault();
                e.returnValue = 'Hai modifiche non salvate. Sei sicuro di voler uscire?';
            }
        });
    }
    
    markUnsavedChanges() {
        this.hasUnsavedChanges = true;
    }
    
    scheduleAutoSave() {
        if (this.autoSaveTimer) {
            clearTimeout(this.autoSaveTimer);
        }
        
        this.autoSaveTimer = setTimeout(() => {
            this.autoSave();
        }, 2000); // Auto-save after 2 seconds of inactivity
    }
    
    autoSave() {
        const formData = this.gatherFormData();
        
        try {
            localStorage.setItem('productCreateAutoSave', JSON.stringify({
                data: formData,
                timestamp: Date.now(),
                images: this.images.map(img => ({ ...img, file: null })) // Don't save file objects
            }));
            
            this.showAutoSaveIndicator();
            this.hasUnsavedChanges = false;
        } catch (error) {
            console.warn('Auto-save failed:', error);
        }
    }
    
    loadAutoSavedData() {
        try {
            const saved = localStorage.getItem('productCreateAutoSave');
            if (saved) {
                const { data, timestamp } = JSON.parse(saved);
                
                // Only load if saved within last 24 hours
                if (Date.now() - timestamp < 24 * 60 * 60 * 1000) {
                    this.populateForm(data);
                    this.showNotification('Dati precedenti ripristinati automaticamente', 'info');
                }
            }
        } catch (error) {
            console.warn('Failed to load auto-saved data:', error);
        }
    }
    
    showAutoSaveIndicator() {
        const indicator = document.getElementById('autoSaveStatus');
        if (indicator) {
            indicator.parentElement.classList.add('show');
            setTimeout(() => {
                indicator.parentElement.classList.remove('show');
            }, 2000);
        }
    }
    
    gatherFormData() {
        return {
            name: document.getElementById('productName')?.value || '',
            price: document.getElementById('productPrice')?.value || '',
            description: document.getElementById('productDescription')?.value || '',
            category: document.getElementById('productCategory')?.value || '',
            condition: document.querySelector('input[name="condition"]:checked')?.value || ''
        };
    }
    
    populateForm(data) {
        Object.entries(data).forEach(([key, value]) => {
            if (key === 'condition') {
                const radio = document.querySelector(`input[name="condition"][value="${value}"]`);
                if (radio) radio.checked = true;
            } else {
                const field = document.getElementById(`product${key.charAt(0).toUpperCase() + key.slice(1)}`);
                if (field) field.value = value;
            }
        });
        
        this.updatePreview();
    }
    
    setupSmartFeatures() {
        this.setupNameSuggestions();
        this.setupPriceSuggestions();
    }
    
    setupNameSuggestions() {
        const nameInput = document.getElementById('productName');
        const suggestionsContainer = document.getElementById('nameSuggestions');
        
        if (nameInput && suggestionsContainer) {
            nameInput.addEventListener('input', () => {
                const value = nameInput.value.toLowerCase();
                this.showNameSuggestions(value, suggestionsContainer);
            });
        }
    }
    
    showNameSuggestions(query, container) {
        container.innerHTML = '';
        
        if (query.length < 2) return;
        
        const suggestions = [];
        Object.entries(this.categorySuggestions).forEach(([keyword, categories]) => {
            if (keyword.includes(query)) {
                suggestions.push(`${keyword.charAt(0).toUpperCase() + keyword.slice(1)} - ${categories[0]}`);
            }
        });
        
        if (suggestions.length > 0) {
            container.style.display = 'block';
            suggestions.slice(0, 5).forEach(suggestion => {
                const item = document.createElement('div');
                item.className = 'suggestion-item';
                item.textContent = suggestion;
                item.addEventListener('click', () => {
                    document.getElementById('productName').value = suggestion.split(' - ')[0];
                    container.style.display = 'none';
                    this.updatePreview();
                });
                container.appendChild(item);
            });
        } else {
            container.style.display = 'none';
        }
    }
    
    setupPriceSuggestions() {
        const priceInput = document.getElementById('productPrice');
        const categorySelect = document.getElementById('productCategory');
        
        if (priceInput && categorySelect) {
            const showPriceSuggestion = () => {
                const category = categorySelect.value;
                const name = document.getElementById('productName')?.value.toLowerCase() || '';
                
                // Mock price suggestion logic
                if (category && name) {
                    this.showPriceSuggestion(category, name);
                }
            };
            
            priceInput.addEventListener('blur', showPriceSuggestion);
            categorySelect.addEventListener('change', showPriceSuggestion);
        }
    }
    
    showPriceSuggestion(category, name) {
        const container = document.getElementById('priceSuggestions');
        if (!container) return;
        
        // Mock price suggestion (in real app, this would call an API)
        let suggestedPrice = 0;
        if (name.includes('iphone')) suggestedPrice = 500;
        else if (name.includes('laptop')) suggestedPrice = 800;
        else if (name.includes('scarpe')) suggestedPrice = 80;
        
        if (suggestedPrice > 0) {
            container.innerHTML = `
                <div class="price-suggestion">
                    <i class="fas fa-lightbulb"></i>
                    <span>Prezzo suggerito: €${suggestedPrice}</span>
                    <button type="button" onclick="document.getElementById('productPrice').value=${suggestedPrice}; productCreator.updatePreview()">
                        Usa
                    </button>
                </div>
            `;
            container.style.display = 'block';
        } else {
            container.style.display = 'none';
        }
    }
    
    showStep(step) {
        // Hide all steps
        document.querySelectorAll('.form-step').forEach(stepEl => {
            stepEl.classList.remove('active');
        });
        
        // Show current step
        const currentStepEl = document.getElementById(`step${step}`);
        if (currentStepEl) {
            currentStepEl.classList.add('active');
        }
        
        // Update navigation buttons
        this.updateNavigationButtons();
        
        // Update step indicator
        this.updateStepIndicator();
        
        // Update progress bar
        this.updateProgressBar();
    }
    
    updateNavigationButtons() {
        const prevBtn = document.getElementById('prevBtn');
        const nextBtn = document.getElementById('nextBtn');
        const submitBtn = document.getElementById('submitBtn');
        
        if (prevBtn) {
            prevBtn.style.display = this.currentStep > 1 ? 'inline-flex' : 'none';
        }
        
        if (nextBtn) {
            nextBtn.style.display = this.currentStep < this.totalSteps ? 'inline-flex' : 'none';
        }
        
        if (submitBtn) {
            submitBtn.style.display = this.currentStep === this.totalSteps ? 'inline-flex' : 'none';
        }
    }
    
    updateStepIndicator() {
        const indicator = document.getElementById('currentStepText');
        if (indicator) {
            indicator.textContent = `Passo ${this.currentStep} di ${this.totalSteps}`;
        }
    }
    
    updateProgressBar() {
        const progressLine = document.getElementById('progressLine');
        const steps = document.querySelectorAll('.step');
        
        // Update progress line width
        if (progressLine) {
            const progressPercentage = ((this.currentStep - 1) / (this.totalSteps - 1)) * 100;
            progressLine.style.width = `${progressPercentage}%`;
        }
        
        // Update step states
        steps.forEach((stepEl, index) => {
            const stepNumber = index + 1;
            stepEl.classList.remove('active', 'completed');
            
            if (stepNumber < this.currentStep) {
                stepEl.classList.add('completed');
            } else if (stepNumber === this.currentStep) {
                stepEl.classList.add('active');
            }
        });
    }
    
    nextStep() {
        let canProceed = false;
        
        switch (this.currentStep) {
            case 1:
                canProceed = this.validateStep1();
                break;
            case 2:
                canProceed = this.validateStep2();
                break;
            default:
                canProceed = true;
        }
        
        if (canProceed && this.currentStep < this.totalSteps) {
            this.currentStep++;
            this.showStep(this.currentStep);
        } else if (!canProceed) {
            this.showNotification('Completa tutti i campi obbligatori prima di continuare', 'error');
        }
    }
    
    prevStep() {
        if (this.currentStep > 1) {
            this.currentStep--;
            this.showStep(this.currentStep);
        }
    }
    
    async submitForm(e) {
        e.preventDefault();
        
        if (!this.validateStep2()) {
            this.showNotification('Completa tutti i campi obbligatori', 'error');
            return;
        }
        
        const submitBtn = document.getElementById('submitBtn');
        const loadingOverlay = document.getElementById('loadingOverlay');
        
        try {
            // Show loading
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.querySelector('.btn-loading').style.display = 'inline-block';
            }
            if (loadingOverlay) loadingOverlay.style.display = 'flex';
            
            // Prepare form data
            const formData = new FormData();
            const data = this.gatherFormData();
            
            Object.entries(data).forEach(([key, value]) => {
                formData.append(key, value);
            });
            
            // Add images
            this.images.forEach((image, index) => {
                if (image.file) {
                    formData.append('images', image.file);
                }
            });
            
            // Submit form
            const response = await fetch('/product/create', {
                method: 'POST',
                body: formData
            });
            
            if (response.ok) {
                // Clear auto-saved data
                localStorage.removeItem('productCreateAutoSave');
                
                // Show success modal
                this.showSuccessModal();
            } else {
                throw new Error('Errore durante il salvataggio');
            }
            
        } catch (error) {
            console.error('Submit error:', error);
            this.showNotification('Errore durante il salvataggio del prodotto', 'error');
        } finally {
            // Hide loading
            if (submitBtn) {
                submitBtn.disabled = false;
                submitBtn.querySelector('.btn-loading').style.display = 'none';
            }
            if (loadingOverlay) loadingOverlay.style.display = 'none';
        }
    }
    
    showSuccessModal() {
        const modal = document.getElementById('successModal');
        if (modal) {
            modal.style.display = 'flex';
        }
    }
    
    showNotification(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <div class="notification-content">
                <i class="fas fa-${this.getNotificationIcon(type)}"></i>
                <span>${message}</span>
                <button class="notification-close">&times;</button>
            </div>
        `;
        
        // Add to page
        document.body.appendChild(notification);
        
        // Show with animation
        setTimeout(() => notification.classList.add('show'), 100);
        
        // Auto-hide after 5 seconds
        setTimeout(() => this.hideNotification(notification), 5000);
        
        // Close button
        notification.querySelector('.notification-close').addEventListener('click', () => {
            this.hideNotification(notification);
        });
    }
    
    hideNotification(notification) {
        notification.classList.remove('show');
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }
    
    getNotificationIcon(type) {
        switch (type) {
            case 'success': return 'check-circle';
            case 'error': return 'exclamation-triangle';
            case 'warning': return 'exclamation-circle';
            default: return 'info-circle';
        }
    }
}

// Initialize when page loads
let productCreator;
document.addEventListener('DOMContentLoaded', () => {
    productCreator = new EnhancedProductCreator();
    // Make it globally accessible for onclick handlers
    window.productCreator = productCreator;
});
