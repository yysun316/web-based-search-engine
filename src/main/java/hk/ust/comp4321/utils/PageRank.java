package hk.ust.comp4321.utils;

import hk.ust.comp4321.indexer.StopStem;
import hk.ust.comp4321.invertedIndex.ForwardInvertedIndex;
import hk.ust.comp4321.invertedIndex.IndexTable;

import java.util.*;

public class PageRank {
    private static StopStem stopStem;

    // PageRankByStem gets the query and returns the score for documents based on query only
    // If the query contain the stem again, the stem will be rated as more important
    // a0t1b2: title find score for body fill in 2, find score for title fill in 1
    public static List<List<Double>> RankStem(IndexTable indexTable, ForwardInvertedIndex forwardIndexTable, Integer a0t1b2) throws Exception {
        Integer stemSize;
        if (a0t1b2 == 1)
            stemSize = forwardIndexTable.getWordIdTitle();
        else // a0t1b2 == 2
            stemSize = forwardIndexTable.getWordIdBody();

        //System.out.println("stemSize " + stemSize);
        //System.out.println("test page is at " + forwardIndexTable.getWordIdTitleFromStem("test page"));

        List<Integer> df = new ArrayList<>(); // number of doc. containing term j
        List<List<Integer>> tflist = new ArrayList<>(); // row:document, col:stemmed query words (number of documents * number of unique stem in query)
        List<Double> idf = new ArrayList<>(); // how many documents contains this stem (length = number of unique stem in query)
        Integer N = indexTable.getPageId();
        List<Integer> maxtf = new ArrayList<>(); // the stem that occurs at highest frequency in this document (length = doc len)

        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
            Map<String, Integer> keyword2Freq = forwardIndexTable.getKeywordFrequency(pageid, -1, a0t1b2);
            //System.out.println("keyword2Freq: " + keyword2Freq.toString());
            List<Integer> tf = new ArrayList<>(); // frequency of term j in document i
            for (int stemID = 0; stemID < stemSize; stemID++) {
                // set up tf and df
                Integer frequencyInDoc = null;
                if (a0t1b2 == 1)
                    frequencyInDoc = keyword2Freq.get(forwardIndexTable.getWordFromIdTitle(stemID));
                if(a0t1b2 == 2)
                    frequencyInDoc = keyword2Freq.get(forwardIndexTable.getWordFromIdBody(stemID));
                if(frequencyInDoc!=null)
                {
                    //System.out.println("not null " + frequencyInDoc);
                }
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
            Integer sum = 0;
            for (Integer number : tf) {
                sum += number;
            }
            //System.out.println("sum" + sum + "tf" + tf);
        }
        //for each term, calculate it's document frequency
        //System.out.println("df" + df);
        // calculate idf ( larger idf means less document contain stem means stem more important)
        for (Integer dfItem : df) {
            if (dfItem != 0) {
                idf.add(Math.log(N / dfItem) / Math.log(2)); // inverse document frequency
            } else {
                idf.add(0.0);
            }
        }
        //System.out.println("idf" + idf);
        // get the maxtf
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
            Integer firstValue = 0;
            Map<String, Integer> keyword2Freq = forwardIndexTable.getKeywordFrequency(pageid, 1, a0t1b2);
            if (!keyword2Freq.isEmpty()) {
                //Map.Entry<String, Integer> firstEntry = keyword2Freq.entrySet().iterator().next();
                //firstValue = firstEntry.getValue();
                firstValue = keyword2Freq.values().iterator().next();
            }
            maxtf.add(firstValue);
        }
        // term weights
        List<List<Double>> weight = new ArrayList<>();
        Integer countRow = 0; // document
        for (List<Integer> tfElements : tflist) {
            List<Double> weightOfRow = new ArrayList<>();
            Integer countColumn = 0;
            //System.out.println("tfElements " + tfElements);
            for (Integer tfElement : tfElements) {
                Integer maxtfElement = maxtf.get(countRow);
                Integer dfItem = df.get(countColumn);
                if (maxtfElement != 0) // avoid divide by zero
                    weightOfRow.add((double) tfElement * idf.get(countColumn)/ (double) maxtfElement);
                else
                {
                    //System.out.println("maxtfElement " + maxtfElement);
                    weightOfRow.add(0.0);
                }

                countColumn++;
            }
            double sum = 0.0;
            for (Double weight1 : weightOfRow) {
                sum = sum + weight1;
            }
            if(true)
            {
                //System.out.println("weightOfRow" + countRow + " sum " + sum + " " + weightOfRow);
                //System.out.println(forwardIndexTable.getWordFromIdBody(1447));
                //System.out.println("weightOfRow at row " + countRow + " " +weightOfRow);
                int length = weightOfRow.size();
                //System.out.println("Length of the list: " + length);
            }
            countRow++;
            weight.add(weightOfRow);
        }
        return weight;
    }

    public static List<Double> RankStemWithQuery(IndexTable indexTable, ForwardInvertedIndex forwardIndexTable, String query, Integer a0t1b2, List<List<Double>> weight, Integer wp, Integer phalen, String stoppath) throws Exception {
        stopStem = new StopStem(stoppath);
        if(query==null)
        {
            System.out.println("error: in RankStemWithQuery query is null");
            return new ArrayList<>();
        }
        String[] words = query.split(" ");
        List<String> stemsList = new ArrayList<>(); // convert query to stems and store it here
        List<Integer> frequency = new ArrayList<>(); // in case query contain the stem word more than once, the frequency is stored here

        if(wp == 0)
        {
            for (String word : words) {
                if (!stopStem.isStopWord(word)) {
                    String curStem = stopStem.stem(word);
                    if (curStem.equals(""))
                        continue;
                    Integer stemIndex = 0;
                    boolean added = false;
                    for (String stemElement : stemsList) {
                        if (stemElement.equals(curStem)) {
                            added = true;
                            frequency.set(stemIndex, frequency.get(stemIndex) + 1);
                            break;
                        }
                        stemIndex++;
                    }
                    if (added == false) {
                        stemsList.add(curStem);
                        frequency.add(1);
                    }
                }
            }
        }
        if(wp == 1)
        {
            int curPos = 0;
            ArrayList<Integer> pos = new ArrayList<>();
            pos.add(-1);

            for (String word : words) {
                if (stopStem.isStopWord(word) || stopStem.stem(word).equals("")) {
                    pos.add(curPos);
                }
                curPos++;
            }

            ArrayList<String> phrases = new ArrayList<>();
            pos.add(words.length);
            for (int i = 0; i < words.length; i++) {
                words[i] = stopStem.stem(words[i].toLowerCase());
            }
            int start = 0;
            int end = 0;
            for (int i = 0; i < pos.size(); i++) {
                end = pos.get(i);
                if (start == end) {
                    start = end + 1;
                } else if (end - start > 1) {
                    String[] phrase = Arrays.copyOfRange(words, start, end);
                    //System.out.println("Phrase: " + Arrays.toString(phrase));
                    for(int t = 2; t <= phrase.length; t++){// length allowed
                        for (int j = 0; j <= phrase.length - t; j++){ // start position
                            StringJoiner sj = new StringJoiner(" ");
                            for (int k = j; k < j+t; k++) {
                                sj.add(phrase[k]);
                            }
                            phrases.add(sj.toString());
                        }
                    }
                }
                start = end + 1;
            }

            System.out.println("print query phases");
            for (String element : phrases) {
                System.out.println(element);
            }

            for (String curStem : phrases) {
                Integer stemIndex = 0;
                boolean added = false;
                for (String stemElement : stemsList) {
                    if (stemElement.equals(curStem)) {
                        added = true;
                        frequency.set(stemIndex, frequency.get(stemIndex) + 1);
                        break;
                    }
                    stemIndex++;
                }
                if (added == false) {
                    stemsList.add(curStem);
                    frequency.add(1);
                }
            }
        }
        System.out.println("stemsList " + stemsList);
        System.out.println("frequency " + frequency);

        // if we can get no stem from the query
        if(frequency.size()==0)
        {
            List <Double> returnValue = new ArrayList<>();
            for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
                returnValue.add(0.0);
            }
            return returnValue;
        }

        List<Integer> queryVector = new ArrayList<>();
        // turn the query also into the format of the weight
        Integer stemSize = 0;
        if (a0t1b2 == 1)
            stemSize = forwardIndexTable.getWordIdTitle();
        else // a0t1b2 == 2
            stemSize = forwardIndexTable.getWordIdBody();

        //System.out.println("stemSize "+ stemSize);
        //System.out.println("frequency "+ frequency);
        //System.out.println("stemsList "+ stemsList);
        for (int stemID = 0; stemID < stemSize; stemID++) {
            Integer frequencyIndex = 0;
            Boolean added = false;
            for (String stem : stemsList)
            {
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
                        //System.out.println("stem " + stem);
                        //System.out.println("stemID " + stemID);
                        //System.out.println("forwardIndexTable.getWordFromIdBody(stemID) " + forwardIndexTable.getWordFromIdBody(stemID));

                        queryVector.add(frequency.get(frequencyIndex));
                        //System.out.println("stemID is " + stemID);
                        added = true;
                    }
                }
                frequencyIndex++;
            }
            if(added == false)
            {
                queryVector.add(0);
            }
            if(stemID<10)
            {
                //System.out.println("stemID " + stemID);
                //System.out.println("queryVector.size() " + queryVector.size());
            }

        }
        Integer qs = 0;
        for (Integer q : queryVector) {
            qs += q;
        }
        //System.out.println("queryVector" + qs + " | " + queryVector);
        int index = queryVector.indexOf(1);
        //System.out.println("Index of element 1: " + index);
        int length = queryVector.size();
        //System.out.println("Length of the list: " + length);

        //System.out.println("frequency " + frequency);
        Double freqSqSummation = 0.0;
        for(Integer freq : frequency)
        {
            freqSqSummation += (freq * freq);
        }

        List<Double> score = new ArrayList<>();
        Integer countRow = 0;
        for (List<Double> weightOfRow: weight)
        {   Double weightSqSummation = 0.0;
            Double scoreOfRow = 0.0;
            Integer count = 0;
            //System.out.println("weightOfRowInScore" + countRow + " " + weightOfRow);
            for(Double weightElement : weightOfRow)
            {
                if(countRow == 1 && count==7)
                {
                    //System.out.println("weightElement" + weightElement);
                    //System.out.println("queryVector.get(count)" + queryVector.get(count));
                }

                scoreOfRow = scoreOfRow + queryVector.get(count) * weightElement;
                count++;
                weightSqSummation += (weightElement*weightElement);
            }
            if(weightSqSummation!=0.0 && freqSqSummation!=0.0)
                score.add(scoreOfRow / (Math.sqrt(weightSqSummation) * Math.sqrt(freqSqSummation)));
            else
            {
                //System.out.println("weightSqSummation " + weightSqSummation);
                //System.out.println("freqSqSummation " + freqSqSummation);
                score.add(0.0);
            }
            countRow++;
        }
        System.out.println("score" + score);
        return score;
    }

    public static void CheckPageRank(IndexTable indexTable, ForwardInvertedIndex forwardIndexTable, String query, Integer a0t1b2, List<Double>score) throws Exception
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
        // if we can get no stem from the query
        if(frequency.size()==0)
        {
            List <Double> returnValue = new ArrayList<>();
            for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
                returnValue.add(0.0);
            }
        }
