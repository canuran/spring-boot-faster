package canuran.faster.dao.impl;

import canuran.faster.common.vo.DictionaryNode;
import canuran.faster.common.vo.FindDictionaryParam;
import canuran.faster.dao.DictionaryDao;
import canuran.faster.dao.entity.Dictionary;
import canuran.faster.dao.query.QDictionary;
import canuran.query.BaseQueryFactory;
import canuran.query.clause.BaseQuery;
import canuran.query.paging.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static canuran.faster.dao.query.QDictionary.dictionary;

/**
 * 数据字典数据访问实现。
 */
@Repository
public class DictionaryDaoImpl implements DictionaryDao {

    @Autowired
    private BaseQueryFactory queryFactory;

    private static final QDictionary qAllDictionary = new QDictionary("all");

    @Override
    public Page<Dictionary> findWithSubDictionary(FindDictionaryParam findDictionaryParam) {
        // 结果条数以根字典为准
        BaseQuery<Dictionary> rootQuery = queryFactory.selectDistinct(dictionary)
                .from(dictionary)
                .leftJoin(qAllDictionary)
                .on(dictionary.dictionaryId.eq(qAllDictionary.rootId))
                .where(dictionary.dictionaryId.eq(dictionary.rootId))
                // 搜索条件，支持子字典名称搜索
                .whereIfHasText(findDictionaryParam.getName(), qAllDictionary.name::contains)
                .whereIfHasText(findDictionaryParam.getValue(), qAllDictionary.value::contains);
        // 先查询根字典的总数
        long total = rootQuery.fetchCount();

        // 关联查根字典下的所有子字典项
        rootQuery.limit(findDictionaryParam.getLimit()).offset(findDictionaryParam.getOffset());
        List<Dictionary> dictionaries = queryFactory.selectDistinct(qAllDictionary)
                .from(rootQuery.as(dictionary))
                .leftJoin(qAllDictionary)
                .on(dictionary.dictionaryId.eq(qAllDictionary.rootId))
                .orderBy(dictionary.dictionaryId.asc())
                .fetch();

        return new Page<>(total, dictionaries);
    }

    @Override
    public List<DictionaryNode> findRootSubDictionaries(String[] rootValues) {
        return queryFactory.selectDistinct(qAllDictionary)
                .from(dictionary)
                .join(qAllDictionary)
                .on(dictionary.dictionaryId.eq(qAllDictionary.rootId))
                .where(dictionary.value.in(rootValues))
                .orderBy(qAllDictionary.dictionaryId.asc())
                .fitBean(DictionaryNode.class)
                .fetch();
    }
}
