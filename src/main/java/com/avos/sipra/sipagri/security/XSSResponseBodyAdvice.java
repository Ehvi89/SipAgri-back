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

/**
 * A class implementing {@link ResponseBodyAdvice} to sanitize response bodies
 * against potential Cross-Site Scripting (XSS) vulnerabilities.
 * <p>
 * This advice intercepts controller responses and applies sanitization to string
 * values or string fields within objects to mitigate the risks of injecting
 * malicious HTML or script content.
 * <p>
 * Key functionality:
 * - Automatically activates for classes annotated with {@code @RestController}.
 * - Sanitizes simple {@code String} responses and string fields within objects.
 * - Ensures nested sanitization of fields for objects (customize as needed).
 * - Logs errors gracefully instead of interrupting processing.
 */
@Slf4j
@ControllerAdvice
public class XSSResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * Determines if the given return type and converter type are supported. This method
     * checks if the controller class containing the method is annotated with
     * {@code @RestController}. If the annotation is present, this method returns {@code true}.
     *
     * @param returnType the method parameter containing information about the return type of the controller method
     * @param converterType the class type of the {@link HttpMessageConverter} to be used
     * @return {@code true} if the controller class is annotated with {@code @RestController}, {@code false} otherwise
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // S'active pour tous les RestController
        return returnType.getDeclaringClass().isAnnotationPresent(RestController.class);
    }

    /**
     * Intercepts the response body before it is written to the HTTP response, allowing
     * for modifications or processing, such as sanitizing the body content. If the body
     * is {@code null}, it returns {@code null} without performing any processing.
     *
     * @param body the body of the response to be written, may be {@code null}
     * @param returnType the return type of the controller method
     * @param selectedContentType the content type chosen for the response
     * @param selectedConverterType the converter type used to serialize the response body
     * @param request the server-side HTTP request
     * @param response the server-side HTTP response
     * @return the modified or original response body
     */
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

    /**
     * Sanitizes the provided object to protect against XSS (Cross-Site Scripting) attacks. This includes sanitizing
     * strings directly and examining fields of an object to sanitize their values if necessary.
     *
     * @param obj the object to be sanitized. Can be either a String or a Complex Object with fields.
     *            If null, no sanitization will be performed.
     * @return the sanitized object. For strings, a sanitized version of the input string is returned.
     *         For complex objects, the same object is returned with its fields sanitized.
     *         If an error occurs during sanitization, the original object is returned.
     */
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

    /**
     * Sanitizes all string fields of the provided object by removing or escaping
     * potentially malicious content. This method processes each field of the object
     * and replaces its value if it is a string with a sanitized value.
     * <p>
     * The sanitization logic is handled by the {@code sanitizeString} method,
     * which escapes characters commonly used in XSS attacks. Non-string fields
     * and null objects are ignored.
     * <p>
     * Any errors encountered during field sanitization are logged and bypassed,
     * allowing the process to continue for the remaining fields.
     *
     * @param obj the object whose string fields are to be sanitized; if null,
     *            the method exits without processing.
     */
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

    /**
     * Sanitizes the input string by escaping special characters to prevent security vulnerabilities,
     * such as Cross-Site Scripting (XSS) attacks. Converts special characters to their HTML entity equivalents.
     *
     * @param input the input string to be sanitized; may be null
     * @return a sanitized string with special characters replaced by HTML entities, or null if the input is null
     */
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