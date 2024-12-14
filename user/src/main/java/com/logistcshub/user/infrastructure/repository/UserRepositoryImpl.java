package com.logistcshub.user.infrastructure.repository;

import com.logistcshub.user.presentation.response.QUserDto;
import com.logistcshub.user.domain.repository.UserRepositoryCustom;
import com.logistcshub.user.presentation.response.UserSearchResponse;
import com.logistcshub.user.presentation.response.UserDto;
import com.logistcshub.user.domain.model.UserRoleEnum;
import com.logistcshub.user.presentation.request.UserSearchRequest;
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

import static com.logistcshub.user.domain.model.QUser.user;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<UserSearchResponse> findAllUser(Pageable pageable, UserSearchRequest condition) {

        JPAQuery<UserDto> query = queryFactory
                .select(new QUserDto(
                        user.id,
                        user.username,
                        user.email,
                        user.tel,
                        user.slackId,
                        user.role,
                        user.createdAt,
                        user.createdBy,
                        user.updatedAt,
                        user.updatedBy
                ))
                .from(user)
                .where(userIdEq(condition.userId()),
                        userRoleEq(condition.role()),
                        usernameContains(condition.username()),
                        emailContains(condition.email()),
                        user.isDelete.eq(false));

        if (pageable.getSort().isEmpty()) {
            query.orderBy(user.createdAt.desc());
        } else {
            Sort sort = pageable.getSort();
            sort.forEach(order -> {
                String property = order.getProperty();
                Sort.Direction direction= order.getDirection();

                if ("createAt".equals(property)) {
                    query.orderBy(direction.isAscending()? user.updatedAt.desc() : user.createdAt.asc());
                } else if ("updateAt".equals(property)) {
                    query.orderBy(direction.isAscending()? user.updatedAt.desc() : user.createdAt.asc());
                }
            });
        }

        List<UserDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(user.count())
                .from(user)
                .where(userIdEq(condition.userId()),
                        userRoleEq(condition.role()),
                        usernameContains(condition.username()),
                        emailContains(condition.email()),
                        user.isDelete.eq(false));

        UserSearchResponse userSearchResponse = new UserSearchResponse(content);

        return PageableExecutionUtils.getPage(Collections.singletonList(userSearchResponse), pageable, countQuery::fetchOne);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? user.id.eq(userId) : null;
    }

    private BooleanExpression userRoleEq(UserRoleEnum role) {
        return role != null ? user.role.eq(role) : null;
    }

    private BooleanExpression usernameContains(String username) {
        return hasText(username) ? user.username.contains(username) : null;
    }

    private BooleanExpression emailContains(String email) {
        return hasText(email) ? user.email.contains(email) : null;
    }
}
