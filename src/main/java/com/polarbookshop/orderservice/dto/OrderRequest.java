package com.polarbookshop.orderservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(

    @NotBlank(message="The book ISBN MUST BE DEFINED")
    String isbn,

    @NotNull(message="The book quantity MUST BE DEFINED")
    @Min(value=1, message="The book quantity MUST BE AT LEAST 1")
    @Max(value=5, message="The book quantity MUST BE AT MOST 5")
    Integer quantity

) {
    
}
