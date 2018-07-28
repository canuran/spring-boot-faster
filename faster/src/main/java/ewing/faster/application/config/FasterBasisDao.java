package ewing.faster.application.config;

import com.querydsl.sql.RelationalPathBase;
import com.querydsl.sql.SQLQueryFactory;
import ewing.query.BasisDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 每个项目使用自己的数据源构建的查询工厂。
 *
 * @author Ewing
 * @since 2018年06月12日
 */
public abstract class FasterBasisDao<BASE extends RelationalPathBase<BEAN>, BEAN> extends BasisDao<BASE, BEAN> {

    @Autowired
    private SQLQueryFactory queryFactory;

    public SQLQueryFactory getQueryFactory() {
        return queryFactory;
    }

}
