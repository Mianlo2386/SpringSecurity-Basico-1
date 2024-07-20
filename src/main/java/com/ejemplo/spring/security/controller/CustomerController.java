package com.ejemplo.spring.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class CustomerController {

    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping("v1/index")
    public String index() {
        return "Hola gente con seguridad";
    }

    @GetMapping("v1/index2")
    public String index2() {
        return "Hola gente SIN seguridad!";
    }

    @GetMapping("/session")
    public ResponseEntity<?> getDetailsSession() {
        // Inicializar una cadena para almacenar el ID de sesión
        String sessionId = "";
        // Inicializar un objeto User para almacenar el usuario autenticado
        User userObject = null;

        // Obtener la lista de todos los principales (usuarios) actualmente autenticados
        List<Object> sessions = sessionRegistry.getAllPrincipals();

        // Iterar sobre todos los principales obtenidos
        for (Object session : sessions) {
            // Verificar si el principal es una instancia de User
            if (session instanceof User) {
                // Si es un User, asignarlo al objeto userObject
                userObject = (User) session;
                // Obtener la lista de todas las sesiones asociadas al usuario
                List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(session, false);

                // Iterar sobre todas las sesiones del usuario
                for (SessionInformation sessionInformation : sessionInformations) {
                    // Obtener el ID de sesión y asignarlo a la variable sessionId
                    sessionId = sessionInformation.getSessionId();
                }
            }
        }

        // Crear un mapa para almacenar la respuesta
        Map<String, Object> response = new HashMap<>();
        // Agregar un mensaje de saludo al mapa de respuesta
        response.put("response", "Hola Gente");
        // Agregar el ID de sesión al mapa de respuesta
        response.put("sessionId", sessionId);
        // Agregar el objeto User al mapa de respuesta
        response.put("sessionUser", userObject);

        // Retornar la respuesta con código HTTP 200 OK y el mapa de respuesta
        return ResponseEntity.ok(response);
    }

}

//EXPLICACIONES ADICIONALES:
//
//        sessionRegistry.getAllPrincipals():
//Recupera todos los principales (usuarios) actualmente autenticados desde el SessionRegistry. Esto devuelve una lista de objetos que representan a los usuarios autenticados en la aplicación.
//
//        session instanceof User:
//Verifica si el objeto principal es una instancia de User. Esto asegura que solo se procesen los objetos que representan usuarios autenticados.
//
//        sessionRegistry.getAllSessions(session, false):
//Obtiene todas las sesiones asociadas al principal (usuario). El segundo parámetro (false) indica que no se deben obtener sesiones inválidas.
//
//        response.put("response", "Hola Gente"):
//Agrega un mensaje de saludo al mapa de respuesta para proporcionar una respuesta amigable al usuario.
//
//        ResponseEntity.ok(response):
//Retorna la respuesta con el estado HTTP 200 OK y el mapa de respuesta que contiene la información de la sesión y el usuario autenticado.