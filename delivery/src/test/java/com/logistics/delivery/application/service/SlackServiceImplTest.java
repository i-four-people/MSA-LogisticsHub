package com.logistics.delivery.application.service;

import com.logistics.delivery.application.dto.company.CompanyResponse;
import com.logistics.delivery.application.dto.event.EventType;
import com.logistics.delivery.application.dto.event.consume.SlackCreateConsume;
import com.logistics.delivery.application.dto.gemini.GeminiResponseDTO;
import com.logistics.delivery.application.dto.hub.HubResponse;
import com.logistics.delivery.application.dto.order.OrderDetailResponse;
import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.domain.model.DeliveryRoute;
import com.logistics.delivery.domain.repository.DeliveryRepository;
import com.logistics.delivery.domain.repository.DeliveryRouteRepository;
import com.logistics.delivery.infrastructure.client.CompanyClient;
import com.logistics.delivery.infrastructure.client.HubClient;
import com.logistics.delivery.infrastructure.client.OrderClient;
import com.logistics.delivery.infrastructure.repository.DeliveryRepositoryImpl;
import com.logistics.delivery.infrastructure.repository.DeliveryRouteRepositoryImpl;
import com.logistics.delivery.infrastructure.repository.JpaDeliveryRepository;
import com.logistics.delivery.presentation.response.ApiResponse;
import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.*;

import static com.logistics.delivery.presentation.response.MessageType.RETRIEVE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@TestPropertySource("classpath:application-test.yml")
@RestClientTest(value = SlackServiceImplTest.class)
class SlackServiceImplTest {

    @Mock private OrderClient orderClient;
    @Mock private DeliveryRepository deliveryRepository;
    @Mock private HubClient hubClient;
    @Mock private DeliveryRouteRepository deliveryRouteRepository;
    @Mock private CompanyClient companyClient;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private MockRestServiceServer mockServer;

    @Mock private Slack slack;

    @InjectMocks private
    SlackServiceImpl slackService;

    // 슬랙 설정
    @Value("${slack.webhook.url}")
    private String webhookUrl;

    @Value("${gemini.apiKey}")
    private String geminiApiKey;

    @Value("${gemini.url}")
    private String geminiUrl;


    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        ReflectionTestUtils.setField(slackService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(slackService, "webhookUrl", webhookUrl);
        ReflectionTestUtils.setField(slackService, "geminiApiKey", geminiApiKey);
        ReflectionTestUtils.setField(slackService, "geminiUrl", geminiUrl);
}

