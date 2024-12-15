package com.logistics.order.infrastructure.repository;

import com.logistics.order.application.dto.SearchParameter;
import com.logistics.order.domain.model.Order;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

import static com.logistics.order.domain.model.QOrder.order;

@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> searchOrders(SearchParameter searchParameter) {

        // 페이징 처리
        int skip = (searchParameter.page() - 1) * searchParameter.limit();

        List<Order> results = queryFactory.selectFrom(order)
                .where(filterOrders(searchParameter)
                        .and(order.isDelete.isFalse()))
                .orderBy(getOrderSpecifier(searchParameter))
                .offset(skip)
                .limit(searchParameter.limit())
                .fetch();

        long totalCount = getOrdersTotalCount(searchParameter);

        return new PageImpl<>(results, searchParameter.getPageable(), totalCount);
    }

    private long getOrdersTotalCount(SearchParameter searchParameter) {
        return queryFactory.select(Wildcard.count)
                .from(order)
                .where(filterOrders(searchParameter)
                        .and(order.isDelete.isFalse()))
                .fetch().getFirst();
    }

    private BooleanBuilder filterOrders(SearchParameter searchParameter) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if (StringUtils.hasText(searchParameter.searchValue())) {
            booleanBuilder.and(order.id.eq(UUID.fromString(searchParameter.searchValue())));
        }
        return booleanBuilder;
    }

    @Override
    public Page<Order> searchOrdersByCompanyIds(SearchParameter searchParameter, List<UUID> companyIds) {

        // 페이징 처리
        int skip = (searchParameter.page() - 1) * searchParameter.limit();

        List<Order> results = queryFactory.selectFrom(order)
                .where(filterOrdersByCompanyIds(searchParameter, companyIds)
                        .and(order.isDelete.isFalse()))
                .orderBy(getOrderSpecifier(searchParameter))
                .offset(skip)
                .limit(searchParameter.limit())
                .fetch();

        long totalCount = getOrdersByCompanyIdsTotalCount(searchParameter, companyIds);

        return new PageImpl<>(results, searchParameter.getPageable(), totalCount);
    }

    private long getOrdersByCompanyIdsTotalCount(SearchParameter searchParameter, List<UUID> companyIds) {
        return queryFactory.select(Wildcard.count)
                .from(order)
                .where(filterOrdersByCompanyIds(searchParameter, companyIds)
                        .and(order.isDelete.isFalse()))
                .fetch().getFirst();
    }

    private BooleanBuilder filterOrdersByCompanyIds(SearchParameter searchParameter, List<UUID> companyIds) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        if ("RECIPIENT_NAME".equals(searchParameter.searchType())) {
            booleanBuilder.and(order.recipientCompanyId.in(companyIds));
        } else if ("SUPPLIER_NAME".equals(searchParameter.searchType())) {
            booleanBuilder.and(order.supplyCompanyId.in(companyIds));
        }
        return booleanBuilder;
    }

    private OrderSpecifier<?> getOrderSpecifier(SearchParameter searchParameter) {
        PathBuilder<Order> orderByExpression = new PathBuilder<>(Order.class, searchParameter.orderBy());
        com.querydsl.core.types.Order direction = searchParameter.sort().equals(Sort.Direction.DESC) ? com.querydsl.core.types.Order.DESC : com.querydsl.core.types.Order.ASC;
        return new OrderSpecifier(direction, orderByExpression);
    }

}
