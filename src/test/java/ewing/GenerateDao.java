package ewing;

import ewing.application.query.DaoGenerator;

/**
 * 自动生成Dao接口与实现，先用Maven插件export后再执行Dao生成。
 *
 * @author Ewing
 * @since 2018年05月21日
 */
public class GenerateDao {

    public static void main(String[] args) {
        new DaoGenerator()
                .daoPackage("ewing.dao")
                .queryBeanPackage("ewing.dao.query")
                .generate();
    }

}
