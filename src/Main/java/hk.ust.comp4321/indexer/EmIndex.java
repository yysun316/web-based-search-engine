package hk.ust.comp4321.indexer;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;

// EmIndex is called by EmIndexContainer only. You are not expected to explicitly call them.
public class EmIndex {
    private String stem;
    private List<List<Integer>> docFreq;
    private Integer Emid;

    public EmIndex(String stem, List<Integer> intList, Integer Emid) {
        this.stem = stem;
        this.docFreq = new ArrayList<>();
        this.Emid = Emid;
    }
    public String getStem() {
        return stem;
    }
    public Integer getID() {
        return Emid;
    }
    public List<List<Integer>> getIntList() {
        return docFreq;
    }
    public void appendIntList(List<Integer> intList) {
        this.docFreq.add(intList);
    }

    // getFreq returns the frequency of the stem in a document (not across all the documents)
    public int getFreq(Integer docId) {
        for (int i = 0; i < this.docFreq.size(); i++)
        {
            if(docFreq.get(i).get(0) == docId)
            {
                return docFreq.get(i).get(1);
            }
        }
        return 0;
    }
}
