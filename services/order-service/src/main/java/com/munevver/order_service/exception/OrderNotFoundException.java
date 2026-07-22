package com.munevver.order_service.exception;

public class OrderNotFoundException extends RuntimeException {
    
    private final Object[] args;

    public OrderNotFoundException(String messageKey, Object... args) {
        super(messageKey);
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }
}
