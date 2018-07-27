package ewing.faster.querydsldemo.dao;

import ewing.faster.application.config.SBFBasisDao;
import ewing.faster.querydsldemo.entity.DemoUser;
import ewing.faster.querydsldemo.query.QDemoUser;
import org.springframework.stereotype.Repository;

@Repository
public class DemoUserDaoImpl extends SBFBasisDao<QDemoUser, DemoUser> implements DemoUserDao {

}
