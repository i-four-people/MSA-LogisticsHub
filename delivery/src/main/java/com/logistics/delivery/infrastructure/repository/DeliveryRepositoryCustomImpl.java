package com.logistics.delivery.infrastructure.repository;

import com.logistics.delivery.application.dto.SearchParameter;
import com.logistics.delivery.domain.model.Delivery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static com.logistics.delivery.domain.model.QDelivery.delivery;

@RequiredArgsConstructor
public class DeliveryRepositoryCustomImpl implements DeliveryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Delivery> searchDeliveries(SearchParameter searchParameter) {

        // 페이징 처리
        int skip = (searchParameter.getPage() - 1) * searchParameter.getLimit();

        List<Delivery> results = queryFactory.selectFrom(delivery)
                .where(filterDeliveries(searchParameter)
                        .and(delivery.isDelete.isFalse()))
                .orderBy(getOrderSpecifier(searchParameter))
                .offset(skip)
                .limit(searchParameter.getLimit())
                .fetch();

        long totalCount = getDeliveriesTotalCount(searchParameter);

        return new PageImpl<>(results, searchParameter.getPageable(), totalCount);
    }

    private long getDeliveriesTotalCount(SearchParameter searchParameter) {
        return queryFactory.select(Wildcard.count)
                .from(delivery)
                .where(filterDeliveries(searchParameter)
                        .and(delivery.isDelete.isFalse()))
                .fetch().getFirst();
    }

    private BooleanBuilder filterDeliveries(SearchParameter searchParameter) {
        BooleanBuilder builder = new BooleanBuilder();
        if ("ORDER_ID".equals(searchParameter.getSearchType())) {
            builder.and(delivery.orderId.eq(UUID.fromString(searchParameter.getSearchValue())));
        } else if ("DELIVERY_ID".equals(searchParameter.getSearchType())) {
            builder.and(delivery.id.eq(UUID.fromString(searchParameter.getSearchValue())));
        }
        return builder;
    }

    private OrderSpecifier<?> getOrderSpecifier(SearchParameter searchParameter) {
        PathBuilder<Delivery> orderByExpression = new PathBuilder<>(Delivery.class, searchParameter.getOrderBy());
        com.querydsl.core.types.Order direction = searchParameter.getSort().equals(Sort.Direction.DESC) ? com.querydsl.core.types.Order.DESC : com.querydsl.core.types.Order.ASC;
        return new OrderSpecifier(direction, orderByExpression);
    }

}
