package hk.ust.comp4321.indexer;

import hk.ust.comp4321.extractors.StringExtractor;
import hk.ust.comp4321.extractors.TitleExtractor;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;
import hk.ust.comp4321.utils.TreeNames;
import jdbm.btree.BTree;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import hk.ust.comp4321.utils.Posting;

public class dealWithPhases {
    private static StopStem stopStem;
    public static ArrayList<String> getPhases(String query,StopStem stopStem){
        ArrayList<String> arrayPhase = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            String phrase = matcher.group(1);
            if(stopStem.isStopWord(phrase))
                continue;
            String sisEmpty = stopStem.stem(phrase);
            if(sisEmpty.isEmpty())
                continue;
            arrayPhase.add(sisEmpty);
        }
        return arrayPhase;
    }

    public static ArrayList<Double> weightIncreaseByPhase(IndexTable db1, ForwardInvertedIndex db2, String query,StopStem stopStem, int tb) throws IOException {
        ArrayList<String> phrases = getPhases(query, stopStem);
        int pageIdLimit = db1.getPageId();
        ArrayList<Double> weights = new ArrayList<>(Collections.nCopies(pageIdLimit, 0.0));
        Double weightPhase = 3.0;
            for (String phrase : phrases) {
                String[] words = phrase.split(" ");
                int wordsLength = words.length;
                BTree btreestemofpage1 = null;
                BTree btreestemofpage2 = null;
                BTree btreestemofpage3 = null;
                ArrayList<Integer> position1 = null;
                ArrayList<Integer> position2 = null;
                ArrayList<Integer> position3 = null;
                System.out.println("wordsLength="+wordsLength);
                if(wordsLength>0)
                {
                    if(tb == 1)
                        btreestemofpage1 = db2.getTreeTitleFromWordId(db2.getWordIdTitleFromStem(words[0]));
                    if(tb == 2)
                        btreestemofpage1 = db2.getTreeBodyFromWordId(db2.getWordIdTitleFromStem(words[0]));
                    if(btreestemofpage1 == null)
                    {
                        continue;
                    }
                    TupleBrowser browser1 = btreestemofpage1.browse();
                    Tuple tuple1 = new Tuple();
                    while(browser1.getNext(tuple1)) {
                        Posting post = (Posting) tuple1.getKey(); // post are doc that contain word[0]
                        Object posOb1 = btreestemofpage1.find(post); // this document this word where appear?
                        position1 = (ArrayList<Integer>) posOb1;
                        if(wordsLength>1) {
                            Object posOb2 = btreestemofpage2.find(post); // this document this word where appear?
                            position2 = (ArrayList<Integer>) posOb2;
                        }
                        if(wordsLength>2) {
                            Object posOb3 = btreestemofpage3.find(post); // this document this word where appear?
                            position3 = (ArrayList<Integer>) posOb3;
                        }
                        int id = post.getId();
                        for (int element : position1) {
                            if (wordsLength==1||position2.contains(element+1)&&(wordsLength==2||position3.contains(element+2))){
                                weights.set(id, weights.get(id));
                            }
                        }
                    }
                }
                weights.add(weightPhase);
            }
        return weights;
    }

    public static void main(String[] args) {
        String test = "hello world \"phrases\" and \"haha\" ";
        stopStem = new StopStem("resources/stopwords.txt");
        System.out.println(getPhases(test, stopStem));
    }
}