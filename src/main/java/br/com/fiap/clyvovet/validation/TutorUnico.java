package br.com.fiap.clyvovet.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TutorUnicoValidator.class)
public @interface TutorUnico {

	String message() default "Tutor já cadastrado.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
