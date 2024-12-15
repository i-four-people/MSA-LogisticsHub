package com.logistics.delivery.application.service;

import com.logistics.delivery.application.dto.hub.HubToHubResponse;
import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.domain.model.DeliveryRoute;
import com.logistics.delivery.domain.repository.DeliveryRouteRepository;
import com.logistics.delivery.domain.service.DeliveryRouteService;
import com.logistics.delivery.infrastructure.client.HubClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryRouteServiceImpl implements DeliveryRouteService {

    private final DeliveryRouteRepository deliveryRouteRepository;

    private final HubClient hubClient;

    @Override
    public void createRoutesForDelivery(Delivery delivery) {

        // 출발 허브 -> 도착 허브 이동 경로 조회
        HubToHubResponse hubToHubResponse = hubClient.getHubToHubRoutes(delivery.getOriginHubId(), delivery.getDestinationHubId());

        // 경유지 포함 경로 생성
        List<DeliveryRoute> deliveryRoutes = buildRoutes(delivery, hubToHubResponse);

        // 경로 저장
        deliveryRoutes.forEach(deliveryRouteRepository::save);

    }

    private List<DeliveryRoute> buildRoutes(Delivery delivery, HubToHubResponse hubToHubResponse) {

        List<HubToHubResponse.HubDetail> fullRoute = new ArrayList<>();
        fullRoute.add(hubToHubResponse.getStartHub());
        fullRoute.addAll(hubToHubResponse.getStopover());
        fullRoute.add(hubToHubResponse.getEndHub());

        return IntStream.range(0, fullRoute.size() - 1)
                .mapToObj(i -> {
                    HubToHubResponse.HubDetail start = fullRoute.get(i);
                    HubToHubResponse.HubDetail end = fullRoute.get(i + 1);

                    HubToHubResponse.HubToHubInfo info = findHubToHubInfo(hubToHubResponse, start.getHubId(), end.getHubId());

                    int sequence = i + 1;
                    return DeliveryRoute.create(delivery, sequence, start, end, info);
                }).toList();
    }

    private HubToHubResponse.HubToHubInfo findHubToHubInfo(HubToHubResponse hubToHubResponse, UUID startHubId, UUID endHubId) {
        return hubToHubResponse.getHubToHubInfoList().stream()
                .filter(info -> info.getStartHubId().equals(startHubId) && info.getEndHubId().equals(endHubId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching HubToHubInfo found for the route."));
    }
}
