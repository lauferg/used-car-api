package de.bredex.backendtest.usedcar.data.applicationuser;

import de.bredex.backendtest.usedcar.data.ad.Ad;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Entity
@Table(name = "application_user")
@NoArgsConstructor
@Getter
@Setter
public class ApplicationUser {

    @Id
    private String email;
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "applicationUser")
    private Set<Ad> ads;
}
