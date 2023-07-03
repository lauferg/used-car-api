package de.bredex.backendtest.usedcar.data.applicationuser;

import de.bredex.backendtest.usedcar.data.ad.Ad;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Set;

@Data
@Entity
@Table(name = "application_user")
@NoArgsConstructor
public class ApplicationUser {

    @Id
    private String email;
    private String name;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "applicationUser")
    private Set<Ad> ads;
}
