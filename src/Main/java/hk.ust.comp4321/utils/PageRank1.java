package hk.ust.comp4321.utils;

import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;

import java.util.*;

//    public String getWordFromIdTitle(int wordId) throws IOException {
//        return (String) IdTitle2Word.get(wordId);
//    }



public class PageRank1 {
//    private static StopStem stopStem;
//
//    // PageRankByStem gets the query and returns the score for documents based on query only
//    // If the query contain the stem again, the stem will be rated as more important
//    // a0t1b2: title find score for body fill in 2, find score for title fill in 1
//    public static List<List<Double>> RankStem(IndexTable indexTable, ForwardInvertedIndex forwardIndexTable, Integer a0t1b2) throws Exception {
//        Integer stemSize = 0;
//        if (a0t1b2 == 1)
//            stemSize = forwardIndexTable.getWordIdTitle();
//        else // a0t1b2 == 2
//            stemSize = forwardIndexTable.getWordIdBody();
//
//        List<Integer> df = new ArrayList<>(); // number of doc. containing term j
//        List<List<Integer>> tflist = new ArrayList<>(); // row:document, col:stemmed query words (number of documents * number of unique stem in query)
//        List<Double> idf = new ArrayList<>(); // how many documents contains this stem (length = number of unique stem in query)
//        Integer N = indexTable.getPageId() + 1;
//        List<Integer> maxtf = new ArrayList<>(); // the stem that occurs at highest frequency in this document (length = doc len)
//
//        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
//            Map<String, Integer> keyword2Freq = forwardIndexTable.getKeywordFrequency(pageid, -1, a0t1b2);
//            List<Integer> tf = new ArrayList<>(); // frequency of term j in document i
//            for (int stemID = 0; stemID < stemSize; stemID++) {
//                // set up tf and df
//                Integer frequencyInDoc = keyword2Freq.get(forwardIndexTable.getWordFromIdBody(stemID));
//                if (pageid == 0) {
//                    df.add(0); // maintain dimension of df = number of unique stem in query so that we can set later
//                }
//                if (!(frequencyInDoc == null)) {
//                    df.set(stemID, df.get(stemID) + 1);
//                    tf.add(frequencyInDoc);
//                } else {
//                    frequencyInDoc = 0;
//                    tf.add(frequencyInDoc);
//                }
//            }
//            tflist.add(tf);
//        }
//        //System.out.println("df" + df);
//        // calculate idf ( larger idf means less document contain stem means stem more important)
//        for (Integer dfItem : df) {
//            if (dfItem != 0) {
//                idf.add(Math.log(N / dfItem) / Math.log(2)); // inverse document frequency
//            } else {
//                idf.add(0.0);
//            }
//        }
//        //System.out.println("idf" + idf);
//        // get the maxtf
//        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
//            Integer firstValue = 0;
//            Map<String, Integer> keyword2Freq = forwardIndexTable.getKeywordFrequency(pageid, 1, a0t1b2);
//            if (!keyword2Freq.isEmpty()) {
//                //Map.Entry<String, Integer> firstEntry = keyword2Freq.entrySet().iterator().next();
//                //firstValue = firstEntry.getValue();
//                firstValue = keyword2Freq.values().iterator().next();
//            }
//            maxtf.add(firstValue);
//        }
//        // improved term weights
//        // wij = (0.5 + 0.5 (tf/maxtf)) * log(1+N/df)
//        List<List<Double>> weight = new ArrayList<>();
//        Integer countRow = 0; // document
//        for (List<Integer> tfElements : tflist) {
//            List<Double> weightOfRow = new ArrayList<>();
//            Integer countColumn = 0;
//            for (Integer tfElement : tfElements) {
//                Integer maxtfElement = maxtf.get(countRow);
//                Integer dfItem = df.get(countColumn);
//                if (maxtfElement != 0 && N != 0 && dfItem != 0) // avoid divide by zero
//                    weightOfRow.add(0.5 + ((double) tfElement / (double) maxtfElement) * Math.log(1 + N / dfItem));
//                else
//                    weightOfRow.add(0.5);
//                countColumn++;
//            }
//            countRow++;
//            weight.add(weightOfRow);
//        }
//        return weight;
//    }
//
//    public static List<Double> RankStemWithQuery(IndexTable indexTable, ForwardInvertedIndex forwardIndexTable, String query, Integer a0t1b2, List<List<Double>> weight) throws Exception {
//        stopStem = new StopStem("resources/stopwords.txt");
//        String[] words = query.split(" ");
//        List<String> stemsList = new ArrayList<>(); // convert query to stems and store it here
//        List<Integer> frequency = new ArrayList<>(); // in case query contain the stem word more than once, the frequency is stored here
//        for (String word : words)
//        {
//            if (!stopStem.isStopWord(word)) {
//                String curStem = stopStem.stem(word);
//                if(curStem == "")
//                    continue;
//                Integer stemIndex = 0;
//                boolean added = false;
//                for (String stemElement : stemsList) {
//                    if(stemElement.equals(curStem))
//                    {
//                        added = true;
//                        frequency.set(stemIndex, frequency.get(stemIndex) + 1);
//                        break;
//                    }
//                    stemIndex++;
//                }
//                if(added == false)
//                {
//                    stemsList.add(curStem);
//                    frequency.add(1);
//                }
//            }
//        }
//        // if we can get no stem from the query
//        if(frequency.size()==0)
//        {
//            List <Double> returnValue = new ArrayList<>();
//            for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
//                returnValue.add(0.0);
//            }
//            return returnValue;
//        }
//
//        List<Integer> queryVector = new ArrayList<>();
//        // turn the query also into the format of the weight
//        Integer stemSize = 0;
//        if (a0t1b2 == 1)
//            stemSize = forwardIndexTable.getWordIdTitle();
//        else // a0t1b2 == 2
//            stemSize = forwardIndexTable.getWordIdBody();
//
//        for (int stemID = 0; stemID < stemSize; stemID++) {
//            Integer frequencyIndex = 0;
//            Boolean added = false;
//            for (String stem : stemsList)
//            {
//                if (a0t1b2 == 1) {
//                    if(forwardIndexTable.getWordFromIdTitle(stemID).equals(stem))
//                    {
//                        queryVector.add(frequency.get(frequencyIndex));
//                        added = true;
//                    }
//                }
//                if (a0t1b2 == 2){
//                    if(forwardIndexTable.getWordFromIdBody(stemID).equals(stem))
//                    {
//                        queryVector.add(frequency.get(frequencyIndex));
//                        added = true;
//                    }
//                }
//                if(added == false)
//                {
//                    queryVector.add(0);
//                }
//                frequencyIndex++;
//            }
//        }
//        System.out.println("queryVector" + queryVector);
//
//        Double freqSqSummation = 0.0;
//        for(Integer freq : frequency)
//        {
//            freqSqSummation += (freq * freq);
//        }
//
//        List<Double> score = new ArrayList<>();
//        for (List<Double> weightOfRow: weight)
//        {   Double weightSqSummation = 0.0;
//            Double scoreOfRow = 0.0;
//            Integer count = 0;
//            for(Double weightElement : weightOfRow)
//            {
//                scoreOfRow = scoreOfRow + queryVector.get(count) * weightElement;
//                count++;
//                weightSqSummation += (weightElement*weightElement);
//            }
//            score.add(scoreOfRow / (Math.sqrt(weightSqSummation) * Math.sqrt(freqSqSummation)));
//        }
//        System.out.println("score" + score);
//        return score;
//    }
//
//    public static void CheckPageRank(IndexTable indexTable, ForwardInvertedIndex forwardIndexTable, String query, Integer a0t1b2, List<Double>score) throws Exception
//    {
//        stopStem = new StopStem("resources/stopwords.txt");
//        String[] words = query.split(" ");
//        List<String> stemsList = new ArrayList<>(); // convert query to stems and store it here
//        List<Integer> frequency = new ArrayList<>(); // in case query contain the stem word more than once, the frequency is stored here
//        for (String word : words)
//        {
//            if (!stopStem.isStopWord(word)) {
//                String curStem = stopStem.stem(word);
//                if(curStem == "")
//                    continue;
//                Integer stemIndex = 0;
//                boolean added = false;
//                for (String stemElement : stemsList) {
//                    if(stemElement.equals(curStem))
//                    {
//                        added = true;
//                        frequency.set(stemIndex, frequency.get(stemIndex) + 1);
//                        break;
//                    }
//                    stemIndex++;
//                }
//                if(added == false)
//                {
//                    stemsList.add(curStem);
//                    frequency.add(1);
//                }
//            }
//        }
//        // if we can get no stem from the query
//        if(frequency.size()==0)
//        {
//            List <Double> returnValue = new ArrayList<>();
//            for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
//                returnValue.add(0.0);
//            }
//        }
//// Done with handling query. Did not do tfidf for query, but just count its related unique stems
///////////////////////////////////////////////////////////////////////////////////////////////////////
//// The following will handle words in document by tfidf/maxtf and do cosine with query
//
//        Integer N = indexTable.getPageId() + 1; // number of documents in collection
//        List<Integer> df = new ArrayList<>(); // number of doc. containing term j
//        List<List<Integer>> tflist = new ArrayList<>(); // row:document, col:stemmed query words (number of documents * number of unique stem in query)
//        List <Double> idf = new ArrayList<>(); // how many documents contains this stem (length = number of unique stem in query)
//        List<Integer> maxtf = new ArrayList<>(); // the stem that occurs at highest frequency in this document (length = doc len)
//        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) { // for document i
//            List<Integer> tf = new ArrayList<>(); // frequency of term j in document i
//            Map<String, Integer> keyword2Freq = forwardIndexTable.getKeywordFrequency(pageid,-1, a0t1b2);
//
//            // set up tf and df
//            Integer count = 0;
//            for (String stem : stemsList) { // for query stem j
//                Integer frequencyInDoc = keyword2Freq.get(stem);
//                if (pageid == 0) {
//                    df.add(0); // maintain dimension of df = number of unique stem in query so that we can set later
//                }
//                if (!(frequencyInDoc == null)) {
//                    df.set(count, df.get(count) + 1);
//                    tf.add(frequencyInDoc);
//                }
//                else{
//                    frequencyInDoc = 0;
//                    tf.add(frequencyInDoc);
//                }
//                count++;
//            }
//            tflist.add(tf);
//            System.out.println("tf" + pageid + tf + " score" + score.get(pageid));
//        }
//    }

    public static void PageRankByLink(IndexTable indexTable) throws Exception
    {
        Double dampingFactor = 0.5;
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++)
        {
            WebNode currentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), pageid, WebNode.class);
            currentWebNode.updatePagerank(1-dampingFactor);
            List<String> ParentList = currentWebNode.getParent();
            for (String parentURL : ParentList) {
                WebNode parentWebNode;
                int parentId = indexTable.getIdFromUrl(parentURL);
                if(parentId == -1) // this should not happen(?)
                {
                    continue;
                }
                parentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), parentId, WebNode.class);
                Double ParentPageRank = parentWebNode.getPagerank();
                Double PageRankIncrease = ParentPageRank / parentWebNode.getChildren().size();
                currentWebNode.updatePagerank( currentWebNode.getPagerank() + dampingFactor * PageRankIncrease);
            }
        }
    }

    public static List<Double> getLinkWeights(IndexTable indexTable) throws Exception
    {
        List<Double> linkWeights = new LinkedList<>();
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++)
        {
            WebNode webNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), pageid, WebNode.class);
            linkWeights.add(webNode.getPagerank());
        }
        return linkWeights;
    }




}

