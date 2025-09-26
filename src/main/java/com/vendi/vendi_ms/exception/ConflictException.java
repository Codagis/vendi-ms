package com.vendi.vendi_ms.exception;

/**
 * Exceção lançada quando há conflito de dados (ex: duplicação).
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}
