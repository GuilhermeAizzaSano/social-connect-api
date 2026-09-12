package br.com.socialconnect.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<CPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) return true; // Use @NotBlank para obrigar

        cpf = cpf.replaceAll("\\D", ""); // Remove não-dígitos

        if (cpf.length() != 11) return false;

        // Lógica de validação do CPF (dígitos verificadores)
        return true;
    }
}
