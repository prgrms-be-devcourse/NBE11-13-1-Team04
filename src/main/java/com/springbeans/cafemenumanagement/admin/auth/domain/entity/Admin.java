package com.springbeans.cafemenumanagement.admin.auth.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Table(name = "admin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, length = 60)
    private String password;

    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
