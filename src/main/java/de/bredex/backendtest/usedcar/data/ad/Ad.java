package de.bredex.backendtest.usedcar.data.ad;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "ad")
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_user_email", referencedColumnName = "email")
    private ApplicationUser applicationUser;
    private String make;
    private String type;
    private String description;
    private Long price;

}
