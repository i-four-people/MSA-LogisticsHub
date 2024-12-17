package com.logistics.delivery.application.service;

import com.logistics.delivery.application.dto.company.CompanyResponse;
import com.logistics.delivery.application.dto.event.consume.SlackCreateConsume;
import com.logistics.delivery.application.dto.gemini.GeminiResponseDTO;
import com.logistics.delivery.application.dto.hub.HubResponse;
import com.logistics.delivery.application.dto.order.OrderDetailResponse;
import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.domain.model.DeliveryRoute;
import com.logistics.delivery.domain.repository.DeliveryRepository;
import com.logistics.delivery.domain.repository.DeliveryRouteRepository;
import com.logistics.delivery.domain.service.SlackService;
import com.logistics.delivery.infrastructure.client.CompanyClient;
import com.logistics.delivery.infrastructure.client.HubClient;
import com.logistics.delivery.infrastructure.client.OrderClient;
import com.logistics.delivery.presentation.exception.BusinessException;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.logistics.delivery.presentation.exception.ErrorCode.INTERNAL_ERROR;
import static com.logistics.delivery.presentation.exception.ErrorCode.INVALID_INPUT;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackServiceImpl implements SlackService {

    private final OrderClient orderClient;
    private final DeliveryRepository deliveryRepository;
    private final HubClient hubClient;
    private final DeliveryRouteRepository deliveryRouteRepository;
    private final CompanyClient companyClient;

    // 슬랙 설정
    @Value("${slack.webhook.url}")
    private String webhookUrl;

    @Value("${gemini.apiKey}")
    private String geminiApiKey;

    @Value("${gemini.url}")
    private String geminiUrl;

    private final RestTemplate restTemplate;

    private final Slack slack = Slack.getInstance();

    @Override
    public void createSlackMessage(SlackCreateConsume slackCreateConsume) {
        Delivery delivery = deliveryRepository.findById(slackCreateConsume.deliveryId()).orElseThrow(
                () -> new BusinessException(INVALID_INPUT)
        );

        List<Delivery> deliveries = deliveryRepository.findByOrderId(delivery.getOrderId());
        Map<UUID, DeliveryRoute> routeMap = new HashMap<>();
        int[] totalTime = {0};

        deliveries.forEach(deli -> {
            DeliveryRoute deliveryRoute = deliveryRouteRepository.findByDeliveryId(deli.getId()).orElseThrow(() ->
                    new BusinessException(INVALID_INPUT));
            routeMap.put(deli.getOriginHubId(), deliveryRoute);
            totalTime[0] += (int)deliveryRoute.getEstimatedDuration();
        });


        Set<UUID> hubIds = new HashSet<>();
        for(Delivery del : deliveries) {
            hubIds.add(del.getOriginHubId());
            hubIds.add(del.getDestinationHubId());
        }

        Map<UUID, HubResponse> hubMap = new HashMap<>();
        hubClient.getHubsToHubIds(hubIds.stream().toList()).forEach(hubResponse -> hubMap.put(hubResponse.id(), hubResponse));

        List<HubResponse> sortHubs = new ArrayList<>();
        sortHubs.add(hubMap.get(slackCreateConsume.startHubId()));

        while(sortHubs.size() < hubIds.size()) {
            UUID nextHubId = routeMap.get(sortHubs.getLast().id()).getEndHubId();
            sortHubs.add(hubMap.get(nextHubId));
        }

        OrderDetailResponse orderDetailResponse = orderClient.orderDetails(delivery.getOrderId()).data();
        CompanyResponse companyResponse = companyClient.findCompanyById(orderDetailResponse.recipientCompanyId());

        StringBuilder sb = new StringBuilder();

        for(int i=0; i<sortHubs.size(); i++) {
            sb.append(sortHubs.get(i).name()).append(" ");
        }

        String stopover = sb.toString().trim();

        String text = """
                    다음 주문을 보고 최종 발송 시한을 알려줘. 몇 월 며칠 몇 시 까지 보내면 되는지.
                    만약 경유지가 있는 경우 하나의 경유지 당 3시간의 대기 시간이 있어.
                    대기 시간을 제외한 예상 이동 시간은 
                    """ + totalTime[0] +
                """ 
                분이야.
                주문 정보를 포함한 최종 발송 시한 날짜와 시간을 추가해서 보내줘
                주문 정보 + 최종 발송 시한 : 00년 00월 00일 00시 00분
                형태로. 계산 과정 필요없어. 밑에 주문 형식에서 최종 발송 시한을 추가한 내용만 보내.
                
                주문 번호 : """
                + orderDetailResponse.id() + "\n" +
                "주문자 정보 : " + companyResponse.companyName() + "\n" +
                "상품 정보 : " + orderDetailResponse.productName() + orderDetailResponse.quantity() + "개\n" +
                "요청 사항 : " + orderDetailResponse.requestNotes() + "\n" +
                "발송지 : " + hubMap.get(sortHubs.getFirst().name()) + "\n" +
                "경유지 : " + stopover + "\n" +
                "도착지 : " + companyResponse.address() + "\n" +
                "담당 배송 허브 : " + hubMap.get(slackCreateConsume.startHubId()).name() + " 에서 " +
                hubMap.get(slackCreateConsume.startHubId()).name() + " 까지\n" +
                "배송담당자 : " + slackCreateConsume.deliveryManagerName() + " / <@" + slackCreateConsume.deliveryManagerSlackId() + ">";


        sendToSlack(extracted(text));
    }

    public String extracted(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Body 생성
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", text);
            content.put("parts", new Object[]{part});
            requestBody.put("contents", new Object[]{content});

            System.out.println(requestBody.toString());

            // HttpEntity 생성
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // URI 생성
            URI targetUrl = UriComponentsBuilder.fromUriString(geminiUrl)
                    .queryParam("key", geminiApiKey)
                    .encode(StandardCharsets.UTF_8)
                    .build()
                    .toUri();

            // RestTemplate 요청
            ResponseEntity<GeminiResponseDTO> responseEntity = restTemplate.exchange(
                    targetUrl,
                    HttpMethod.POST,
                    requestEntity,
                    GeminiResponseDTO.class
            );

            // 응답 처리
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                GeminiResponseDTO responseBody = responseEntity.getBody();
                //System.out.println("Response: " + responseBody.getCandidates().getFirst().getContent().getParts().getFirst().getText());
                return responseBody == null ? "" : responseBody.getCandidates().getFirst().getContent().getParts().getFirst().getText();
            } else {
                System.err.println("Request failed with status: " + responseEntity.getStatusCode());
                return "";
            }
        } catch (HttpClientErrorException e) {
            // 클라이언트 오류 (4xx)
            throw new BusinessException(INVALID_INPUT);
        } catch (HttpServerErrorException e) {
            // 서버 오류 (5xx)
            throw new BusinessException(INTERNAL_ERROR);
        } catch (RestClientException e) {
            // 일반적인 RestTemplate 오류 처리
            throw new BusinessException(INTERNAL_ERROR);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new BusinessException(INTERNAL_ERROR);
        }

    }

    public WebhookResponse sendToSlack(String paramText) {
        Payload payload = Payload.builder().text(paramText).build();        // Slack에 전달할 Payload 구성
        WebhookResponse response;
        try {
            response = slack.send(webhookUrl, payload);                     // Slack 메시지 전송
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
