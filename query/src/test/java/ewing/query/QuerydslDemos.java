package ewing.query;

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
import com.querydsl.sql.dml.AbstractSQLUpdateClause;
import com.querydsl.sql.dml.DefaultMapper;
import ewing.query.paging.Page;
import ewing.query.paging.Pager;
import ewing.query.querydsldemo.dao.DemoUserDao;
import ewing.query.querydsldemo.entity.DemoAddress;
import ewing.query.querydsldemo.entity.DemoUser;
import ewing.query.querydsldemo.query.QDemoAddress;
import ewing.query.querydsldemo.query.QDemoUser;
import ewing.query.querydsldemo.vo.DemoAddressDetail;
import ewing.query.querydsldemo.vo.DemoAddressUser;
import ewing.query.querydsldemo.vo.DemoUserDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Querydsl查询测试案例，可作为参考。
 * 测试使用Spring Boot配置的H2内存数据库。
 * 注意：测试事务会自动回滚。
 */
@Transactional
@SpringBootTest
@RunWith(SpringRunner.class)
public class QuerydslDemos {

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
     */
    @Test
    public void basicOperation() {
        DemoUser demoUser = newDemoUser();
        // 1.新增实体，并返回主键
        Integer userId = queryFactory.insert(qDemoUser)
                .populate(demoUser)
                .executeWithKey(qDemoUser.userId);

        System.out.println(userId);

        // 新增实体，包括null属性
        long rows = queryFactory.insert(qDemoUser)
                .populate(demoUser, DefaultMapper.WITH_NULL_BINDINGS)
                .execute();

        System.out.println(rows);

        // 2.更新实体，使用实体对象
        demoUser.setUsername("元宝");
        demoUser.setPassword("ABC123");
        rows = queryFactory.update(qDemoUser)
                .where(qDemoUser.addressId.eq(userId))
                .populate(demoUser)
                .execute();

        System.out.println(rows);

        // 更新实体，使用上下文参数
        rows = queryFactory.update(qDemoUser)
                .where(qDemoUser.addressId.eq(userId))
                .set(qDemoUser.username, "元宝")
                .set(qDemoUser.password, "123ABC")
                // 使用字段表达式更新（在数据库事务下可保证一致性）
                .set(qDemoUser.gender, qDemoUser.gender.add(1))
                .execute();

        System.out.println(rows);

        // 3.查询实体，根据ID查询
        demoUser = queryFactory.selectFrom(qDemoUser)
                .where(qDemoUser.userId.eq(userId))
                .fetchOne();

        System.out.println(demoUser);

        // 查询实体，条件模糊查询
        List<DemoUser> users = queryFactory.selectFrom(qDemoUser)
                .where(qDemoUser.username.contains("元宝"))
                .fetch();

        System.out.println(users);

        // 4.删除实体，根据ID删除
        rows = queryFactory.delete(qDemoUser)
                .where(qDemoUser.userId.eq(userId))
                .execute();

        System.out.println(rows);

        // 删除实体，根据条件删除
        rows = queryFactory.delete(qDemoUser)
                .where(qDemoUser.username.contains("元宝"))
                .execute();

        System.out.println(rows);
    }

    /**
     * 使用简单封装的API进行CRUD操作。
     * 点击Dao的方法查看更多API及实现，覆盖几乎所有单表操作。
     */
    @Test
    public void warpedOperation() {
        DemoUser demoUser = newDemoUser();
        // 1.新增实体，并返回主键，主键也会被设置到Bean中
        Integer userId = demoUserDao.insertWithKey(demoUser);

        System.out.println(userId);
        System.out.println(demoUser);

        // 批量新增实体
        List<DemoUser> newUsers = Arrays.asList(newDemoUser(), newDemoUser());
        List<Integer> userIds = demoUserDao.insertWithKeys(newUsers);

        System.out.println(userIds);

        // 2.更新实体，使用实体对象
        demoUser.setUsername("元宝");
        demoUser.setPassword("ABC123");
        long rows = demoUserDao.updateBean(demoUser);

        System.out.println(rows);

        // 更新实体，使用上下文参数
        rows = demoUserDao.updaterByKey(userId)
                .set(qDemoUser.username, "元宝")
                .set(qDemoUser.password, "123ABC")
                // 使用字段表达式更新（在数据库事务下可保证一致性）
                .set(qDemoUser.gender, qDemoUser.gender.add(1))
                .execute();

        System.out.println(rows);

        // 3.查询实体，根据ID查询
        demoUser = demoUserDao.selectByKey(userId);

        System.out.println(demoUser);

        // 查询实体，条件模糊查询
        List<DemoUser> users = demoUserDao.selector()
                .where(qDemoUser.username.contains("元宝"))
                .fetch();

        System.out.println(users);

        // 4.删除实体，根据ID删除
        rows = demoUserDao.deleteByKey(userId);

        System.out.println(rows);

        // 删除实体，根据条件删除
        rows = demoUserDao.deleter()
                .where(qDemoUser.username.contains("元宝"))
                .execute();

        System.out.println(rows);
    }

