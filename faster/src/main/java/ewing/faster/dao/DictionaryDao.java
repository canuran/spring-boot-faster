package ewing.faster.dao;

import ewing.faster.common.vo.DictionaryNode;
import ewing.faster.common.vo.FindDictionaryParam;
import ewing.faster.dao.entity.Dictionary;
import ewing.query.paging.Page;

import java.util.List;

/**
 * 数据字典数据访问接口。
 */
public interface DictionaryDao {

    Page<Dictionary> findWithSubDictionary(FindDictionaryParam findDictionaryParam);

    List<DictionaryNode> findRootSubDictionaries(String[] rootValues);
}
