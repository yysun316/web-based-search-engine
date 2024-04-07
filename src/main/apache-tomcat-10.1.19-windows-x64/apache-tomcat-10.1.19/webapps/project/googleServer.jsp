<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ page import="hk.ust.comp4321.SearchEngine" %>
<%@ page import="hk.ust.comp4321.utils.WebNode"%>
<%@ page import="hk.ust.comp4321.extractors.TitleExtractor"%>
<%@ page import="hk.ust.comp4321.extractors.PageSizeExtractor"%>
<%@ page import="hk.ust.comp4321.extractors.LastModifiedDateExtractor"%>
<%@ page import="java.util.ArrayList" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<script src="jquery"></script>
<title>Worse than Google</title>
    <style>
        .input-box {
            width: 40vw;
            height: 5vh;
        }
        .optionContainer {
            position: fixed;
            top: 0;
            right: 0;
        }
        .titleText {
            font-size: 25px;
        }

    </style>
</head>
<body>

<%
	String current = request.getParameter("txtname");
	String currentoption = request.getParameter("option");
	String currentcheckboxValue = request.getParameter("checkboxName");
	String inputString = current;
	String selectedOption = currentoption;
	String checkboxValue = currentcheckboxValue;
%>


<%
    ServletContext servletContext = getServletContext();
    String filePath = servletContext.getRealPath("stopwords.txt");
%>



    <%-- JSP code to handle form submission --%>
    <%
        inputString = request.getParameter("inputString");
        selectedOption = request.getParameter("option");
        checkboxValue = request.getParameter("checkboxName");
        SearchEngine javaObject = new SearchEngine();
        ArrayList<Double> outputS = javaObject.processInput(current, selectedOption, checkboxValue, filePath);
        ArrayList<Integer> outputI = javaObject.pageRanking(outputS);
        ArrayList<WebNode> outputW = javaObject.nodeRanking(outputI);
        if (inputString != null && !inputString.isEmpty()) {
            outputS = javaObject.processInput(inputString, selectedOption, checkboxValue, filePath);
            outputI = javaObject.pageRanking(outputS);
            outputW = javaObject.nodeRanking(outputI);
            current = inputString;}
    %>

    <form action="" method="post">
    <h1 style="display: inline; font-size: 2.5em;">Google &nbsp; </h1>
    <input type="text" name="inputString" id="inputString" class="input-box" placeholder="<%= current %>" style="display: inline;">

    <div>
        <select name="option" id="option">
            <option value="2">Phase Length <= 2</option>
            <option value="3">Phase Length <= 3</option>
            <option value="-1">Phase Length <= Infinite</option>
        </select>
    <label for="checkboxId">Link based ranking</label>
    <input type="checkbox" name="checkboxName" id="checkboxId" value="checkboxValue">
    <label class="submit"> &nbsp; </label>
    <input type="submit" value="Search">
    </div>



    </form>

    <%
        out.println("<p>Input string: " + current + "</p>");
        out.println("<p>Input option: " + currentoption + "</p>");
        out.println("<p>Checkbox option: " + currentcheckboxValue + "</p>");

        for (int i = 0; i < outputW.size(); i++){
           WebNode currentW = outputW.get(i);
           Double currentS = outputS.get(outputI.get(i));
           out.println("score: " + currentS + "<br>");
    %>
            <span class="titleText"><%= TitleExtractor.extractTitle(currentW.getUrl()) %></span><br>
    <%
            String currentURL = currentW.getUrl();
            out.println("URL: " + "<a href=\"" + currentURL + "\">" + currentURL + "</a><br>");

            out.println(currentW.getLastModifiedDate());
            out.println(" &nbsp; size: " + String.valueOf(PageSizeExtractor.extractPageSize(currentW.getUrl())) + "<br>");

            out.println(javaObject.nodeKeyWord(currentW) + "<br>");

            for (String parentLink : currentW.getParent()) {
                out.println("parent link: " + "<a href=\"" + parentLink + "\">" + parentLink + "</a><br>");
            }
            for (String childLink : currentW.getParent()) {
                out.println("children link: " + "<a href=\"" + childLink + "\">" + childLink + "</a><br>");
            }

            out.println("<br>");
        }
    %>


</body>
</html>