package de.bredex.backendtest.usedcar.api.ad.dto;

import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Size;

@Data
@Builder
public class AdDto {
    @Size(min = 1, max = 20, message = "The make of the car must be between 1 and 20 characters in length.")
    private String make;
    @Size(min = 1, max = 20, message = "The type of the car must be between 1 and 20 characters in length.")
    private String type;
    @Size(min = 1, max = 200, message = "The description of the car must be between 1 and 200 characters in length.")
    private String description;
    @Range(min = 0, max = 9_999_999_999L, message = "The price of the car must be between 0 and 9999999999.")
    private Long price;
}
