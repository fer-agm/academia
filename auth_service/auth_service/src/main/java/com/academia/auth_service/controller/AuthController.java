package com.academia.auth_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.auth_service.service.UsuarioService;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/login")
    public java.util.Map<String,String> login(@RequestBody java.util.Map<String,String> request){
        String run = request.get("run");
        String clave = request.get("clave");
        String token = usuarioService.login(run,clave);

        java.util.Map<String, String> resp = new java.util.HashMap<>();
        if (token == null) {
            resp.put("status", "error");
            resp.put("token", "");
        } else {
            resp.put("status", "ok");
            resp.put("token", token);
        }
        return resp;
    }

@PostMapping("/registrar")
public java.util.Map<String,String> register(@RequestBody java.util.Map<String,String> request ) {
    String run = request.get("run");
    String clave = request.get("clave");
        String resultado = usuarioService.register(run, clave);

        java.util.Map<String,String> resp = new java.util.HashMap<>();
        resp.put("message", resultado);
    return resp;
}

}
