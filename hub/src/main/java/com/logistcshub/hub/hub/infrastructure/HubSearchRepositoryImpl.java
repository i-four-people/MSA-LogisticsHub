package com.logistcshub.hub.hub.infrastructure;

import static com.logistcshub.hub.area.domain.model.QArea.area;

import com.logistcshub.hub.area.domain.model.type.City;
import com.logistcshub.hub.hub.application.dtos.HubResponseDto;
import com.logistcshub.hub.hub.application.dtos.HubSearchDto;
import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub.domain.repository.HubSearchRepository;
import com.logistcshub.hub.hub.presentation.request.type.HubSearchType;
import com.logistcshub.hub.hub.presentation.request.type.SortType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import static com.logistcshub.hub.hub.domain.mode.QHub.hub;

@Repository
public class HubSearchRepositoryImpl implements HubSearchRepository {
    private final JPAQueryFactory queryFactory;

    public HubSearchRepositoryImpl(EntityManager em) {this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public Page<HubResponseDto> findAllHubResponseDto(String keyword, HubSearchType type, Pageable pageable,
                                        SortType sortBy, boolean isAsc) {

        List<HubSearchDto> result = queryFactory
                .select(Projections.constructor(HubSearchDto.class,
                        hub.id,
                        hub.name,
                        hub.area.state,
                        hub.area.city,
                        hub.address,
                        hub.lat,
                        hub.lng
                ))
                .from(hub)
                .join(hub.area, area)
                .where(search(keyword, type),
                        hub.isDeleted.eq(false))
                .orderBy(orderSpecifier(sortBy, isAsc))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = queryFactory.select(hub.count())
                .from(hub)
                .where(search(keyword, type),
                        hub.isDeleted.eq(false));

        return new PageImpl<>(result.stream().map(HubResponseDto::of).toList(), pageable, total.fetchOne() == null ? null : total.fetchOne());
    }

    private BooleanExpression search(String keyword, HubSearchType type) {
        if(keyword == null || keyword.isEmpty()) {
            return null;
        }

        return switch (type) {
            case NAME -> hub.name.containsIgnoreCase(keyword);
            case ADDRESS -> hub.address.containsIgnoreCase(keyword);
            case AREA -> hub.area.city.in(City.findAllCityAndState(keyword));
            default -> hub.name.containsIgnoreCase(keyword)
                    .or(hub.address.containsIgnoreCase(keyword)).or(hub.area.city.in(City.findAllCityAndState(keyword)));
        };
    }

    private OrderSpecifier<?> orderSpecifier(SortType sortBy, boolean isAsc) {
        return switch (sortBy) {
            case NAME -> isAsc ? hub.name.asc() : hub.name.desc();
            case AREA -> isAsc ? hub.area.city.asc() : hub.area.city.desc();
            case CREATEDAT -> isAsc ? hub.createdAt.asc() : hub.createdAt.desc();
            default -> isAsc ? hub.updatedAt.asc() : hub.updatedAt.desc();
        };
    }
}
