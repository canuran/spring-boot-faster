package ewing.querydsldemo.dao;

import ewing.application.query.BaseBeanDao;
import ewing.querydsldemo.entity.DemoUser;
import ewing.querydsldemo.query.QDemoUser;
import org.springframework.stereotype.Repository;

@Repository
public class DemoUserDaoImpl extends BaseBeanDao<QDemoUser, DemoUser> implements DemoUserDao {
}
