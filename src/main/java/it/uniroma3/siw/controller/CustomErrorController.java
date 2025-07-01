package it.uniroma3.siw.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);
    
    private void addAuthenticationAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        
        model.addAttribute("isAuthenticated", isAuthenticated);
        if (isAuthenticated && auth != null) {
            model.addAttribute("username", auth.getName());
        }
    }
    
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        addAuthenticationAttributes(model);
        
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        
        String errorMessage = "Si è verificato un errore imprevisto.";
        String errorDetails = "";
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("status", statusCode);
            
            HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
            model.addAttribute("error", httpStatus.getReasonPhrase());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                errorMessage = "La pagina richiesta non è stata trovata.";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                errorMessage = "Errore interno del server.";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                errorMessage = "Accesso negato.";
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                errorMessage = "Autenticazione richiesta.";
            }
        }
        
        if (message != null) {
            model.addAttribute("message", message.toString());
            errorMessage = message.toString();
        } else {
            model.addAttribute("message", errorMessage);
        }
        
        if (exception != null) {
            Throwable throwable = (Throwable) exception;
            model.addAttribute("exception", throwable.getClass().getSimpleName());
            
            // Log dell'errore completo
            logger.error("Error occurred at {}: {}", requestUri, throwable.getMessage(), throwable);
            
            // Stack trace per sviluppo (rimuovere in produzione)
            StringBuilder stackTrace = new StringBuilder();
            stackTrace.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage()).append("\n");
            for (StackTraceElement element : throwable.getStackTrace()) {
                stackTrace.append("\tat ").append(element.toString()).append("\n");
                // Limitiamo a 20 righe per evitare output troppo lunghi
                if (stackTrace.length() > 2000) {
                    stackTrace.append("\t... (more stack trace)");
                    break;
                }
            }
            model.addAttribute("trace", stackTrace.toString());
            
            errorDetails = "Eccezione: " + throwable.getClass().getSimpleName() + 
                          (throwable.getMessage() != null ? " - " + throwable.getMessage() : "");
        }
        
        if (requestUri != null) {
            model.addAttribute("path", requestUri.toString());
            logger.error("Error at path: {}, Status: {}, Message: {}", requestUri, status, errorMessage);
        }
        
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("errorDetails", errorDetails);
        
        return "error";
    }
}
