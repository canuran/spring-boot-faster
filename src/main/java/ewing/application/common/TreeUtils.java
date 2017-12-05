package ewing.application.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 树工具类。
 */
public class TreeUtils {

    /**
     * 树节点集合转换为树形结构。
     * 有父节点的挂在父节点下，未找到父结节的置于顶级。
     */
    public static <E extends TreeNode> List<E> toTree(List<E> nodes) {
        if (nodes == null) {
            return null;
        }
        // 清理之前构建的树，只支持节点为单节点
        for (E node : nodes) {
            node.setChildren(null);
        }
        List<E> tree = new ArrayList<>();
        boolean single;
        for (E node : nodes) {
            // 没有父节点作为根节点
            if (node.getParentId() == null) {
                tree.add(node);
            } else {
                single = true;
                for (E parent : nodes) {
                    // 有父节点ID，添加到它的父节点
                    if (node.getParentId().equals(parent.getId())) {
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
