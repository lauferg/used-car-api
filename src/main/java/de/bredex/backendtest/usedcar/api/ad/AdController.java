package de.bredex.backendtest.usedcar.api.ad;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ad")
public class AdController {

    @PostMapping
    public void postNewAd() {

    }

    @DeleteMapping("/{id}")
    public void deleteAd(@PathVariable String id) {
    }

    @GetMapping("/search")
    public void searchAd() {

    }

    @GetMapping("/{id}")
    public void fetchAd(@PathVariable String id) {

    }
}
