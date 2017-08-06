package ewing.common.queryutils;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.types.*;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;

/**
 * 查询帮助类。
 */
public class QueryHelper {

    private QueryHelper() {
    }

    /**
     * 使用分页参数和查询对象进行分页查询。
     */
    public static <T> PageData<T> queryPage(PageParam pageParam, SQLQuery<T> query) {
        // 是否统计总数
        if (pageParam.isCount()) {
            PageData<T> pageData = new PageData<>();
            pageData.setTotal(query.fetchCount());
            if (pageData.getTotal() < 1) {
                // 一条也没有则返回空集
                return pageData.setContent(Collections.emptyList());
            } else {
                query.limit(pageParam.getLimit()).offset(pageParam.getOffset());
                return pageData.setContent(query.fetch());
            }
        } else {
            query.limit(pageParam.getLimit()).offset(pageParam.getOffset());
            return new PageData<>(query.fetch());
        }
    }

    /**
     * 使用全部Expression参数查询指定类型的Bean。
     */
    public static <T> QBean<T> projectionExpressions(Class<? extends T> type,
                                                     RelationalPathBase pathBase, Expression... expressions) {
        if (pathBase == null)
            return Projections.bean(type, expressions);
        Path[] paths = pathBase.all();
        Expression<?>[] allExpress = new Expression[paths.length + expressions.length];
        System.arraycopy(paths, 0, allExpress, 0, paths.length);
        System.arraycopy(expressions, 0, allExpress, paths.length, expressions.length);
        return Projections.bean(type, allExpress);
    }

    /**
     * 使用与Bean属性有对应的Expression参数查询Bean。
     */
    public static <T> QBean<T> projectionBean(Class<? extends T> type,
                                              RelationalPathBase pathBase, Expression... expressions) {
        if (pathBase == null)
            return Projections.bean(type, expressions);
        ImmutableMap.Builder<String, Expression<?>> mapBuilder = ImmutableMap.builder();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
            matchBindings(mapBuilder, properties, pathBase.all());
            matchBindings(mapBuilder, properties, expressions);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return Projections.bean(type, mapBuilder.build());
    }

    /**
     * 根据属性匹配Expression并添加绑定到MapBuilder。
     * 实现逻辑参考自QBean.createBindings方法。
     */
    private static void matchBindings(ImmutableMap.Builder<String, Expression<?>> mapBuilder,
                                      PropertyDescriptor[] properties, Expression<?>[] expressions) {
        for (Expression<?> expression : expressions) {
            if (expression instanceof Path<?>) {
                String name = ((Path<?>) expression).getMetadata().getName();
                for (PropertyDescriptor property : properties) {
                    if (property.getName().equals(name) && property.getWriteMethod() != null) {
                        mapBuilder.put(name, expression);
                        break; // 匹配到属性结束内层循环
                    }
                }
            } else if (expression instanceof Operation<?>) {
                Operation<?> operation = (Operation<?>) expression;
                if (operation.getOperator() == Ops.ALIAS
                        && operation.getArg(1) instanceof Path<?>) {
                    String name = ((Path<?>) operation.getArg(1)).getMetadata().getName();
                    for (PropertyDescriptor property : properties) {
                        if (property.getName().equals(name) && property.getWriteMethod() != null) {
                            Expression<?> express = operation.getArg(0);
                            if (express instanceof FactoryExpression || express instanceof GroupExpression) {
                                mapBuilder.put(name, express);
                            } else {
                                mapBuilder.put(name, operation);
                            }
                            break; // 匹配到属性结束内层循环
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported expression " + expression);
                }
            } else {
                throw new IllegalArgumentException("Unsupported expression " + expression);
            }
        }
    }

}
