package com.logistcshub.hub.hub_transfer.application.dtos;

import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class HubTransferPageDto  {

    private HubTransferPage hubTransferPage;

    public static HubTransferPageDto of(Page<HubTransferPage.HubTransfer> hubTransferPage) {
        return HubTransferPageDto.builder()
                .hubTransferPage(new HubTransferPage(hubTransferPage))
                .build();
    }

    @Getter
    @ToString
    public static class HubTransferPage extends PagedModel<HubTransferPage.HubTransfer> {
        public HubTransferPage(Page<HubTransfer> page) {
            super(new PageImpl<>(
                    page.getContent(),
                    page.getPageable(),
                    page.getTotalElements()
            ));
        }

        @Getter
        @Builder
        public static class HubTransfer {
            private UUID id;
            private String startHub;
            private String endHub;
            private Integer timeTaken;
            private Integer distance;

            @QueryProjection
            public HubTransfer(UUID id, String startHub, String endHub, Integer timeTaken, Integer distance) {
                this.id = id;
                this.startHub = startHub;
                this.endHub = endHub;
                this.timeTaken = timeTaken;
                this.distance = distance;
            }

            public static List<HubTransfer> from(List<com.logistcshub.hub.hub_transfer.domain.model.HubTransfer> hubTransferList) {
                return hubTransferList.stream()
                        .map(HubTransfer::from)
                        .toList();
            }

            public static HubTransfer from(com.logistcshub.hub.hub_transfer.domain.model.HubTransfer hubTransfer) {
                return HubTransfer.builder()
                        .id(hubTransfer.getId())
                        .startHub(hubTransfer.getStartHub().getName())
                        .endHub(hubTransfer.getEndHub().getName())
                        .timeTaken(hubTransfer.getTimeTaken())
                        .distance(hubTransfer.getDistance())
                        .build();
            }
    }


    }
}
