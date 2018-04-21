package ewing.common;

import com.querydsl.core.types.dsl.BooleanExpression;
import ewing.application.AppAsserts;
import ewing.application.common.TreeUtils;
import ewing.application.exception.AppRunException;
import ewing.application.query.Page;
import ewing.common.vo.DictionaryNode;
import ewing.common.vo.FindDictionaryParam;
import ewing.entity.Dictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据字典服务实现。
 * 字典有父字典ID和根字典ID，根字典ID用来取所有同类字典值，可以避免递归查询。
 **/
@Service
@Transactional(rollbackFor = Throwable.class)
public class DictionaryServiceImpl implements DictionaryService {

    @Autowired
    private DictionaryDao dictionaryDao;

    @Override
    public Page<Dictionary> findWithSubDictionary(
            FindDictionaryParam findDictionaryParam) {
        AppAsserts.notNull(findDictionaryParam, "查询参数不能为空！");
        return dictionaryDao.findWithSubDictionary(findDictionaryParam);
    }

    @Override
    public void addDictionary(Dictionary dictionary) {
        AppAsserts.notNull(dictionary, "字典项不能为空！");
        AppAsserts.hasText(dictionary.getName(), "字典名不能为空！");
        AppAsserts.hasText(dictionary.getValue(), "字典值不能为空！");

        // 处理父字典和根字典的关系
        if (dictionary.getParentId() != null) {
            Dictionary parent = dictionaryDao.selectOne(
                    qDictionary.dictionaryId.eq(dictionary.getParentId()));
            if (parent == null) {
                throw new AppRunException("父字典项不存在！");
            } else {
                // 父字典存在则根字典继承自父字典
                dictionary.setRootId(parent.getRootId());
            }
        }

        // 相同位置下的字典名称或值不能重复
        BooleanExpression parentIdEquals = dictionary.getParentId() == null ?
                qDictionary.parentId.isNull() :
                qDictionary.parentId.eq(dictionary.getParentId());

        AppAsserts.yes(dictionaryDao.countWhere(parentIdEquals
                        .and(qDictionary.name.eq(dictionary.getName())
                                .or(qDictionary.value.eq(dictionary.getValue())))) < 1,
                "相同位置下的字典名或值不能重复！");

        // 详情不允许为空串
        if (!StringUtils.hasText(dictionary.getDetail())) {
            dictionary.setDetail(null);
        }
        dictionary.setCreateTime(new Date());
        dictionaryDao.insertWithKey(dictionary);

        // 没有父字典则自身就是根字典
        if (dictionary.getParentId() == null) {
            dictionary.setRootId(dictionary.getDictionaryId());
            dictionaryDao.updaterByKey(dictionary.getDictionaryId())
                    .set(qDictionary.rootId, dictionary.getRootId())
                    .execute();
        }
    }

    @Override
    public void updateDictionary(Dictionary dictionary) {
        AppAsserts.notNull(dictionary, "字典项不能为空！");
        AppAsserts.notNull(dictionary.getDictionaryId(), "字典ID不能为空！");
        AppAsserts.hasText(dictionary.getName(), "字典名不能为空！");
        AppAsserts.hasText(dictionary.getValue(), "字典值不能为空！");

        // 相同位置下的字典名称或值不能重复
        BooleanExpression parentIdEquals = qDictionary
                .dictionaryId.ne(dictionary.getDictionaryId())
                .and(dictionary.getParentId() == null ?
                        qDictionary.parentId.isNull() :
                        qDictionary.parentId.eq(dictionary.getParentId()));

        AppAsserts.yes(dictionaryDao.countWhere(parentIdEquals
                        .and(qDictionary.name.eq(dictionary.getName())
                                .or(qDictionary.value.eq(dictionary.getValue())))) < 1,
                "相同位置下的字典名或值不能重复！");

        // 不能修改父字典和根字典
        dictionary.setRootId(null);
        dictionary.setParentId(null);
        // 详情不允许为空串
        if (!StringUtils.hasText(dictionary.getDetail())) {
            dictionary.setDetail(null);
        }
        dictionaryDao.updateBean(dictionary);
    }

    @Override
    public void deleteDictionary(Long dictionaryId) {
        AppAsserts.notNull(dictionaryId, "字典ID不能为空！");
        AppAsserts.notNull(dictionaryDao.selectByKey(dictionaryId),
                "该字典不存在或已删除！");
        AppAsserts.yes(dictionaryDao.countWhere(
                qDictionary.parentId.eq(dictionaryId)) < 1,
                "请先删除该字典的所有子项！");

        dictionaryDao.deleteByKey(dictionaryId);
    }

    @Override
    public List<DictionaryNode> findDictionaryTrees(String[] rootValues) {
        AppAsserts.notNull(rootValues, "查询参数不能为空！");
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
