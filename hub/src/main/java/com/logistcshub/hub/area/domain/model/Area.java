package com.logistcshub.hub.area.domain.model;

import com.logistcshub.hub.area.domain.model.type.City;
import com.logistcshub.hub.area.domain.model.type.State;
import com.logistcshub.hub.area.presentation.request.UpdateAreaRequestDto;
import com.logistcshub.hub.common.domain.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "p_areas")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@EntityListeners(AuditingEntityListener.class)
public class Area extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Comment("지역 id")
    private UUID id;

    @Column(nullable = false)
    @Comment("시/도")
    private State state;

    @Column(unique = true, nullable = false)
    @Comment("시/군/구")
    private City city;

    public void updateArea(Long userId, UpdateAreaRequestDto request) {
        this.state = State.findState(request.state());
        this.city = City.findCity(request.city(), this.state);
        update(userId);
    }

}
