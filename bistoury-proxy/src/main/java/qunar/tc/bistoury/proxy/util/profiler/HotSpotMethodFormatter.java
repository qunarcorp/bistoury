package qunar.tc.bistoury.proxy.util.profiler;

import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.List;

/**
 * @author cai.wen created on 19-11-24
 */
class HotSpotMethodFormatter {

    private static final HotSpotMethodFormatter INSTANCE = new HotSpotMethodFormatter();

    private static final Comparator<TreeNode<FunctionCounter>> treeNodeComparator = Comparator.comparing(TreeNode::getNode);

    private HotSpotMethodFormatter() {
    }

    static DisplayNode format(TreeNode<FunctionCounter> methodCounterTreeNode) {
        return INSTANCE.doFormat(methodCounterTreeNode);
    }

    private DisplayNode doFormat(TreeNode<FunctionCounter> methodCounterTreeNode) {
        long count = methodCounterTreeNode.getNode().getCount();
        List<TreeNode<FunctionCounter>> children = methodCounterTreeNode.getChildren();
        children.sort(treeNodeComparator.reversed());
        String info = methodCounterTreeNode.getNode().getFunctionInfo().getFuncName();
        List<DisplayNode> displayChildren = Lists.newArrayListWithExpectedSize(children.size());
        for (TreeNode<FunctionCounter> child : children) {
            displayChildren.add(doFormat(child));
        }
        return new DisplayNode(info, count, displayChildren);
    }

    public static class DisplayNode {

        private String text;

        private long count;

        private List<DisplayNode> nodes;

        private DisplayNode(String text, long count, List<DisplayNode> nodes) {
            this.text = text;
            this.count = count;
            this.nodes = nodes;
        }

        public String getText() {
            return text;
        }

        public long getCount() {
            return count;
        }

        public List<DisplayNode> getNodes() {
            return nodes;
        }

        @Override
        public String toString() {
            return "DisplayNode{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }

}
