package com.avos.sipra.sipagri.entities;

import com.avos.sipra.sipagri.enums.HumanGender;
import com.avos.sipra.sipagri.enums.MaritalStatus;
import com.avos.sipra.sipagri.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Planters")
public class Planter {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "planter_seq")
    @SequenceGenerator(name = "planter_seq", sequenceName = "planter_seq", allocationSize = 1)
    private Long id;

    @Column(name = "lastname", nullable = false, length = 50)
    private String lastname;

    @Column(name = "firstname", nullable = false, length = 50)
    private String firstname;

    @Column(name = "birthday", nullable = false)
    private Date birthday;

    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private HumanGender gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "marital_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "children_number", nullable = false)
    private Integer childrenNumber;

    @Column(name = "village", nullable = false, length = 100)
    private String village;

    @ManyToOne
    @JoinColumn(name = "supervisor_id")
    private Supervisor supervisor;

    @OneToMany(mappedBy = "planter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plantation> plantations;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}