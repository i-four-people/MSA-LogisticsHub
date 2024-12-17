package com.logistcshub.hub.hub_transfer.domain.model;

import com.logistcshub.hub.common.domain.model.BaseEntity;
import com.logistcshub.hub.hub.domain.mode.Hub;
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
@Table(name = "p_hub_transfers")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class HubTransfer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_hub_id")
    private Hub startHub;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_hub_id")
    private Hub endHub;

    @Comment("예상 이동 시간(분)")
    @Column(name = "time_taken")
    private Integer timeTaken;

    @Comment("예상 이동 거리(km)")
    @Column(name = "distance")
    private Integer distance;

    public void updateHubTransfer(Hub startHub, Hub endHub, Integer timeTaken, Integer distance, Long userId) {
        this.startHub = startHub;
        this.endHub = endHub;
        this.timeTaken = timeTaken;
        this.distance = distance;
        update(userId);
    }

}
