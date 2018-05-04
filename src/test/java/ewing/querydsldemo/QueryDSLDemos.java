package ewing.querydsldemo;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLBindings;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLUpdateClause;
import ewing.StartApp;
import ewing.application.common.GsonUtils;
import ewing.application.common.JacksonUtils;
import ewing.application.query.Page;
import ewing.application.query.Pager;
import ewing.application.query.QueryUtils;
import ewing.querydsldemo.dao.DemoUserDao;
import ewing.querydsldemo.entity.DemoAddress;
import ewing.querydsldemo.entity.DemoUser;
import ewing.querydsldemo.query.QDemoAddress;
import ewing.querydsldemo.query.QDemoUser;
import ewing.querydsldemo.vo.DemoAddressDetail;
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
 * 执行前请先执行src/test/resources/querydsldemo.sql。
 * 注意：测试事务会自动回滚。
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = StartApp.class)
public class QueryDSLDemos {

    @Autowired
    private SQLQueryFactory queryFactory;

    @Autowired
    private DemoUserDao demoUserDao;

    /**
     * 表映射对象、路径、表达式等都是只会被读取（线程安全）的，可定义为全局的。
     * 它们提供的操作方法都会返回新的对象，原对象不变，所以要使用方法的返回值。
     */
    private QDemoUser qDemoUser = QDemoUser.demoUser;
    private QDemoAddress qDemoAddress = QDemoAddress.demoAddress;
    private QDemoAddress qSubAddress = new QDemoAddress("subAddress");

    /**
     * 创建新对象。
     */
    public DemoUser newDemoUser() {
        DemoUser demoUser = new DemoUser();
        demoUser.setUsername("NAME");
        demoUser.setPassword("123456");
        demoUser.setGender(1);
        demoUser.setCreateTime(new Date());
        demoUser.setAddressId(1);
        return demoUser;
    }

    /**
     * 原始API进行简单的CRUD操作。
     * 点击Dao的方法查看原始实现。
     */
    @Test
    public void simpleCrud() {
        DemoUser demoUser = newDemoUser();
        // 新增实体
        Integer userId = demoUserDao.insertWithKey(demoUser);
        System.out.println(demoUser.getUserId());

        // 更新实体
        demoUser.setUsername("EWING");
        demoUser.setPassword("ABC123");
        demoUserDao.updateBean(demoUser);

        // 更新部分属性，新增也可以这样用
        demoUserDao.updaterByKey(userId)
                .set(qDemoUser.username, "Ewing")
                .set(qDemoUser.password, "123ABC")
                // 使用字段表达式更新（可防止更新被覆盖）
                .set(qDemoUser.gender, qDemoUser.gender.add(1))
                .execute();

        // 查询实体
        demoUser = demoUserDao.selectByKey(userId);

        // 查询部分属性，可自定义返回类型
        String username = demoUserDao.selector(qDemoUser.username).fetchByKey(userId);
        System.out.println(username);

        // 删除实体
        demoUserDao.deleteBean(demoUser);
        System.out.println(JacksonUtils.toJson(demoUser));
    }

    /**
     * 复杂条件组合。
     */
    @Test
    public void queryWhere() {
        SQLQuery<DemoUser> query = queryFactory
                .selectFrom(qDemoUser).distinct()
                .leftJoin(qDemoAddress)
                .on(qDemoUser.addressId.eq(qDemoAddress.addressId))
                .orderBy(qDemoUser.createTime.desc().nullsFirst());
        // where可多次使用，相当于and，注意and优先级高于or
        query.where(qDemoUser.username.contains("元")
                .and(qDemoUser.gender.eq(1))
                .or(qDemoUser.username.contains("宝")
                        .and(qDemoUser.gender.eq(0))
                ));
        // 查看SQL和参数（默认提供SQL日志）
        SQLBindings sqlBindings = query.getSQL();
        System.out.println(sqlBindings.getSQL());
        System.out.println(sqlBindings.getBindings());
        // 分页获取数据
        Page<DemoUser> userPage = QueryUtils.queryPage(new Pager(), query);
        System.out.println(JacksonUtils.toJson(userPage));
    }

    /**
     * 使用各种子查询。
     */
    @Test
    public void selectSubQuery() {
        QDemoUser qAUser = new QDemoUser("A");
        List<String> names = queryFactory
                // 结果中使用子查询
                .select(SQLExpressions.select(qDemoUser.username)
                        .from(qDemoUser)
                        .where(qDemoUser.userId.eq(qAUser.userId)))
                // 嵌套子查询
                .from(SQLExpressions.selectFrom(qDemoUser)
                        .where(qDemoUser.userId.eq(1))
                        .as(qAUser))
                // 条件中使用子查询、EXISTS
                .where(SQLExpressions.selectOne()
                        .from(qDemoUser)
                        .where(qDemoUser.userId.eq(qAUser.userId))
                        .exists())
                .fetch();
        System.out.println(JacksonUtils.toJson(names));
    }

    /**
     * 关联分组并取自定义字段。
     */
    @Test
    public void queryJoinGroup() {
        // 统计城市用户数
        List<DemoAddressUser> addressUsers = queryFactory
                .select(Projections.bean(DemoAddressUser.class,
                        qDemoAddress.name,
                        qDemoUser.count().as("totalUser")))
                .from(qDemoAddress)
                .leftJoin(qDemoUser)
                .on(qDemoAddress.addressId.eq(qDemoUser.userId))
                .groupBy(qDemoAddress.name)
                .having(qDemoUser.count().gt(0))
                .fetch();
        System.out.println(JacksonUtils.toJson(addressUsers));
        // 关联查询取两个表的全部属性
        List<?> addressAndUser = queryFactory
                .select(Projections.list(qDemoAddress, qDemoUser))
                .from(qDemoAddress)
                .leftJoin(qDemoUser)
                .on(qDemoAddress.addressId.eq(qDemoUser.addressId))
                .fetch();
        System.out.println(JacksonUtils.toJson(addressAndUser));
    }

