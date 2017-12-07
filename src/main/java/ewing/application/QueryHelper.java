package ewing.application;

import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.types.*;
import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQuery;
import ewing.application.paging.Page;
import ewing.application.paging.Pager;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 查询帮助类。
 *
 * @author Ewing
 */
public class QueryHelper {

    private QueryHelper() {
    }

    /**
     * 使用分页参数和查询对象进行分页查询。
     */
    public static <T> Page<T> queryPage(Pager pager, SQLQuery<T> query) {
        // 是否统计总数
        if (pager.isCount()) {
            Page<T> page = new Page<>();
            page.setTotal(query.fetchCount());
            if (page.getTotal() < 1) {
                // 一条也没有则返回空集
                return page.setContent(Collections.emptyList());
            } else {
                query.limit(pager.getLimit()).offset(pager.getOffset());
                return page.setContent(query.fetch());
            }
        } else {
            query.limit(pager.getLimit()).offset(pager.getOffset());
            return new Page<>(query.fetch());
        }
    }

    /**
     * 使用与Bean属性匹配的Expression（包括实体查询对象）参数查询Bean。
     */
    public static <T> QBean<T> fitBean(
            Class<? extends T> type, Expression... expressions) {
        // 获取到Bean的所有属性
        PropertyDescriptor[] properties;
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            properties = beanInfo.getPropertyDescriptors();
        } catch (IntrospectionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        // 获取参数中能够用的上的表达式
        Map<String, Expression<?>> expressionMap = new HashMap<>();
        for (Expression expression : expressions) {
            if (expression instanceof RelationalPathBase) {
                // 逐个匹配实体查询对象中的路径
                Expression[] paths = ((RelationalPathBase) expression).all();
                for (Expression path : paths) {
                    matchBindings(expressionMap, properties, path);
                }
            } else {
                // 匹配单个路径表达式是否用的上
                matchBindings(expressionMap, properties, expression);
            }
        }
        return Projections.bean(type, expressionMap);
    }

    /**
     * 根据属性匹配Expression并添加绑定到MapBuilder。
     * 实现逻辑参考自QBean.createBindings方法。
     */
    private static void matchBindings(
            Map<String, Expression<?>> expressionMap,
            PropertyDescriptor[] properties, Expression expression) {
        if (expression instanceof Path<?>) {
            String name = ((Path<?>) expression).getMetadata().getName();
            for (PropertyDescriptor property : properties) {
                if (property.getName().equals(name) && property.getWriteMethod() != null) {
                    expressionMap.put(name, expression);
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
                        if (express instanceof FactoryExpression
                                || express instanceof GroupExpression) {
                            expressionMap.put(name, express);
                        } else {
                            expressionMap.put(name, operation);
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
