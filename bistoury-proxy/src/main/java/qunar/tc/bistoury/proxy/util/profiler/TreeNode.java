package qunar.tc.bistoury.proxy.util.profiler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author cai.wen created on 19-11-24
 */
public class TreeNode<T> {

    private T node;

    private Map<T, TreeNode<T>> children = Maps.newHashMap();

    TreeNode(T t) {
        this.node = t;
    }

    TreeNode<T> getOrCreate(T t) {
        TreeNode<T> child = children.get(t);
        if (child == null) {
            child = new TreeNode<>(t);
            children.put(t, child);
        }
        return child;
    }

    public T getNode() {
        return node;
    }

    public List<TreeNode<T>> getChildren() {
        return Lists.newArrayList(children.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeNode<?> treeNode = (TreeNode<?>) o;
        return Objects.equals(node, treeNode.node);
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public String toString() {
        return "TreeNode{" +
                "node=" + node +
                '}';
    }
}