    /**
     * 自定义SQL查询。
     */
    @Test
    public void customQuery() {
        SQLQuery<Tuple> query = queryFactory.select(
                // 常用的表达式构建方法
                Expressions.asNumber(2).sum(),
                Expressions.asNumber(1).count(),
                SQLExpressions.sum(Expressions.constant(3)),
                Expressions.cases().when(
                        qDemoUser.gender.max().eq(qDemoUser.gender.min()))
                        .then("性别相同")
                        .otherwise("性别不同"),
                qDemoUser.createTime.milliSecond().avg())
                .from(qDemoUser);

        // 【非必要则不用】使用数据库的 HINTS 优化查询
        query.addFlag(QueryFlag.Position.AFTER_SELECT, "SQL_NO_CACHE ");

        // 【非必要则不用】自定义表达式、可调用数据库函数等
        query.where(Expressions.booleanTemplate(
                "NOW() < {0} OR NOW() > {0}",
                DateExpression.currentDate()));
        try {
            System.out.println(query.fetchFirst());
        } catch (BadSqlGrammarException e) {
            System.out.println("数据库不支持该SQL！");
        }
    }

    /**
     * 使用CASE以及查询附加字段。
     */
    @Test
    public void queryDetail() {
        List<DemoUserDetail> demoUsers = queryFactory.select(
                // 使用与Bean属性匹配的表达式
                QueryUtils.fitBean(DemoUserDetail.class,
                        qDemoUser,
                        qDemoUser.gender.when(1).then("男")
                                .when(2).then("女")
                                .otherwise("保密")
                                .as("genderName"),
                        qDemoAddress.name.as("addressName")))
                .from(qDemoUser)
                .leftJoin(qDemoAddress)
                .on(qDemoUser.addressId.eq(qDemoAddress.addressId))
                .fetch();
        System.out.println(JacksonUtils.toJson(demoUsers));
    }

    /**
     * 聚合UNION查询并分页。
     */
    @Test
    @SuppressWarnings("unchecked")
    public void queryUnion() {
        // 只需要保证和union后的别名一致即可
        SQLQuery<DemoUser> query = queryFactory.select(qDemoUser)
                .from(SQLExpressions.unionAll(
                        SQLExpressions.selectFrom(qDemoUser)
                                .where(qDemoUser.gender.eq(0)),
                        SQLExpressions.selectFrom(qDemoUser)
                                .where(qDemoUser.gender.eq(1)),
                        SQLExpressions.selectFrom(qDemoUser)
                                .where(qDemoUser.gender.eq(2))
                ).as(qDemoUser));
        System.out.println(JacksonUtils.toJson(QueryUtils
                .queryPage(new Pager(1, 1), query)));

        // 复杂UNION，定义别名并使结果列和别名一致
        QBean<DemoUserDetail> qUserAddress = QueryUtils.fitBean(
                DemoUserDetail.class, qDemoUser,
                qDemoAddress.name.as("addressName"));

        SQLQuery<DemoUserDetail> queryDetail = queryFactory.select(
                QueryUtils.fitBean(DemoUserDetail.class,
                        new QDemoUser("alias"),
                        Expressions.stringPath("addressName"))
        ).from(SQLExpressions.union(
                SQLExpressions.select(qUserAddress).from(qDemoUser)
                        .leftJoin(qDemoAddress)
                        .on(qDemoUser.addressId.eq(qDemoAddress.addressId))
                        .where(qDemoUser.gender.eq(1)),
                SQLExpressions.select(qUserAddress).from(qDemoUser)
                        .leftJoin(qDemoAddress)
                        .on(qDemoUser.addressId.eq(qDemoAddress.addressId))
                        .where(qDemoUser.gender.eq(2))
        ).as("alias"));
        System.out.println(JacksonUtils.toJson(QueryUtils
                .queryPage(new Pager(1, 1), queryDetail)));
    }

    /**
     * 执行批量操作。
     */
    @Test
    public void executeBatch() {
        // 批量更新 插入和删除操作类似
        SQLUpdateClause update = queryFactory.update(qDemoUser);
        update.set(qDemoUser.username, qDemoUser.username.append("哥哥"))
                .where(qDemoUser.gender.eq(1)).addBatch();
        update.set(qDemoUser.username, qDemoUser.username.append("妹妹"))
                .where(qDemoUser.gender.eq(2)).addBatch();
        System.out.println(update.execute());
    }

    /**
     * 关联查询一对多层级对象集合，把子对象一次性查询出来。
     */
    @Test
    public void querySubObjects() {
        // 先关联查询自动封装成并列的对象
        QBean<DemoAddressDetail> qAddressDetail = Projections
                .bean(DemoAddressDetail.class, qDemoAddress.all());
        List<Tuple> rows = queryFactory.select(
                qAddressDetail, qSubAddress)
                .from(qDemoAddress)
                .leftJoin(qSubAddress)
                .on(qDemoAddress.addressId.eq(qSubAddress.parentId))
                .where(qDemoAddress.parentId.isNull())
                .fetch();
        // 将并列的对象转换成一对多层级对象
        List<DemoAddressDetail> addressDetails = QueryUtils.oneToMany(
                rows, qAddressDetail, qSubAddress,
                DemoAddressDetail::getAddressId,
                DemoAddress::getAddressId,
                DemoAddressDetail::getSubAddresses,
                DemoAddressDetail::setSubAddresses);
        System.out.println(GsonUtils.toJson(addressDetails));
    }

}