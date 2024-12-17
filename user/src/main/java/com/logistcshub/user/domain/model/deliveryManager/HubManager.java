package com.logistcshub.user.domain.model.deliveryManager;

import com.logistcshub.user.domain.model.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Table(name = "p_hub_managers")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class HubManager {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private UUID hubId;

    public static HubManager create(User user, UUID hubId) {
        return HubManager.builder()
                .user(user)
                .hubId(hubId)
                .build();
    }
}
