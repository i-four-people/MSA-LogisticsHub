package com.logistcshub.user.infrastructure.repository;

import com.logistcshub.user.presentation.response.deliveryManager.DeliveryManagerDto;
import com.logistcshub.user.presentation.response.QDeliveryManagerDto;
import com.logistcshub.user.domain.model.deliveryManager.DeliveryManagerType;
import com.logistcshub.user.domain.model.deliveryManager.DeliveryStatus;
import com.logistcshub.user.domain.model.user.UserRoleEnum;
import com.logistcshub.user.domain.repository.DeliveryManagerRepositoryCustom;
import com.logistcshub.user.presentation.request.deliveryManager.DeliSearchRequest;
import com.logistcshub.user.presentation.response.deliveryManager.DeliSearchResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.logistcshub.user.domain.model.deliveryManager.QDeliveryManager.deliveryManager;
import static com.logistcshub.user.domain.model.deliveryManager.QHubManager.hubManager;

@Repository
public class DeliveryManagerRepositoryImpl implements DeliveryManagerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DeliveryManagerRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<DeliSearchResponse> findAllDeliveryPerson(Pageable pageable, DeliSearchRequest condition,
                                                            Long userId, UserRoleEnum role) {
        JPAQuery<DeliveryManagerDto> query = queryFactory
                .select(new QDeliveryManagerDto(
                        deliveryManager.id,
                        deliveryManager.ksuid,
                        deliveryManager.userId,
                        deliveryManager.hubId,
                        deliveryManager.deliveryManagerType,
                        deliveryManager.status,
                        deliveryManager.createdAt,
                        deliveryManager.createdBy,
                        deliveryManager.updatedAt,
                        deliveryManager.updatedBy
                ))
                .from(deliveryManager)
                .leftJoin(hubManager)
                .on(deliveryManager.hubId.eq(hubManager.hubId))
                .where(hubManagerFilter(role, userId),
                        userIdEq(condition.userId()),
                        deliveryManagerTypeEq(condition.deliveryManagerType()),
                        hubIdEq(condition.hubId()),
                        statusEq(condition.deliveryStatus()),
                        deliveryManager.isDelete.eq(false));

        if (pageable.getSort().isEmpty()) {
            query.orderBy(deliveryManager.id.desc());
        } else {
            Sort sort = pageable.getSort();
            sort.forEach(order -> {
                String property = order.getProperty();
                Sort.Direction direction = order.getDirection();

                if ("createdAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? deliveryManager.createdAt.asc() : deliveryManager.createdAt.desc());
                } else if ("updatedAt".equals(property)) {
                    query.orderBy(direction.isAscending() ? deliveryManager.updatedAt.asc() : deliveryManager.updatedAt.desc());
                }
            });
        }

        List<DeliveryManagerDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(deliveryManager.count())
                .from(deliveryManager)
                .where(userIdEq(condition.userId()),
                        deliveryManagerTypeEq(condition.deliveryManagerType()),
                        hubIdEq(condition.hubId()),
                        statusEq(condition.deliveryStatus()),
                        deliveryManager.isDelete.eq(false));

        DeliSearchResponse deliSearchResponse = new DeliSearchResponse(content);

        return PageableExecutionUtils.getPage(Collections.singletonList(deliSearchResponse), pageable, countQuery::fetchOne);
    }

    // 허브 매니저인 경우 담당 허브 정보만 조회 가능
    private BooleanExpression hubManagerFilter(UserRoleEnum role, Long userId) {
        if (role == UserRoleEnum.HUB_MANAGER) {
            return hubManager.user.id.eq(userId);
        }
        return null; // 허브 매니저가 아닌 경우 조건 없음
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? deliveryManager.userId.eq(userId) : null;
    }

    private BooleanExpression deliveryManagerTypeEq(DeliveryManagerType type) {
        return type != null ? deliveryManager.deliveryManagerType.eq(type) : null;
    }

    private BooleanExpression hubIdEq(UUID hubId) {
        return hubId != null ? deliveryManager.hubId.eq(hubId) : null;
    }

    private BooleanExpression statusEq(DeliveryStatus status) {
        return status != null ? deliveryManager.status.eq(status) : null;
    }
}
