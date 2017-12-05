package ewing.user;

import ewing.application.common.TreeNode;
import ewing.entity.Permission;

import java.util.List;

/**
 * 权限树节点。
 *
 * @author 权限树节点。
 */
public class PermissionTree extends Permission implements TreeNode<PermissionTree, Long> {

    private List<PermissionTree> children;

    @Override
    public Long getId() {
        return getPermissionId();
    }

    @Override
    public List<PermissionTree> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<PermissionTree> children) {
        this.children = children;
    }

}
