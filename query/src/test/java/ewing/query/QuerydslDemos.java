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
import ewing.query.querydsldemo.vo.DemoAddressUser;
import ewing.query.querydsldemo.vo.DemoUserDetail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.BadSqlGrammarException;
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
     * 原始API进行简单的CRUD操作。
     */
    @Test
    public void originOperation() throws Exception {
        DemoUser demoUser = newDemoUser();

        // 1.新增实体并返回主键
        Integer userId = queryFactory.insert(qDemoUser)
                .populate(demoUser)
                .executeWithKey(qDemoUser.userId);
        System.out.println(userId);

        // 2.使用对象或简单变量更新实体
        long updateCount = queryFactory.update(qDemoUser)
                .where(qDemoUser.userId.eq(userId))
                // 使用对象的属性填充
                .populate(demoUser)
                // 或使用作用域中的简单变量
                .set(qDemoUser.username, "元宝")
                .set(qDemoUser.password, "123ABC")
                // 或使用字段表达式更新（事务一致性）
                .set(qDemoUser.gender, qDemoUser.gender.add(1))
                .execute();
        System.out.println(updateCount);

        // 3.根据条件查询实体
        demoUser = queryFactory.selectFrom(qDemoUser)
                .where(qDemoUser.userId.eq(userId))
                .where(qDemoUser.username.contains("元宝"))
                .fetchOne();
        System.out.println(demoUser);

        // 4.根据条件删除实体
        long deleteCount = queryFactory.delete(qDemoUser)
                .where(qDemoUser.userId.eq(userId))
                .where(qDemoUser.username.contains("元宝"))
                .execute();
        System.out.println(deleteCount);

        // 5.极端场景执行任意SQL（暂未遇到过）
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

    /**
     * 使用简单封装的API进行CRUD操作。
     */
    @Test
    public void simpleOperation() {
        DemoUser demoUser = newDemoUser();

        // 1.新增实体，更多快捷方法见BaseInsert类
        Integer userId = queryFactory.insert(qDemoUser).insertGetKey(demoUser);
        System.out.println(userId);
        System.out.println(demoUser);

        // 批量插入对象并获取主键
        List<DemoUser> newUsers = Arrays.asList(newDemoUser(), newDemoUser());
        List<Integer> userIds = queryFactory.insert(qDemoUser).insertGetKeys(newUsers);
        System.out.println(userIds);

        // 2.更新实体，更多快捷方法见BaseUpdate类
        long rows = queryFactory.update(qDemoUser).updateBean(demoUser);
        System.out.println(rows);

        rows = queryFactory.update(qDemoUser).updateWithNull(demoUser);
        System.out.println(rows);

        // 动态更新符合要求的字段
        rows = queryFactory.update(qDemoUser).whereEqKey(userId)
                .setIfHasText(qDemoUser.username, "元宝")
                .setIfNotNull(qDemoUser.createTime, new Date())
                .execute();
        System.out.println(rows);

        // 3.查询实体，更多快捷方法见BaseQuery类
        List<DemoUser> demoUsers = queryFactory.selectFrom(qDemoUser).fetch();
        System.out.println(demoUsers);

        demoUser = queryFactory.selectFrom(qDemoUser).fetchByKey(userId);
        System.out.println(demoUser);

        // 4.删除实体，更多快捷方法见BaseDelete类
        rows = queryFactory.delete(qDemoUser).deleteByKey(userId);
        System.out.println(rows);

        rows = queryFactory.delete(qDemoUser).deleteBean(demoUser);
        System.out.println(rows);
    }

    /**
     * 进阶构建复杂查询。
     */
    @Test
    public void advanceQuery() {
        // 准备数据
        DemoUser demoUser = newDemoUser();
        queryFactory.insert(qDemoUser).insertGetKey(demoUser);
        String address = "上海";

        // 完全链式方法调用
        Page<DemoUser> userPage = queryFactory.selectFrom(qDemoUser)
                .distinct()

                // 多表关联、动态关联条件
                .leftJoin(qDemoAddress)
                .on(qDemoUser.addressId.eq(qDemoAddress.addressId))
                .onIfNotNull(address, qDemoAddress.name::contains)

                // 简单的动态查询条件
                .whereIfNotNull(demoUser.getUsername(), qDemoUser.username::contains)
                .whereIfNotNull(demoUser.getCreateTime(), qDemoUser.createTime::goe)
                .whereIfNotNull(demoUser.getGender(), qDemoUser.gender::eq)

                // 高级自由嵌套条件表达式
                .where(qDemoUser.username.contains("元")
                        .and(qDemoUser.gender.eq(1))
                        .or(qDemoUser.username.contains("宝")
                                .and(qDemoUser.gender.eq(0))
                        ))

                // 原生的排序方法
                .orderBy(qDemoUser.createTime.desc().nullsFirst())
                // 根据传入的参数自动排序
                .orderBy("username asc")

                // 获取结果
                .fetchPage(new Pager());
        System.out.println(userPage);
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
     * 聚合查询及高级结果集转换。
     */
    @Test
    public void advanceResults() {
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
        List<Tuple> addressAndUser = queryFactory
                .select(qDemoAddress, qDemoUser)
                .from(qDemoAddress)
                .leftJoin(qDemoUser)
                .on(qDemoAddress.addressId.eq(qDemoUser.addressId))
                .fetch();
        System.out.println(addressAndUser);
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
    public void customSqlTemplate() {
        BaseQuery<Tuple> query = queryFactory.select(
                // 常用的函数和表达式
                Expressions.asNumber(2).sum(),
                Expressions.asNumber(1).count(),
                SQLExpressions.sum(Expressions.constant(3)),
                qDemoUser.createTime.milliSecond().avg(),
                qDemoUser.gender.when(1).then("男")
                        .when(2).then("女")
                        .otherwise("保密")
                        .as("genderName"),
                Expressions.cases().when(
                        qDemoUser.gender.eq(qDemoUser.gender))
                        .then("性别相同")
                        .otherwise("性别不同")
                        .as("sameGender"),

                // 使用元信息作为别名
                Expressions.stringPath(qDemoUser.username.getMetadata()),
                Expressions.numberPath(BigInteger.class, qDemoUser.userId.getMetadata()),

                // 【不推荐】自定义列名和表达式
                Expressions.datePath(Date.class, "demo_user.create_time"),
                Expressions.numberTemplate(Integer.class, "user_id % {0}", 1000),
                Expressions.stringTemplate("group_concat({0})", qDemoUser.username))

                // 【强烈不推荐】完全自定义查询语句
                .from(Expressions.path(DemoUser.class, "(select * from demo_user)").as(qDemoUser))

                // 【不推荐】使用指定索引优化查询
                .addFlag(QueryFlag.Position.BEFORE_FILTERS, " force index(ix_user_id) ")

                // 【不推荐】自定义条件表达式
                .where(Expressions.booleanTemplate(
                        "now() < {0} or now() > {0}", DateExpression.currentDate()))

                .groupBy(qDemoUser.userId);
        try {
            System.out.println(query.getSQL().getSQL());
            System.out.println(query.fetchFirst());
        } catch (BadSqlGrammarException e) {
            System.out.println("数据库不支持该SQL！");
        }
    }

}