package ewing.user;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限工具类。
 */
public class PermissionUtils {

    private PermissionUtils() {
    }

    /**
     * 权限列表转换为树形结构。
     * 有父节点的挂在父节点下，未找到父结节的置于顶级。
     */
    public static List<PermissionTree> toTree(List<PermissionTree> nodes) {
        if (nodes == null) {
            return null;
        }
        List<PermissionTree> tree = new ArrayList<>();
        boolean single;
        for (PermissionTree node : nodes) {
            // 没有父节点作为根节点
            if (node.getParentId() == null) {
                tree.add(node);
            } else {
                single = true;
                for (PermissionTree parent : nodes) {
                    // 有父节点ID，添加到它的父节点
                    if (node.getParentId().equals(parent.getPermissionId())) {
                        if (parent.getChildren() == null) {
                            parent.setChildren(new ArrayList<>());
                        }
                        parent.getChildren().add(node);
                        single = false;
                        break;
                    }
                }
                // 没有找到父节点的也做为根节点
                if (single) {
                    tree.add(node);
                }
            }
        }
        return tree;
    }

}
