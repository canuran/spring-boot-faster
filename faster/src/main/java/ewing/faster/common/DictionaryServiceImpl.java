package ewing.faster.common;

import ewing.common.exception.BusinessException;
import ewing.common.utils.Arguments;
import ewing.common.utils.GlobalIds;
import ewing.common.utils.TreeUtils;
import ewing.faster.common.vo.DictionaryNode;
import ewing.faster.common.vo.FindDictionaryParam;
import ewing.faster.dao.DictionaryDao;
import ewing.faster.dao.entity.Dictionary;
import ewing.query.BaseQueryFactory;
import ewing.query.paging.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据字典服务实现。
 * 字典有父字典ID和根字典ID，根字典ID用来取所有同类字典值，可以避免递归查询。
 **/
@Service
public class DictionaryServiceImpl implements DictionaryService {

    @Autowired
    private DictionaryDao dictionaryDao;
    @Autowired
    private BaseQueryFactory queryFactory;

    @Override
    public Page<Dictionary> findWithSubDictionary(
            FindDictionaryParam findDictionaryParam) {
        Arguments.of(findDictionaryParam).notNull("查询参数不能为空！");
        return dictionaryDao.findWithSubDictionary(findDictionaryParam);
    }

    @Override
    public void addDictionary(Dictionary dictionary) {
        checkCommonSave(dictionary);

        Arguments.of(queryFactory.selectFrom(qDictionary)
                .where(dictionary.getParentId() == null ?
                        qDictionary.parentId.isNull() :
                        qDictionary.parentId.eq(dictionary.getParentId()))
                .where(qDictionary.name.eq(dictionary.getName())
                        .or(qDictionary.value.eq(dictionary.getValue())))
                .fetchCount())
                .lessThan(1, "相同位置下的字典名或值不能重复！");

        // 处理父字典和根字典的关系
        if (dictionary.getParentId() != null) {
            Dictionary parent = queryFactory.selectFrom(qDictionary)
                    .where(qDictionary.dictionaryId.eq(dictionary.getParentId()))
                    .fetchOne();
            if (parent == null) {
                throw new BusinessException("父字典项不存在！");
            } else {
                // 父字典存在则根字典继承自父字典
                dictionary.setRootId(parent.getRootId());
            }
        }

        dictionary.setCreateTime(new Date());
        dictionary.setDictionaryId(GlobalIds.nextId());
        queryFactory.insert(qDictionary).insertBean(dictionary);

        // 没有父字典则自身就是根字典
        if (dictionary.getParentId() == null) {
            dictionary.setRootId(dictionary.getDictionaryId());
            queryFactory.update(qDictionary).whereEqKey(dictionary.getDictionaryId())
                    .set(qDictionary.rootId, dictionary.getRootId())
                    .execute();
        }
    }

    private void checkCommonSave(Dictionary dictionary) {
        Arguments.of(dictionary).notNull("字典项不能为空！");
        Arguments.of(dictionary.getName()).hasText("字典名不能为空！")
                .maxLength(32, "字典名长度不能超过32字符！");
        Arguments.of(dictionary.getValue()).hasText("字典值不能为空！")
                .maxLength(32, "字典值长度不能超过32字符！");
    }

    @Override
    public void updateDictionary(Dictionary dictionary) {
        checkCommonSave(dictionary);
        Arguments.of(dictionary.getDictionaryId()).notNull("字典ID不能为空！");

        Arguments.of(queryFactory.selectFrom(qDictionary)
                .where(qDictionary.dictionaryId.ne(dictionary.getDictionaryId()))
                .where(dictionary.getParentId() == null ?
                        qDictionary.parentId.isNull() :
                        qDictionary.parentId.eq(dictionary.getParentId()))
                .where(qDictionary.name.eq(dictionary.getName())
                        .or(qDictionary.value.eq(dictionary.getValue())))
                .fetchCount())
                .lessThan(1, "相同位置下的字典名或值不能重复！");

        // 不能修改父字典和根字典
        dictionary.setRootId(null);
        dictionary.setParentId(null);
        queryFactory.update(qDictionary).updateBean(dictionary);
    }

    @Override
    public void deleteDictionary(BigInteger dictionaryId) {
        Arguments.of(dictionaryId).notNull("字典ID不能为空！");

        Dictionary dictionary = queryFactory.selectFrom(qDictionary).fetchByKey(dictionaryId);
        Arguments.of(dictionary).notNull("该字典不存在或已删除！");

        Arguments.of(queryFactory.selectFrom(qDictionary)
                .where(qDictionary.parentId.eq(dictionaryId))
                .fetchCount())
                .lessThan(1, "请先删除该字典的所有子项！");

        queryFactory.delete(qDictionary).deleteByKey(dictionaryId);
    }

    @Override
    public List<DictionaryNode> findDictionaryTrees(String[] rootValues) {
        Arguments.of(rootValues).notNull("查询参数不能为空！");
        List<DictionaryNode> dictionaries = dictionaryDao
                .findRootSubDictionaries(rootValues);
        return TreeUtils.toTree(dictionaries,
                ArrayList::new,
                DictionaryNode::getDictionaryId,
                DictionaryNode::getParentId,
                DictionaryNode::getChildren,
                DictionaryNode::setChildren);
    }

}
