package canuran.query;

import com.querydsl.core.QueryFlag;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.dml.AbstractSQLUpdateClause;
import canuran.query.clause.BaseQuery;
import canuran.query.clause.BaseUpdate;
import canuran.query.paging.NumPaging;
import canuran.query.paging.Page;
import canuran.query.querydsldemo.entity.DemoAddress;
import canuran.query.querydsldemo.entity.DemoUser;
import canuran.query.querydsldemo.query.QDemoAddress;
import canuran.query.querydsldemo.query.QDemoUser;
import canuran.query.querydsldemo.vo.DemoAddressDetail;
import canuran.query.querydsldemo.vo.DemoUserDetail;
import canuran.query.querydsldemo.vo.DemoUserSimple;
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
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static canuran.query.querydsldemo.query.QDemoAddress.demoAddress;
import static canuran.query.querydsldemo.query.QDemoUser.demoUser;

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
     * 用做别名的表，当同一个SQL中重复使用这个表时才会用上，否则使用静态导入。
     * <p>
     * 表映射对象、路径、表达式等都是只会被读取（线程安全）的，可定义为全局的。
     * 它们提供的操作方法都会返回新的对象，原对象不变，所以要使用方法的返回值。
     */
    private QDemoUser subDemoUser = new QDemoUser("subDemoUser");
    private QDemoAddress subAddress = new QDemoAddress("subAddress");

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
        DemoUser demoUserDo = newDemoUser();

        // 新增实体、并获取主键
        Integer userId = queryFactory.insert(demoUser).insertGetKey(demoUserDo);
        System.out.println(userId);
        System.out.println(demoUserDo);

        // 新增实体、包含null属性
        demoUserDo = newDemoUser();
        demoUserDo.setGender(null);
        userId = queryFactory.insert(demoUser).insertWithNullGetKey(demoUserDo);
        System.out.println(userId);

        // 批量插入对象并获取主键
        List<DemoUser> newUsers = Arrays.asList(newDemoUser(), newDemoUser());
        List<Integer> userIds = queryFactory.insert(demoUser).insertGetKeys(newUsers);
        System.out.println(userIds);

        // 自定义字段、动态字段
        long rows = queryFactory.insert(demoUser)
                .set(demoUser.username, demoUserDo.getUsername())
                .set(demoUser.createTime, demoUserDo.getCreateTime())
                .setIfHasText(demoUser.password, demoUserDo.getPassword())
                .execute();
        System.out.println(rows);
    }

    /**
     * 更新示例，更多详见BaseQuery类。
     */
    @Test
    public void updateDemo() {
        DemoUser demoUserDo = queryFactory.selectFrom(demoUser).fetchFirst();

        // 更新实体、忽略null属性
        long rows = queryFactory.update(demoUser).updateBean(demoUserDo);
        System.out.println(rows);

        // 更新实体、包含null属性
        rows = queryFactory.update(demoUser).updateWithNull(demoUserDo);
        System.out.println(rows);

        // 条件更新、动态字段
        rows = queryFactory.update(demoUser).whereEqKey(demoUserDo.getUserId())
                .setIfHasText(demoUser.username, "元宝")
                .setIfNotNull(demoUser.createTime, new Date())
                .execute();
        System.out.println(rows);
    }

    /**
     * 删除示例，更多详见BaseQuery类。
     */
    @Test
    public void deleteDemo() {
        DemoUser demoUserDo = queryFactory.selectFrom(demoUser).fetchFirst();

        // 通过ID删除
        long rows = queryFactory.delete(demoUser).deleteByKey(demoUserDo.getUserId());
        System.out.println(rows);

        // 删除实体
        rows = queryFactory.delete(demoUser).deleteBean(demoUserDo);
        System.out.println(rows);

        // 条件删除、动态条件
        rows = queryFactory.delete(demoUser)
                .where(demoUser.userId.goe(demoUserDo.getUserId()))
                .whereIfHasText(demoUserDo.getUsername(), demoUser.username::contains)
                .execute();
        System.out.println(rows);
    }

    /**
     * 简单查询，更多详见BaseQuery类。
     */
    @Test
    public void simpleQuery() {
        // 根据ID查询
        DemoUser demoUserDo = queryFactory.selectFrom(demoUser).fetchByKey(1);
        System.out.println(demoUserDo);

        // 根据条件查询
        List<DemoUser> demoUsers = queryFactory.selectFrom(demoUser)
                .where(demoUser.userId.gt(0L))
                .fetch();
        System.out.println(demoUsers);

        // 查询结果自适应类型
        List<DemoUserSimple> demoUserSimples = queryFactory.selectFrom(demoUser)
                .fitBean(DemoUserSimple.class)
                .fetch();
        System.out.println(demoUserSimples);

        // 关联查询取两个表的全部属性
        List<Tuple> addressAndUser = queryFactory
                .select(demoAddress, demoUser)
                .from(demoAddress)
                .leftJoin(demoUser)
                .on(demoAddress.addressId.eq(demoUser.addressId))
                .fetch();
        System.out.println(addressAndUser);
    }

    /**
     * 分页查询，更多详见BaseQuery类。
     */
    @Test
    public void queryPage() {
        // 查询分页
        Page<DemoUser> demoUserPage = queryFactory.selectFrom(demoUser)
                .pagingIfNotnull(1, 10)
                .fetchPage();
        System.out.println(demoUserPage);

        // 只统计总数
        Page<DemoUser> countPage = queryFactory.selectFrom(demoUser)
                .pageFetchRows(false)
                .fetchPage();
        System.out.println(countPage);

        // 只查询数据
        Page<DemoUser> rowsPage = queryFactory.selectFrom(demoUser)
                .pagingIfNotnull(1, 10)
                .pageCountRows(false)
                .fetchPage();
        System.out.println(rowsPage);

        // 根据NumPaging对象分页，还有OffsetPaging分页
        Page<DemoUser> totalPage = queryFactory.selectFrom(demoUser)
                .pagingIfNotnull(new NumPaging(1, 10))
                .fetchPage();
        System.out.println(totalPage);

        // 智能分页，当没有更多数据时，只做统计，不再查询数据
        Page<DemoUser> smartPage = queryFactory.selectFrom(demoUser)
                .offset(100)
                .fetchPage();
        System.out.println(smartPage);
    }

    /**
     * 超全功能的查询案例。
     */
    @Test
    public void advanceQuery() {
        // 准备参数
        DemoUser demoUserDo = newDemoUser();
        Integer userId = queryFactory.insert(demoUser).insertGetKey(demoUserDo);
        String address = "上海";
        Date startDate = new Date(System.currentTimeMillis() - 36000000L);
        Date endDate = new Date();
        String orderParam = "username asc";

        // 完全链式方法调用
        Page<Tuple> tuplePage = queryFactory.select(
                // 指定字段与常用函数
                demoUser.username,
                demoUser.createTime,
                demoUser.userId.avg(),
                demoUser.userId.max(),
                demoUser.userId.min(),
                demoUser.userId.sum(),
                demoUser.userId.count(),
                demoUser.userId.multiply(2),
                demoUser.userId.add(10),
                demoUser.userId.subtract(10),
                demoUser.userId.divide(2))
                .distinct()

                // 多表关联
                .from(demoUser)
                .leftJoin(demoAddress)
                .on(demoUser.addressId.eq(demoAddress.addressId))
                // 动态关联条件
                .onIfNotNull(address, demoAddress.name::contains)

                // 简单动态条件
                .whereIfNotNull(demoUserDo.getUsername(), demoUser.username::contains)
                .whereIfNotNull(demoUserDo.getCreateTime(), demoUser.createTime::goe)
                .whereIfNotNull(demoUserDo.getGender(), demoUser.gender::eq)
                // 万能动态条件
                .whereIfTrue(userId != null && userId > 0, () -> demoUser.userId.goe(userId))

                // 复杂条件组合
                .where(demoUser.username.contains("元").or(demoUser.username.contains("宝"))
                        .and(demoUser.createTime.goe(startDate).or(demoUser.createTime.loe(endDate))))

                // 分组
                .groupBy(demoUser.gender)
                // 分组条件
                .having(demoUser.userId.count().gt(0))

                // 原生的排序方法
                .orderBy(demoUser.createTime.desc().nullsFirst())
                // 根据传入参数自动排序
                .orderBy(orderParam)

                // 获取结果
                .pagingIfNotnull(1, 100)
                .fetchPage();
        System.out.println(tuplePage);
    }

    /**
     * 使用各种子查询。
     */
    @Test
    public void selectSubQuery() {
        List<String> names = queryFactory
                // 结果中使用子查询
                .select(SQLExpressions.select(demoUser.username)
                        .from(demoUser)
                        .where(demoUser.userId.eq(subDemoUser.userId)))
                // 嵌套子查询
                .from(SQLExpressions.selectFrom(demoUser)
                        .where(demoUser.userId.eq(1))
                        .as(subDemoUser))
                // 条件中使用子查询、EXISTS
                .where(SQLExpressions.selectOne()
                        .from(demoUser)
                        .where(demoUser.userId.eq(subDemoUser.userId))
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
        // 简单UNION查询
        List<DemoUser> demoUsers = queryFactory.query()
                .union(SQLExpressions.selectFrom(demoUser)
                                .where(demoUser.gender.eq(0)),
                        SQLExpressions.selectFrom(demoUser)
                                .where(demoUser.gender.eq(1)),
                        SQLExpressions.selectFrom(demoUser)
                                .where(demoUser.gender.eq(2)))
                .fetch();
        System.out.println(demoUsers);

        // 复杂UNION，定义别名并使结果列和别名一致
        String aliasName = "alias";
        QDemoUser qDemoUserUnions = new QDemoUser(aliasName);
        QDemoAddress qDemoAddressUnions = new QDemoAddress(aliasName);

        Page<DemoUserDetail> detailPage = queryFactory.select(
                QueryUtils.fitBean(DemoUserDetail.class,
                        qDemoUserUnions,
                        qDemoAddressUnions.name.as("addressName")))
                .from(SQLExpressions.union(
                        SQLExpressions.select(demoUser, demoAddress.name).from(demoUser)
                                .leftJoin(demoAddress)
                                .on(demoUser.addressId.eq(demoAddress.addressId))
                                .where(demoUser.gender.eq(1)),
                        SQLExpressions.select(demoUser, demoAddress.name).from(demoUser)
                                .leftJoin(demoAddress)
                                .on(demoUser.addressId.eq(demoAddress.addressId))
                                .where(demoUser.gender.eq(2)))
                        .as(aliasName))
                .fetchPage();
        System.out.println(detailPage);
    }

    /**
     * 执行批量操作。
     */
    @Test
    public void executeBatch() {
        // 批量更新，插入和删除操作类似，设置参数后调用addBatch即可
        AbstractSQLUpdateClause<?> update = queryFactory.update(demoUser);

        update.set(demoUser.username, demoUser.username.append("哥哥"))
                .where(demoUser.gender.eq(1)).addBatch();

        update.set(demoUser.username, demoUser.username.append("妹妹"))
                .where(demoUser.gender.eq(2)).addBatch();

        System.out.println(((BaseUpdate) update).executeBatch());
    }

    /**
     * 关联查询一对多层级对象集合。
     */
    @Test
    public void queryOneToMany() {
        // 先关联查询自动封装成并列的对象
        QBean<DemoAddressDetail> qAddressDetail = Projections
                .bean(DemoAddressDetail.class, demoAddress.all());
        List<Tuple> rows = queryFactory.select(
                qAddressDetail, subAddress)
                .from(demoAddress)
                .leftJoin(subAddress)
                .on(demoAddress.addressId.eq(subAddress.parentId))
                .where(demoAddress.parentId.isNull())
                .fetch();

        // 将并列的对象转换成一对多层级对象
        List<DemoAddressDetail> addressDetails = QueryUtils.rowsToTree(
                rows, qAddressDetail, subAddress,
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
                // 基础表达式
                Expressions.asNumber(2).sum(),
                demoUser.userId.count(),
                SQLExpressions.sum(Expressions.constant(3)),

                // CASE选择语句
                demoUser.gender.when(1).then("男")
                        .when(2).then("女")
                        .otherwise("保密")
                        .as("genderName"),

                // CASE判断语句
                Expressions.cases().when(
                        demoUser.gender.eq(demoUser.gender))
                        .then("性别相同")
                        .otherwise("性别不同")
                        .as("sameGender"),

                // 使用元信息作为别名
                Expressions.stringPath(demoUser.username.getMetadata()),
                Expressions.numberPath(BigInteger.class, demoUser.userId.getMetadata()),

                // 【慎用】自定义查询结果表达式
                Expressions.stringTemplate("group_concat({0})", demoUser.username))

                .from(demoUser)

                // 【慎用】使用指定索引优化查询
                .addFlag(QueryFlag.Position.BEFORE_FILTERS, " force index(ix_user_id) ")

                // 【慎用】自定义条件表达式
                .where(Expressions.booleanTemplate(
                        "now() < {0} or now() > {0}", DateExpression.currentDate()))

                .groupBy(demoUser.userId);

        System.out.println(query.getSQL().getSQL());

        nativeSql();
    }

    /**
     * 极端场景执行本地SQL，暂未遇到过。
     */
    @Test
    public void nativeSql() throws SQLException {
        String anySql = "select 123 from dual";
        Connection connection = queryFactory.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(anySql)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println(resultSet.getInt(1));
                }
            }
        }
    }

}