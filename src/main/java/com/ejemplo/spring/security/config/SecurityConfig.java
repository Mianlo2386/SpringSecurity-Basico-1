package com.ejemplo.spring.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Configuración de las reglas de autorización de solicitudes HTTP
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir el acceso sin autenticación a la URL "/v1/index2"
                        .requestMatchers("/v1/index2").permitAll()
                        // Requerir autenticación para cualquier otra solicitud
                        .anyRequest().authenticated()
                )
                // Configuración del inicio de sesión basado en formularios
                .formLogin(formLogin -> formLogin
                        // Usar el successHandler personalizado
                        .successHandler(successHandler())
                        // Permitir el acceso a la página de inicio de sesión sin autenticación
                        .permitAll()
                )
                // Configuración de manejo de sesiones
                .sessionManagement(sessionManagement -> sessionManagement
                        // Política de creación de sesiones: siempre crear una nueva sesión
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        // Redirigir a la URL "/login" cuando la sesión es inválida
                        .invalidSessionUrl("/login")
                        // Permitir un máximo de una sesión por usuario
                        .maximumSessions(1)
                        // Redirigir a la URL "/login" cuando la sesión ha expirado
                        .expiredUrl("/login")
                        // Registrar las sesiones con el SessionRegistry
                        .sessionRegistry(sessionRegistry())
                )
                // Fijación de sesión
                .sessionManagement(sessionManagement -> sessionManagement
                        // Prevenir la fijación de sesión cambiando el ID de sesión al autenticarse
                        .sessionFixation().migrateSession()
                )
                // Deshabilitar la protección CSRF
                .csrf(csrf -> csrf.disable());

        // Configuración de autenticación HTTP Basic (comentado para referencia)
                /*
                .httpBasic(httpBasic -> httpBasic
                        // Permitir el acceso a la URL "/v1/index2" sin autenticación
                        .realmName("Example") porque las credenciales van en el header
                )
                */

        // Construir y retornar el objeto SecurityFilterChain configurado
        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        // Retornar un AuthenticationSuccessHandler usando una expresión lambda
        return (request, response, authentication) -> {
            // Redirigir a la URL "/v1/index" después de un inicio de sesión exitoso
            response.sendRedirect("/v1/index");
        };
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        // Crear y retornar un nuevo objeto SessionRegistryImpl
        return new SessionRegistryImpl();
    }
}

// Políticas de creación de sesiones:

// SessionCreationPolicy.ALWAYS:
// Siempre crea una nueva sesión al autenticarse, incluso si ya existe una sesión

// SessionCreationPolicy.IF_REQUIRED:
// Solo crea una nueva sesión si es necesaria. Usa una sesión existente si está disponible.

// SessionCreationPolicy.NEVER:
// Nunca crea una sesión, pero usa una sesión existente si está disponible.

// SessionCreationPolicy.STATELESS:
// No crea ni usa una sesión. Todas las solicitudes deben ser completamente autenticadas, típicamente usando tokens (por ejemplo, JWT).

// Explicaciones adicionales:

// invalidSessionUrl("/login"):
// Redirige al usuario a la URL "/login" cuando se detecta que la sesión es inválida o ha caducado.

// maximumSessions(1):
// Establece el número máximo de sesiones permitidas por usuario a la vez. En este caso, se permite solo una sesión simultánea por usuario.

// expiredUrl("/login"):
// Redirige al usuario a la URL "/login" cuando su sesión ha expirado.

// sessionFixation().migrateSession():
// Cambia el ID de la sesión al autenticarse para prevenir ataques de fijación de sesión. Esto migra la sesión a un nuevo ID de sesión.

// sessionRegistry():
// SessionRegistry es un componente que permite mantener el registro de todas las sesiones autenticadas. Aquí, se configura un nuevo objeto SessionRegistryImpl.

// sessionFixation().migrateSession().newSession():
// Crea una nueva sesión y descarta la sesión anterior, asegurando que cualquier dato de sesión potencialmente comprometido no se lleve a la nueva sesión.

// sessionFixation().migrateSession().none():
// No realiza ningún cambio en la sesión actual. Esta opción es la MENOS!!! recomendable porque puede dejar la aplicación vulnerable a ataques de fijación de sesión, donde un atacante podría interceptar o preestablecer el ID de sesión para acceder a la cuenta del usuario.