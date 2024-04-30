package hk.ust.comp4321.indexer;

import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhasesSearch {
    public static ArrayList<String> getPhases(String query, StopStem stopStem) {
        ArrayList<String> arrayPhase = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            String phrase = matcher.group(1);
            if (stopStem.isStopWord(phrase))
                continue;

            String[] words = phrase.split(" ");
            boolean firstone = true;
            for (String word : words) {
                String sisEmpty = stopStem.stem(word);
                if (sisEmpty.isEmpty())
                    continue;
                if (firstone) {
                    arrayPhase.add(sisEmpty);
                    firstone = false;
                } else {
                    arrayPhase.set(arrayPhase.size() - 1, arrayPhase.get(arrayPhase.size() - 1) + " " + sisEmpty);
                }
            }
        }
        return arrayPhase;
    }

//    public static ArrayList<Double> weightIncreaseByPhase(IndexTable db1, ForwardInvertedIndex db2, String query,StopStem stopStem, int tb) throws IOException {
//        ArrayList<String> phrases = getPhases(query, stopStem);
//        int pageIdLimit = db1.getPageId();
//        ArrayList<Double> weights = new ArrayList<>(Collections.nCopies(pageIdLimit, 0.0));
//        Double weightPhase = 3.0;
//        for (String phrase : phrases) {
//            String[] words = phrase.split(" ");
//            int wordsLength = words.length;
//            BTree btreestemofpage1 = null;
//            BTree btreestemofpage2 = null;
//            BTree btreestemofpage3 = null;
//            ArrayList<Integer> position1 = null;
//            ArrayList<Integer> position2 = null;
//            ArrayList<Integer> position3 = null;
//            if(wordsLength>0) {
//                if(tb == 1) {
//                    btreestemofpage1 = db2.getTreeTitleFromWordId(db2.getWordIdTitleFromStem(words[0]));
//                    if(wordsLength>1) {
//                        btreestemofpage2 = db2.getTreeTitleFromWordId(db2.getWordIdTitleFromStem(words[1]));
//                    }
//                    if(wordsLength>2) {
//                        btreestemofpage3 = db2.getTreeTitleFromWordId(db2.getWordIdTitleFromStem(words[2]));
//                    }
//                }
//
//                if(tb == 2) {
//                    btreestemofpage1 = db2.getTreeBodyFromWordId(db2.getWordIdBodyFromStem(words[0]));
//                    if(wordsLength>1)
//                    {
//                        btreestemofpage2 = db2.getTreeBodyFromWordId(db2.getWordIdBodyFromStem(words[1]));
//                    }
//                    if(wordsLength>2)
//                    {
//                        btreestemofpage3 = db2.getTreeBodyFromWordId(db2.getWordIdBodyFromStem(words[2]));
//                    }
//                }
//
//                if(btreestemofpage1 == null) {
//                    continue;
//                }
//
//                TupleBrowser browser1 = btreestemofpage1.browse();
//                Tuple tuple1 = new Tuple();
//                while(browser1.getNext(tuple1)) {
//                    Posting post = (Posting) tuple1.getKey(); // post are doc that contain word[0]
//                    Object posOb1 = btreestemofpage1.find(post); // this document this word where appear?
//                    position1 = (ArrayList<Integer>) posOb1;
//                    if(wordsLength>1) {
//                        if(btreestemofpage2 == null) {
//                            continue;
//                        }
//                        Object posOb2 = btreestemofpage2.find(post); // this document this word where appear?
//                        position2 = (ArrayList<Integer>) posOb2;
//                    }
//                    if(wordsLength>2) {
//                        Object posOb3 = btreestemofpage3.find(post); // this document this word where appear?
//                        position3 = (ArrayList<Integer>) posOb3;
//                    }
//                    int id = post.getId();
//                    for (int element : position1) {
//                        if (wordsLength==1){
//                            weights.set(id, weights.get(id) + weightPhase);
//                            continue;
//                        }
//                        if(position2!=null&&wordsLength==2)
//                        {
//                            if (position2.contains(element+1)){
//                                weights.set(id, weights.get(id) + weightPhase);
//                                continue;
//                            }
//                        }
//                        if(position3!=null&&wordsLength==3)
//                        {
//                            if (position2.contains(element+1)&&(position3.contains(element+2))){
//                                weights.set(id, weights.get(id) + weightPhase);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return weights;
//    }

    public static ArrayList<Double> weightIncreaseByPhase
            (IndexTable indexTable,
             ForwardInvertedIndex forwardInvertedIndex,
             String query,
             StopStem stopStem,
             int tb) throws IOException {
        Double weightPhase = 3.0;
        ArrayList<String> phrases = getPhases(query, stopStem);
        int pageIdLimit = indexTable.getPageId();
        ArrayList<Double> weights = new ArrayList<>();
        for (String phrase: phrases)
        {
            String[] words = phrase.split(" ");
            int wordsLength = words.length;
            HashMap<Integer, ArrayList<Integer>> map1 = null;
            HashMap<Integer, ArrayList<Integer>> map2 = null;
            HashMap<Integer, ArrayList<Integer>> map3 = null;
            ArrayList<Integer> position1 = null;
            ArrayList<Integer> position2 = null;
            ArrayList<Integer> position3 = null;
            if (wordsLength > 0)
            {
                if (tb == 1) {
                    map1 = forwardInvertedIndex.getPageIDPosFromWordIdTitle(forwardInvertedIndex.getWordIdTitleFromStem(words[0]));
                    if (wordsLength > 1) {
                        map2 = forwardInvertedIndex.getPageIDPosFromWordIdTitle(forwardInvertedIndex.getWordIdTitleFromStem(words[1]));
                    }
                    if (wordsLength > 2) {
                        map3 = forwardInvertedIndex.getPageIDPosFromWordIdTitle(forwardInvertedIndex.getWordIdTitleFromStem(words[2]));
                    }
                }
                if (tb == 2) {
                    map1 = forwardInvertedIndex.getPageIDPosFromWordIdBody(forwardInvertedIndex.getWordIdBodyFromStem(words[0]));
                    if (wordsLength > 1) {
                        map2 = forwardInvertedIndex.getPageIDPosFromWordIdBody(forwardInvertedIndex.getWordIdBodyFromStem(words[1]));
                    }
                    if (wordsLength > 2) {
                        map3 = forwardInvertedIndex.getPageIDPosFromWordIdBody(forwardInvertedIndex.getWordIdBodyFromStem(words[2]));
                    }
                }
                if (map1 == null) {
                    continue;
                }
                for (int id : map1.keySet()) {
                    position1 = map1.get(id);
                    if (wordsLength > 1) {
                        if (map2 == null) {
                            continue;
                        }
                        position2 = map2.get(id);
                    }
                    if (wordsLength > 2) {
                        position3 = map3.get(id);
                    }
                    for (int element : position1) {
                        if (wordsLength == 1) {
                            weights.set(id, weights.get(id) + weightPhase);
                            continue;
                        }
                        if (position2 != null && wordsLength == 2) {
                            if (position2.contains(element + 1)) {
                                weights.set(id, weights.get(id) + weightPhase);
                                continue;
                            }
                        }
                        if (position3 != null && wordsLength == 3) {
                            if (position2.contains(element + 1) && (position3.contains(element + 2))) {
                                weights.set(id, weights.get(id) + weightPhase);
                            }
                        }
                    }
                }
            }
        }
        return weights;
    }

}