package com.vendi.vendi_ms.exception;

/**
 * Exceção lançada quando o acesso é negado.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
public class AccessDeniedException extends RuntimeException {
    
    public AccessDeniedException(String message) {
        super(message);
    }
    
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
