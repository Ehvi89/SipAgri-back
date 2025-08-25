package com.avos.sipra.sipagri.security;

import com.avos.sipra.sipagri.entities.Supervisor;
import com.avos.sipra.sipagri.repositories.SupervisorRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SupervisorRepository supervisorRepository;

    public CustomUserDetailsService(SupervisorRepository supervisorRepository) {
        this.supervisorRepository = supervisorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Supervisor supervisor = supervisorRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        return new CustomUserDetails(supervisor);
    }
}

