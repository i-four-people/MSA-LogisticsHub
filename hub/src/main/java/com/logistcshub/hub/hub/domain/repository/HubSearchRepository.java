package com.logistcshub.hub.hub.domain.repository;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.hub.application.dtos.HubResponseDto;
import com.logistcshub.hub.hub.presentation.request.type.HubSearchType;
import com.logistcshub.hub.hub.presentation.request.type.SortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

public interface HubSearchRepository {
    Page<HubResponseDto> findAllHubResponseDto(String keyword, HubSearchType type, Pageable pageable, SortType sortBy, boolean isAsc);
}
