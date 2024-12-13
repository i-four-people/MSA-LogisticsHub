package com.logistcshub.user.domain.model;

import com.logistcshub.user.infrastructure.common.AuditEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class User extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String tel;

    @Column(nullable = false)
    private String slackId;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    // 유저 생성
    public static User create(String username, String password, String email, String tel, String slackId, UserRoleEnum role) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .tel(tel)
                .slackId(slackId)
                .role(role)
                .build();
    }

    public void updateUserRole(UserRoleEnum role) {
        this.role = role;
    }
}
