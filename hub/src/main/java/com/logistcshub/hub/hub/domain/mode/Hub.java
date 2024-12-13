package com.logistcshub.hub.hub.domain.mode;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.common.domain.model.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "p_hubs")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hub extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Comment("허브 아이디")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private Area area;

    public void update(Long userId, Hub hub) {
        this.name = hub.getName();
        this.address = hub.getAddress();
        this.lat = hub.getLat();
        this.lng = hub.getLng();
        this.area = hub.getArea();
        update(userId);
    }

    public void updateName(Long userId, String name) {
        this.name = name;
        update(userId);
    }
}
