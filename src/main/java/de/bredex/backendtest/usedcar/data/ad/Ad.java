package de.bredex.backendtest.usedcar.data.ad;

import de.bredex.backendtest.usedcar.data.applicationuser.ApplicationUser;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ad")
@NoArgsConstructor
public class Ad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private BigDecimal id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_user_email", referencedColumnName = "email")
    private ApplicationUser applicationUser;
    private String make;
    private String type;
    private String description;
    private BigDecimal price;

}