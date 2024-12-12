package com.logistcshub.hub.area.application.service;

import static com.logistcshub.hub.common.exception.application.type.ErrorCode.AREA_NOT_FOUND;
import static com.logistcshub.hub.common.exception.application.type.ErrorCode.FORBIDDEN;

import com.logistcshub.hub.area.application.dtos.AddAreaResponseDto;
import com.logistcshub.hub.area.application.dtos.AreaResponseDto;
import com.logistcshub.hub.area.application.dtos.DeleteAreaResponseDto;
import com.logistcshub.hub.area.application.dtos.UpdateAreaResponseDto;
import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.area.presentation.type.AreaSearchType;
import com.logistcshub.hub.area.presentation.type.SortType;
import com.logistcshub.hub.area.domain.repository.AreaFindRepository;
import com.logistcshub.hub.area.domain.repository.AreaRepository;
import com.logistcshub.hub.area.presentation.request.AddAreaRequestDto;
import com.logistcshub.hub.area.presentation.request.UpdateAreaRequestDto;
import com.logistcshub.hub.common.exception.RestApiException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AreaService {
    private final AreaRepository areaRepository;
    private final AreaFindRepository areaFindRepository;

    @Transactional(readOnly = true)
    public PagedModel<AreaResponseDto> searchAreas(Long userId, String role, String keyword, AreaSearchType type, Pageable pageable, SortType sortBy, boolean isAsc) {
        if(role == null || role.isEmpty()) {
            throw new RestApiException(FORBIDDEN);
        }

        return new PagedModel<>(areaFindRepository.findAll(keyword, type, pageable, sortBy, isAsc).map(AreaResponseDto::of));
    }

    public AddAreaResponseDto addArea(AddAreaRequestDto request, Long userId, String role) {
        validateArea(role);

        Area area = request.toEntity();
        area.create(userId);

        return new AddAreaResponseDto(areaRepository.save(area));
    }

    public UpdateAreaResponseDto updateArea(UUID id, UpdateAreaRequestDto request, Long userId, String role) {
        validateArea(role);

        Area area = findById(id);
        area.updateArea(userId, request);

        return new UpdateAreaResponseDto(areaRepository.save(area));
    }

    public DeleteAreaResponseDto deleteArea(UUID id, Long userId, String role) {
        validateArea(role);

        Area area = findById(id);
        area.delete(userId);

        return new DeleteAreaResponseDto(areaRepository.save(area));
    }

    private void validateArea(String role) {
        if(role == null || !role.equals("MASTER")) {
            throw new RestApiException(FORBIDDEN);
        }
    }

    private Area findById(UUID id) {
        return areaRepository.findById(id).orElseThrow(() -> new RestApiException(AREA_NOT_FOUND));
    }

    public AreaResponseDto getArea(UUID id, Long userId, String role) {
        return AreaResponseDto.of(findById(id));
    }
}
