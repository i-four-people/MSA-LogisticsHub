package com.logistcshub.user.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;


@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class User {

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

    @Column(nullable = false)
    private boolean isDelete = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column
    private String updatedBy;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private String deletedBy;

    // 유저 생성
    public static User create(String username, String password, String email, String tel, String slackId, UserRoleEnum role) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .tel(tel)
                .slackId(slackId)
                .role(role)
                .createdAt(LocalDateTime.now())
                .createdBy(username)
                .build();
    }

    public void updateUserRole(UserRoleEnum role) {
        this.role = role;
    }

    public void delete(String id) {
        deletedAt = LocalDateTime.now();
        deletedBy = id;
        isDelete = true;
    }
}
