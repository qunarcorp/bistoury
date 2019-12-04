package qunar.tc.bistoury.proxy.util.profiler;

import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.List;

/**
 * @author cai.wen created on 19-11-24
 */
public class HotSpotMethodFormatter {

    private static final HotSpotMethodFormatter INSTANCE = new HotSpotMethodFormatter();

    private HotSpotMethodFormatter() {
    }

    public static DisplayNode format(TreeNode<FunctionCounter> methodCounterTreeNode) {
        return INSTANCE.doFormat(methodCounterTreeNode);
    }

    private static final Comparator<TreeNode<FunctionCounter>> treeNodeComparator = (node1, node2)
            -> node2.getNode().compareTo(node1.getNode());

    public DisplayNode doFormat(TreeNode<FunctionCounter> methodCounterTreeNode) {
        long count = methodCounterTreeNode.getNode().getCount();
        List<TreeNode<FunctionCounter>> children = methodCounterTreeNode.getChildren();
        children.sort(treeNodeComparator);
        String info = methodCounterTreeNode.getNode().getFunctionInfo().getFuncName();
        List<DisplayNode> displayChildren = Lists.newArrayListWithExpectedSize(children.size());
        for (TreeNode<FunctionCounter> child : children) {
            displayChildren.add(doFormat(child));
        }

        DisplayNode root = new DisplayNode(info, count, displayChildren);
        return root;
    }

    public static class DisplayNode {

        private String text;

        private long count;

        private List<DisplayNode> nodes;

        public DisplayNode(String text, long count, List<DisplayNode> nodes) {
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
    }

}
