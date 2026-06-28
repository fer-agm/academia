package com.academia.user_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.academia.user_service.model.Rol;
import com.academia.user_service.model.User;

/**
 * Spring Data REST oculta por defecto el campo @Id en la serialización de entidades
 * (lo deja solo en el enlace self). Aquí lo exponemos para que el id aparezca en el JSON.
 */
@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(User.class, Rol.class);
    }
}
