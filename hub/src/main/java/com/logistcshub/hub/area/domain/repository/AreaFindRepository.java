package com.logistcshub.hub.area.domain.repository;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.area.presentation.type.AreaSearchType;
import com.logistcshub.hub.area.presentation.type.SortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AreaFindRepository  {
    Page<Area> findAll(String keyword, AreaSearchType type, Pageable pageable, SortType sortBy, boolean isAsc);
}
