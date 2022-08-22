package canuran.faster.dao;

import canuran.faster.common.vo.DictionaryNode;
import canuran.faster.common.vo.FindDictionaryParam;
import canuran.faster.dao.entity.Dictionary;
import canuran.query.paging.Page;

import java.util.List;

/**
 * 数据字典数据访问接口。
 */
public interface DictionaryDao {

    Page<Dictionary> findWithSubDictionary(FindDictionaryParam findDictionaryParam);

    List<DictionaryNode> findRootSubDictionaries(String[] rootValues);
}