    @Test
    void testCreateSlackMessage() throws IOException {
        UUID deliveryId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID startHubId = UUID.randomUUID();
        UUID endHubId = UUID.randomUUID();
        Long deliveryManagerId = 1L;
        String deliveryManagerName = "John Doe";
        String deliveryManagerSlackId = "U1234567890"; // 수정된 부분
        String productName = "상품 명";
        String companyName = "회사 명";
        String companyAddress = "회사 주소";
        UUID[] hubIds = new UUID[] {startHubId, UUID.randomUUID(), UUID.randomUUID(), endHubId};
        String[] hubNames = new String[] {"서울특별시 센터", "경기 남부 센터", "대구광역시 센터", "부산광역시 센터"};
        Delivery delivery = Delivery.builder()
                .orderId(orderId)
                .build();

        List<DeliveryRoute> deliveryRoutes = new ArrayList<>();
        Map<UUID, HubResponse> hubMap = new HashMap<>();

        for(int i = 0; i < hubIds.length-1; i++) {
            deliveryRoutes.add(
                    DeliveryRoute.builder()
                            .sequence(i+1)
                            .estimatedDuration(30 * (i + 1))
                            .deliveryId(deliveryId)
                            .startHubId(hubIds[i])
                            .endHubId(hubIds[i+1])
                            .build()
            );
            hubMap.put(hubIds[i], new HubResponse(hubIds[i], hubNames[i]));
        }

        hubMap.put(hubIds[hubIds.length-1], new HubResponse(hubIds[hubIds.length-1], hubNames[hubNames.length-1]));

        // Mock behavior
        SlackCreateConsume slackCreateConsume = new SlackCreateConsume(null, deliveryId, deliveryManagerId, deliveryManagerName, deliveryManagerSlackId, startHubId, endHubId);
        OrderDetailResponse orderDetailResponse = OrderDetailResponse.test(UUID.randomUUID(), deliveryId, companyName, productName, 5, "12월 30일 12시까지 도착해야합니다.");
        CompanyResponse companyResponse = new CompanyResponse(UUID.randomUUID(), companyName, null, companyAddress);

        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));
        when(deliveryRouteRepository.findByDeliveryId(any())).thenReturn(deliveryRoutes);
        when(hubClient.getHubsToHubIds(anyList())).thenReturn(ApiResponse.success(RETRIEVE,hubMap.values().stream().toList()));
        when(orderClient.orderDetails(any())).thenReturn(ApiResponse.success(RETRIEVE, orderDetailResponse));
        when(companyClient.findCompanyById(any())).thenReturn(ApiResponse.success(RETRIEVE,companyResponse));

        String uri = geminiUrl + "?key=" + geminiApiKey;

        GeminiResponseDTO.Part part = new GeminiResponseDTO.Part();
        part.setText("test");
        GeminiResponseDTO.Content content = new GeminiResponseDTO.Content();
        content.setParts(List.of(part));
        GeminiResponseDTO.Candidate candidate = new GeminiResponseDTO.Candidate();
        candidate.setContent(content);

        GeminiResponseDTO geminiResponseDTO = new GeminiResponseDTO();
        geminiResponseDTO.setCandidates(List.of(candidate));

        mockServer.expect(requestTo(uri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        "{\n" +
                                "    \"candidates\": [\n" +
                                "        {\n" +
                                "            \"content\": {\n" +
                                "                \"parts\": [\n" +
                                "                    {\n" +
                                "\"text\": \"주문 번호 : 1\\n" +
                                "주문자 정보 : John Doe / U1234567890\\n" +
                                "상품 정보 : 상품 명\\n" +
                                "요청 사항 : 12월 30일 12시까지 도착해야합니다.\\n" +
                                "발송지 : 서울특별시 센터\\n" +
                                "경유지 : 경기 남부 센터, 대구광역시 센터, 부산광역시 센터\\n" +
                                "도착지 : 회사 주소\\n" +
                                "담당 경유지 : 경기 남부 센터 에서 대구광역시 센터 까지\\n" +
                                "배송담당자 : John Doe / <@U1234567890>\\n\\n" +
                                "최종 발송 시한 : 2023년 12월 29일 18시 00분\\n\"\n" +
                                "                    }\n" +
                                "                ],\n" +
                                "                \"role\": \"model\"\n" +
                                "            },\n" +
                                "            \"finishReason\": \"STOP\",\n" +
                                "            \"avgLogprobs\": -0.009802425191515968\n" +
                                "        }\n" +
                                "    ],\n" +
                                "    \"usageMetadata\": {\n" +
                                "        \"promptTokenCount\": 333,\n" +
                                "        \"candidatesTokenCount\": 168,\n" +
                                "        \"totalTokenCount\": 501\n" +
                                "    },\n" +
                                "    \"modelVersion\": \"gemini-1.5-flash\"\n" +
                                "}\n",
                        MediaType.APPLICATION_JSON
                ));

        // Call the method
        slackService.createSlackMessage(slackCreateConsume);
    }

    @Test
    void testSendToSlack() throws IOException {
        // Arrange
        String message = "Test Message";
        // Act
        WebhookResponse response = slackService.sendToSlack(message);
        // Assert
        System.out.println(response.getBody().toString());
    }

    @Test
    void getGeminiTest() {

        String message =
                "다음 주문을 보고 최종 발송 시한을 알려줘. 몇 월 며칠 몇 시 까지 보내면 되는지.\n" +
                        "만약 경유지가 있는 경우 하나의 경유지 당 3시간의 대기 시간이 있어.\n" +
                        "대기 시간을 제외한 예상 이동 시간은 240 분이야.\n" +
                        "주문 정보를 포함한 최종 발송 시한 날짜와 시간을 추가해서 보내줘\n" +
                        "주문 정보 + 최종 발송 시한 : 00년 00월 00일 00시 00분\n" +
                        "형태로. 계산 과정 필요없어. 밑에 주문 형식에서 최종 발송 시한을 추가한 내용만 보내.\n" +
                        "\n" +
                        "주문 번호 : 1\n" +
                        "주문자 정보 : 김말숙 / msk@seafood.world\n" +
                        "상품 정보 : 마른 오징어 50박스\n" +
                        "요청 사항 : 12월 12일 3시까지는 보내주세요!\n" +
                        "발송지 : 경기 북부 센터\n" +
                        "경유지 : 대전광역시 센터, 부산광역시 센터\n" +
                        "도착지 : 부산시 사하구 낙동대로 1번길 1 해산물월드\n" +
                        "배송담당자 : 고길동 / kdk@sparta.world\n";

        String uri = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent" + "?key=" + "AIzaSyADtRE_Gtg0_sAJuqQKp-2m6y4ZEfbPUQY";
        String response = "{\n" +
                "    \"candidates\": [\n" +
                "        {\n" +
                "            \"content\": {\n" +
                "                \"parts\": [\n" +
                "                    {\n" +
                "                        \"text\": \"주문 번호 : 1\\n주문자 정보 : 김말숙 / msk@seafood.world\\n상품 정보 : 마른 오징어 50박스\\n요청 사항 : 12월 12일 3시까지는 보내주세요!\\n발송지 : 경기 북부 센터\\n경유지 : 대전광역시 센터, 부산광역시 센터\\n도착지 : 부산시 사하구 낙동대로 1번길 1 해산물월드\\n배송담당자 : 고길동 / kdk@sparta.world\\n\\n최종 발송 시한 : 2023년 12월 11일 21시 00분\\n\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"role\": \"model\"\n" +
                "            },\n" +
                "            \"finishReason\": \"STOP\",\n" +
                "            \"avgLogprobs\": -0.009802425191515968\n" +
                "        }\n" +
                "    ],\n" +
                "    \"usageMetadata\": {\n" +
                "        \"promptTokenCount\": 333,\n" +
                "        \"candidatesTokenCount\": 168,\n" +
                "        \"totalTokenCount\": 501\n" +
                "    },\n" +
                "    \"modelVersion\": \"gemini-1.5-flash\"\n" +
                "}\n";

        mockServer.expect(requestTo(uri)).andExpect(method(HttpMethod.POST)).andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        String text = slackService.extracted(message);


        System.out.println(text);

    }


}