// Done with handling query. Did not do tfidf for query, but just count its related unique stems
/////////////////////////////////////////////////////////////////////////////////////////////////////
// The following will handle words in document by tfidf/maxtf and do cosine with query

        Integer N = indexTable.getPageId() + 1; // number of documents in collection
        List<Integer> df = new ArrayList<>(); // number of doc. containing term j
        List<List<Integer>> tflist = new ArrayList<>(); // row:document, col:stemmed query words (number of documents * number of unique stem in query)
        List <Double> idf = new ArrayList<>(); // how many documents contains this stem (length = number of unique stem in query)
        List<Integer> maxtf = new ArrayList<>(); // the stem that occurs at highest frequency in this document (length = doc len)
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) { // for document i
            List<Integer> tf = new ArrayList<>(); // frequency of term j in document i
            Map<String, Integer> keyword2Freq = forwardIndexTable.getKeywordFrequency(pageid,-1, a0t1b2);

            // set up tf and df
            Integer count = 0;
            for (String stem : stemsList) { // for query stem j
                Integer frequencyInDoc = keyword2Freq.get(stem);
                if (pageid == 0) {
                    df.add(0); // maintain dimension of df = number of unique stem in query so that we can set later
                }
                if (!(frequencyInDoc == null)) {
                    df.set(count, df.get(count) + 1);
                    tf.add(frequencyInDoc);
                }
                else{
                    frequencyInDoc = 0;
                    tf.add(frequencyInDoc);
                }
                count++;
            }
            tflist.add(tf);
            //System.out.println("tf" + pageid + tf + " score" + score.get(pageid));
        }
    }

    public static void PageRankByLink(IndexTable indexTable, Double dampingFactor) throws Exception
    {
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++)
        {
            WebNode currentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), pageid, WebNode.class);
            currentWebNode.updatePagerank(1-dampingFactor);
            indexTable.updateEntry(TreeNames.id2WebNode.toString(), pageid, currentWebNode);
            List<String> ParentList = currentWebNode.getParentForRanking();
            for (String parentURL : ParentList) {
                WebNode parentWebNode;
                int parentId = indexTable.getIdFromUrl(parentURL);
                if(parentId == -1) // this should not happen(?)
                {
                    System.out.println("parent does not exist");
                    continue;
                }
                parentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), parentId, WebNode.class);
                Double ParentPageRank = parentWebNode.getPagerank();
                Double PageRankIncrease = ParentPageRank / parentWebNode.getChildForRanking().size();
                // TODO: need delete unnecessary check
                if(parentWebNode.getChildForRanking().size() == 0)
                {
                    System.out.println("parent identified, but parent does not recognize any child");
                    System.out.println("parent is " + parentWebNode.getUrl());
                    System.out.println("child is " + currentWebNode.getUrl());
                    continue;
                }
                currentWebNode.updatePagerank( currentWebNode.getPagerank() + dampingFactor * PageRankIncrease);
                indexTable.updateEntry(TreeNames.id2WebNode.toString(), pageid, currentWebNode);
            }
