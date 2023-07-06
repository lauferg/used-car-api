package de.bredex.backendtest.usedcar.api.ad;

import de.bredex.backendtest.usedcar.api.ad.dto.AdDto;
import de.bredex.backendtest.usedcar.api.ad.response.AdSearchResponse;
import de.bredex.backendtest.usedcar.api.ad.response.NewAdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ad")
public class AdController {

    private final AdService adService;

    @Operation(summary = "Post a new ad for a used car.", security = @SecurityRequirement(name = "JWT"))
    @PostMapping("/")
    public ResponseEntity<NewAdResponse> postNewAd(@RequestBody @Valid AdDto adDto, Authentication authentication) {
        NewAdResponse response = new NewAdResponse();
        String newAdUrl = adService.saveNewAd(adDto, authentication);
        response.setUrl(newAdUrl);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Remove a specific used car ad.", security = @SecurityRequirement(name = "JWT"))
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAd(@PathVariable long id, Authentication authentication) {
        long numRemovedAds = adService.deleteAd(id, authentication);
        return numRemovedAds == 1 ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @Operation(summary = "Search for an ad based on keywords.")
    @GetMapping("/search")
    public ResponseEntity<AdSearchResponse> searchAd(
            @RequestParam
            @Size(max = 20, message = "The make keyword cannot be longer than 20 characters.")
            String makeKeyword,
            @RequestParam
            @Size(max = 20, message = "The type keyword cannot be longer than 20 characters.")
            String typeKeyword) {
        List<String> adUrls = adService.findAds(makeKeyword, typeKeyword);
        AdSearchResponse adSearchResponse = new AdSearchResponse();
        adSearchResponse.setAdUrls(adUrls);
        return ResponseEntity.ok(adSearchResponse);
    }

    @Operation(summary = "Get a specific ad.")
    @GetMapping("/{id}")
    public ResponseEntity<AdDto> fetchAd(@PathVariable long id) {
        AdDto adDto = adService.fetchAd(id);
        return ResponseEntity.ok(adDto);
    }
}
