package ewing.security;

import ewing.entity.Role;
import org.springframework.security.core.GrantedAuthority;

/**
 * 适配Security的角色权限对象。
 *
 * @author Ewing
 */
public class RoleAsAuthority extends Role implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return getCode();
    }

}
