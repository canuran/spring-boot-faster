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
import com.querydsl.sql.dml.DefaultMapper;
import com.querydsl.sql.dml.SQLUpdateClause;
import ewing.StartApp;
import ewing.common.GsonUtils;
import ewing.common.JsonConverter;
import ewing.common.QueryHelper;
import ewing.common.paging.Page;
import ewing.common.paging.Pager;
import ewing.config.QueryFactory;
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

import java.sql.Timestamp;
import java.util.ArrayList;
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
    private QueryFactory queryFactory;

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
        demoUser.setBirthday(new Timestamp(System.currentTimeMillis()));
        demoUser.setAddressId(1);
        return demoUser;
    }

    /**
     * 简单的CRUD操作。
     */
    @Test
    public void simpleCrud() {
        DemoUser demoUser = newDemoUser();
        // 新增实体
        Integer userId = queryFactory.insert(qDemoUser)
                .populate(demoUser, DefaultMapper.WITH_NULL_BINDINGS)
                .executeWithKey(qDemoUser.userId);
        System.out.println(userId);
        // 快捷新增实体（不包含null）
        queryFactory.insertWithKey(qDemoUser, demoUser);

        // 更新实体
        demoUser.setUserId(userId);
        demoUser.setUsername("EWING");
        demoUser.setPassword("ABC123");
        queryFactory.update(qDemoUser)
                .where(qDemoUser.userId.eq(demoUser.getUserId()))
                .populate(demoUser)
                .execute();
        // 更新部分属性，新增也可以这样用
        queryFactory.update(qDemoUser)
                .set(qDemoUser.username, "Ewing")
                .set(qDemoUser.password, "123ABC")
                .where(qDemoUser.userId.eq(demoUser.getUserId()))
                .execute();
        // 快捷更新实体（不包含null）
        queryFactory.updateByBean(qDemoUser, demoUser);

        // 查询实体
        demoUser = queryFactory.selectFrom(qDemoUser)
                .where(qDemoUser.userId.eq(demoUser.getUserId()))
                .fetchOne();
        // 查询部分属性，多个属性默认返回为Tuple，可自定义返回类型
        String username = queryFactory.select(qDemoUser.username)
                .from(qDemoUser)
                .where(qDemoUser.userId.eq(demoUser.getUserId()))
                .fetchOne();
        System.out.println(username);
        // 快捷根据ID查询实体
        demoUser = queryFactory.selectByKey(qDemoUser, demoUser.getUserId());
        DemoUserDetail userDetail = queryFactory.selectToBean(
                qDemoUser, DemoUserDetail.class, demoUser.getUserId());
        System.out.println(JsonConverter.toJson(userDetail));

        // 删除实体
        queryFactory.delete(qDemoUser)
                .where(qDemoUser.userId.eq(demoUser.getUserId()))
                .execute();
        System.out.println(JsonConverter.toJson(demoUser));
        // 快捷根据ID删除实体
        queryFactory.deleteByKey(qDemoUser, demoUser.getUserId());
        queryFactory.deleteByBean(qDemoUser, demoUser);
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
                .orderBy(qDemoUser.birthday.desc().nullsFirst());
        // where可多次使用，相当于and，注意and优先级高于or
        query.where(qDemoAddress.name.contains("深圳")
                .and(qDemoUser.username.contains("元")
                        .and(qDemoUser.gender.eq(1))
                        .or(qDemoUser.username.contains("宝")
                                .and(qDemoUser.gender.eq(0))
                        )));
        // 查看SQL和参数（默认提供SQL日志）
        SQLBindings sqlBindings = query.getSQL();
        System.out.println(sqlBindings.getSQL());
        System.out.println(sqlBindings.getBindings());
        // 分页获取数据
        Page<DemoUser> userPage = QueryHelper.queryPage(new Pager(), query);
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
                .select(SQLExpressions.select(qDemoUser.username)
                        .from(qDemoUser)
                        .where(qDemoUser.userId.eq(UserA.userId)))
                // 嵌套子查询
                .from(SQLExpressions.selectFrom(qDemoUser)
                        .where(qDemoUser.userId.eq(1))
                        .as(UserA))
                // 条件中使用子查询
                .where(SQLExpressions.selectOne()
                        .from(qDemoUser)
                        .where(qDemoUser.userId.eq(UserA.userId))
                        .exists())
                .fetch();
        System.out.println(JsonConverter.toJson(names));
    }

    /**
     * 关联分组并取自定义字段。
     */
    @Test
    public void queryJoinGroup() {
        // 统计城市用户数
        List<DemoAddressUser> addressUsers = queryFactory
                .select(Projections.bean(DemoAddressUser.class,
                        qDemoAddress.name, qDemoUser.count().as("totalUser")))
                .from(qDemoAddress)
                .leftJoin(qDemoUser).on(qDemoAddress.addressId.eq(qDemoUser.userId))
                .groupBy(qDemoAddress.name)
                .having(qDemoUser.count().gt(0))
                .fetch();
        System.out.println(JsonConverter.toJson(addressUsers));
        // 关联查询取两个表的全部属性
        List addressAndUser = queryFactory
                .select(Projections.list(qDemoAddress, qDemoUser))
                .from(qDemoAddress)
                .leftJoin(qDemoUser).on(qDemoAddress.addressId.eq(qDemoUser.addressId))
                .fetch();
        System.out.println(JsonConverter.toJson(addressAndUser));
    }

    /**
     * 自定义SQL查询。
     */
    @Test
    public void customQuery() {
        SQLQuery<Tuple> query = queryFactory.select(
                // 常用的表达式构建方法
                Expressions.asNumber(1).count(),
                Expressions.asNumber(2).sum(),
                SQLExpressions.sum(Expressions.constant(3)),
                Expressions.cases().when(qDemoUser.birthday.max()
                        .gt(qDemoUser.birthday.min())).then("生日不同")
                        .otherwise("生日相同"),
                qDemoUser.gender.avg())
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
     * 聚合UNION查询结果。
     */
    @Test
    @SuppressWarnings("unchecked")
    public void queryUnion() {
        List<DemoUser> demoUsers = queryFactory.query().unionAll(
                SQLExpressions.selectFrom(qDemoUser).where(qDemoUser.gender.eq(0)),
                SQLExpressions.selectFrom(qDemoUser).where(qDemoUser.gender.eq(1)),
                SQLExpressions.selectFrom(qDemoUser).where(qDemoUser.gender.eq(2))
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
                        qDemoUser,
                        qDemoUser.gender.when(1).then("男")
                                .when(2).then("女")
                                .otherwise("保密").as("genderName"),
                        qDemoAddress.name.as("addressName")))
                .from(qDemoUser)
                .leftJoin(qDemoAddress).on(qDemoUser.addressId.eq(qDemoAddress.addressId))
                .fetch();
        System.out.println(JsonConverter.toJson(demoUsers));
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
     * 关联查询下级对象集合，把子对象一次性查询出来。
     */
    @Test
    public void querySubObjects() {
        // 先关联查询自动封装成并列的对象
        QBean<DemoAddressDetail> addressDetailQBean = Projections
                .bean(DemoAddressDetail.class, qDemoAddress.all());
        List<Tuple> tuples = queryFactory.select(
                addressDetailQBean, qSubAddress)
                .from(qDemoAddress)
                .leftJoin(qSubAddress)
                .on(qDemoAddress.addressId.eq(qSubAddress.parentId))
                .fetch();
        // 将并列的对象转换成上下级结构
        List<DemoAddressDetail> addressDetails = new ArrayList<>();
        for (Tuple tuple : tuples) {
            // 第一级对象，一级集合中存在就取已有的，不存在则添加
            DemoAddressDetail addressDetail = tuple.get(addressDetailQBean);
            if (addressDetail == null)
                continue;
            int index = addressDetails.indexOf(addressDetail);
            if (index == -1) {
                addressDetails.add(addressDetail);
            } else {
                addressDetail = addressDetails.get(index);
            }
            // 第二级对象，二级集合中存在就跳过，不存在则添加
            DemoAddress subAddress = tuple.get(qSubAddress);
            if (subAddress == null || subAddress.getAddressId() == null)
                continue;
            if (addressDetail.getSubAddresses() == null)
                addressDetail.setSubAddresses(new ArrayList<>());
            if (addressDetail.getSubAddresses().indexOf(subAddress) == -1)
                addressDetail.getSubAddresses().add(subAddress);
        }
        System.out.println(GsonUtils.toJson(addressDetails));
    }

}