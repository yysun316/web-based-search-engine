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
</head>
<body>
<h1>Google</h1>

<%
	String current = request.getParameter("txtname");
	String currentoption = request.getParameter("option");
	String inputString = current;
	String selectedOption = currentoption;
%>


<%
    ServletContext servletContext = getServletContext();
    String filePath = servletContext.getRealPath("stopwords.txt");
%>



    <%-- JSP code to handle form submission --%>
    <%
        inputString = request.getParameter("inputString");
        selectedOption = request.getParameter("option");
        SearchEngine javaObject = new SearchEngine();
        ArrayList<WebNode> outputW = javaObject.processInput(current, selectedOption, filePath);

        if (inputString != null && !inputString.isEmpty()) {
            out.println("selectedOption is " + selectedOption);
            outputW = javaObject.processInput(inputString, selectedOption, filePath);
            current = inputString;}
    %>

    <%-- HTML form to input the string --%>
    <form action="" method="post">
        <label for="inputString">Enter a string:</label>
        <input type="text" name="inputString" id="inputString" placeholder="<%= current %>">

    <label for="option">Select an option:</label>
    <select name="option" id="option">
        <option value="2">Phase Length <= 2</option>
        <option value="3">Phase Length <= 3</option>
        <option value="4">Phase Length <= Infinite</option>
    </select>

    <input type="submit" value="Search">

    </form>

    <%
        out.println("<p>Input string: " + current + "</p>");
        out.println("<p>Output string: " + "</p>");
        for (WebNode currentW : outputW) {
            out.println(TitleExtractor.extractTitle(currentW.getUrl()) + "<br>");
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