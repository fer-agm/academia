package com.academia.inscripciones_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.academia.inscripciones_service.model.Cupos;
import com.academia.inscripciones_service.model.Inscripciones;

/**
 * Spring Data REST oculta por defecto el campo @Id en la serialización de entidades.
 * Aquí lo exponemos para que el id aparezca en el JSON.
 */
@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(Inscripciones.class, Cupos.class);
    }
}
