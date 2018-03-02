package ewing.security.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

/**
 * Spring Security中的Authority或Role。
 * 该对象需要缓存和序列化，尽量保持结构稳定。
 *
 * @author Ewing
 */
public class AuthorityNode implements GrantedAuthority {

    private Long authorityId;

    private Long parentId;

    private String name;

    private String code;

    private String type;

    private String content;

    private List<AuthorityNode> children;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public List<AuthorityNode> getChildren() {
        return children;
    }

    public void setChildren(List<AuthorityNode> nodes) {
        this.children = nodes;
    }

    @Override
    @JsonIgnore
    public String getAuthority() {
        return code;
    }

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
