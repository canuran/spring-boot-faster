package ewing.query.querydsldemo.dao;

import ewing.query.QueryBasisDao;
import ewing.query.querydsldemo.entity.DemoUser;
import ewing.query.querydsldemo.query.QDemoUser;
import org.springframework.stereotype.Repository;

@Repository
public class DemoUserDaoImpl extends QueryBasisDao<QDemoUser, DemoUser> implements DemoUserDao {

}
