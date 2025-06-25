package it.uniroma3.siw.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.model.Product;
import it.uniroma3.siw.model.ProductForm;
import it.uniroma3.siw.model.ProductImage;
import it.uniroma3.siw.model.Users;
import it.uniroma3.siw.service.FileStorageService;
import it.uniroma3.siw.service.ProductService;
import it.uniroma3.siw.service.RatingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private RatingService ratingService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @GetMapping("/products/categoria")
    public String getProductsByCategoria(
    @RequestParam(required = false) String categoria,
    Model model) {
        List<Product> prodotti = productService.getProductsByCategoria(categoria);
        model.addAttribute("products", prodotti);
        model.addAttribute("categoria", categoria);
        model.addAttribute("redirectUrl", categoria != null ? "/products/categoria?categoria=" + categoria : "/products");
        return "products";
    }
    
    @GetMapping("/products")
    public String searchProducts(
    @RequestParam(required = false) String categoria,
    @RequestParam(required = false) Integer price,
    @RequestParam(required = false) Integer rating,
    Model model) {
        List<Product> filteredProducts = productService.filterProducts(categoria, price, rating);
        model.addAttribute("products", filteredProducts);
        StringBuilder redirectUrl = new StringBuilder("/products");
        boolean hasParams = false;
        if (categoria != null) {
            redirectUrl.append(hasParams ? "&" : "?").append("categoria=").append(categoria);
            hasParams = true;
        }
        if (price != null) {
            redirectUrl.append(hasParams ? "&" : "?").append("price=").append(price);
            hasParams = true;
        }
        if (rating != null) {
            redirectUrl.append(hasParams ? "&" : "?").append("rating=").append(rating);
        }
        model.addAttribute("redirectUrl", redirectUrl.toString());
        return "products";
    }
    
    @GetMapping("/formNewProduct")
    public String showNewProductForm(Model model, HttpSession session) {
        List<ProductImage> tempImages = (List<ProductImage>) session.getAttribute("tempImages");
        if (tempImages == null || tempImages.size() != 10) {
            tempImages = new ArrayList<>(Collections.nCopies(10, null));
            session.setAttribute("tempImages", tempImages);
        }
        model.addAttribute("productForm", new ProductForm());
        model.addAttribute("tempImages", tempImages);
        return "formNewProduct";
    }
    
    @PostMapping("/product")
    public String addProduct(
    @Valid @ModelAttribute("productForm") ProductForm productForm,
    BindingResult bindingResult,
    HttpSession session,
    Model model) {
        logger.debug("Inizio elaborazione POST /product");
        
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Utente non loggato, redirect a /login");
            return "redirect:/login";
        }
        
        // Recupera le immagini temporanee dalla sessione
        Object tempImagesObj = session.getAttribute("tempImages");
        List<ProductImage> tempImages = null;
        if (tempImagesObj instanceof List<?> list) {
            tempImages = list.stream()
            .filter(ProductImage.class::isInstance)
            .map(ProductImage.class::cast)
            .toList();
        }
        
        // *** SOLO QUI controlla il numero di immagini ***
        // Conta solo le immagini non null
        long validImagesCount = (tempImages != null)
            ? tempImages.stream().filter(img -> img != null).count()
            : 0;
        if (validImagesCount < 2 || validImagesCount > 10) {
            model.addAttribute("errorMessage", "Devi caricare tra 2 e 10 immagini valide.");
            model.addAttribute("productForm", productForm);
            model.addAttribute("tempImages", tempImages);
            return "formNewProduct";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("errorMessage", "Errore nei dati inseriti. Controlla i campi.");
            model.addAttribute("productForm", productForm);
            model.addAttribute("tempImages", tempImages);
            return "formNewProduct";
        }
        
        try {
            Product product = new Product();
            product.setNome(productForm.getNome());
            product.setCategoria(productForm.getCategoria());
            product.setPrezzo(productForm.getPrezzo());
            product.setDescrizione(productForm.getDescrizione());
            product.setAutore(loggedUser);
            
            // Associa le immagini temporanee al prodotto
            if (tempImages != null) {
                for (ProductImage productImage : tempImages) {
                    productImage.setProduct(product);
                    product.getImages().add(productImage);
                }
            }
            
            productService.saveProduct(product);
            session.removeAttribute("tempImages"); // Pulisci la sessione
            logger.info("Prodotto creato con successo: {}", product.getNome());
            return "redirect:/my-products";
        } catch (Exception e) {
            logger.error("Errore durante la creazione del prodotto", e);
            model.addAttribute("errorMessage", "Errore durante la creazione del prodotto: " + e.getMessage());
            model.addAttribute("productForm", productForm);
            model.addAttribute("tempImages", tempImages);
            return "formNewProduct";
        }
    }
    
    @PostMapping("/product/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session, Model model) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        Product product = productService.findById(id);
        if (product != null && product.getAutore().equals(loggedUser)) {
            productService.deleteProduct(id);
        } else {
            model.addAttribute("errorMessage", "Non puoi rimuovere questo prodotto.");
        }
        return "redirect:/my-products";
    }
    
    @GetMapping("/my-products")
    public String getMyProducts(HttpSession session, Model model, HttpServletRequest request) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        List<Product> myProducts = productService.findProductsByAutore(loggedUser);
        model.addAttribute("products", myProducts);
        model.addAttribute("redirectUrl", request.getRequestURI());
        return "myProducts";
    }
    
    @GetMapping("/product/edit/{id}")
    public String editProduct(@PathVariable Long id, HttpSession session, Model model, @RequestParam(required = false) String redirectUrl) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        Product product = productService.findById(id);
        if (product == null || !product.getAutore().equals(loggedUser)) {
            return "redirect:/my-products";
        }
        // Convert Product to ProductForm
        ProductForm productForm = new ProductForm();
        productForm.setNome(product.getNome());
        productForm.setCategoria(product.getCategoria());
        productForm.setPrezzo(product.getPrezzo());
        productForm.setDescrizione(product.getDescrizione());
        model.addAttribute("productForm", productForm);
        model.addAttribute("productId", id);
        model.addAttribute("redirectUrl", redirectUrl);
        model.addAttribute("productImages", product.getImages() != null ? product.getImages() : List.of()); // AGGIUNGI QUESTA RIGA
        return "editProduct";
    }
    
    @PostMapping("/product/update/{id}")
    public String updateProduct(
    @PathVariable Long id,
    @Valid @ModelAttribute("productForm") ProductForm productForm,
    BindingResult bindingResult,
    @RequestParam(value = "images", required = false) List<MultipartFile> images,
    @RequestParam(required = false) String redirectUrl,
    HttpSession session,
    Model model) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        
        Product existingProduct = productService.findById(id);
        if (existingProduct == null || !existingProduct.getAutore().equals(loggedUser)) {
            return "redirect:/my-products";
        }
        
        // Validate number of images
        long validImagesCount = (images != null)
        ? images.stream().filter(image -> !image.isEmpty()).count()
        : 0;
        if (validImagesCount + existingProduct.getImages().size() > 10) {
            logger.warn("Numero di immagini non valido: {} (totale con esistenti: {})", validImagesCount, validImagesCount + existingProduct.getImages().size());
            model.addAttribute("errorMessage", "Il numero totale di immagini non può superare 10.");
            model.addAttribute("productForm", productForm);
            model.addAttribute("productId", id);
            model.addAttribute("redirectUrl", redirectUrl);
            return "editProduct";
        }
        
        // Validate form fields
        if (bindingResult.hasErrors()) {
            StringBuilder errors = new StringBuilder("Errori di validazione: ");
            for (ObjectError error : bindingResult.getAllErrors()) {
                errors.append(error.getDefaultMessage()).append("; ");
            }
            logger.warn(errors.toString());
            model.addAttribute("errorMessage", "Errore nei dati inseriti. Controlla i campi.");
            model.addAttribute("productForm", productForm);
            model.addAttribute("productId", id);
            model.addAttribute("redirectUrl", redirectUrl);
            return "editProduct";
        }
        
        try {
            // Update existing product
            existingProduct.setNome(productForm.getNome());
            existingProduct.setCategoria(productForm.getCategoria());
            existingProduct.setPrezzo(productForm.getPrezzo());
            existingProduct.setDescrizione(productForm.getDescrizione());
            
            // Process new images
            if (images != null) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String filePath = fileStorageService.storeFile(image);
                        ProductImage productImage = new ProductImage();
                        productImage.setFilePath(filePath);
                        productImage.setProduct(existingProduct);
                        existingProduct.getImages().add(productImage);
                    }
                }
            }
            
            productService.saveProduct(existingProduct);
            logger.info("Prodotto aggiornato con successo: {}", existingProduct.getNome());
            return (redirectUrl != null && !redirectUrl.isEmpty()) ? "redirect:" + redirectUrl : "redirect:/my-products";
        } catch (Exception e) {
            logger.error("Errore durante l'aggiornamento del prodotto", e);
            model.addAttribute("errorMessage", "Errore durante l'aggiornamento del prodotto: " + e.getMessage());
            model.addAttribute("productForm", productForm);
            model.addAttribute("productId", id);
            model.addAttribute("redirectUrl", redirectUrl);
            return "editProduct";
        }
    }
    
    @GetMapping("/product/{id}")
    public String getProduct(
    @PathVariable Long id,
    @RequestParam(required = false) String redirectUrl,
    HttpSession session,
    HttpServletRequest request,
    Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/products";
        }
        
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        boolean hasRated = loggedUser != null && ratingService.hasUserRatedProduct(loggedUser, product);
        Double userRating = null;
        if (hasRated) {
            userRating = ratingService.getUserRating(loggedUser, product);
        }
        
        String finalRedirectUrl = redirectUrl != null && !redirectUrl.isEmpty() ? redirectUrl : request.getHeader("Referer");
        if (finalRedirectUrl == null || finalRedirectUrl.contains("/product/" + id)) {
            finalRedirectUrl = "/products";
        }
        
        model.addAttribute("product", product);
        model.addAttribute("hasRated", hasRated);
        model.addAttribute("userRating", userRating);
        model.addAttribute("redirectUrl", finalRedirectUrl);
        
        return "product";
    }
    
    @PostMapping("/product/add-image")
    public String addProductImage(@PathVariable Long id,
    @RequestParam("image") MultipartFile image,
    @RequestParam(required = false) String redirectUrl,
    HttpSession session,
    Model model) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) return "redirect:/login";
        Product product = productService.findById(id);
        if (product == null || !product.getAutore().equals(loggedUser)) return "redirect:/my-products";
        if (image != null && !image.isEmpty() && product.getImages().size() < 10) {
            String filePath = fileStorageService.storeFile(image);
            ProductImage productImage = new ProductImage();
            productImage.setFilePath(filePath);
            productImage.setProduct(product);
            product.getImages().add(productImage);
            productService.saveProduct(product);
        }
        return "redirect:/product/edit/" + id + (redirectUrl != null ? "?redirectUrl=" + redirectUrl : "");
    }
    
    @PostMapping("/product/{id}/remove-image")
    public String removeProductImage(@PathVariable Long id,
    @RequestParam("imageId") Long imageId,
    HttpSession session,
    RedirectAttributes redirectAttributes) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            return "redirect:/login";
        }
        Product product = productService.findById(id);
        if (product == null || !product.getAutore().equals(loggedUser)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Prodotto non trovato o non autorizzato.");
            return "redirect:/my-products";
        }
        if (product.getImages().size() <= 2) {
            redirectAttributes.addFlashAttribute("errorMessage", "Un prodotto deve avere almeno due immagini.");
            return "redirect:/product/edit/" + id;
        }
        try {
            productService.removeImageFromProduct(product, imageId);
            redirectAttributes.addFlashAttribute("successMessage", "Immagine rimossa con successo.");
        } catch (IllegalArgumentException e) {
            logger.error("Errore durante la rimozione dell'immagine con ID {} per il prodotto {}: {}", imageId, id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            logger.error("Errore imprevisto durante la rimozione dell'immagine con ID {} per il prodotto {}", imageId, id, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante la rimozione dell'immagine.");
        }
        return "redirect:/product/edit/" + id;
    }
    
    @PostMapping("/product/add-image-temp")
    public String addImageTemp(@RequestParam("image") MultipartFile image,
                           @RequestParam("slotIndex") int slotIndex,
                           HttpSession session, Model model) {
    Users loggedUser = (Users) session.getAttribute("loggedUser");
    if (loggedUser == null) {
        return "redirect:/login";
    }
    // Recupera o inizializza la lista di 10 slot
    List<ProductImage> tempImages = (List<ProductImage>) session.getAttribute("tempImages");
    if (tempImages == null || tempImages.size() != 10) {
        tempImages = new ArrayList<>(Collections.nCopies(10, null));
    }
    if (image != null && !image.isEmpty() && slotIndex >= 0 && slotIndex < 10) {
        String filePath = fileStorageService.storeFile(image);
        ProductImage productImage = new ProductImage();
        productImage.setFilePath(filePath);
        tempImages.set(slotIndex, productImage);
        // Dopo aver modificato tempImages
        while (tempImages.size() < 10) tempImages.add(null);
        if (tempImages.size() > 10) tempImages = tempImages.subList(0, 10);
        session.setAttribute("tempImages", tempImages);
    }
    return "redirect:/formNewProduct";
}
    
    @PostMapping("/product/remove-image-temp")
    public String removeImageTemp(@RequestParam("slotIndex") int slotIndex, HttpSession session, Model model) {
        Users loggedUser = (Users) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            logger.warn("Utente non loggato, redirect a /login");
            return "redirect:/login";
        }
        List<ProductImage> tempImages = null;
        Object tempImagesObj = session.getAttribute("tempImages");
        if (tempImagesObj instanceof List<?> list) {
            tempImages = new ArrayList<>();
            for (Object obj : list) {
                if (obj instanceof ProductImage pi) {
                    tempImages.add(pi);
                } else {
                    tempImages.add(null);
                }
            }
        }
        if (tempImages != null && slotIndex >= 0 && slotIndex < tempImages.size()) {
            tempImages.set(slotIndex, null);
            session.setAttribute("tempImages", tempImages);
            logger.info("Immagine temporanea rimossa dallo slot {}", slotIndex);
        }
        return "redirect:/formNewProduct";
    }
}