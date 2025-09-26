package com.vendi.vendi_ms.exception;

/**
 * Exceção lançada quando as credenciais são inválidas.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
public class InvalidCredentialsException extends RuntimeException {
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }
}
