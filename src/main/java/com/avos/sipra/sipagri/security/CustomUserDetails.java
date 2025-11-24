package com.avos.sipra.sipagri.security;

import com.avos.sipra.sipagri.entities.Supervisor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Represents a custom implementation of {@link UserDetails} for authentication purposes.
 * This implementation wraps a {@link Supervisor} entity and provides the required methods
 * for Spring Security integration. Instances of this class are immutable due to its record structure.
 * <p>
 * The class overrides the necessary methods of {@link UserDetails} and adapts them to
 * use the properties of the wrapped {@link Supervisor} object.
 */
public record CustomUserDetails(Supervisor supervisor) implements UserDetails {

    /**
     * Retrieves the collection of authorities granted to the user.
     *
     * @return a collection of granted authorities, or {@code null} if no authorities
     *         are assigned to the user
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * Retrieves the password of the user wrapped within the {@link Supervisor} instance.
     * This method is part of the {@link UserDetails} interface implementation and is
     * used for authentication purposes.
     *
     * @return the password of the associated {@link Supervisor}
     */
    @Override
    public String getPassword() {
        return supervisor.getPassword();
    }

    /**
     * Retrieves the username of the associated {@link Supervisor}.
     *
     * @return the email address of the supervisor, which is used as the username
     */
    @Override
    public String getUsername() {
        return supervisor.getEmail();
    }

    /**
     * Determines whether the user account is non-expired.
     *
     * @return {@code true} if the account is non-expired, otherwise {@code false}.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Determines whether the account is not locked.
     *
     * @return {@code true} if the account is not locked, {@code false} otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the credentials of the user are non-expired.
     * <p>
     * This method determines the validity of the user's credentials.
     * In this implementation, it always returns {@code true}, meaning
     * the credentials are considered never to expire.
     *
     * @return {@code true} if the user's credentials are non-expired; {@code false} otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the user is enabled.
     *
     * @return {@code true} if the user is enabled, {@code false} otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
