package com.logistcshub.hub.area.application.config;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.area.domain.model.type.City;
import com.logistcshub.hub.area.domain.repository.AreaRepository;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddAreaListInserter {
    private final AreaRepository areaRepository;

    @PostConstruct
    public void init() {
        List<City> list = Arrays.stream(City.values()).toList();
        UUID userid = UUID.fromString("1f40b195-4bcd-408c-8589-ed4567c5294e");
        List<Area> saveList = new ArrayList<>();
        for(City city : list) {
            if(!areaRepository.existsByCity(city)) {
                Area area = Area.builder().city(city).state(city.getState()).build();
                area.create(userid);
                saveList.add(area);
            }
        }

        areaRepository.saveAll(saveList);

    }
}
