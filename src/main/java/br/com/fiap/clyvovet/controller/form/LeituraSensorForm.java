package br.com.fiap.clyvovet.controller.form;

import java.math.BigDecimal;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LeituraSensorForm {
    @NotNull @Positive private Long petId;
    @NotBlank @Size(max = 30) private String dispositivo;
    @NotNull @DecimalMin("20") @DecimalMax("50") private BigDecimal temperatura;
    @NotNull @Min(1) @Max(400) private Integer frequenciaCardiaca;
    @NotNull @DecimalMin("0") @DecimalMax("100") private BigDecimal spo2;
    @NotNull @DecimalMin("0") @DecimalMax("100") private BigDecimal atividade;
}
