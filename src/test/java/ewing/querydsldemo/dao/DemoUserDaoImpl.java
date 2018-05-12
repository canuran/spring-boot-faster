package ewing.querydsldemo.dao;

import ewing.application.query.BasisDao;
import ewing.querydsldemo.entity.DemoUser;
import ewing.querydsldemo.query.QDemoUser;
import org.springframework.stereotype.Repository;

@Repository
public class DemoUserDaoImpl extends BasisDao<QDemoUser, DemoUser> implements DemoUserDao {
}
