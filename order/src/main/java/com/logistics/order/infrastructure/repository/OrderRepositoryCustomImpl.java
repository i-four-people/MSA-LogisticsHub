package com.logistics.order.infrastructure.repository;

import com.logistics.order.domain.model.Order;
import com.logistics.order.domain.model.QOrder;
import com.logistics.order.presentation.request.SearchParameter;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;

import static com.logistics.order.domain.model.QOrder.*;

@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> searchOrders(SearchParameter searchParameter) {

        // 페이징 처리
        int skip = (searchParameter.page() - 1) * searchParameter.limit();

        return null;
    }

}
