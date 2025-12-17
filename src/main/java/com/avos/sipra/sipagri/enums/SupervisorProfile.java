package com.avos.sipra.sipagri.enums;

/**
 * Enumeration representing different profiles or roles a supervisor can hold
 * within the system. This enum is used to categorize supervisors and define
 * their levels of authority and responsibility.
 */
public enum SupervisorProfile {
    /**
     * Represents a supervisor profile within the system.
     * This profile typically corresponds to roles with standard
     * supervisory authority and responsibilities for managing
     * operations or overseeing tasks.
     */
    SUPERVISOR, /**
     * Represents the Administrator profile for a supervisor in the system.
     * This role is typically associated with higher levels of authority and
     * responsibilities, often involving administrative tasks and management
     * operations. The Administrator profile is generally positioned above
     * standard supervisor roles.
     */
    ADMINISTRATOR, /**
     * Represents the highest level of authority within the system.
     * The SUPER_ADMIN role typically has unrestricted access and
     * the ability to perform all actions, including administrative tasks
     * and system configurations.
     */
    SUPER_ADMIN;
}
