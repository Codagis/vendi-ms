package com.vendi.vendi_ms.exception;

/**
 * Exceção lançada quando uma entidade não é encontrada.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
