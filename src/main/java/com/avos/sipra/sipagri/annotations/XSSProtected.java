package com.avos.sipra.sipagri.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation indicating that an endpoint method applies input sanitization
 * or validation measures to mitigate Cross-Site Scripting (XSS) risks.
 * <p>
 * This annotation is documentation-only unless there is an aspect/interceptor in
 * the application that actively enforces XSS protections based on this marker.
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XSSProtected {
}
