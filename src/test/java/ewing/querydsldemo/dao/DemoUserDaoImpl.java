package ewing.querydsldemo.dao;

import ewing.application.config.SBFBasisDao;
import ewing.querydsldemo.entity.DemoUser;
import ewing.querydsldemo.query.QDemoUser;
import org.springframework.stereotype.Repository;

@Repository
public class DemoUserDaoImpl extends SBFBasisDao<QDemoUser, DemoUser> implements DemoUserDao {

}
