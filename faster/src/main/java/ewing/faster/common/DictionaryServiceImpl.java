package ewing.faster.common;

import ewing.common.exception.BusinessException;
import ewing.common.exception.Checks;
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
import org.springframework.util.StringUtils;

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
        Checks.notNull(findDictionaryParam, "查询参数不能为空！");
        return dictionaryDao.findWithSubDictionary(findDictionaryParam);
    }

    @Override
    public void addDictionary(Dictionary dictionary) {
        Checks.notNull(dictionary, "字典项不能为空！");
        Checks.hasText(dictionary.getName(), "字典名不能为空！");
        Checks.hasText(dictionary.getValue(), "字典值不能为空！");

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

        Checks.isTrue(queryFactory.selectFrom(qDictionary)
                        .where(dictionary.getParentId() == null ?
                                qDictionary.parentId.isNull() :
                                qDictionary.parentId.eq(dictionary.getParentId()))
                        .where(qDictionary.name.eq(dictionary.getName())
                                .or(qDictionary.value.eq(dictionary.getValue())))
                        .fetchCount() < 1,
                "相同位置下的字典名或值不能重复！");

        // 详情不允许为空串
        if (!StringUtils.hasText(dictionary.getDetail())) {
            dictionary.setDetail(null);
        }
        dictionary.setCreateTime(new Date());
        dictionary.setDictionaryId(GlobalIds.nextId());
        queryFactory.insert(qDictionary).insertBean(dictionary);

        // 没有父字典则自身就是根字典
        if (dictionary.getParentId() == null) {
            dictionary.setRootId(dictionary.getDictionaryId());
            queryFactory.update(qDictionary).whereKey(dictionary.getDictionaryId())
                    .set(qDictionary.rootId, dictionary.getRootId())
                    .execute();
        }
    }

    @Override
    public void updateDictionary(Dictionary dictionary) {
        Checks.notNull(dictionary, "字典项不能为空！");
        Checks.notNull(dictionary.getDictionaryId(), "字典ID不能为空！");
        Checks.hasText(dictionary.getName(), "字典名不能为空！");
        Checks.hasText(dictionary.getValue(), "字典值不能为空！");

        Checks.isTrue(queryFactory.selectFrom(qDictionary)
                        .where(qDictionary.dictionaryId.ne(dictionary.getDictionaryId()))
                        .where(dictionary.getParentId() == null ?
                                qDictionary.parentId.isNull() :
                                qDictionary.parentId.eq(dictionary.getParentId()))
                        .where(qDictionary.name.eq(dictionary.getName())
                                .or(qDictionary.value.eq(dictionary.getValue())))
                        .fetchCount() < 1,
                "相同位置下的字典名或值不能重复！");

        // 不能修改父字典和根字典
        dictionary.setRootId(null);
        dictionary.setParentId(null);
        // 详情不允许为空串
        if (!StringUtils.hasText(dictionary.getDetail())) {
            dictionary.setDetail(null);
        }
        queryFactory.update(qDictionary).updateBean(dictionary);
    }

    @Override
    public void deleteDictionary(BigInteger dictionaryId) {
        Checks.notNull(dictionaryId, "字典ID不能为空！");
        Checks.notNull(queryFactory.selectFrom(qDictionary).fetchByKey(dictionaryId),
                "该字典不存在或已删除！");
        Checks.isTrue(queryFactory.selectFrom(qDictionary)
                        .where(qDictionary.parentId.eq(dictionaryId))
                        .fetchCount() < 1,
                "请先删除该字典的所有子项！");

        queryFactory.delete(qDictionary).deleteByKey(dictionaryId);
    }

    @Override
    public List<DictionaryNode> findDictionaryTrees(String[] rootValues) {
        Checks.notNull(rootValues, "查询参数不能为空！");
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
