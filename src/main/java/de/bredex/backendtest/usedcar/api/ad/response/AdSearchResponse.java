package de.bredex.backendtest.usedcar.api.ad.response;

import lombok.Data;

import java.util.List;

@Data
public class AdSearchResponse {

    private List<String> adUrls;
}
