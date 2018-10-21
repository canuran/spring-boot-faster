package ewing.faster.dao.impl;

import ewing.faster.common.vo.DictionaryNode;
import ewing.faster.common.vo.FindDictionaryParam;
import ewing.faster.dao.DictionaryDao;
import ewing.faster.dao.entity.Dictionary;
import ewing.faster.dao.query.QDictionary;
import ewing.query.BaseQueryFactory;
import ewing.query.clause.BaseQuery;
import ewing.query.paging.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 数据字典数据访问实现。
 */
@Repository
public class DictionaryDaoImpl implements DictionaryDao {

    @Autowired
    private BaseQueryFactory queryFactory;

    private QDictionary qAllDictionary = new QDictionary("all");

    @Override
    public Page<Dictionary> findWithSubDictionary(FindDictionaryParam findDictionaryParam) {
        // 结果条数以根字典为准
        BaseQuery<Dictionary> rootQuery = queryFactory.selectDistinct(qDictionary)
                .from(qDictionary)
                .leftJoin(qAllDictionary)
                .on(qDictionary.dictionaryId.eq(qAllDictionary.rootId))
                .where(qDictionary.dictionaryId.eq(qDictionary.rootId))
                // 搜索条件，支持子字典名称搜索
                .whereIfHasText(findDictionaryParam.getName(), qAllDictionary.name::contains)
                .whereIfHasText(findDictionaryParam.getValue(), qAllDictionary.value::contains);
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
        return queryFactory.selectDistinct(qAllDictionary)
                .from(qDictionary)
                .join(qAllDictionary)
                .on(qDictionary.dictionaryId.eq(qAllDictionary.rootId))
                .where(qDictionary.value.in(rootValues))
                .fetch(DictionaryNode.class);
    }
}
