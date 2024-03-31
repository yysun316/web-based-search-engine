package hk.ust.comp4321.utils;

import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;

import java.util.*;

public class PageRank {
    private static StopStem stopStem;

    // PageRankByLink gets a rootURL and undergoes BFS where the link score is updated in webnode
    public static void PageRankByLink(IndexTable indexTable, String rootURL) throws Exception {
        Double dampingFactor = 0.5;
        List<String> res = new LinkedList<>(); // store the result
        Queue<String> queue = new LinkedList<>();
        queue.add(rootURL);
        Integer count = 0;
        while (!queue.isEmpty()) {
            String parentURL = queue.poll();
            res.add(parentURL);
            // get parent webnode
            WebNode parentWebNode;
            int parId = indexTable.getIdFromUrl(parentURL);
            if (parId == -1) {
                //System.out.println("Error: Em expect the node to exist but it doesn't according to indextable");
                //System.out.println("Corresponding problematic node is with URL: " + parentURL);
                // The webpage does not exist in dataBase as we only index 30 pages
                System.out.println(parentURL +" has not been indexed");
                continue;
            }
            count++;
            System.out.println("count is " + count);
            parentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), parId, WebNode.class);
            Double parentOriginalPageRank = parentWebNode.getPagerank();
            System.out.println(parentURL +" has children:");

            List<String> ChildrenList = parentWebNode.getChildren();
            Double parentFactor = parentOriginalPageRank / ChildrenList.size();

            // iterate through the list of child url of parent
            // get child webnodes and update pageranks
            for (String childURL : ChildrenList) {
                System.out.println(childURL);
                // if the parent child relationship has been set up previously then we need to do nothing
                WebNode childWebNode;
                int childId = indexTable.getIdFromUrl(childURL);
                childWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), childId, WebNode.class);
                Double childOriginalPageRank = childWebNode.getPagerank();
                childWebNode.updatePagerank(childOriginalPageRank + dampingFactor * parentFactor);
                if (!res.contains(childURL)) {
                    queue.add(childURL);
                }
            }
            parentWebNode.updatePagerank(parentOriginalPageRank - dampingFactor);
        }
    }

    // PageRankByStem gets the query and returns the score for documents based on query only
    // If the query contain the stem again, the stem will be rated as more important
    public static List<Double> PageRankByStem(IndexTable indexTable, ForwardInvertedIndex forwardIndexTable, String query) throws Exception
    {
        stopStem = new StopStem("resources/stopwords.txt");
        String[] words = query.split(" ");
        List<String> stemsList = new ArrayList<>(); // convert query to stems and store it here
        List<Integer> frequency = new ArrayList<>(); // in case query contain the stem word more than once, the frequency is stored here
        for (String word : words)
        {
            if (!stopStem.isStopWord(word)) {
                String curStem = stopStem.stem(word);
                if(curStem == "")
                    continue;
                Integer stemIndex = 0;
                boolean added = false;
                for (String stemElement : stemsList) {
                    if(stemElement.equals(curStem))
                    {
                        added = true;
                        frequency.set(stemIndex, frequency.get(stemIndex) + 1);
                        break;
                    }
                    stemIndex++;
                }
                if(added == false)
                {
                    stemsList.add(curStem);
                    frequency.add(1);
                }
            }
        }
        if(frequency.size()==0)
        {
            List <Double> returnValue = new ArrayList<>();
            for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
                returnValue.add(0.0);
            }
            return returnValue;
        }

        Integer N = indexTable.getPageId() + 1; // number of documents in collection
        List<Integer> df = new ArrayList<>(); // number of doc. containing term j
        List<List<Integer>> tflist = new ArrayList<>(); // row:document, col:frequency of words
        List <Double> idf = new ArrayList<>();
        List<Integer> maxtf = new ArrayList<>();
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
            List<Integer> tf = new ArrayList<>(); // frequency of term j in document i
            Hashtable<String, Integer> keyword2Freq = forwardIndexTable.getKeywordFrequency(pageid);
            Integer count = 0;
            for (String stem : stemsList) {
                Integer frequencyInDoc = keyword2Freq.get(stem);
                if (pageid == 0) {
                    df.add(0);
                    maxtf.add(0);
                }
                if (!(frequencyInDoc == null)) {
                    df.set(count, df.get(count) + 1);
                    tf.add(frequencyInDoc);
                }
                else{
                    frequencyInDoc = 0;
                    tf.add(frequencyInDoc);
                }
                if(maxtf.get(count)<frequencyInDoc)
                {
                    maxtf.set(count, frequencyInDoc);
                }
                count++;
            }
            tflist.add(tf);
            for (Integer dfItem : df) {
                if (dfItem != 0) {
                    idf.add(Math.log(N / dfItem) / Math.log(2)); // inverse document frequency
                } else {
                    idf.add(0.0);
                }
            }
        }
        List<List<Double>> weight = new ArrayList<>();
        for (List<Integer> tfElements: tflist)
        {
            List<Double> weightOfRow = new ArrayList<>();
            Integer count = 0;
            for(Integer tfElement : tfElements)
            {
                Integer maxtfElement = maxtf.get(count);
                Integer dfItem = df.get(count);
                if(maxtfElement!=0)
                    weightOfRow.add(0.5+((double)tfElement/(double)maxtfElement)*Math.log(1 + N / dfItem));
                else
                    weightOfRow.add(0.0);
                count++;
            }
            weight.add(weightOfRow);
        }

        List<Double> score = new ArrayList<>();
        for (List<Double> weightOfRow: weight)
        {
            Double scoreOfRow = 0.0;
            Integer count = 0;

            for(Double weightElement : weightOfRow)
            {
                scoreOfRow = scoreOfRow + frequency.get(count) * weightElement;
                count++;
            }
            score.add(scoreOfRow);
        }
        return score;
    }

    // PageRankByBoth should be run after running PageRankByLink for n times and PageRankByStem for one time
    // it return a list of id. The order in the list corresponds to the order it should be displayed by search engine
    // change stemWeight if you want to alter the importance of link based and stem based ranking
    public static List<Integer> PageRankByBoth(IndexTable indexTable, List<Double> stemRank) throws Exception
    {
        List<Double> ratedScore = new ArrayList<>();
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
            WebNode webNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), pageid, WebNode.class);
            Double stemWeight = 11.7;
            ratedScore.add(webNode.getPagerank() +stemWeight * stemRank.get(pageid));
        }
        List<Integer> indices = new ArrayList<>(ratedScore.size());
        for (int i = 0; i < ratedScore.size(); i++) {
            indices.add(i);
        }
        Collections.sort(indices, (a, b) -> Double.compare(ratedScore.get(b), ratedScore.get(a)));
        return indices;
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
