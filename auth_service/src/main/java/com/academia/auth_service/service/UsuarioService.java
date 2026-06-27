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

    @Autowired
    private HashService hashService;


    public String login (String run, String clave) {
        Optional<Usuario> optUsuario = usuarioRepository.findByRun(run);
        if (!optUsuario.isPresent()) return null;
        Usuario usuario = optUsuario.get();
        // compare SHA-1 hashes
        String hashedInput = hashService.sha1(clave);
        if (!hashedInput.equals(usuario.getClave())) return null;

        return jwtService.generateToken(run);
    }

    public String register(String run, String clave) {
        Optional<Usuario> existingOpt = usuarioRepository.findByRun(run);
        if (existingOpt.isPresent()) {
            return "¡Usuario ya existe!";
                }


        Usuario usuario = new Usuario();
        usuario.setRun(run);
        // store SHA-1 hash of the password
        usuario.setClave(hashService.sha1(clave));
        usuario.setNombre("");
        usuario.setApellido("");
        usuario.setEmail(run+"@academia.cl");

        usuarioRepository.save(usuario);

        return "¡Usuario creado exitosamente!";
    }         
}