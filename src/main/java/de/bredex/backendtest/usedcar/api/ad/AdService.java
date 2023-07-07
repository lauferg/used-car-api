package de.bredex.backendtest.usedcar.api.ad;

import de.bredex.backendtest.usedcar.api.ad.dto.AdDto;
import de.bredex.backendtest.usedcar.data.ad.Ad;
import de.bredex.backendtest.usedcar.data.ad.AdRepository;
import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdService {

    private final AdRepository adRepository;
    private final ApplicationUserRepository applicationUserRepository;


    @Transactional
    public long deleteAd(long id, Authentication authentication) {
        ApplicationUser applicationUser = findCurrentUserInDb(authentication);
        return adRepository.deleteAdByApplicationUserAndId(applicationUser, id);
    }

    public List<String> findAds(String makeKeyword, String typeKeyword) {
        List<Ad> ads = adRepository.findByKeywords(makeKeyword, typeKeyword);
        return ads
                .stream()
                .map(this::generateLinkForAd)
                .collect(Collectors.toList());
    }

    public AdDto fetchAd(Long id) {
        Ad adEntity = adRepository.findById(id).orElseThrow();
        return adDtoFromAdEntity(adEntity);
    }

    public String saveNewAd(final AdDto adRequest, Authentication authentication) {
        ApplicationUser applicationUser = findCurrentUserInDb(authentication);
        Ad adEntity = adRepository.save(adDtoToAdEntity(adRequest, applicationUser));
        return generateLinkForAd(adEntity);
    }

    private ApplicationUser findCurrentUserInDb(Authentication authentication) {
        String currentUserEmail = (String) authentication.getPrincipal();
        String currentUserName = (String) authentication.getCredentials();
        return applicationUserRepository
                .findByEmailAndName(currentUserEmail, currentUserName).orElseThrow();
    }

    private Ad adDtoToAdEntity(final AdDto adRequest, ApplicationUser applicationUser) {
        Ad adEntity = new Ad();
        adEntity.setMake(adRequest.getMake());
        adEntity.setType(adRequest.getType());
        adEntity.setDescription(adRequest.getDescription());
        adEntity.setPrice(adRequest.getPrice());
        adEntity.setApplicationUser(applicationUser);
        return adEntity;
    }

    private String generateLinkForAd(final Ad adEntity) {
        String id = String.valueOf(adEntity.getId());
        String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return url + "/ad/" + id;
    }

    private AdDto adDtoFromAdEntity(Ad adEntity) {
        AdDto adDto = new AdDto();
        adDto.setMake(adEntity.getMake());
        adDto.setType(adEntity.getType());
        adDto.setDescription(adEntity.getDescription());
        adDto.setPrice(adEntity.getPrice());
        return adDto;
    }
}
