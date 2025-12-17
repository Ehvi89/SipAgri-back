package com.avos.sipra.sipagri.exceptions;

import lombok.Getter;

/**
 * Exception thrown when a requested resource cannot be found.
 * This exception is typically used to indicate that a specific entity
 * with the given identifier or attribute does not exist in the system.
 * <p>
 * The exception provides additional context about the missing resource,
 * including its name, the field used for lookup, and the value of that field.
 */
@Getter
public class ResourceNotFoundException extends RuntimeException {
    /**
     * The name of the resource that could not be found.
     * This variable is used to identify the specific resource type
     * associated with the exception, providing context about what entity
     * is missing.
     */
    private final String resourceName;
    /**
     * The name of the field used to identify or search for the resource that
     * could not be found. This field typically represents the attribute or column
     * used to query the resource, such as "id", "name", or "email".
     */
    private final String fieldName;
    /**
     * The value of the field used to look up the resource that could not be found.
     * Provides additional context about the missing resource by specifying the
     * particular value for which the lookup was performed.
     * <p>
     * For example, in a scenario where a search is performed for a resource
     * based on a specific attribute, this variable represents the value
     * of that attribute.
     */
    private final Object fieldValue;

    /**
     * Constructs a new ResourceNotFoundException with details about the missing resource.
     * This exception is typically used to indicate that a specific entity with the given
     * identifier or attribute does not exist in the system.
     *
     * @param resourceName the name of the resource that was not found
     * @param fieldName the field used to identify the resource
     * @param fieldValue the value of the field used to identify the resource
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}