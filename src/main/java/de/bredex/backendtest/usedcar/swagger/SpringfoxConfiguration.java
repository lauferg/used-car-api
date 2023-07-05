package de.bredex.backendtest.usedcar.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SpringfoxConfiguration {


    @Bean
    public Docket usedCarApi() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("de.bredex.backendtest.usedcar.api"))
                .build()
                .securitySchemes(List.of(new HttpAuthenticationScheme("JWT", "Desc", "bearer", "bearer", "JWT", Collections.emptyList())));
    }
}
