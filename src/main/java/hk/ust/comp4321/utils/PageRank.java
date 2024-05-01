package hk.ust.comp4321.utils;

import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;

import java.util.*;

public class PageRank {
    private static StopStem stopStem;


    /***
     * RankStem gets the index table and forward index table and return the importance of the stem
     * @param indexTable index table
     * @param forwardIndexTable forward index table
     * @param a0t1b2 1 for title, 2 for body
     * @return List<List<Double>>  a matrix that corresponds to the weight of the stem
     * @throws Exception exception
     */
    public static List<List<Double>> RankStem(IndexTable indexTable, ForwardInvertedIndex forwardIndexTable, Integer a0t1b2) throws Exception {
        int stemSize;
        if (a0t1b2 == 1)
            stemSize = forwardIndexTable.getWordIdTitle();
        else // a0t1b2 == 2
            stemSize = forwardIndexTable.getWordIdBody();

        List<Integer> df = new ArrayList<>(); // number of doc. containing term j
        List<List<Integer>> tflist = new ArrayList<>(); // row:document, col:stemmed query words (number of documents * number of unique stem in query)
        List<Double> idf = new ArrayList<>(); // how many documents contains this stem (length = number of unique stem in query)
        int N = indexTable.getPageId();
        List<Integer> maxtf = new ArrayList<>(); // the stem that occurs at highest frequency in this document (length = doc len)

        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
            Map<String, Integer> keyword2Freq = forwardIndexTable.getKeywordFrequency(pageid, -1, a0t1b2);
            List<Integer> tf = new ArrayList<>(); // frequency of term j in document i
            for (int stemID = 0; stemID < stemSize; stemID++) {
                // set up tf and df
                Integer frequencyInDoc = null;
                if (a0t1b2 == 1)
                    frequencyInDoc = keyword2Freq.get(forwardIndexTable.getWordFromIdTitle(stemID));
                if(a0t1b2 == 2)
                    frequencyInDoc = keyword2Freq.get(forwardIndexTable.getWordFromIdBody(stemID));
                if (pageid == 0) {
                    df.add(0); // maintain dimension of df = number of unique stem in query so that we can set later
                }
                if (!(frequencyInDoc == null)) {
                    df.set(stemID, df.get(stemID) + 1);
                    tf.add(frequencyInDoc);
                } else {
                    frequencyInDoc = 0;
                    tf.add(frequencyInDoc);
                }
            }
            tflist.add(tf);
        }
        //for each term, calculate it's document frequency
        // calculate idf ( larger idf means less document contain stem means stem more important)
        for (Integer dfItem : df) {
            if (dfItem != 0) {
                idf.add(Math.log((double) N / dfItem) / Math.log(2)); // inverse document frequency
            } else {
                idf.add(0.0);
            }
        }
        // get the maxtf
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
            Integer firstValue = 0;
            Map<String, Integer> keyword2Freq = forwardIndexTable.getKeywordFrequency(pageid, 1, a0t1b2);
            if (!keyword2Freq.isEmpty()) {
                firstValue = keyword2Freq.values().iterator().next();
            }
            maxtf.add(firstValue);
        }
        // term weights
        List<List<Double>> weight = new ArrayList<>();
        int countRow = 0; // document
        for (List<Integer> tfElements : tflist) {
            List<Double> weightOfRow = new ArrayList<>();
            int countColumn = 0;
            for (Integer tfElement : tfElements) {
                Integer maxtfElement = maxtf.get(countRow);
                if (maxtfElement != 0) // avoid divide by zero
                    weightOfRow.add((double) tfElement * idf.get(countColumn)/ (double) maxtfElement);
                else
                {
                    weightOfRow.add(0.0);
                }
                countColumn++;
            }
            double sum = 0.0;
            for (Double weight1 : weightOfRow) {
                sum = sum + weight1;
            }
            countRow++;
            weight.add(weightOfRow);
        }
        return weight;
    }

    /***
     * RankStemWithQuery gets the query and returns the score for documents based on query words only
     * If the query contain the stem again, the stem will be rated as more important
     * @param indexTable index table
     * @param forwardIndexTable forward index table
     * @param query query
     * @param a0t1b2 title find score for body fill in 2, find score for title fill in 1
     * @param weight weight
     * @param stopPath stop path
     * @return List<Double> score
     * @throws Exception exception
     */
    public static List<Double> RankStemWithQuery(IndexTable indexTable, ForwardInvertedIndex forwardIndexTable, String query, Integer a0t1b2, List<List<Double>> weight, String stopPath) throws Exception {
        stopStem = new StopStem(stopPath);
        if(query==null)
        {
            return new ArrayList<>();
        }
        String[] words = query.split(" ");
        List<String> stemsList = new ArrayList<>(); // convert query to stems and store it here
        List<Integer> frequency = new ArrayList<>(); // in case query contain the stem word more than once, the frequency is stored here

        for (String word : words) {
            if (!stopStem.isStopWord(word)) {
                String curStem = stopStem.stem(word);
                if (curStem.isEmpty())
                    continue;
                int stemIndex = 0;
                boolean added = false;
                for (String stemElement : stemsList) {
                    if (stemElement.equals(curStem)) {
                        added = true;
                        frequency.set(stemIndex, frequency.get(stemIndex) + 1);
                        break;
                    }
                    stemIndex++;
                }
                if (!added) {
                    stemsList.add(curStem);
                    frequency.add(1);
                }
            }
        }

        // if we can get no stem from the query
        if(frequency.isEmpty()) {
            List <Double> returnValue = new ArrayList<>();
            for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
                returnValue.add(0.0);
            }
            return returnValue;
        }

        List<Integer> queryVector = new ArrayList<>();
        // turn the query also into the format of the weight
        int stemSize;
        if (a0t1b2 == 1)
            stemSize = forwardIndexTable.getWordIdTitle();
        else // a0t1b2 == 2
            stemSize = forwardIndexTable.getWordIdBody();

        for (int stemID = 0; stemID < stemSize; stemID++) {
            int frequencyIndex = 0;
            boolean added = false;
            for (String stem : stemsList) {
                if (a0t1b2 == 1) {
                    if(forwardIndexTable.getWordFromIdTitle(stemID).equals(stem))
                    {
                        queryVector.add(frequency.get(frequencyIndex));
                        added = true;
                    }
                }
                if (a0t1b2 == 2){
                    if(forwardIndexTable.getWordFromIdBody(stemID).equals(stem))
                    {
                        queryVector.add(frequency.get(frequencyIndex));
                        added = true;
                    }
                }
                frequencyIndex++;
            }
            if(!added) {
                queryVector.add(0);
            }
        }

        double freqSqSummation = 0.0;
        for(Integer freq : frequency) {
            freqSqSummation += (freq * freq);
        }

        List<Double> score = new ArrayList<>();
        for (List<Double> weightOfRow: weight) {
            double weightSqSummation = 0.0;
            double scoreOfRow = 0.0;
            int count = 0;
            for(Double weightElement : weightOfRow) {
                scoreOfRow = scoreOfRow + queryVector.get(count) * weightElement;
                count++;
                weightSqSummation += (weightElement*weightElement);
            }
            if(weightSqSummation!=0.0 && freqSqSummation!=0.0)
                score.add(scoreOfRow / (Math.sqrt(weightSqSummation) * Math.sqrt(freqSqSummation)));
            else {
                score.add(0.0);
            }
        }
        System.out.println("score" + score);
        return score;
    }

    /**
     * This method calculates the PageRank for each page in the index table.
     * PageRank is a measure of the importance of each page, based on the structure of the hyperlink graph.
     * The method uses the iterative algorithm for PageRank computation,
     * which involves repeatedly updating the PageRank of each page based on the PageRank of its linked pages.
     *
     * @param indexTable The index table containing the pages for which to calculate PageRank.
     * @param dampingFactor The damping factor to be used in the PageRank calculation.
     *                      This is the probability that a random surfer will continue to click on links,
     *                      rather than jumping to a random page. Typically set to 0.85.
     * @throws Exception If an error occurs during the PageRank calculation.
     */
    public static void PageRankByLink(IndexTable indexTable, Double dampingFactor) throws Exception
    {
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
            WebNode currentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), pageid, WebNode.class);
            currentWebNode.updatePagerank(1.0);
            indexTable.updateEntry(TreeNames.id2WebNode.toString(), pageid, currentWebNode);
        }
        for (int count = 0; count < 2; count++) {
            for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
                WebNode currentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), pageid, WebNode.class);
                currentWebNode.updatePagerank(currentWebNode.getPagerank() - dampingFactor);
                indexTable.updateEntry(TreeNames.id2WebNode.toString(), pageid, currentWebNode);
                List<String> ParentList = currentWebNode.getParentForRanking();
                for (String parentURL : ParentList) {
                    WebNode parentWebNode;
                    int parentId = indexTable.getIdFromUrl(parentURL);
                    parentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), parentId, WebNode.class);
                    Double ParentPageRank = parentWebNode.getPagerank();
                    Double PageRankIncrease = ParentPageRank / parentWebNode.getChildForRanking().size();
                    currentWebNode.updatePagerank(currentWebNode.getPagerank() + dampingFactor * PageRankIncrease);
                    indexTable.updateEntry(TreeNames.id2WebNode.toString(), pageid, currentWebNode);
                }
            }
        }
    }
    //
    //
    /***
     * PageScoreByBoth gets the scores acquired in the aspect of stem, phases and links based ranking
     * It summarizes the above and provide an overall score for each page
     * @param indexTable index table
     * @param checked checked
     * @param stemRankt stem rank for title
     * @param stemRankb stem rank for body
     * @param stemRanktp stem rank for title with phase
     * @param stemRankbp stem rank for body with phase
     * @param stemWeightt stem weight for title
     * @param stemWeightb stem weight for body
     * @param stemWeighttp stem weight for title with phase
     * @param stemWeightbp stem weight for body with phase
     * @return ArrayList<Double> rated score
     * @throws Exception exception
     */
    public static ArrayList<Double> PageScoreByBoth(IndexTable indexTable, int checked, List<Double> stemRankt, List<Double> stemRankb, List<Double> stemRanktp, List<Double> stemRankbp, Double stemWeightt, Double stemWeightb, Double stemWeighttp, Double stemWeightbp) throws Exception
    {
        ArrayList<Double> ratedScore = new ArrayList<>();
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
            WebNode webNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), pageid, WebNode.class);
            ratedScore.add(webNode.getPagerank() * checked / 3 + stemWeightt * stemRankt.get(pageid) + stemWeightb * stemRankb.get(pageid) + stemWeighttp * stemRanktp.get(pageid) + stemWeightbp * stemRankbp.get(pageid));
        }
        return ratedScore;
    }

    /***
     * PageRankByBoth gets the webpages' scores and return the id of the webpages with their scores in descending order
     * @param ratedScore rated score
     * @return ArrayList<Integer> indices
     */
    public static ArrayList<Integer> PageRankByBoth(List<Double> ratedScore)
    {
        ArrayList<Integer> indices = new ArrayList<>(ratedScore.size());
        for (int i = 0; i < ratedScore.size(); i++) {
            indices.add(i);
        }
        indices.sort((a, b) -> Double.compare(ratedScore.get(b), ratedScore.get(a)));
        return indices;
    }
}

