package ewing.query;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.dml.AbstractSQLUpdateClause;
import ewing.query.clause.BaseQuery;
import ewing.query.paging.Page;
import ewing.query.paging.Pager;
import ewing.query.querydsldemo.entity.DemoAddress;
import ewing.query.querydsldemo.entity.DemoUser;
import ewing.query.querydsldemo.query.QDemoAddress;
import ewing.query.querydsldemo.query.QDemoUser;
import ewing.query.querydsldemo.vo.DemoAddressDetail;
import ewing.query.querydsldemo.vo.DemoUserDetail;
import ewing.query.querydsldemo.vo.DemoUserSimple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private BaseQueryFactory queryFactory;

    /**
     * 表映射对象、路径、表达式等都是只会被读取（线程安全）的，可定义为全局的。
     * 它们提供的操作方法都会返回新的对象，原对象不变，所以要使用方法的返回值。
     */
    private QDemoUser qDemoUser = QDemoUser.demoUser;
    private QDemoAddress qDemoAddress = QDemoAddress.demoAddress;

    // 用做别名的表
    private QDemoUser subDemoUser = new QDemoUser("subDemoUser");
    private QDemoAddress qSubAddress = new QDemoAddress("subAddress");

    /**
     * 创建新对象。
     */
    public DemoUser newDemoUser() {
        DemoUser demoUser = new DemoUser();
        demoUser.setUsername("元宝");
        demoUser.setPassword("123456");
        demoUser.setGender(1);
        demoUser.setCreateTime(new Date());
        demoUser.setAddressId(1);
        return demoUser;
    }

    /**
     * 插入示例，更多详见BaseQuery类。
     */
    @Test
    public void insertDemo() {
        DemoUser demoUser = newDemoUser();

        // 新增实体、并获取主键
        Integer userId = queryFactory.insert(qDemoUser).insertGetKey(demoUser);
        System.out.println(userId);
        System.out.println(demoUser);

        // 新增实体、包含null属性
        demoUser = newDemoUser();
        demoUser.setGender(null);
        userId = queryFactory.insert(qDemoUser).insertWithNullGetKey(demoUser);
        System.out.println(userId);

        // 批量插入对象并获取主键
        List<DemoUser> newUsers = Arrays.asList(newDemoUser(), newDemoUser());
        List<Integer> userIds = queryFactory.insert(qDemoUser).insertGetKeys(newUsers);
        System.out.println(userIds);

        // 自定义字段、动态字段
        long rows = queryFactory.insert(qDemoUser)
                .set(qDemoUser.username, demoUser.getUsername())
                .set(qDemoUser.createTime, demoUser.getCreateTime())
                .setIfHasText(qDemoUser.password, demoUser.getPassword())
                .execute();
        System.out.println(rows);
    }

    /**
     * 更新示例，更多详见BaseQuery类。
     */
    @Test
    public void updateDemo() {
        DemoUser demoUser = queryFactory.selectFrom(qDemoUser).fetchFirst();

        // 更新实体、忽略null属性
        long rows = queryFactory.update(qDemoUser).updateBean(demoUser);
        System.out.println(rows);

        // 更新实体、包含null属性
        rows = queryFactory.update(qDemoUser).updateWithNull(demoUser);
        System.out.println(rows);

        // 条件更新、动态字段
        rows = queryFactory.update(qDemoUser).whereEqKey(demoUser.getUserId())
                .setIfHasText(qDemoUser.username, "元宝")
                .setIfNotNull(qDemoUser.createTime, new Date())
                .execute();
        System.out.println(rows);
    }

    /**
     * 删除示例，更多详见BaseQuery类。
     */
    @Test
    public void deleteDemo() {
        DemoUser demoUser = queryFactory.selectFrom(qDemoUser).fetchFirst();

        // 通过ID删除
        long rows = queryFactory.delete(qDemoUser).deleteByKey(demoUser.getUserId());
        System.out.println(rows);

        // 删除实体
        rows = queryFactory.delete(qDemoUser).deleteBean(demoUser);
        System.out.println(rows);

        // 条件删除、动态条件
        rows = queryFactory.delete(qDemoUser)
                .where(qDemoUser.userId.goe(demoUser.getUserId()))
                .whereIfHasText(demoUser.getUsername(), qDemoUser.username::contains)
                .execute();
        System.out.println(rows);
    }

    /**
     * 简单查询，更多详见BaseQuery类。
     */
    @Test
    public void simpleQuery() {
        // 根据ID查询
        DemoUser demoUser = queryFactory.selectFrom(qDemoUser).fetchByKey(1);
        System.out.println(demoUser);

        // 查询列表
        List<DemoUser> demoUsers = queryFactory.selectFrom(qDemoUser)
                .where(qDemoUser.userId.gt(0L))
                .fetch();
        System.out.println(demoUsers);

        // 查询分页
        Page<DemoUser> demoUserPage = queryFactory.selectFrom(qDemoUser)
                .fetchPage(Pager.of(1, 100));
        System.out.println(demoUserPage);

        // 字段自适应对象属性
        List<DemoUserSimple> demoUserSimples = queryFactory.selectFrom(qDemoUser)
                .fetch(DemoUserSimple.class);
        System.out.println(demoUserSimples);

        // 关联查询取两个表的全部属性
        List<Tuple> addressAndUser = queryFactory
                .select(qDemoAddress, qDemoUser)
                .from(qDemoAddress)
                .leftJoin(qDemoUser)
                .on(qDemoAddress.addressId.eq(qDemoUser.addressId))
                .fetch();
        System.out.println(addressAndUser);
    }

    /**
     * 进阶构建复杂查询。
     */
    @Test
    public void advanceQuery() {
        // 准备参数
        DemoUser demoUser = newDemoUser();
        Integer userId = queryFactory.insert(qDemoUser).insertGetKey(demoUser);
        String address = "上海";
        Date startDate = new Date(System.currentTimeMillis() - 36000000L);
        Date endDate = new Date();
        String orderParam = "username asc";

        // 完全链式方法调用
        Page<DemoUser> userPage = queryFactory.selectFrom(qDemoUser)
                .distinct()

                // 多表关联、动态关联条件
                .leftJoin(qDemoAddress)
                .on(qDemoUser.addressId.eq(qDemoAddress.addressId))
                .onIfNotNull(address, qDemoAddress.name::contains)

                // 简单动态条件
                .whereIfNotNull(demoUser.getUsername(), qDemoUser.username::contains)
                .whereIfNotNull(demoUser.getCreateTime(), qDemoUser.createTime::goe)
                .whereIfNotNull(demoUser.getGender(), qDemoUser.gender::eq)
                // 万能动态条件
                .whereIfTrue(userId != null && userId > 0, () -> qDemoUser.userId.goe(userId))

                // 复杂条件组合
                .where(qDemoUser.username.contains("元").or(qDemoUser.username.contains("宝"))
                        .and(qDemoUser.createTime.goe(startDate).or(qDemoUser.createTime.loe(endDate))))

                // 分组
                .groupBy(qDemoUser.gender)
                // 分组条件
                .having(qDemoUser.userId.count().gt(0))

                // 原生的排序方法
                .orderBy(qDemoUser.createTime.desc().nullsFirst())
                // 根据传入参数自动排序
                .orderBy(orderParam)

                // 获取结果
                .fetchPage(Pager.of(1, 100));
        System.out.println(userPage);
    }

    /**
     * 使用各种子查询。
     */
    @Test
    public void selectSubQuery() {
        List<String> names = queryFactory
                // 结果中使用子查询
                .select(SQLExpressions.select(qDemoUser.username)
                        .from(qDemoUser)
                        .where(qDemoUser.userId.eq(subDemoUser.userId)))
                // 嵌套子查询
                .from(SQLExpressions.selectFrom(qDemoUser)
                        .where(qDemoUser.userId.eq(1))
                        .as(subDemoUser))
                // 条件中使用子查询、EXISTS
                .where(SQLExpressions.selectOne()
                        .from(qDemoUser)
                        .where(qDemoUser.userId.eq(subDemoUser.userId))
                        .exists())
                .fetch();
        System.out.println(names);
    }

    /**
     * 同表出现多次时使用别名，以及UNION查询。
     */
    @Test
    @SuppressWarnings("unchecked")
    public void aliasAndUnion() {
        // 只需要保证和union后的别名一致即可
        Page<DemoUser> userPage = queryFactory.select(qDemoUser)
                .from(SQLExpressions.unionAll(
                        SQLExpressions.selectFrom(qDemoUser)
                                .where(qDemoUser.gender.eq(0)),
                        SQLExpressions.selectFrom(qDemoUser)
                                .where(qDemoUser.gender.eq(1)),
                        SQLExpressions.selectFrom(qDemoUser)
                                .where(qDemoUser.gender.eq(2))
                ).as(qDemoUser))
                .fetchPage(new Pager());
        System.out.println(userPage);

        // 复杂UNION，定义别名并使结果列和别名一致
        String aliasName = "alias";
        QDemoUser qDemoUserUnions = new QDemoUser(aliasName);
        QDemoAddress qDemoAddressUnions = new QDemoAddress(aliasName);

        Page<DemoUserDetail> detailPage = queryFactory.select(
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
                        .as(aliasName))
                .fetchPage(new Pager());
        System.out.println(detailPage);
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
     * 关联查询一对多层级对象集合。
     */
    @Test
    public void queryOneToMany() {
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

    /**
     * 更多表达式及自定义查询。
     */
    @Test
    public void customSqlTemplate() throws Exception {
        BaseQuery<Tuple> query = queryFactory.select(
                // 常用的函数
                qDemoUser.userId.avg(),
                qDemoUser.userId.max(),
                qDemoUser.userId.min(),
                qDemoUser.userId.sum(),
                qDemoUser.userId.count(),
                qDemoUser.userId.multiply(2),
                qDemoUser.userId.add(10),
                qDemoUser.userId.subtract(10),
                qDemoUser.userId.divide(2),
                Expressions.asNumber(2).sum(),
                Expressions.asNumber(1).count(),
                SQLExpressions.sum(Expressions.constant(3)),

                // CASE选择语句
                qDemoUser.gender.when(1).then("男")
                        .when(2).then("女")
                        .otherwise("保密")
                        .as("genderName"),

                // CASE判断语句
                Expressions.cases().when(
                        qDemoUser.gender.eq(qDemoUser.gender))
                        .then("性别相同")
                        .otherwise("性别不同")
                        .as("sameGender"),

                // 使用元信息作为别名
                Expressions.stringPath(qDemoUser.username.getMetadata()),
                Expressions.numberPath(BigInteger.class, qDemoUser.userId.getMetadata()),

                // 【不推荐】自定义列名和表达式
                Expressions.numberTemplate(Integer.class, "user_id % {0}", 1000),
                Expressions.stringTemplate("group_concat({0})", qDemoUser.username))

                .from(qDemoUser)

                // 【不推荐】使用指定索引优化查询
                .addFlag(QueryFlag.Position.BEFORE_FILTERS, " force index(ix_user_id) ")

                // 【不推荐】自定义条件表达式
                .where(Expressions.booleanTemplate(
                        "now() < {0} or now() > {0}", DateExpression.currentDate()))

                .groupBy(qDemoUser.userId);

        System.out.println(query.getSQL().getSQL());

        // 极端场景执行任意SQL（暂未遇到过）
        String anySql = "select 123 from dual";
        Connection connection = queryFactory.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(anySql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println(resultSet.getInt(1));
                }
            }
        }
    }

}