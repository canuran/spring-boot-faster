package ewing.user;

import ewing.entity.Permission;

import java.util.List;

/**
 * 权限节点。
 */
public class PermissionNode extends Permission {

    private List<PermissionNode> children;

    public List<PermissionNode> getChildren() {
        return children;
    }

    public void setChildren(List<PermissionNode> children) {
        this.children = children;
    }

}
