package ewing.dao;

import ewing.application.query.BasicDao;
import ewing.application.query.Page;
import ewing.common.DictionaryBeans;
import ewing.common.vo.DictionaryNode;
import ewing.common.vo.FindDictionaryParam;
import ewing.dao.entity.Dictionary;

import java.util.List;

/**
 * 数据字典数据访问接口。
 */
public interface DictionaryDao extends BasicDao<Dictionary>, DictionaryBeans {

    Page<Dictionary> findWithSubDictionary(FindDictionaryParam findDictionaryParam);

    List<DictionaryNode> findRootSubDictionaries(String[] rootValues);
}
