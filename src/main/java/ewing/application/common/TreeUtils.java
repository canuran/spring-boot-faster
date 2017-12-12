package ewing.application.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 树工具类。
 */
public class TreeUtils {

    /**
     * 树节点集合转换为树形结构。
     * 有父节点的挂在父节点下，未找到父结节的置于顶级。
     */
    @SuppressWarnings("unchecked")
    public static <E extends TreeNode> List<E> toTree(List<E> nodes) {
        if (nodes == null) {
            return null;
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

    /**
     * 先根遍历原子节点，无递归，支持大树。
     */
    @SuppressWarnings("unchecked")
    public static <E extends TreeNode> void traverseTree(List<E> tree, Consumer<E> consumer) {
        if (tree == null) {
            return;
        }
        // 使用迭代器和栈记录所有遍历状态
        Stack<Iterator<E>> stack = new Stack<>();
        stack.push(tree.iterator());
        while (!stack.isEmpty()) {
            Iterator<E> iterator = stack.pop();
            while (iterator.hasNext()) {
                // 先遍历自己，然后遍历子节点
                E node = iterator.next();
                if (node.getChildren() != null) {
                    stack.push(iterator);
                    iterator = node.getChildren().iterator();
                }
                consumer.accept(node);
            }
        }
    }

    /**
     * 把树的所有节点放入列表，无递归，支持大树。
     */
    @SuppressWarnings("unchecked")
    public static <E extends TreeNode> List<E> toList(List<E> tree) {
        if (tree == null) {
            return null;
        }
        List<E> nodes = new ArrayList<>();
        // 使用迭代器和栈记录所有遍历状态
        Stack<Iterator<E>> stack = new Stack<>();
        stack.push(tree.iterator());
        while (!stack.isEmpty()) {
            Iterator<E> iterator = stack.pop();
            while (iterator.hasNext()) {
                // 先遍历自己，然后遍历子节点
                E node = iterator.next();
                if (node.getChildren() != null) {
                    stack.push(iterator);
                    iterator = node.getChildren().iterator();
                }
                nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * 从树的所有节点中查找原子节点，无递归，支持大树。
     */
    @SuppressWarnings("unchecked")
    public static <E extends TreeNode> List<E> filterTree(List<E> tree, Predicate<E> predicate) {
        if (tree == null) {
            return null;
        }
        List<E> nodes = new ArrayList<>();
        // 使用迭代器和栈记录所有遍历状态
        Stack<Iterator<E>> stack = new Stack<>();
        stack.push(tree.iterator());
        while (!stack.isEmpty()) {
            Iterator<E> iterator = stack.pop();
            while (iterator.hasNext()) {
                // 先遍历自己，然后遍历子节点
                E node = iterator.next();
                if (node.getChildren() != null) {
                    stack.push(iterator);
                    iterator = node.getChildren().iterator();
                }
                if (predicate.test(node)) {
                    nodes.add(node);
                }
            }
        }
        return nodes;
    }

    /**
     * 从树的所有节点中查找第一个原子节点，无递归，支持大树。
     */
    @SuppressWarnings("unchecked")
    public static <E extends TreeNode> E findFirst(List<E> tree, Predicate<E> predicate) {
        if (tree == null) {
            return null;
        }
        // 使用迭代器和栈记录所有遍历状态
        Stack<Iterator<E>> stack = new Stack<>();
        stack.push(tree.iterator());
        while (!stack.isEmpty()) {
            Iterator<E> iterator = stack.pop();
            while (iterator.hasNext()) {
                // 先遍历自己，然后遍历子节点
                E node = iterator.next();
                if (predicate.test(node)) {
                    return node;
                }
                if (node.getChildren() != null) {
                    stack.push(iterator);
                    iterator = node.getChildren().iterator();
                }
            }
        }
        return null;
    }

}
