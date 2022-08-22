package canuran.faster.common;

import canuran.faster.common.vo.DictionaryNode;
import canuran.faster.common.vo.FindDictionaryParam;
import canuran.faster.dao.entity.Dictionary;
import canuran.query.paging.Page;

import java.util.List;

/**
 * 数据字典服务接口。
 **/
public interface DictionaryService {

    Page<Dictionary> findWithSubDictionary(FindDictionaryParam findDictionaryParam);

    void addDictionary(Dictionary dictionary);

    void updateDictionary(Dictionary dictionary);

    void deleteDictionary(Long dictionaryId);

    List<DictionaryNode> findDictionaryTrees(String[] rootValues);
}
