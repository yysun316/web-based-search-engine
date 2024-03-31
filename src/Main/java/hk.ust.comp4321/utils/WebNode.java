package hk.ust.comp4321.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WebNode implements Serializable {
    private int id; // page id
    private List<String> children;
    private List<String> parent;
    private String URL; // URL of this page
    private String lastModifiedDate; // Last modified date of the page
    private Double pageRank;
    public WebNode (){
        this.children = new ArrayList<>();
    }

    public WebNode(int id, String URL, String lastModifiedDate) {
        this.id = id;
        this.children = new ArrayList<>();
        this.parent = new ArrayList<>();
        this.URL = URL;
        this.lastModifiedDate = lastModifiedDate;
        this.pageRank = 1.0;
    }

    public void addChild(String child) {
        children.add(child);
    }

    public void addParent(String par) {
        parent.add(par);
    }

    public List<String> getChildren() {
        return children;
    }

    public List<String> getParent() {
        return parent;
    }

    public void removeChild(String child) {
        children.remove(child);
    }

    public void removeParent(String par) {
        parent.remove(par);
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChildren(List<String> children) {
        this.children = children;
    }

    public void setParent(List<String> parent) {
        this.parent = parent;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    public void updatePagerank(Double newRank) {
        this.pageRank = newRank;
    }
    public Double getPagerank() {
        return this.pageRank;
    }
}

