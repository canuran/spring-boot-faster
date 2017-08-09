package ewing.querydsldemo;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.SQLUpdateClause;
import ewing.StartApp;
import ewing.common.JsonConverter;
import ewing.common.QueryHelper;
import ewing.common.paging.Page;
import ewing.common.paging.Paging;
import ewing.querydsldemo.entity.DemoUser;
import ewing.querydsldemo.query.QDemoAddress;
import ewing.querydsldemo.query.QDemoUser;
import ewing.querydsldemo.vo.DemoAddressUser;
import ewing.querydsldemo.vo.DemoUserDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 独立的QueryDSL查询案例可作为参考。
 * 注意：测试事务会自动回滚。
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StartApp.class)
public class QueryDSLDemos {

    @Autowired
    private SQLQueryFactory queryFactory;

    // 约定查询对象以大写字母开头
    private QDemoUser DemoUser = QDemoUser.demoUser;
    private QDemoAddress DemoAddress = QDemoAddress.demoAddress;

    /**
     * 创建新对象。
     */
    public DemoUser newDemoUser() {
        DemoUser demoUser = new DemoUser();
        demoUser.setUsername("NAME");
        demoUser.setPassword("123456");
        demoUser.setGender(1);
        demoUser.setBirthday(new Date());
        demoUser.setAddressId(1);
        return demoUser;
    }

    /**
     * 简单的CRUD操作。
     */
    @Test
    public void simpleCrud() {
        DemoUser demoUser = newDemoUser();
        // 新增
        demoUser.setUserId(queryFactory.insert(DemoUser)
                .populate(demoUser, DefaultMapper.WITH_NULL_BINDINGS)
                .executeWithKey(DemoUser.userId));
        System.out.println(demoUser.getUserId());
        // 更新
        demoUser.setUsername("EWING");
        demoUser.setPassword("ABC123");
        queryFactory.update(DemoUser)
                .where(DemoUser.userId.eq(demoUser.getUserId()))
                .populate(demoUser)
                .execute();
        // 查询
        demoUser = queryFactory.selectFrom(DemoUser)
                .where(DemoUser.userId.eq(demoUser.getUserId()))
                .fetchOne();
        // 删除
        queryFactory.delete(DemoUser)
                .where(DemoUser.userId.eq(demoUser.getUserId()))
                .execute();
        System.out.println(JsonConverter.toJson(demoUser));
    }

    /**
     * 复杂条件组合。
     */
    @Test
    public void queryWhere() {
        SQLQuery<DemoUser> query = queryFactory
                .selectFrom(DemoUser).distinct()
                .leftJoin(DemoAddress)
                .on(DemoUser.addressId.eq(DemoAddress.addressId))
                .orderBy(DemoUser.birthday.desc().nullsFirst());
        // where可多次使用，相当于and，注意and优先级高于or
        query.where(DemoAddress.city.contains("深圳")
                .and((
                        DemoUser.username.contains("元")
                                .and(DemoUser.gender.eq(1))
                ).or(
                        DemoUser.username.contains("宝")
                                .and(DemoUser.gender.eq(0))
                ))
        );
        // 查看SQL和参数
        SQLBindings sqlBindings = query.getSQL();
        System.out.println(sqlBindings.getSQL());
        System.out.println(sqlBindings.getBindings());
        // 分页获取数据
        Paging paging = new Paging();
        Page<DemoUser> userPage = QueryHelper.queryPage(paging, query);
        System.out.println(JsonConverter.toJson(userPage));
    }

