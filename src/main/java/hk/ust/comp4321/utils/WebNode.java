package hk.ust.comp4321.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WebNode implements Serializable {
    private int id;
    private List<String> children;
    private List<String> parentForRanking;
    private List<String> childForRanking;
    private List<String> parent;
    private String URL;
    private String lastModifiedDate;
    private Double pageRank;
    public WebNode (){
        this.children = new ArrayList<>();
    }

    /***
     * Constructor
     * @param id page id
     * @param URL URL of the page
     * @param lastModifiedDate last modified date of the page
     */
    public WebNode(int id, String URL, String lastModifiedDate) {
        this.id = id;
        this.children = new ArrayList<>();
        this.parentForRanking = new ArrayList<>();
        this.childForRanking = new ArrayList<>();
        this.parent = new ArrayList<>();
        this.URL = URL;
        this.lastModifiedDate = lastModifiedDate;
        this.pageRank = 1.0;
    }

    /***
     * Add a child to the current node
     * @param child child URL
     */

    public void addChild(String child) {
        children.add(child);
    }

    /***
     * Add a parent to the current node
     * @param parent parent URL
     */
    public void addParentForRanking(String parent) {
        parentForRanking.add(parent);
    }

    /***
     * Add a child to the current node
     * @param child child URL
     */
    public void addChildForRanking(String child) {
        childForRanking.add(child);
    }

    /***
     * Add a parent to the current node
     * @param par parent URL
     */
    public void addParent(String par) {
        parent.add(par);
    }

    /***
     * Get the children of the current node
     * @return children of the current node
     */

    public List<String> getChildren() {
        return children;
    }

    /***
     * Get the parents of the current node
     * @return parents of the current node
     */
    public List<String> getParentForRanking() {
        return parentForRanking;
    }

    /***
     * Get the children of the current node
     * @return children of the current node
     */
    public List<String> getChildForRanking() {
        return childForRanking;
    }

    /***
     * Get the parents of the current node
     * @return parents of the current node
     */
    public List<String> getParent() {
        return parent;
    }

    /***
     * Get the id of the current node
     * @return id of the current node
     */
    public int getId() {
        return id;
    }

    /***
     * Get the URL of the current node
     * @return URL of the current node
     */
    public String getUrl() {
        return URL;
    }

    /***
     * Set the URL of the current node
     * @param URL URL of the current node
     */
    public void setURL(String URL) {
        this.URL = URL;
    }

    /***
     * Set the id of the current node
     * @param id id of the current node
     */
    public void setId(int id) {
        this.id = id;
    }

    /***
     * Set the children of the current node
     * @param children children of the current node
     */
    public void setChildren(List<String> children) {
        this.children = children;
    }

    /***
     * Set the parents of the current node
     * @param parent parents of the current node
     */
    public void setParent(List<String> parent) {
        this.parent = parent;
    }

    /***
     * Set the parents of the current node
     * @return lastModifiedDate of the current node
     */
    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    /***
     * Set the lastModifiedDate of the current node
     * @param lastModifiedDate lastModifiedDate of the current node
     */
    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /***
     * Update the pagerank of the current node
     * @param newRank new pagerank of the current node
     */
    public void updatePagerank(Double newRank) {
        this.pageRank = newRank;
    }

    /***
     * Get the pagerank of the current node
     * @return pagerank of the current node
     */
    public Double getPagerank() {
        return this.pageRank;
    }
}

