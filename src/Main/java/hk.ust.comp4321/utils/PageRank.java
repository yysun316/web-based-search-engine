package hk.ust.comp4321.utils;

import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;

import java.util.*;

public class PageRank {
    private static StopStem stopStem;

    // PageRankByLink gets a rootURL and undergoes BFS where the link score is updated in webnode
    public static void PageRankByLinkRemoved(IndexTable indexTable, String rootURL) throws Exception {
        Double dampingFactor = 0.5;
        List<String> res = new LinkedList<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(rootURL);
        while (!queue.isEmpty()) {
            String parentURL = queue.poll();
            res.add(parentURL);
            // get parent webnode
            WebNode parentWebNode;
            int parId = indexTable.getIdFromUrl(parentURL);
            if (parId == -1) {
                // The webpage does not exist in dataBase as we only index 30 pages
                continue;
            }
            parentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), parId, WebNode.class);
            Double parentOriginalPageRank = parentWebNode.getPagerank();

            List<String> ChildrenList = parentWebNode.getChildren();
            Double parentFactor = parentOriginalPageRank / ChildrenList.size();

            // iterate through the list of child url of parent
            // get child webnodes and update pageranks
            for (String childURL : ChildrenList) {
                //System.out.println(childURL);
                // if the parent child relationship has been set up previously then we need to do nothing
                WebNode childWebNode;
                int childId = indexTable.getIdFromUrl(childURL);
                if(childId == -1)
                {
                    continue;
                }
                childWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), childId, WebNode.class);
                Double childOriginalPageRank = childWebNode.getPagerank();
                childWebNode.updatePagerank(childOriginalPageRank + dampingFactor * parentFactor);
                indexTable.updateEntry(TreeNames.id2WebNode.toString(), childId ,childWebNode);
                if (!res.contains(childURL)) {
                    queue.add(childURL);
                }
            }
            parentWebNode.updatePagerank(parentOriginalPageRank - dampingFactor);
            indexTable.updateEntry(TreeNames.id2WebNode.toString(), parId ,parentWebNode);
        }
    }

//    // PageRankByStem gets the query and returns the score for documents based on query only
//    // If the query contain the stem again, the stem will be rated as more important
//    // a0t1b2: title find score for body fill in 2, find score for title fill in 1
//    public static List<Double> CheckPageRankRemoved(IndexTable indexTable, ForwardInvertedIndex forwardIndexTable, String query, Integer a0t1b2) throws Exception
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
//            return returnValue;
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
//            System.out.println("tf" + pageid + tf);
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
//            Map<String, Integer> keyword2Freq = forwardIndexTable.getKeywordFrequency(pageid,1, a0t1b2);
//            if (!keyword2Freq.isEmpty()) {
//                //Map.Entry<String, Integer> firstEntry = keyword2Freq.entrySet().iterator().next();
//                //firstValue = firstEntry.getValue();
//                firstValue = keyword2Freq.values().iterator().next();
//            }
//                maxtf.add(firstValue);
//        }
//        //System.out.println("maxtf" + maxtf);
//
//        // improved term weights
//        // wij = (0.5 + 0.5 (tf/maxtf)) * log(1+N/df)
//        List<List<Double>> weight = new ArrayList<>();
//        Integer countRow = 0;
//        for (List<Integer> tfElements: tflist)
//        {
//            List<Double> weightOfRow = new ArrayList<>();
//            Integer countColumn = 0;
//            for(Integer tfElement : tfElements)
//            {
//                Integer maxtfElement = maxtf.get(countRow);
//                Integer dfItem = df.get(countColumn);
//                if(maxtfElement!=0 && N!=0 && dfItem!=0) // avoid divide by zero
//                    weightOfRow.add(0.5+((double)tfElement/(double)maxtfElement)*Math.log(1 + N / dfItem));
//                else
//                    weightOfRow.add(0.5);
//                countColumn++;
//            }
//            countRow++;
//            weight.add(weightOfRow);
//            System.out.println("weight of row" + countRow + weightOfRow);
//            //System.out.println("weightOfRow" + weightOfRow);
//        }
//// finished calculating tfidf for stems
////////////////////////////////////////////////////////////////////////////////////////////////
//// do cosine similarity
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
//                scoreOfRow = scoreOfRow + frequency.get(count) * weightElement;
//                count++;
//                weightSqSummation += (weightElement*weightElement);
//            }
//            score.add(scoreOfRow / (Math.sqrt(weightSqSummation) * Math.sqrt(freqSqSummation)));
//        }
//        return score;
//    }

    // PageRankByBoth should be run after running PageRankByLink for n times and PageRankByStem for one time
    // it return a list of id. The order in the list corresponds to the order it should be displayed by search engine
    // change stemWeight if you want to alter the importance of link based and stem based ranking
    public static List<Integer> PageRankByBoth(IndexTable indexTable, List<Double> stemRankt, List<Double> stemRankb) throws Exception
    {
        List<Double> ratedScore = new ArrayList<>();
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
            WebNode webNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), pageid, WebNode.class);
            Double stemWeightb = 11.7;
            Double stemWeightt = 21.7;
            ratedScore.add(webNode.getPagerank() + stemWeightt * stemRankt.get(pageid) + stemWeightb * stemRankb.get(pageid));
        }
        List<Integer> indices = new ArrayList<>(ratedScore.size());
        for (int i = 0; i < ratedScore.size(); i++) {
            indices.add(i);
        }
        Collections.sort(indices, (a, b) -> Double.compare(ratedScore.get(b), ratedScore.get(a)));
        return indices;
    }

    public static List<Double> getLinkWeightsRemoved(IndexTable indexTable) throws Exception
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

