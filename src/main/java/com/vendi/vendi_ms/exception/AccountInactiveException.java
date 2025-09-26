package com.vendi.vendi_ms.exception;

/**
 * Exceção lançada quando uma conta está inativa ou bloqueada.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
public class AccountInactiveException extends RuntimeException {
    
    public AccountInactiveException(String message) {
        super(message);
    }
    
    public AccountInactiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
