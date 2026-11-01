package br.com.fiap.clyvovet.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ClinicaUnicaValidator.class)
public @interface ClinicaUnica {

	String message() default "Clínica já cadastrada.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
