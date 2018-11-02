package ewing.query;

import com.mysema.commons.lang.Assert;
import com.querydsl.core.FetchableQuery;
import com.querydsl.core.JoinExpression;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.core.util.BeanUtils;
import com.querydsl.core.util.ReflectionUtils;
import com.querydsl.sql.PrimaryKey;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import ewing.query.paging.Page;
import ewing.query.paging.Pager;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 查询帮助类。
 *
 * @author Ewing
 */
public class QueryUtils {

    private QueryUtils() {
    }

    /**
     * 常量转换成表达式。
     */
    public static <T> Expression<T> constant(T value) {
        return value == null ? Expressions.nullExpression() : Expressions.constant(value);
    }

    /**
     * 使用分页参数和查询对象进行分页查询。
     * <p>
     * 分页是多次查询，确保开启事务！
     */
    public static <T> Page<T> queryPage(FetchableQuery<T, ?> query, Pager pager) {
        if (pager == null) {
            return new Page<>(query.fetch());
        } else if (pager.isCount()) {
            Page<T> page = new Page<>();
            page.setTotal(query.fetchCount());
            // 能查到数据才进行查询
            if (pager.getLimit() > 0 && page.getTotal() > 0 && page.getTotal() > pager.getOffset()) {
                query.limit(pager.getLimit()).offset(pager.getOffset());
                page.setRows(query.fetch());
                return page;
            } else {
                page.setRows(Collections.emptyList());
                return page;
            }
        } else {
            List<T> rows = pager.getLimit() > 0 ? query.limit(pager.getLimit())
                    .offset(pager.getOffset()).fetch() : Collections.emptyList();
            return new Page<>(rows);
        }
    }

    /**
     * 获取主键中的字段属性。
     */
    @SuppressWarnings("unchecked")
    public static List<? extends Path<?>> getKeyPaths(RelationalPathBase pathBase) {
        Assert.notNull(pathBase, "PathBase missing");
        PrimaryKey primaryKey = pathBase.getPrimaryKey();
        Assert.notNull(primaryKey, "PrimaryKey missing");
        return primaryKey.getLocalColumns();
    }

    /**
     * 获取实体中的单字段主键。
     */
    @SuppressWarnings("unchecked")
    public static <E> Path<E> getSinglePrimaryKey(RelationalPathBase pathBase) {
        List<? extends Path<?>> keyPaths = getKeyPaths(pathBase);
        Assert.notNull(keyPaths, "Key paths missing");
        Assert.isTrue(keyPaths.size() == 1, "Multiple primary key");
        return (Path<E>) keyPaths.get(0);
    }

    /**
     * 创建主键等于参数的表达式。
     */
    @SuppressWarnings("unchecked")
    public static BooleanExpression baseKeyEquals(RelationalPathBase pathBase, Object key) {
        List<? extends Path<?>> keyPaths = getKeyPaths(pathBase);
        Assert.notEmpty(keyPaths, "Key paths missing");
        if (keyPaths.size() == 1) {
            return ((SimpleExpression) keyPaths.get(0)).eq(key);
        } else {
            // 多个主键时使用实体作为主键值创建表达式
            return beanKeyEquals(keyPaths, key);
        }
    }

