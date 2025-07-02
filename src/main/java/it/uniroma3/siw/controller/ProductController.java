package it.uniroma3.siw.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.dto.ProductFormDTO;
import it.uniroma3.siw.model.Category;
import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.ProductImage;
import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.repository.CategoryRepository;
import it.uniroma3.siw.repository.ProductImageRepository;
import it.uniroma3.siw.repository.ProductRepository;
import it.uniroma3.siw.repository.UsersRepository;
import it.uniroma3.siw.service.ProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/product")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private void addAuthenticationAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        
        model.addAttribute("isAuthenticated", isAuthenticated);
        if (isAuthenticated && auth != null) {
            model.addAttribute("username", auth.getName());
        }
    }

    private Users getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            try {
                // Prova prima il cast diretto
                if (auth.getPrincipal() instanceof Users users) {
                    return users;
                }
                // Se non funziona, cerca per username nel database
                String username = auth.getName();
                return usersRepository.findByUsername(username).orElse(null);
            } catch (ClassCastException e) {
                logger.error("Error casting principal to Users: {}", e.getMessage());
                // Fallback: cerca per username
                String username = auth.getName();
                return usersRepository.findByUsername(username).orElse(null);
            }
        }
        return null;
    }

    @GetMapping("/create/images")
    public String showImageUploadPage(Model model) {
        addAuthenticationAttributes(model);
        model.addAttribute("productFormDTO", new ProductFormDTO());
        logger.debug("Displaying image upload page");
        return "image-upload";
    }

    @PostMapping("/create/images")
    public String handleImageUpload(@RequestParam("images") List<MultipartFile> images,
                                    HttpSession session, Model model) {
        try {
            addAuthenticationAttributes(model);
            if (images.size() < 2 || images.size() > 10) {
                model.addAttribute("errorMessage", "Seleziona da 2 a 10 immagini.");
                return "image-upload";
            }
            productService.saveImagesToSession(images, session);
            logger.debug("Images saved to session successfully");
            return "redirect:/product/create/details";
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error("Error uploading images: {}", sw.toString());
            model.addAttribute("errorMessage", "Errore durante il caricamento delle immagini: " + e.getMessage());
            return "image-upload";
        }
    }

    @GetMapping("/create/details")
    public String showCreateDetailsPage(Model model, HttpSession session) {
        addAuthenticationAttributes(model);
        @SuppressWarnings("unchecked")
        List<byte[]> imageDataList = (List<byte[]>) session.getAttribute("productImages");
        if (imageDataList == null || imageDataList.isEmpty()) {
            return "redirect:/product/create/images";
        }
        model.addAttribute("productFormDTO", new ProductFormDTO());
        model.addAttribute("imageCount", imageDataList.size());
        
        // Aggiungi le categorie al modello
        List<Category> categories = (List<Category>) categoryRepository.findAll();
        model.addAttribute("categories", categories);
        
        logger.debug("Displaying create details page with {} images", imageDataList.size());
        return "product-details";
    }

    @PostMapping("/create/details")
    public String handleCreateDetails(@Valid @ModelAttribute ProductFormDTO productFormDTO,
                                      BindingResult result, HttpSession session, Model model) {
        try {
            addAuthenticationAttributes(model);
            @SuppressWarnings("unchecked")
            List<byte[]> imageDataList = (List<byte[]>) session.getAttribute("productImages");
            if (imageDataList == null || imageDataList.isEmpty()) {
                model.addAttribute("errorMessage", "Nessuna immagine trovata. Ricarica le immagini.");
                return "redirect:/product/create/images";
            }

            if (result.hasErrors()) {
                model.addAttribute("imageCount", imageDataList.size());
                // Aggiungi le categorie quando ci sono errori
                List<Category> categories = (List<Category>) categoryRepository.findAll();
                model.addAttribute("categories", categories);
                return "product-details";
            }

            // Passa i dati inseriti e le immagini al riepilogo
            session.setAttribute("productFormDTO", productFormDTO);
            model.addAttribute("productFormDTO", productFormDTO);
            // Crea lista di indici per le immagini temporanee
            List<Integer> imageIndices = new ArrayList<>();
            for (int i = 0; i < imageDataList.size(); i++) {
                imageIndices.add(i);
            }
            model.addAttribute("imageIndices", imageIndices);
            return "summary";
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error("Error creating product: {}", sw.toString());
            model.addAttribute("errorMessage", "Errore durante la creazione del prodotto: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/create/confirm")
    public String confirmCreateProduct(HttpSession session, Model model) {
        try {
            addAuthenticationAttributes(model);
            // Recupera i dati del prodotto e le immagini dalla sessione
            ProductFormDTO productFormDTO = (ProductFormDTO) session.getAttribute("productFormDTO");
            @SuppressWarnings("unchecked")
            List<byte[]> imageDataList = (List<byte[]>) session.getAttribute("productImages");
            Users authenticatedUser = getAuthenticatedUser();
            if (productFormDTO == null || imageDataList == null || imageDataList.isEmpty() || authenticatedUser == null) {
                model.addAttribute("errorMessage", "Dati mancanti o sessione scaduta. Riprova la creazione del prodotto.");
                return "error";
            }
            // Salva il prodotto
            Product savedProduct = productService.saveProduct(productFormDTO, imageDataList, authenticatedUser);
            // Pulisci la sessione
            session.removeAttribute("productFormDTO");
            session.removeAttribute("productImages");
            session.removeAttribute("imageIds");
            // Redirect alla pagina di dettaglio del prodotto
            return "redirect:/product/" + savedProduct.getId();
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error("Error confirming product creation: {}", sw.toString());
            model.addAttribute("errorMessage", "Errore durante la conferma del prodotto: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public String showProduct(@PathVariable Long id, Model model) {
        try {
            addAuthenticationAttributes(model);
            Optional<Product> productOpt = productRepository.findByIdWithImages(id);
            if (productOpt.isEmpty()) {
                model.addAttribute("errorMessage", "Prodotto non trovato.");
                return "error";
            }
            model.addAttribute("product", productOpt.get());
            logger.debug("Displaying product with ID: {}", id);
            return "product-view";
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error("Error loading product: {}", sw.toString());
            model.addAttribute("errorMessage", "Errore durante il caricamento del prodotto: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/image/{imageId}")
    @ResponseBody
    @Transactional(readOnly = true)
    public org.springframework.http.ResponseEntity<byte[]> getImage(@PathVariable Long imageId) {
        try {
            Optional<ProductImage> imageOpt = productImageRepository.findById(imageId);
            if (imageOpt.isEmpty()) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }
            ProductImage image = imageOpt.get();
            
            // Se contentType è null, usa un valore di default
            String contentType = image.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "image/jpeg"; // Default content type
            }
            
            return org.springframework.http.ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(image.getImageData());
        } catch (Exception e) {
            logger.error("Error loading image with ID: {}", imageId, e);
            return org.springframework.http.ResponseEntity.internalServerError().build();
        }
    }

    // Endpoint per pagina di scelta modifica prodotto
    @GetMapping("/edit/{id}")
    @Transactional(readOnly = true)
    public String showEditProductPage(@PathVariable Long id, Model model) {
        try {
            addAuthenticationAttributes(model);
            Users authenticatedUser = getAuthenticatedUser();
            if (authenticatedUser == null) {
                model.addAttribute("errorMessage", "Devi essere autenticato per modificare un prodotto.");
                return "error";
            }
            
            Optional<Product> productOpt = productRepository.findByIdWithSeller(id);
            if (productOpt.isEmpty()) {
                model.addAttribute("errorMessage", "Prodotto non trovato.");
                return "error";
            }
            
            Product product = productOpt.get();
            
            // Verifica che l'autore del prodotto non sia null
            if (product.getSeller() == null) {
                logger.error("Product with ID {} has null author", id);
                model.addAttribute("errorMessage", "Errore: prodotto senza autore.");
                return "error";
            }
            
            // Verifica che l'utente sia il proprietario del prodotto
            if (!product.getSeller().getId().equals(authenticatedUser.getId())) {
                model.addAttribute("errorMessage", "Non hai i permessi per modificare questo prodotto.");
                return "error";
            }
            
            model.addAttribute("product", product);
            return "edit-product";
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error("Error loading edit page: {}", sw.toString());
            model.addAttribute("errorMessage", "Errore durante il caricamento della pagina di modifica: " + e.getMessage());
            return "error";
        }
    }

    // Endpoint per modificare le immagini di un prodotto
    @GetMapping("/edit/{id}/images")
    @Transactional(readOnly = true)
    public String showEditImages(@PathVariable Long id, Model model, HttpSession session) {
        try {
            addAuthenticationAttributes(model);
            Users authenticatedUser = getAuthenticatedUser();
            if (authenticatedUser == null) {
                model.addAttribute("errorMessage", "Devi essere autenticato per modificare un prodotto.");
                return "error";
            }
            
            Optional<Product> productOpt = productRepository.findByIdWithImages(id);
            if (productOpt.isEmpty()) {
                model.addAttribute("errorMessage", "Prodotto non trovato.");
                return "error";
            }
            
            Product product = productOpt.get();
            
            // Verifica che l'utente sia il proprietario del prodotto
            if (!product.getSeller().getId().equals(authenticatedUser.getId())) {
                model.addAttribute("errorMessage", "Non hai i permessi per modificare questo prodotto.");
                return "error";
            }
            
            // Salva l'ID del prodotto in sessione per i passaggi successivi
            session.setAttribute("editingProductId", id);
            model.addAttribute("product", product);
            return "edit-images";
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error("Error loading images for edit: {}", sw.toString());
            model.addAttribute("errorMessage", "Errore durante il caricamento delle immagini: " + e.getMessage());
            return "error";
        }
    }

    // Endpoint per modificare i dettagli di un prodotto
    @GetMapping("/edit/{id}/details")
    public String showEditDetails(@PathVariable Long id, Model model, HttpSession session) {
        try {
            addAuthenticationAttributes(model);
            Users authenticatedUser = getAuthenticatedUser();
            if (authenticatedUser == null) {
                model.addAttribute("errorMessage", "Devi essere autenticato per modificare un prodotto.");
                return "error";
            }
            
            Optional<Product> productOpt = productRepository.findByIdWithSeller(id);
            if (productOpt.isEmpty()) {
                model.addAttribute("errorMessage", "Prodotto non trovato.");
                return "error";
            }
            
            Product product = productOpt.get();
            
            // Verifica che l'autore del prodotto non sia null
            if (product.getSeller() == null) {
                logger.error("Product with ID {} has null author", id);
                model.addAttribute("errorMessage", "Errore: prodotto senza autore.");
                return "error";
            }
            
            // Verifica che l'utente sia il proprietario del prodotto
            if (!product.getSeller().getId().equals(authenticatedUser.getId())) {
                model.addAttribute("errorMessage", "Non hai i permessi per modificare questo prodotto.");
                return "error";
            }
            
            // Crea un DTO dal prodotto esistente
            ProductFormDTO productFormDTO = new ProductFormDTO();
            productFormDTO.setNome(product.getName());
            productFormDTO.setCategoria(product.getCategory() != null ? product.getCategory().getName() : "");
            productFormDTO.setDescrizione(product.getDescription());
            productFormDTO.setPrezzo(product.getPrice());
            
            // Salva l'ID del prodotto in sessione per i passaggi successivi
            session.setAttribute("editingProductId", id);
            model.addAttribute("productFormDTO", productFormDTO);
            model.addAttribute("product", product);
            
            // Aggiungi le categorie al modello
            List<Category> categories = (List<Category>) categoryRepository.findAll();
            model.addAttribute("categories", categories);
            
            return "edit-details";
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error("Error loading details for edit: {}", sw.toString());
            model.addAttribute("errorMessage", "Errore durante il caricamento dei dettagli: " + e.getMessage());
            return "error";
        }
    }

    // Endpoint per salvare le modifiche alle immagini
    @PostMapping("/edit/images")
    @Transactional
    public String handleEditImages(@RequestParam("images") List<MultipartFile> images,
                                   HttpSession session, Model model) {
        try {
            addAuthenticationAttributes(model);
            Long productId = (Long) session.getAttribute("editingProductId");
            if (productId == null) {
                model.addAttribute("errorMessage", "Sessione scaduta. Riprova.");
                return "error";
            }

            Users authenticatedUser = getAuthenticatedUser();
            if (authenticatedUser == null) {
                model.addAttribute("errorMessage", "Devi essere autenticato per modificare un prodotto.");
                return "error";
            }
            
            Optional<Product> productOpt = productRepository.findByIdWithImages(productId);
            if (productOpt.isEmpty()) {
                model.addAttribute("errorMessage", "Prodotto non trovato.");
                return "error";
            }
            
            Product product = productOpt.get();
            
            // Verifica che l'utente sia il proprietario del prodotto
            if (!product.getSeller().getId().equals(authenticatedUser.getId())) {
                model.addAttribute("errorMessage", "Non hai i permessi per modificare questo prodotto.");
                return "error";
            }

            if (images.size() < 2 || images.size() > 10) {
                model.addAttribute("errorMessage", "Seleziona da 2 a 10 immagini.");
                model.addAttribute("product", product);
                return "edit-images";
            }
            
            // Aggiorna le immagini del prodotto
            productService.updateProductImages(product, images);
            session.removeAttribute("editingProductId");
            
            logger.debug("Product images updated successfully for ID: {}", productId);
            return "redirect:/product/" + productId;
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error("Error updating images: {}", sw.toString());
            model.addAttribute("errorMessage", "Errore durante l'aggiornamento delle immagini: " + e.getMessage());
            return "error";
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error("Error updating images: {}", sw.toString());
            model.addAttribute("errorMessage", "Errore durante l'aggiornamento delle immagini: " + e.getMessage());
            return "error";
        }
    }

    // Endpoint per salvare le modifiche ai dettagli
    @PostMapping("/edit/details")
    public String handleEditDetails(@Valid @ModelAttribute ProductFormDTO productFormDTO,
                                    BindingResult result, HttpSession session, Model model) {
        try {
            addAuthenticationAttributes(model);
            Long productId = (Long) session.getAttribute("editingProductId");
            if (productId == null) {
                model.addAttribute("errorMessage", "Sessione scaduta. Riprova.");
                return "error";
            }

            Users authenticatedUser = getAuthenticatedUser();
            if (authenticatedUser == null) {
                model.addAttribute("errorMessage", "Devi essere autenticato per modificare un prodotto.");
                return "error";
            }
            
            Optional<Product> productOpt = productRepository.findByIdWithSeller(productId);
            if (productOpt.isEmpty()) {
                model.addAttribute("errorMessage", "Prodotto non trovato.");
                return "error";
            }
            
            Product product = productOpt.get();
            
            // Verifica che l'autore del prodotto non sia null
            if (product.getSeller() == null) {
                logger.error("Product with ID {} has null author", productId);
                model.addAttribute("errorMessage", "Errore: prodotto senza autore.");
                return "error";
            }
            
            // Verifica che l'utente sia il proprietario del prodotto
            if (!product.getSeller().getId().equals(authenticatedUser.getId())) {
                model.addAttribute("errorMessage", "Non hai i permessi per modificare questo prodotto.");
                return "error";
            }

            if (result.hasErrors()) {
                model.addAttribute("product", product);
                // Aggiungi le categorie quando ci sono errori
                List<Category> categories = (List<Category>) categoryRepository.findAll();
                model.addAttribute("categories", categories);
                return "edit-details";
            }
            
            // Aggiorna i dettagli del prodotto
            productService.updateProductDetails(product, productFormDTO);
            session.removeAttribute("editingProductId");
            
            logger.debug("Product details updated successfully for ID: {}", productId);
            return "redirect:/product/" + productId;
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error("Error updating details: {}", sw.toString());
            model.addAttribute("errorMessage", "Errore durante l'aggiornamento dei dettagli: " + e.getMessage());
            return "error";
        }
    }

    // Endpoint per eliminare un prodotto
    @PostMapping("/delete/{id}")
    @Transactional
    public String deleteProduct(@PathVariable Long id, Model model) {
        try {
            addAuthenticationAttributes(model);
            Users authenticatedUser = getAuthenticatedUser();
            if (authenticatedUser == null) {
                model.addAttribute("errorMessage", "Devi essere autenticato per eliminare un prodotto.");
                return "error";
            }
            
            Optional<Product> productOpt = productRepository.findByIdWithSeller(id);
            if (productOpt.isEmpty()) {
                model.addAttribute("errorMessage", "Prodotto non trovato.");
                return "error";
            }
            
            Product product = productOpt.get();
            
            // Verifica che l'autore del prodotto non sia null
            if (product.getSeller() == null) {
                logger.error("Product with ID {} has null author", id);
                model.addAttribute("errorMessage", "Errore: prodotto senza autore.");
                return "error";
            }
            
            // Verifica che l'utente sia il proprietario del prodotto
            if (!product.getSeller().getId().equals(authenticatedUser.getId())) {
                model.addAttribute("errorMessage", "Non hai i permessi per eliminare questo prodotto.");
                return "error";
            }
            
            productService.deleteProduct(product);
            logger.debug("Product deleted successfully with ID: {}", id);
            return "redirect:/users/products";
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error("Error deleting product: {}", sw.toString());
            model.addAttribute("errorMessage", "Errore durante l'eliminazione del prodotto: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/create/image/{index}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<byte[]> getTemporaryImage(@PathVariable int index, HttpSession session) {
        try {
            @SuppressWarnings("unchecked")
            List<byte[]> imageDataList = (List<byte[]>) session.getAttribute("productImages");
            if (imageDataList == null || index < 0 || index >= imageDataList.size()) {
                return org.springframework.http.ResponseEntity.notFound().build();
            }
            
            byte[] imageData = imageDataList.get(index);
            return org.springframework.http.ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        } catch (Exception e) {
            logger.error("Error loading temporary image with index: {}", index, e);
            return org.springframework.http.ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/details/{id}")
    @Transactional(readOnly = true)
    public String showProductDetails(@PathVariable Long id, Model model) {
        try {
            addAuthenticationAttributes(model);
            Optional<Product> productOpt = productRepository.findByIdWithImages(id);
            if (productOpt.isEmpty()) {
                model.addAttribute("errorMessage", "Prodotto non trovato.");
                return "error";
            }
            
            Product product = productOpt.get();
            model.addAttribute("product", product);
            
            // Controlla se l'utente corrente è il proprietario del prodotto
            Users authenticatedUser = getAuthenticatedUser();
            boolean isOwner = authenticatedUser != null && 
                            product.getSeller() != null && 
                            product.getSeller().getId().equals(authenticatedUser.getId());
            model.addAttribute("isOwner", isOwner);
            
            // Aggiungi informazioni sui rating tramite il servizio dedicato
            try {
                Double avgRating = 0.0;
                Integer ratingCount = 0;
                
                // Carica ratings dal repository se necessario
                if (product.getRatings() != null && !product.getRatings().isEmpty()) {
                    avgRating = product.getAverageRating();
                    ratingCount = product.getRatingCount();
                }
                
                model.addAttribute("averageRating", avgRating);
                model.addAttribute("ratingCount", ratingCount);
            } catch (Exception e) {
                // Fallback se c'è un problema con i rating
                logger.warn("Impossibile caricare i rating: {}", e.getMessage());
                model.addAttribute("averageRating", 0.0);
                model.addAttribute("ratingCount", 0);
            }
            
            logger.debug("Displaying product details for ID: {}", id);
            return "product-details-view";
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error("Error loading product details: {}", sw.toString());
            model.addAttribute("errorMessage", "Errore durante il caricamento dei dettagli del prodotto: " + e.getMessage());
            return "error";
        }
    }
}
