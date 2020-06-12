package ewing.faster.common;

import ewing.common.exception.BusinessException;
import ewing.common.utils.Asserts;
import ewing.common.utils.TreeUtils;
import ewing.faster.common.vo.DictionaryNode;
import ewing.faster.common.vo.FindDictionaryParam;
import ewing.faster.dao.DictionaryDao;
import ewing.faster.dao.entity.Dictionary;
import ewing.query.BaseQueryFactory;
import ewing.query.paging.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.LongSupplier;

import static ewing.faster.dao.query.QDictionary.dictionary;

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
    @Autowired
    private LongSupplier longSupplier;

    @Override
    public Page<Dictionary> findWithSubDictionary(
            FindDictionaryParam findDictionaryParam) {
        Asserts.of(findDictionaryParam).name("查询字典参数").notNull();

        return dictionaryDao.findWithSubDictionary(findDictionaryParam);
    }

    @Override
    public void addDictionary(Dictionary dictionaryParam) {
        checkCommonSave(dictionaryParam);

        Asserts.of(queryFactory.selectFrom(dictionary)
                .where(dictionaryParam.getParentId() == null ?
                        dictionary.parentId.isNull() :
                        dictionary.parentId.eq(dictionaryParam.getParentId()))
                .where(dictionary.name.eq(dictionaryParam.getName())
                        .or(dictionary.value.eq(dictionaryParam.getValue())))
                .fetchCount())
                .lessThan(1, "相同位置下的字典名或值不能重复");

        // 处理父字典和根字典的关系
        if (dictionaryParam.getParentId() != null) {
            Dictionary parent = queryFactory.selectFrom(dictionary)
                    .where(dictionary.dictionaryId.eq(dictionaryParam.getParentId()))
                    .fetchOne();
            if (parent == null) {
                throw new BusinessException("父字典项不存在");
            } else {
                // 父字典存在则根字典继承自父字典
                dictionaryParam.setRootId(parent.getRootId());
            }
        }

        dictionaryParam.setCreateTime(new Date());
        dictionaryParam.setDictionaryId(longSupplier.getAsLong());
        queryFactory.insert(dictionary).insertBean(dictionaryParam);

        // 没有父字典则自身就是根字典
        if (dictionaryParam.getParentId() == null) {
            dictionaryParam.setRootId(dictionaryParam.getDictionaryId());
            queryFactory.update(dictionary).whereEqKey(dictionaryParam.getDictionaryId())
                    .set(dictionary.rootId, dictionaryParam.getRootId())
                    .execute();
        }
    }

    private void checkCommonSave(Dictionary dictionaryParam) {
        Asserts.of(dictionaryParam).name("字典项").notNull();

        Asserts.of(dictionaryParam.getName()).name("字典名")
                .hasText().minLength(1).maxLength(32).normalChars();

        Asserts.of(dictionaryParam.getValue()).name("字典值")
                .hasText().minLength(1).maxLength(32).normalChars();
    }

    @Override
    public void updateDictionary(Dictionary dictionaryParam) {
        checkCommonSave(dictionaryParam);
        Asserts.of(dictionaryParam.getDictionaryId()).name("字典ID").notNull();

        Asserts.of(queryFactory.selectFrom(dictionary)
                .where(dictionary.dictionaryId.ne(dictionaryParam.getDictionaryId()))
                .where(dictionaryParam.getParentId() == null ?
                        dictionary.parentId.isNull() :
                        dictionary.parentId.eq(dictionaryParam.getParentId()))
                .where(dictionary.name.eq(dictionaryParam.getName())
                        .or(dictionary.value.eq(dictionaryParam.getValue())))
                .fetchCount())
                .lessThan(1, "相同位置下的字典名或值不能重复");

        // 不能修改父字典和根字典
        dictionaryParam.setRootId(null);
        dictionaryParam.setParentId(null);
        queryFactory.update(dictionary).updateBean(dictionaryParam);
    }

    @Override
    public void deleteDictionary(Long dictionaryId) {
        Asserts.of(dictionaryId).name("字典ID").notNull();

        Dictionary dictionaryDto = queryFactory.selectFrom(dictionary).fetchByKey(dictionaryId);
        Asserts.of(dictionaryDto).notNull("该字典不存在或已删除");

        Asserts.of(queryFactory.selectFrom(dictionary)
                .where(dictionary.parentId.eq(dictionaryId))
                .fetchCount())
                .lessThan(1, "请先删除该字典的所有子项");

        queryFactory.delete(dictionary).deleteByKey(dictionaryId);
    }

    @Override
    public List<DictionaryNode> findDictionaryTrees(String[] rootValues) {
        Asserts.of(rootValues).name("查询参数").notNull();

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
