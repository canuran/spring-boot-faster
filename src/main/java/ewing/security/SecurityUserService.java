package ewing.security;

import com.querydsl.core.types.Projections;
import com.querydsl.sql.SQLQueryFactory;
import ewing.query.QRole;
import ewing.query.QUser;
import ewing.query.QUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SecurityUserService implements UserDetailsService {

    @Autowired
    private SQLQueryFactory queryFactory;
    private QUser User = QUser.user;
    private QUserRole UserRole = QUserRole.userRole;
    private QRole Role = QRole.role;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityUser securityUser = queryFactory.select(
                Projections.bean(SecurityUser.class, User.all()))
                .from(User)
                .where(User.username.eq(username))
                .fetchOne();
        if (securityUser == null) {
            throw new UsernameNotFoundException("Username not found.");
        } else {
            securityUser.addAuthoritiesByRoles(
                    queryFactory.selectFrom(Role)
                            .leftJoin(UserRole).on(Role.roleId.eq(UserRole.roleId))
                            .where(UserRole.userId.eq(securityUser.getUserId()))
                            .fetch()
            );
        }
        return securityUser;
    }

}