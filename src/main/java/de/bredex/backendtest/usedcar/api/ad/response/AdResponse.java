package de.bredex.backendtest.usedcar.api.ad.response;

import lombok.Data;

@Data
public class AdResponse {

    private String make;
    private String type;
    private Long price;
    private String description;
}
