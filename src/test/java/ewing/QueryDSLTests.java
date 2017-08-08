package ewing;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLUpdateClause;
import ewing.common.JsonConverter;
import ewing.common.QueryHelper;
import ewing.entity.User;
import ewing.query.QRole;
import ewing.query.QUser;
import ewing.query.QUserRole;
import ewing.vo.RoleUsers;
import ewing.vo.UserDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * QueryDSL查询案例，测试事务会自动回滚。
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StartApp.class)
public class QueryDSLTests {

    @Autowired
    private SQLQueryFactory queryFactory;

    // 约定查询对象以大写字母开头
    private QUser User = QUser.user;
    private QUserRole UserRole = QUserRole.userRole;
    private QRole Role = QRole.role;

    /**
     * 创建新对象。
     */
    public User newUser() {
        User user = new User();
        user.setUsername("User");
        user.setPassword("123456");
        user.setGender(1);
        user.setBirthday(new Date(System.currentTimeMillis() - 63072000000L));
        return user;
    }

    /**
     * 简单的CRUD操作。
     */
    @Test
    public void simpleCrud() {
        User user = newUser();
        // 新增
        user.setUserId(queryFactory.insert(User)
                .populate(user)
                .executeWithKey(User.userId));
        System.out.println(user.getUserId());
        // 更新
        user.setUsername("123ABC");
        user.setPassword("ABC123");
        queryFactory.update(User)
                .where(User.userId.eq(user.getUserId()))
                .populate(user)
                .execute();
        // 查询
        user = queryFactory.selectFrom(User)
                .where(User.userId.eq(user.getUserId()))
                .fetchOne();
        // 删除
        queryFactory.delete(User)
                .where(User.userId.eq(user.getUserId()))
                .execute();
        System.out.println(JsonConverter.toJson(user));
    }

    /**
     * 复杂条件组合。
     */
    @Test
    public void queryWhere() {
        SQLQuery<User> query = queryFactory
                .selectFrom(User).distinct()
                .leftJoin(UserRole).on(User.userId.eq(UserRole.userId))
                .leftJoin(Role).on(UserRole.roleId.eq(Role.roleId))
                .orderBy(User.birthday.desc().nullsFirst())
                .where( // 注意 sql 中的 and 优先级高于 or
                        Role.name.eq("用户").and(
                                (
                                        User.username.contains("元").and(User.gender.eq(1))
                                ).or(
                                        User.username.contains("宝").and(User.gender.eq(0))
                                )
                        )
                );
        // 查看SQL和绑定的参数
        SQLBindings sqlBindings = query.getSQL();
        System.out.println(sqlBindings.getSQL());
        System.out.println(sqlBindings.getBindings());
        System.out.println(JsonConverter.toJson(query.fetch()));
    }

    /**
     * 自定义语句片段。
     */
    @Test
    public void customStatement() {
        List<User> users = queryFactory.selectFrom(User)
                // 使用数据库的 HINTS 优化查询
                .addFlag(QueryFlag.Position.AFTER_SELECT, "/*HINTS*/ ")
                .where(
                        // 该方法可自定义表达式及其返回类型、可调用数据库函数等
                        Expressions.booleanTemplate("{0} * {0} + {1} * {1} < {2}",
                                User.gender, User.gender, 100)
                ).fetch();
        System.out.println(JsonConverter.toJson(users));
    }

    /**
     * 使用嵌套子查询。
     */
    @Test
    public void selectSubQuery() {
        List<User> names = queryFactory.select(new QUser("A"))
                .from(SQLExpressions.select(User)
                        .from(User)
                        .where(User.userId.eq(1))
                        .as("A"))
                .fetch();
        System.out.println(JsonConverter.toJson(names));
    }

    /**
     * 分组聚合关联查询并取自定义字段。
     */
    @Test
    public void queryVoJoinGroup() {
        // 权限用户统计
        List<RoleUsers> roleUsers = queryFactory.select(
                Projections.bean(RoleUsers.class, Role.name, User.count().as("users")))
                .from(Role)
                .leftJoin(UserRole).on(Role.roleId.eq(UserRole.roleId))
                .leftJoin(User).on(UserRole.userId.eq(User.userId))
                .groupBy(Role.name)
                .having(User.count().gt(0))
                .fetch();
        System.out.println(JsonConverter.toJson(roleUsers));
    }

    /**
     * 聚合UNION查询结果。
     */
    @Test
    public void queryUnion() {
        List<User> users = queryFactory.query().unionAll(
                SQLExpressions.selectFrom(User).where(User.gender.eq(0)),
                SQLExpressions.selectFrom(User).where(User.gender.eq(1)),
                SQLExpressions.selectFrom(User).where(User.gender.eq(2))
        ).fetch();
        System.out.println(JsonConverter.toJson(users));
    }

    /**
     * 使用CASE查询附加字段。
     */
    @Test
    public void queryDetail() {
        List<UserDetail> users = queryFactory
                .select( // 如果取部分属性字段则用matchToBean
                        QueryHelper.allToBean(UserDetail.class,
                                User, Expressions.cases()
                                        .when(User.gender.eq(1)).then("男")
                                        .when(User.gender.eq(2)).then("女")
                                        .otherwise("保密").as("genderName"))
                ).from(User)
                .leftJoin(UserRole).on(User.userId.eq(UserRole.userId))
                .leftJoin(Role).on(UserRole.roleId.eq(Role.roleId))
                .fetch();
        System.out.println(JsonConverter.toJson(users));
    }

    /**
     * 执行批量操作。
     */
    @Test
    public void executeBatch() {
        // 批量更新 插入和删除操作类似
        SQLUpdateClause update = queryFactory.update(User);
        update.set(User.username, User.username.append("哥哥"))
                .where(User.userId.eq(1)).addBatch();
        update.set(User.username, User.username.append("妹妹"))
                .where(User.userId.eq(2)).addBatch();
        System.out.println(update.execute());
    }

}