    /**
     * 使用实体作为主键值创建表达式。
     */
    @SuppressWarnings("unchecked")
    public static BooleanExpression beanKeyEquals(List<? extends Path<?>> keyPaths, Object bean) {
        Assert.notNull(bean, "Bean param missing");
        Assert.notEmpty(keyPaths, "Key paths missing");
        BooleanExpression equals = Expressions.FALSE;
        for (Path path : keyPaths) {
            String name = path.getMetadata().getName();
            Method getter = ReflectionUtils.getGetterOrNull(bean.getClass(), name);
            Assert.notNull(getter, "Key property getter missing");
            try {
                BooleanExpression keyEqual = ((SimpleExpression) path).eq(getter.invoke(bean));
                equals = (equals == Expressions.FALSE ? keyEqual : equals.and(keyEqual));
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
        return equals;
    }

    /**
     * 获取Bean的属性Setter。
     */
    @SuppressWarnings("unchecked")
    public static Method findSetter(Object bean, String name, Class type) {
        Assert.notNull(bean, "Bean param missing");
        String methodName = "set" + BeanUtils.capitalize(name);
        Class beanClass = bean.getClass();
        while (beanClass != null && !beanClass.equals(Object.class)) {
            try {
                return beanClass.getDeclaredMethod(methodName, type);
            } catch (SecurityException | NoSuchMethodException e) {
                // 跳过当前类并从父类查找
                beanClass = beanClass.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 给Bean设置属性值。
     */
    public static void setBeanProperty(Object bean, String name, Object value) {
        Assert.notNull(value, "Property value missing");
        Method setter = findSetter(bean, name, value.getClass());
        Assert.notNull(setter, "Key setter missing");
        try {
            setter.invoke(bean, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用与Bean属性匹配的Expression（包括实体查询对象）参数查询Bean。
     */
    public static <T> QBean<T> fitBean(Class<? extends T> type, Expression... expressions) {
        try {
            // 获取到Bean的所有属性
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
            Map<String, Expression<?>> expressionMap = new HashMap<>();
            for (PropertyDescriptor property : properties) {
                if (property.getWriteMethod() != null) {
                    String name = property.getName();
                    for (Expression expression : expressions) {
                        if (expression instanceof RelationalPath) {
                            // 逐个匹配实体查询对象中的路径
                            for (Object path : ((RelationalPath) expression).getColumns()) {
                                matchBindings(expressionMap, name, (Expression) path);
                            }
                        } else if (expression instanceof FactoryExpression) {
                            // 逐个匹配FactoryExpression中的参数
                            for (Object arg : ((FactoryExpression) expression).getArgs()) {
                                matchBindings(expressionMap, name, (Expression) arg);
                            }
                        } else {
                            // 匹配单个路径表达式是否用的上
                            matchBindings(expressionMap, name, expression);
                        }
                    }
                }
            }
            return Projections.bean(type, expressionMap);
        } catch (IntrospectionException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * 根据属性匹配Expression并添加绑定到Map中。
     * 实现逻辑参考自QBean.createBindings方法。
     */
    private static void matchBindings(Map<String, Expression<?>> expressionMap,
                                      String property, Expression expression) {
        if (expression instanceof Path<?>) {
            Path path = (Path<?>) expression;
            if (property.equals(path.getMetadata().getName())) {
                expressionMap.putIfAbsent(property, path);
            }
        } else if (expression instanceof Operation<?>) {
            Operation<?> operation = (Operation<?>) expression;
            if (operation.getOperator() == Ops.ALIAS && operation.getArg(1) instanceof Path<?>) {
                if (property.equals(((Path<?>) operation.getArg(1)).getMetadata().getName())) {
                    Expression<?> express = operation.getArg(0);
                    if (express instanceof FactoryExpression || express instanceof GroupExpression) {
                        expressionMap.putIfAbsent(property, express);
                    } else {
                        expressionMap.putIfAbsent(property, operation);
                    }
                }
            } else {
                throw new IllegalArgumentException("Unsupported expression " + expression);
            }
        } else {
            throw new IllegalArgumentException("Unsupported expression " + expression);
        }
    }

    private static final Pattern ORDER_PATTERN = Pattern.compile("(?i)([a-z_0-9]+)\\s*?(asc|desc)?");

    private static Matcher getOrderMatcher(String orderClause) {
        Assert.hasText(orderClause, "Order clause missing");
        orderClause = orderClause.trim().toLowerCase();
        Matcher matcher = ORDER_PATTERN.matcher(orderClause);
        Assert.isTrue(matcher.matches(), "Illegal order clause");
        return matcher;
    }

    /**
     * 获取排序器，例如：name、name asc、name desc。
     */
    public static OrderSpecifier<?> getOrderSpecifier(Collection<JoinExpression> joins, String orderClause) {
        Matcher matcher = getOrderMatcher(orderClause);
        String name = matcher.group(1);
        if (joins != null && !joins.isEmpty()) {
            for (JoinExpression join : joins) {
                if (join.getTarget() instanceof RelationalPathBase) {
                    for (Path path : ((RelationalPathBase) join.getTarget()).all()) {
                        if (path instanceof ComparableExpressionBase
                                && path.getMetadata().getName().equalsIgnoreCase(name)) {
                            ComparableExpressionBase expression = (ComparableExpressionBase) path;
                            return "desc".equals(matcher.group(2)) ? expression.desc() : expression.asc();
                        }
                    }
                } else if (join.getTarget() instanceof ComparableExpressionBase) {
                    ComparableExpressionBase expression = (ComparableExpressionBase) join.getTarget();
                    return "desc".equalsIgnoreCase(matcher.group(2)) ? expression.desc() : expression.asc();
                }
            }
        }
        return new OrderSpecifier<Comparable>("desc".equalsIgnoreCase(matcher.group(2)) ?
                Order.DESC : Order.ASC, Expressions.comparablePath(Comparable.class, name));
    }

    /**
     * 转换具有父子关系的关联查询的结果集为树形对象集合。
     * <p>
     * 例如把 List<Tuple> rows 转换为树形对象集合：
     * QueryUtils.rowsToTree(
     * rows, qParent, qChild,
     * Parent::getKey,
     * Child::getKey,
     * Parent::getChildren,
     * Parent::setChildren))
     */
    public static <PARENT, CHILD, QPARENT extends Expression<PARENT>, QCHILD extends Expression<CHILD>>
    List<PARENT> rowsToTree(List<Tuple> rows, QPARENT qParent, QCHILD qChild,
                            Function<PARENT, Serializable> parentKeyGetter,
                            Function<CHILD, Serializable> childKeyGetter,
                            Function<PARENT, List<CHILD>> childrenGetter,
                            BiConsumer<PARENT, List<CHILD>> childrenSetter) {
        if (rows == null) {
            return null;
        }
        if (qParent == null || qChild == null
                || parentKeyGetter == null || childKeyGetter == null
                || childrenGetter == null || childrenSetter == null) {
            throw new IllegalArgumentException("Arguments missing");
        }
        // 取父对象并且根据父对象的Key去重
        Map<Serializable, PARENT> parentMap = new HashMap<>();
        for (Tuple row : rows) {
            if (row != null) {
                PARENT parent = row.get(qParent);
                if (parent != null && parentKeyGetter.apply(parent) != null) {
                    PARENT exists = parentMap.putIfAbsent(parentKeyGetter.apply(parent), parent);
                    parent = exists == null ? parent : exists;
                    // 取子对象并添加到父对象中
                    CHILD child = row.get(qChild);
                    if (child != null && childKeyGetter.apply(child) != null) {
                        if (childrenGetter.apply(parent) == null) {
                            childrenSetter.accept(parent, new ArrayList<>());
                        }
                        childrenGetter.apply(parent).add(child);
                    }
                }
            }
        }
        return new ArrayList<>(parentMap.values());
    }

}
