package ewing.application.common;

import java.util.List;

/**
 * 树节点接口。
 */
public interface TreeNode<E extends TreeNode, ID> {

    ID getId();

    default void setId(ID id) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    ID getParentId();

    default void setParentId(ID parentId) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    List<E> getChildren();

    void setChildren(List<E> nodes);

}
