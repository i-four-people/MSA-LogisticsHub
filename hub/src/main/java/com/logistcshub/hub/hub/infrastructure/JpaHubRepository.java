package com.logistcshub.hub.hub.infrastructure;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.hub.domain.mode.Hub;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaHubRepository extends JpaRepository<Hub, UUID> {
    @Query("select h from Hub h join fetch h.area where h.id = :id and h.isDeleted is false")
    Optional<Hub> findByIdWithAreaAndIsDeletedFalse(UUID id);

    boolean existsByAreaAndAddressAndIsDeletedFalse(Area area, String address);

    Optional<Hub> findByIdAndIsDeletedFalse(UUID id);

    List<Hub> findByIsDeletedFalse();

    @Query(value = "select h.* " +
            " from p_hubs h " +
            " join p_areas a on h.area_id = a.id  " +
            " where a.id = :areaId and h.is_deleted is false " +
            " order by earth_distance( " +
            "        ll_to_earth(h.lat, h.lng), " +
            "        ll_to_earth(:lat, :lng) " +
            ") limit 1", nativeQuery = true)
    Optional<Hub> findByAreaAndIsDeletedFalse(UUID areaId, double lat, double lng);


    @Query(value = "select h.* " +
            " from p_hubs h " +
            " join p_areas a on h.area_id = a.id " +
            " where a.id in(:areaList) and h.is_deleted is false " +
            " order by earth_distance( " +
            "        ll_to_earth(h.lat, h.lng), " +
            "        ll_to_earth(:lat, :lng) " +
            ") limit 1", nativeQuery = true)
    Optional<Hub> findByAreaInAndIsDeletedFalse(List<UUID> areaList, double lat, double lng);
}
