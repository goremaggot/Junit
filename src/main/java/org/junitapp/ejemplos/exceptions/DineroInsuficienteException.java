package org.junitapp.ejemplos.exceptions;

public class DineroInsuficienteException extends RuntimeException {
    public DineroInsuficienteException(String menssage){
        super(menssage);
    }
}
