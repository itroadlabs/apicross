package com.github.itroadlabs.oas.apicross;

public class CodeGeneratorException extends RuntimeException {
    public CodeGeneratorException(String message) {
        super(message);
    }

    public CodeGeneratorException(Throwable throwable) {
        super(throwable);
    }
}
