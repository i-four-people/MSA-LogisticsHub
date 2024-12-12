package com.logistcshub.hub.common.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@MappedSuperclass
//@EntityListeners(value = {AuditingEntityListener.class})
public abstract class BaseEntity {
    @CreatedDate
    @Column(name = "created_at")
    @Comment("생성일")
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by")
    @Comment("생성자")
    private String createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Comment("수정일")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    @Comment("수정자")
    private String updatedBy;

    @Column(name = "is_deleted")
    @Comment("비활성화 여부")
    private boolean isDeleted = false;

    @Column(name = "deleted_at")
    @Comment("삭제일")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    @Comment("삭제자")
    private String deletedBy;

    public void update(Long userId) {
        updatedAt = LocalDateTime.now();
        updatedBy = Long.toString(userId);
    }

    public void delete(Long userId) {
        deletedAt = LocalDateTime.now();
        deletedBy = Long.toString(userId);
        isDeleted = true;
    }

    public void create(Long userId) {
        createdAt = LocalDateTime.now();
        createdBy = Long.toString(userId);
        update(userId);
    }
}