    /**
     * 使用各种子查询。
     */
    @Test
    public void selectSubQuery() {
        QDemoUser UserA = new QDemoUser("A");
        List<String> names = queryFactory
                // 结果中使用子查询
                .select(SQLExpressions.select(DemoUser.username)
                        .from(DemoUser)
                        .where(DemoUser.userId.eq(UserA.userId)))
                // 嵌套子查询
                .from(SQLExpressions.selectFrom(DemoUser)
                        .where(DemoUser.userId.eq(1))
                        .as(UserA))
                // 条件中使用子查询
                .where(SQLExpressions.selectOne()
                        .from(DemoUser)
                        .where(DemoUser.userId.eq(UserA.userId))
                        .exists())
                .fetch();
        System.out.println(JsonConverter.toJson(names));
    }

    /**
     * 关联分组并取自定义字段。
     */
    @Test
    public void queryJoinGroup() {
        // 权限用户统计
        List<DemoAddressUser> addressUsers = queryFactory
                .select(Projections.bean(DemoAddressUser.class,
                        DemoAddress.city, DemoUser.count().as("totalUser")))
                .from(DemoAddress)
                .leftJoin(DemoUser).on(DemoAddress.addressId.eq(DemoUser.userId))
                .groupBy(DemoAddress.city)
                .having(DemoUser.count().gt(0))
                .fetch();
        System.out.println(JsonConverter.toJson(addressUsers));
    }

    /**
     * 自定义SQL查询。
     */
    @Test
    public void customQuery() {
        SQLQuery<Tuple> query = queryFactory.select(
                // 常用的表达式构建方法
                ExpressionUtils.count(Expressions.constant(1)),
                SQLExpressions.sum(Expressions.constant(2)),
                DemoUser.gender.max(), DemoUser.gender.avg())
                .from(DemoUser);

        // 【非必要则不用】使用数据库的 HINTS 优化查询
        query.addFlag(QueryFlag.Position.AFTER_SELECT, "SQL_NO_CACHE ");

        // 【非必要则不用】自定义表达式、可调用数据库函数等
        query.where(Expressions.booleanTemplate(
                "NOW() < {0} OR NOW() > {0}",
                DateExpression.currentDate()));
        try {
            System.out.println(query.fetch());
        } catch (BadSqlGrammarException e) {
            System.out.println("数据库不支持该SQL！");
        }
    }

    /**
     * 聚合UNION查询结果。
     */
    @Test
    @SuppressWarnings("unchecked")
    public void queryUnion() {
        List<DemoUser> demoUsers = queryFactory.query().unionAll(
                SQLExpressions.selectFrom(DemoUser).where(DemoUser.gender.eq(0)),
                SQLExpressions.selectFrom(DemoUser).where(DemoUser.gender.eq(1)),
                SQLExpressions.selectFrom(DemoUser).where(DemoUser.gender.eq(2))
        ).fetch();
        System.out.println(JsonConverter.toJson(demoUsers));
    }

    /**
     * 使用CASE以及查询附加字段。
     */
    @Test
    public void queryDetail() {
        List<DemoUserDetail> demoUsers = queryFactory.select(
                // 如果取部分属性字段则用matchToBean
                QueryHelper.allToBean(DemoUserDetail.class,
                        DemoUser, Expressions.cases()
                                .when(DemoUser.gender.eq(1)).then("男")
                                .when(DemoUser.gender.eq(2)).then("女")
                                .otherwise("保密").as("genderName"),
                        DemoAddress.city.as("addressCity")))
                .from(DemoUser)
                .leftJoin(DemoAddress).on(DemoUser.addressId.eq(DemoAddress.addressId))
                .fetch();
        System.out.println(JsonConverter.toJson(demoUsers));
    }

    /**
     * 执行批量操作。
     */
    @Test
    public void executeBatch() {
        // 批量更新 插入和删除操作类似
        SQLUpdateClause update = queryFactory.update(DemoUser);
        update.set(DemoUser.username, DemoUser.username.append("哥哥"))
                .where(DemoUser.userId.eq(1)).addBatch();
        update.set(DemoUser.username, DemoUser.username.append("妹妹"))
                .where(DemoUser.userId.eq(2)).addBatch();
        System.out.println(update.execute());
    }

}