package com.avos.sipra.sipagri.repositories;

import com.avos.sipra.sipagri.entities.PasswordResetToken;
import com.avos.sipra.sipagri.entities.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);

    PasswordResetToken findBySupervisor(Supervisor user_id);
}