//            System.out.println(currentWebNode.getUrl());
//            System.out.println(currentWebNode.getPagerank());
        }
        // TODO: DELETE, as the pagerank should not drop to negative
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++)
        {
            WebNode currentWebNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), pageid, WebNode.class);
            if(currentWebNode.getPagerank() < 0)
            {
                currentWebNode.updatePagerank( 0.0);
                indexTable.updateEntry(TreeNames.id2WebNode.toString(), pageid, currentWebNode);
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

    public static ArrayList<Double> PageScoreByBoth(IndexTable indexTable, int checked, List<Double> stemRankt, List<Double> stemRankb, List<Double> stemRanktp, List<Double> stemRankbp, Double stemWeightt, Double stemWeightb, Double stemWeighttp, Double stemWeightbp) throws Exception
    {
        ArrayList<Double> ratedScore = new ArrayList<>();
        for (int pageid = 0; pageid < indexTable.getPageId(); pageid++) {
            WebNode webNode = indexTable.getEntry(TreeNames.id2WebNode.toString(), pageid, WebNode.class);
            ratedScore.add(webNode.getPagerank() * checked / 3 + stemWeightt * stemRankt.get(pageid) + stemWeightb * stemRankb.get(pageid) + stemWeighttp * stemRanktp.get(pageid) + stemWeightbp * stemRankbp.get(pageid));
        }
        return ratedScore;
    }


    public static ArrayList<Integer> PageRankByBoth(List<Double> ratedScore) throws Exception
    {
        ArrayList<Integer> indices = new ArrayList<>(ratedScore.size());
        for (int i = 0; i < ratedScore.size(); i++) {
            indices.add(i);
        }
        Collections.sort(indices, (a, b) -> Double.compare(ratedScore.get(b), ratedScore.get(a)));
        return indices;
    }
}

