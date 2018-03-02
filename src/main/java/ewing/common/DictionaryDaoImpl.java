package ewing.common;

import com.querydsl.sql.SQLQuery;
import com.querydsl.sql.SQLQueryFactory;
import ewing.application.query.BaseBeanDao;
import ewing.application.query.Page;
import ewing.application.query.QueryUtils;
import ewing.common.vo.DictionaryNode;
import ewing.common.vo.FindDictionaryParam;
import ewing.entity.Dictionary;
import ewing.query.QDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 数据字典数据访问实现。
 */
@Repository
public class DictionaryDaoImpl extends BaseBeanDao<QDictionary, Dictionary> implements DictionaryDao {

    @Autowired
    private SQLQueryFactory queryFactory;

    private QDictionary qAllDictionary = new QDictionary("all");

    @Override
    public Page<Dictionary> findWithSubDictionary(FindDictionaryParam findDictionaryParam) {
        // 结果条数以根字典为准
        SQLQuery<Dictionary> rootQuery = queryFactory.selectDistinct(qDictionary)
                .from(qDictionary)
                .leftJoin(qAllDictionary)
                .on(qDictionary.dictionaryId.eq(qAllDictionary.rootId))
                .where(qDictionary.dictionaryId.eq(qDictionary.rootId))
                // 搜索条件，支持子字典名称搜索
                .where(StringUtils.hasText(findDictionaryParam.getName()) ?
                        qAllDictionary.name.contains(findDictionaryParam.getName()) : null)
                .where(StringUtils.hasText(findDictionaryParam.getValue()) ?
                        qAllDictionary.value.contains(findDictionaryParam.getValue()) : null);
        // 先查询根字典的总数
        long total = rootQuery.fetchCount();

        // 关联查根字典下的所有子字典项
        rootQuery.limit(findDictionaryParam.getLimit()).offset(findDictionaryParam.getOffset());
        List<Dictionary> dictionaries = queryFactory.selectDistinct(qAllDictionary)
                .from(rootQuery.as(qDictionary))
                .leftJoin(qAllDictionary)
                .on(qDictionary.dictionaryId.eq(qAllDictionary.rootId))
                .fetch();

        return new Page<>(total, dictionaries);
    }

    @Override
    public List<DictionaryNode> findRootSubDictionaries(String[] rootValues) {
        return queryFactory.selectDistinct(
                QueryUtils.fitBean(DictionaryNode.class, qAllDictionary))
                .from(qDictionary)
                .leftJoin(qAllDictionary)
                .on(qDictionary.dictionaryId.eq(qAllDictionary.rootId))
                .where(qDictionary.value.in(rootValues))
                .fetch();
    }
}
