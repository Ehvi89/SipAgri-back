package com.avos.sipra.sipagri.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;

@Slf4j
@ControllerAdvice
public class XSSResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // S'active pour tous les RestController
        return returnType.getDeclaringClass().isAnnotationPresent(RestController.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        if (body == null) {
            return null;
        }

        return sanitizeResponse(body);
    }

    private Object sanitizeResponse(Object obj) {
        if (obj == null) return null;

        try {
            // Pour les chaînes directes
            if (obj instanceof String string) {
                return sanitizeString(string);
            }

            // Pour les objets DTO
            sanitizeObjectFields(obj);
            return obj;

        } catch (Exception e) {
            // En cas d'erreur, retourner l'objet original
            log.error("Erreur lors de la sanitisation XSS: {}", e.getMessage());
            return obj;
        }
    }

    private void sanitizeObjectFields(Object obj) {
        if (obj == null) return;

        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value instanceof String string) {
                    field.set(obj, sanitizeString(string));
                }
                // Vous pouvez ajouter la récursion pour les objets imbriqués si nécessaire
            }
        } catch (Exception e) {
            // Log l'erreur mais continue
            log.error("Erreur lors de la sanitisation du champ: {}", e.getMessage());
        }
    }

    private String sanitizeString(String input) {
        if (input == null) return null;

        return input
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("&", "&amp;")
                .replace("/", "&#x2F;");
    }
}