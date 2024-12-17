package com.logistcshub.hub.hub_transfer.infrastructure;

import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub_transfer.application.dtos.HubTransferPageDto;
import com.logistcshub.hub.hub_transfer.application.dtos.QHubTransferPageDto_HubTransferPage_HubTransfer;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;
import com.logistcshub.hub.hub_transfer.domain.repository.HubTransferRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


import java.util.ArrayList;
import java.util.List;

import static com.logistcshub.hub.hub_transfer.domain.model.QHubTransfer.hubTransfer;


@Repository
@RequiredArgsConstructor
public class HubTransferRepositoryImpl implements HubTransferRepository {

    private final JpaHubTransferRepository jpaHubTransferRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public HubTransfer save(HubTransfer hubTransfer) {
        return jpaHubTransferRepository.save(hubTransfer);
    }

    @Override
    public boolean existsByStartHubAndEndHubAndIsDeletedFalse(Hub startHub, Hub endHub) {
        return jpaHubTransferRepository.existsByStartHubAndEndHubAndIsDeletedFalse(startHub, endHub);
    }

    @Override
    public Optional<HubTransfer> findByIdAndIsDeletedFalse(UUID id) {
        return jpaHubTransferRepository.findByIdAndIsDeletedFalse(id);
    }

    @Override
    public void delete(HubTransfer hubTransfer) {
        jpaHubTransferRepository.delete(hubTransfer);
    }

    public boolean existsByStartHubAndEndHubAndDeletedFalse(Hub startHub, Hub endHub) {
        return jpaHubTransferRepository.existsByStartHubAndEndHubAndIsDeletedFalse(startHub, endHub);
    }

    @Override
    public HubTransferPageDto findAll(List<UUID> idList, Predicate predicate, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder(predicate);

        if(idList != null && !idList.isEmpty()) {
            builder.and(hubTransfer.id.in(idList));
        }

        builder.and(hubTransfer.isDeleted.eq(false));

        JPAQuery<HubTransferPageDto.HubTransferPage.HubTransfer> query = queryFactory
                .select(new QHubTransferPageDto_HubTransferPage_HubTransfer(
                        hubTransfer.id,
                        hubTransfer.startHub.name,
                        hubTransfer.endHub.name,
                        hubTransfer.timeTaken,
                        hubTransfer.distance
                ))
                .from(hubTransfer)
                .join(hubTransfer.startHub)
                .join(hubTransfer.endHub)
                .where(builder)
                .orderBy(buildOrderBy(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<HubTransferPageDto.HubTransferPage.HubTransfer> content = query.fetch();

        // 전체 카운트
        Long total = queryFactory
                .select(hubTransfer.count())
                .from(hubTransfer)
                .where(builder)
                .fetchOne();

        // Page 객체 생성
        Page<HubTransferPageDto.HubTransferPage.HubTransfer> page = new PageImpl<>(content, pageable, total != null ? total : 0);

        // HubTransferPageDto 생성 및 반환
        return HubTransferPageDto.builder()
                .hubTransferPage(new HubTransferPageDto.HubTransferPage(page)).build();
    }

    @Override
    public List<HubTransfer> saveAll(List<HubTransfer> saveList) {
        return jpaHubTransferRepository.saveAll(saveList);
    }

    @Override
    public List<HubTransfer> findByIsDeletedFalse() {
        return jpaHubTransferRepository.findByIsDeletedFalse();
    }

    @Override
    public Optional<HubTransfer> findByStartHubIdAndEndHubIdAndIsDeletedFalse(UUID startHubId, UUID endHubId) {
        return jpaHubTransferRepository.findByStartHubIdAndEndHubIdAndIsDeletedFalse(startHubId, endHubId);
    }

    private OrderSpecifier<?>[] buildOrderBy(Sort sort) {
        if (sort.isEmpty()) {
            // 기본 정렬 기준 추가 (예: id 기준 오름차순)
            return new OrderSpecifier<?>[] { hubTransfer.id.desc() }; // 기본적으로 ID로 오름차순 정렬
        }

        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        for (Sort.Order order : sort) {
            // 필드 타입에 따라 PathBuilder를 다르게 사용
            if (order.getProperty().equals("startHub")) {
                OrderSpecifier<?> orderSpecifier = order.isAscending() ? hubTransfer.startHub.name.asc() : hubTransfer.startHub.name.desc();
                orderSpecifiers.add(orderSpecifier);
            } else if (order.getProperty().equals("timeTaken")) {
                OrderSpecifier<?> orderSpecifier = order.isAscending() ? hubTransfer.timeTaken.asc() : hubTransfer.timeTaken.desc();
                orderSpecifiers.add(orderSpecifier);
            } else if (order.getProperty().equals("distance")) {
                // 다른 필드에 대해서도 처리
                OrderSpecifier<?> orderSpecifier = order.isAscending() ? hubTransfer.distance.asc() : hubTransfer.distance.desc();
                orderSpecifiers.add(orderSpecifier);
            } else if(order.getProperty().equals("id")) {
                OrderSpecifier<?> orderSpecifier = order.isAscending() ? hubTransfer.id.asc() : hubTransfer.id.desc();
                orderSpecifiers.add(orderSpecifier);
            } else {
                OrderSpecifier<?> orderSpecifier = order.isAscending() ? hubTransfer.endHub.name.asc() : hubTransfer.endHub.name.desc();
                orderSpecifiers.add(orderSpecifier);
            }
        }
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

}
