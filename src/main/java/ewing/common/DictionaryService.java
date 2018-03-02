package ewing.common;

import ewing.application.query.Page;
import ewing.common.vo.DictionaryNode;
import ewing.common.vo.FindDictionaryParam;
import ewing.entity.Dictionary;

import java.util.List;

/**
 * 数据字典服务接口。
 **/
public interface DictionaryService extends DictionaryBeans {

    Page<Dictionary> findWithSubDictionary(FindDictionaryParam findDictionaryParam);

    void addDictionary(Dictionary dictionary);

    void updateDictionary(Dictionary dictionary);

    void deleteDictionary(Long dictionaryId);

    List<DictionaryNode> findDictionaryTrees(String[] rootValues);
}
