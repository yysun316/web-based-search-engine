package hk.ust.comp4321.utils;

import java.util.ArrayList;
import java.util.List;

public class WebNode {
    private int id; // URL of the page / page id
    private List<WebNode> children; // List of URLs of the children pages
    private WebNode parent; // URL of the parent page
    private String URL; // URL of this page
    private String lastModifiedDate; // Last modified date of the page
    public WebNode (){
        this.children = new ArrayList<>();
    }

    public WebNode(int id, WebNode parent, String URL, String lastModifiedDate) {
        this.id = id;
        this.children = new ArrayList<>();
        this.parent = parent;
        this.URL = URL;
        this.lastModifiedDate = lastModifiedDate;
    }

    public void addChild(WebNode child) {
        children.add(child);
    }

    public List<WebNode> getChildren() {
        return children;
    }

    public int getId() {
        return id;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChildren(List<WebNode> children) {
        this.children = children;
    }

    public WebNode getParent() {
        return parent;
    }

    public void setParent(WebNode parent) {
        this.parent = parent;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}

