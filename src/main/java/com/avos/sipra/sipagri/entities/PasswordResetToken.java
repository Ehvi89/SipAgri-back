package com.avos.sipra.sipagri.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "password_reset_token")
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_reset_token_seq")
    @SequenceGenerator(name = "password_reset_token_seq", sequenceName = "password_reset_token_seq", allocationSize = 1)
    private Long id;

    @Column(name = "token")
    private String token;

    @OneToOne(targetEntity = Supervisor.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "supervisor_id")
    private Supervisor supervisor;

    @Column(name = "expiry_date")
    private Date expiryDate;
}