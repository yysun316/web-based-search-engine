package hk.ust.comp4321.extractors;// HTMLParser Library $Name: v1_6 $ - A java-based parser for HTML
// http://sourceforge.org/projects/htmlparser
// Copyright (C) 2012 Pengfei Zhao
//
//
//package Lab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.htmlparser.beans.LinkBean;
import org.htmlparser.util.ParserException;
import javax.swing.tree.TreeNode;


/**
 * src.src.linkExtractor.LinkExtractor extracts all the links from the given webpage
 * and prints them on standard output.
 */


public class LinkExtractor
{
	private static Vector<String> webVec;
	private static List<TreeNode> nodeList;
	private static int iteration;
	private String link = "";
	public LinkExtractor(String url){
		link = url;
	}
	
	public void extractLinks(TreeNode parent) throws ParserException
	{
		// extract links in url and return them
		// ADD YOUR CODES HERE
		//System.out.println("run extract links ");
	    LinkBean lb = new LinkBean();
	    lb.setURL(link);
	    URL[] URL_array = lb.getLinks();
	    for(int i=0; i<URL_array.length; i++){
	    	//System.out.println(URL_array[i]);
			String stringURL = URL_array[i].toExternalForm();
			if(!webVec.contains(stringURL))
			{	
				//System.out.println("With URL " + link + "found new URL " + stringURL);
				TreeNode child = new TreeNode(stringURL);
				parent.addChild(child);
				nodeList.add(child);
				webVec.add(stringURL);
			}
			else
			{
				//System.out.println("With URL " + link + "meet old URL " + stringURL);
			}
	    }
	}
	
    public static void writeVectorToFile(Vector<String> vector, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String element : vector) {
                writer.write(element);
                writer.newLine();
            }
            System.out.println("Vector elements successfully written to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing the file: " + e.getMessage());
        }
    }

    public static void main (String[] args) throws ParserException
    {	
        //String url = "http://www.cs.ust.hk/";
        //String url = "https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm";
		//String url = "https://comp4321-hkust.github.io/testpages/testpage.htm";
		webVec = new Vector<>();
		webVec.add("https://www.cse.ust.hk/~kwtleung/COMP4321/testpage.htm");
		String url = webVec.firstElement();
        LinkExtractor extractor = new LinkExtractor(url);
		TreeNode root = new TreeNode(url);
		nodeList = new ArrayList<>();
		nodeList.add(root);
        extractor.extractLinks(root);
		iteration = 1;
		while(webVec.size() > iteration)
		{

			url = webVec.get(iteration);
			//System.out.print(iteration +"th iteration with URL " + url);
			extractor = new LinkExtractor(url);
			extractor.extractLinks(nodeList.get(iteration));
			iteration = iteration + 1;
		}
		iteration = 0;

		/**print the elements on the terminal
		for (String element : webVec) {
        	System.out.println(element);
        }
		 */

		writeVectorToFile(webVec, "webpagesExtracted");
		Vector<String> nodeVec = new Vector<>();
		for(int showNo=0; showNo < 30; showNo++)
		{
			//nodeVec.add("ITEM " + String.valueOf(showNo));
			nodeVec.add("PAGE TITLE: " + TitleExtractor.extractTitle(nodeList.get(showNo).getData()));
			nodeVec.add("URL: " + nodeList.get(showNo).getData());
			for(int i=0; i < nodeList.get(showNo).getChildren().size(); i++)
			{
				nodeVec.add("CHILD LINK: " + nodeList.get(showNo).getChildren().get(i).getData());
			}			
			nodeVec.add("------------------------------------------------------------------------------");
		}

		writeVectorToFile(nodeVec, "webpagesParentChild");
    }
}
