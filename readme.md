# Team
| Name         | Student ID | Email                     |
|--------------|------------|---------------------------|
| Yeung Yu San | 20861929   | ysyeungad@connect.ust.hk |
| Choi Hei Ting| 20856508   | htchoiad@connect.ust.hk  |


# Phase1 Testing Guide:
1. Run the ProjectPhase1.java file in the src folder, there is a main specifically for testing.
2. The program will automatically run the crawler and indexer.
3. The program will automatically generate the IndexTable and ForwardInvertedIndex.
4. The program will automatically generate the output file spider_result.txt.
5. Please don't run other test cases in the test folder, as they are for our own testing purposes and may not be updated nor relevant to the current version of the project.

# Project Structure
## Crawler:
The Crawler.java file is a class that providing web crawling function.<br>

## IndexTable:
The IndexTable.java file is a class that providing mapping function, which is primarily constructed by crawler.<br>
### Function: 
1. URL <=> pageId mapping function.<br>
2. pageId <=> WebNode mapping function.<br>
**Remarks: The pageId is a unique identifier for each page, and the WebNode is a class that stores the information of the page.**<br>
**WebNode stores the ID, modified date and URL of the webpage, and its children-parent URL**

## ForwardInvertedIndex: 
The ForwardInvertedIndex.java file is a class that providing forward indexing and inverted indexing function, which is primarily constructed by indexer.<br>
### Function: 
1. Word <=> wordId mapping function for title and body respectively.<br>
2. Inverted indexing for title and body respectively.<br>
3. Forward indexing for title and body respectively.<br>
**Remarks:The position and frequency of each word in the title and body are stored in the both index for fast retrieval.<br>**
.... updating