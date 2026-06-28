package com.academia.auth_service.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.academia.auth_service.model.Usuario;
import com.academia.auth_service.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtService jwtService;

    /**
     * Valida run + clave contra la tabla 'usuarios' (administrada por user-service) y, si
     * coinciden, devuelve un token JWT. La clave se compara en texto plano porque así se
     * almacena en el perfil del usuario. Los usuarios se crean únicamente desde user-service.
     */
    public String login(String run, String clave) {
        Optional<Usuario> optUsuario = usuarioRepository.findByRun(run);
        if (!optUsuario.isPresent()) return null;
        Usuario usuario = optUsuario.get();
        if (clave == null || !clave.equals(usuario.getClave())) return null;
        return jwtService.generateToken(run);
    }

    /** True si existe un usuario con ese run (para distinguir 'clave incorrecta' de 'usuario no encontrado'). */
    public boolean existeUsuario(String run) {
        return usuarioRepository.findByRun(run).isPresent();
    }
}
