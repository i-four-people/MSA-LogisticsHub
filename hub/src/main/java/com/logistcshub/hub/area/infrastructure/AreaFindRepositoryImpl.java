package com.logistcshub.hub.area.infrastructure;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.area.domain.model.type.City;
import com.logistcshub.hub.area.domain.repository.AreaFindRepository;
import com.logistcshub.hub.area.presentation.type.AreaSearchType;
import com.logistcshub.hub.area.presentation.type.SortType;
import com.logistcshub.hub.area.domain.model.type.State;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import static com.logistcshub.hub.area.domain.model.QArea.area;

@Repository
public class AreaFindRepositoryImpl  implements AreaFindRepository {

    private final JPAQueryFactory queryFactory;
    public AreaFindRepositoryImpl(EntityManager em) {this.queryFactory = new JPAQueryFactory(em);}

    @Override
    public Page<Area> findAll(String keyword, AreaSearchType type, Pageable pageable, SortType sortBy, boolean isAsc) {

        List<Area> result = queryFactory
                .selectFrom(area)
                .where(search(keyword, type),
                        area.isDeleted.eq(false))
                .orderBy(orderSpecifier(sortBy, isAsc))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = queryFactory.select(area.count())
                .from(area)
                .where(search(keyword, type),
                        area.isDeleted.eq(false));

        return new PageImpl<>(result, pageable, total.fetchOne() == null ? null : total.fetchOne());
    }

    private BooleanExpression search(String keyword, AreaSearchType type) {
        if(keyword == null || keyword.isEmpty()) {
            return null;
        }

        return switch (type) {
            case STATE -> area.state.in(State.findAllState(keyword));
            case CITY -> area.city.in(City.findAllCity(keyword));
            default -> area.state.in(State.findAllState(keyword))
                    .or(area.city.in(City.findAllCity(keyword)));
        };
    }

    private OrderSpecifier<?> orderSpecifier(SortType sortBy, boolean isAsc) {
        return switch (sortBy) {
            case STATE -> isAsc ? area.state.stringValue().asc() : area.state.stringValue().desc();
            case CITY -> isAsc ? area.city.stringValue().asc() : area.city.stringValue().desc();
            case CREATEDAT -> isAsc ? area.createdAt.asc() : area.createdAt.desc();
            default -> isAsc ? area.updatedAt.asc() : area.updatedAt.desc();
        };
    }

}