    /**
     * 动态条件和分页查询。
     */
    @Test
    public void dynamicWhere() {
        DemoUser demoUser = newDemoUser();

        // 原始API动态条件、分页获取数据
        SQLQuery<DemoUser> query = queryFactory.selectFrom(qDemoUser)
                .distinct()
                // 利用where(null)会被忽略的特性构建单行动态条件
                .where(Where.notNull(demoUser.getUsername(), qDemoUser.username::contains))
                .where(Where.notNull(demoUser.getCreateTime(), qDemoUser.createTime::goe))
                .where(Where.notNull(demoUser.getGender(), qDemoUser.gender::eq));

        Page<DemoUser> userPage = QueryUtils.queryPage(query, new Pager());
        System.out.println(userPage);

        // 简单封装动态条件、分页获取数据
        userPage = demoUserDao.selector()
                // 利用where(null)会被忽略的特性构建单行动态条件
                .where(Where.notNull(demoUser.getUsername(), qDemoUser.username::contains))
                .where(Where.notNull(demoUser.getCreateTime(), qDemoUser.createTime::goe))
                .where(Where.notNull(demoUser.getGender(), qDemoUser.gender::eq))
                .fetchPage(new Pager());
        System.out.println(userPage);
    }

    /**
     * 复杂条件组合以及SQL和参数。
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

        // 查看SQL和参数，另见 SQLLogger 类
        SQLBindings sqlBindings = query.getSQL();
        System.out.println(sqlBindings.getSQL());
        System.out.println(sqlBindings.getNullFriendlyBindings());

        System.out.println(query.fetch());
    }

    /**
     * 使用各种子查询。
     */
    @Test
    public void selectSubQuery() {
        QDemoUser qUserChild = new QDemoUser("child");
        List<String> names = queryFactory
                // 结果中使用子查询
                .select(SQLExpressions.select(qDemoUser.username)
                        .from(qDemoUser)
                        .where(qDemoUser.userId.eq(qUserChild.userId)))
                // 嵌套子查询
                .from(SQLExpressions.selectFrom(qDemoUser)
                        .where(qDemoUser.userId.eq(1))
                        .as(qUserChild))
                // 条件中使用子查询、EXISTS
                .where(SQLExpressions.selectOne()
                        .from(qDemoUser)
                        .where(qDemoUser.userId.eq(qUserChild.userId))
                        .exists())
                .fetch();
        System.out.println(names);
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
        System.out.println(addressUsers);

        // 关联查询取两个表的全部属性
        List<?> addressAndUser = queryFactory
                .select(Projections.list(qDemoAddress, qDemoUser))
                .from(qDemoAddress)
                .leftJoin(qDemoUser)
                .on(qDemoAddress.addressId.eq(qDemoUser.addressId))
                .fetch();
        System.out.println(addressAndUser);
    }

    /**
     * 自定义SQL查询【非必要则不用】。
     */
    @Test
    public void customSql() {
        // 另见 MysqlBasisDao 类
        SQLQuery<Tuple> query = queryFactory.select(
                // 常用的表达式构建方法
                Expressions.asNumber(2).sum(),
                Expressions.asNumber(1).count(),
                SQLExpressions.sum(Expressions.constant(3)),
                // 自定义结果列名
                Expressions.stringPath(qDemoUser.username.getMetadata()),
                // 自定义表达式
                Expressions.stringTemplate("group_concat({0})", qDemoUser.username),
                qDemoUser.createTime.milliSecond().avg())
                .from(qDemoUser)
                .groupBy(qDemoUser.userId);

        // 使用数据库的 HINTS 优化查询
        query.addFlag(QueryFlag.Position.AFTER_SELECT, "SQL_NO_CACHE ");

        // 自定义条件表达式
        query.where(Expressions.booleanTemplate(
                "NOW() < {0} OR NOW() > {0}", DateExpression.currentDate()));
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
                        qDemoAddress.name.as("addressName"),
                        Expressions.cases().when(
                                qDemoUser.gender.eq(qDemoUser.gender))
                                .then("性别相同")
                                .otherwise("性别不同")
                                .as("sameGender")))
                .from(qDemoUser)
                .leftJoin(qDemoAddress)
                .on(qDemoUser.addressId.eq(qDemoAddress.addressId))
                .fetch();
        System.out.println(demoUsers);
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
        System.out.println(QueryUtils.queryPage(query, new Pager()));

        // 复杂UNION，定义别名并使结果列和别名一致
        QDemoUser qDemoUserUnions = new QDemoUser("unions");
        QDemoAddress qDemoAddressUnions = new QDemoAddress("unions");

        SQLQuery<DemoUserDetail> queryDetail = queryFactory.select(
                QueryUtils.fitBean(DemoUserDetail.class,
                        qDemoUserUnions,
                        qDemoAddressUnions.name.as("addressName")))
                .from(SQLExpressions.union(
                        SQLExpressions.select(qDemoUser, qDemoAddress.name).from(qDemoUser)
                                .leftJoin(qDemoAddress)
                                .on(qDemoUser.addressId.eq(qDemoAddress.addressId))
                                .where(qDemoUser.gender.eq(1)),
                        SQLExpressions.select(qDemoUser, qDemoAddress.name).from(qDemoUser)
                                .leftJoin(qDemoAddress)
                                .on(qDemoUser.addressId.eq(qDemoAddress.addressId))
                                .where(qDemoUser.gender.eq(2)))
                        .as("unions"));
        System.out.println(QueryUtils.queryPage(queryDetail, new Pager()));
    }

    /**
     * 执行批量操作。
     */
    @Test
    public void executeBatch() {
        // 批量更新，插入和删除操作类似，设置参数后调用addBatch即可
        AbstractSQLUpdateClause<?> update = queryFactory.update(qDemoUser);

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
        List<DemoAddressDetail> addressDetails = QueryUtils.rowsToTree(
                rows, qAddressDetail, qSubAddress,
                DemoAddressDetail::getAddressId,
                DemoAddress::getAddressId,
                DemoAddressDetail::getSubAddresses,
                DemoAddressDetail::setSubAddresses);

        System.out.println(addressDetails);
    }

}