import java.util.ArrayList;
import java.util.List;

class TreeNode {
    private String  data;
    private List<TreeNode> children;

    public TreeNode(String  data) {
        this.data = data;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        children.add(child);
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public String  getData() {
        return data;
    }
}

