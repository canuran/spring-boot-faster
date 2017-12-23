package ewing.security;

import ewing.entity.Authority;
import org.springframework.security.core.GrantedAuthority;

/**
 * Spring Security中的Authority或Role。
 *
 * @author Ewing
 */
public class AuthorityOrRole extends Authority implements GrantedAuthority {

    @Override
    public String getAuthority() {
        return getCode();
    }

}
