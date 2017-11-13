package ewing.user;

import ewing.entity.Permission;

import java.util.List;

/**
 * 权限树节点。
 *
 * @author 权限树节点。
 */
public class PermissionTree extends Permission {

    private List<PermissionTree> children;

    public List<PermissionTree> getChildren() {
        return children;
    }

    public void setChildren(List<PermissionTree> children) {
        this.children = children;
    }

}
