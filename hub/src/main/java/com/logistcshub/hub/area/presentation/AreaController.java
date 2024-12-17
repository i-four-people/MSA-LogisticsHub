package com.logistcshub.hub.area.presentation;

import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.SUCCESS_CREATE_AREA;
import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.SUCCESS_DELETE_AREA;
import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.SUCCESS_GET_AREA;
import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.SUCCESS_SEARCH_AREA;
import static com.logistcshub.hub.common.domain.model.type.ResponseMessage.SUCCESS_UPDATE_AREA;

import com.logistcshub.hub.area.application.dtos.AddAreaResponseDto;
import com.logistcshub.hub.area.application.dtos.AreaResponseDto;
import com.logistcshub.hub.area.application.dtos.DeleteAreaResponseDto;
import com.logistcshub.hub.area.application.dtos.UpdateAreaResponseDto;
import com.logistcshub.hub.area.application.service.AreaService;
import com.logistcshub.hub.area.presentation.type.AreaSearchType;
import com.logistcshub.hub.area.presentation.type.SortType;
import com.logistcshub.hub.area.presentation.request.AddAreaRequestDto;
import com.logistcshub.hub.area.presentation.request.UpdateAreaRequestDto;
import com.logistcshub.hub.common.domain.model.dtos.SuccessResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @PostMapping
    public ResponseEntity<SuccessResponse<AddAreaResponseDto>> addArea(@RequestBody AddAreaRequestDto request,
                                                                       @RequestHeader(value = "X-User-Id") Long userId,
                                                                       @RequestHeader(value = "X-User-Role") String role) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_CREATE_AREA, areaService.addArea(request, userId, role)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<UpdateAreaResponseDto>> updateArea(@RequestBody UpdateAreaRequestDto request,
                                                                             @RequestHeader(value = "X-User-Id") Long userId,
                                                                             @RequestHeader(value = "X-User-Role") String role,
                                                                             @PathVariable UUID id) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_UPDATE_AREA, areaService.updateArea(id, request, userId, role)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<DeleteAreaResponseDto>> deleteArea(@RequestHeader(value = "X-User-Id") Long userId,
                                                                             @RequestHeader(value = "X-User-Role") String role,
                                                                             @PathVariable UUID id) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_DELETE_AREA, areaService.deleteArea(id, userId, role)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<AreaResponseDto>> getArea(@RequestHeader(value = "X-User-Id") Long userId,
                                                                    @RequestHeader(value = "X-User-Role") String role,
                                                                             @PathVariable UUID id) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_GET_AREA, areaService.getArea(id, userId, role)));
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<PagedModel<AreaResponseDto>>> searchAreas(
            @RequestHeader(value = "X-User-Id") Long userId,
            @RequestHeader(value = "X-User-Role") String role,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "type", defaultValue = "ALL") AreaSearchType type,
            @PageableDefault Pageable pageable,
            @RequestParam(name = "sortBy", defaultValue = "CREATEDAT") SortType sortBy,
            @RequestParam(name = "isAsc", defaultValue = "true") boolean isAsc) {
        return ResponseEntity.ok().body(
                SuccessResponse.of(SUCCESS_SEARCH_AREA, areaService.searchAreas(userId, role, keyword, type, pageable, sortBy, isAsc))
        );
    }

}
