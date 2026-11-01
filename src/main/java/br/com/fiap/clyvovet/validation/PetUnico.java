package br.com.fiap.clyvovet.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PetUnicoValidator.class)
public @interface PetUnico {

	String message() default "Pet já cadastrado.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
