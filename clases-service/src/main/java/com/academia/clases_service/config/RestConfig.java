package com.academia.clases_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.academia.clases_service.model.Categoria;
import com.academia.clases_service.model.Clase;
import com.academia.clases_service.model.Curso;

/**
 * Spring Data REST oculta por defecto el campo @Id en la serialización de entidades.
 * Aquí lo exponemos para que el id aparezca en el JSON.
 */
@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(Curso.class, Categoria.class, Clase.class);
    }
}
