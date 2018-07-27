package ewing.common;

import ewing.dao.query.QDictionary;

/**
 * 数据字典中所有的实体Bean。
 */
public interface DictionaryBeans {

    QDictionary qDictionary = QDictionary.dictionary;

}
