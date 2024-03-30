package hk.ust.comp4321.indexer;

import java.util.ArrayList;
import java.util.List;

public class EmIndexContainer {
    private List<EmIndex> emIndexList;
    public EmIndexContainer() {
        emIndexList = new ArrayList<>();
    }

    // addEmIndex is to increase the number of the corresponding stem by one when encountered
    public void addEmIndex(String providedStem, Integer documentID)
    {
        Integer originalFrequency = withIdNStemgetFrequency(documentID,providedStem);
        setUpEmIndex(providedStem, documentID, originalFrequency + 1);
    }

    // setUpEmIndex get the stem, documentID and frequency of that stem occurring in the document
    public void setUpEmIndex(String providedStem, Integer documentID, Integer StemFrequency) {
        List<Integer> integerList = List.of(documentID, StemFrequency);
        Integer ithStem = getIndexNumber(providedStem);
        if(ithStem == getIndexSize())
        {
            EmIndex emIndex = new EmIndex(providedStem, integerList, getIndexNumber(providedStem));
            emIndexList.add(emIndex);
        }
        else
        {
            emIndexList.get(ithStem).appendIntList(integerList);
        }
    }

    //getIndexNumber returns the id of the stem if available. Else return 0
    public int getIndexNumber(String providedStem) {
        Integer count = 0;
        for (EmIndex emIndex : emIndexList) {
            String thisStem = emIndex.getStem();
            if(thisStem == providedStem)
                return count;
            else
            {
                count++;
            }
        }
        return count;
    }

    //getIndexSize returns the number of different stems that have been indexed
    public int getIndexSize()
    {
        return emIndexList.size();
    }

    //withIdNStemgetFrequency returns the frequency of the stem occuring in that document
    public int withIdNStemgetFrequency(int documentID, String stem)
    {
        //System.out.print("   calling withIdNStemgetFrequency   ");
        Integer ithStem = getIndexNumber(stem);
        if(ithStem == getIndexSize())
        {
            return 0;
        }
        else
        {
            EmIndex relatedEmIndex = emIndexList.get(ithStem);
            return relatedEmIndex.getFreq(documentID);
        }
    }
    //getStemWithId returns the id of the stem
    public String getStemWithId(Integer StemId) {
        return emIndexList.get(StemId).getStem();
    }
}