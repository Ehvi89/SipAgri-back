package com.avos.sipra.sipagri.enums;

/**
 * Enumeration representing the marital status of an individual.
 * <p>
 * This enum defines various possible states of marital relationships.
 * It is suitable for usage in contexts where an individual's marital
 * status needs to be categorized or recorded.
 */
public enum MaritalStatus {
    /**
     * Indicates that an individual is in a legally recognized marital relationship.
     */
    MARRIED,
    /**
     * Represents the marital status of an individual who was married but is no longer
     * legally married due to a formal divorce process.
     */
    DIVORCED,
    /**
     * Represents the marital status of an individual who has lost their spouse through death
     * and has not remarried. This status is used to denote individuals who are widowed.
     */
    WIDOWED,
    /**
     * Represents the marital status of an individual who is not
     * currently married or in a legally recognized marital relationship.
     */
    SINGLE;
}
