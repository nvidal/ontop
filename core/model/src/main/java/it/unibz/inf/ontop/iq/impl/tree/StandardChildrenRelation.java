package it.unibz.inf.ontop.iq.impl.tree;

import java.util.Map;
import java.util.Optional;
import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.iq.node.BinaryOrderedOperatorNode;
import it.unibz.inf.ontop.iq.node.QueryNode;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * TODO: explain
 */
public class StandardChildrenRelation implements ChildrenRelation {

    private final List<TreeNode> children;
    private final TreeNode parent;

    protected StandardChildrenRelation(TreeNode parent) {
        if (parent.getQueryNode() instanceof BinaryOrderedOperatorNode) {
            throw new IllegalArgumentException("The StandardChildrenRelation does not accept " +
                    "BinaryOrderedOperatorNodes as parents");
        }
        this.parent = parent;
        this.children = new LinkedList<>();
    }

    private StandardChildrenRelation(TreeNode parent, List<TreeNode> children) {
        this.parent = parent;
        this.children = children;
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public ImmutableList<TreeNode> getChildren() {
        return ImmutableList.copyOf(children);
    }

    @Override
    public Stream<TreeNode> getChildrenStream() {
        return children.stream();
    }

    @Override
    public boolean contains(TreeNode node) {
        return children.contains(node);
    }

    @Override
    public void addChild(TreeNode childNode, Optional<BinaryOrderedOperatorNode.ArgumentPosition> optionalPosition,
                         boolean canReplace) {
        if (optionalPosition.isPresent()) {
            throw new IllegalArgumentException("The StandardChildrenRelation does not accept argument positions");
        }
        if (!contains(childNode)) {
            children.add(childNode);
        }
    }

    @Override
    public void replaceChild(TreeNode formerChild, TreeNode newChild) {
        int index = children.indexOf(formerChild);
        switch(index) {
            case -1:
                throw new IllegalArgumentException("The former child is not in the child relation");
            default:
                children.set(index, newChild);
        }
    }

    @Override
    public void removeChild(TreeNode childNode) {
        if (contains(childNode)) {
            children.remove(childNode);
        }
    }

    @Override
    public ImmutableList<QueryNode> getChildQueryNodes() {
        ImmutableList.Builder<QueryNode> builder = ImmutableList.builder();
        for (TreeNode treeNode : children) {
            builder.add(treeNode.getQueryNode());
        }
        return builder.build();
    }

    @Override
    public Stream<QueryNode> getChildQueryNodeStream() {
        return children.stream()
                .map(TreeNode::getQueryNode);
    }

    @Override
    public Optional<BinaryOrderedOperatorNode.ArgumentPosition> getOptionalPosition(TreeNode childTreeNode) {
        return Optional.empty();
    }

    @Override
    public Optional<TreeNode> getChild(BinaryOrderedOperatorNode.ArgumentPosition position) {
        return Optional.empty();
    }

    @Override
    public ChildrenRelation clone(Map<QueryNode, TreeNode> newNodeIndex) {
        return new StandardChildrenRelation(parent.findNewTreeNode(newNodeIndex),
                children.stream()
                        .map(c -> c.findNewTreeNode(newNodeIndex))
                        .collect(Collectors.toList()));
    }

    @Override
    public ChildrenRelation convertToBinaryChildrenRelation() {
        if (!children.isEmpty()) {
            throw new IllegalStateException("Conversion from a standard to binary children relation is not supported " +
                    "when there are children");
        }
        return new BinaryChildrenRelation(parent);
    }

    @Override
    public ChildrenRelation convertToStandardChildrenRelation() {
        return this;
    }
}
