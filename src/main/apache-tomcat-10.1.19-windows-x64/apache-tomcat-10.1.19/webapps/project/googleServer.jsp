<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ page import="hk.ust.comp4321.SearchEngine" %>
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
	String inputString = current;
%>


<%
    ServletContext servletContext = getServletContext();
    String filePath = servletContext.getRealPath("stopwords.txt");
%>



    <%-- JSP code to handle form submission --%>
    <%
        inputString = request.getParameter("inputString");
        SearchEngine javaObject = new SearchEngine();
        ArrayList<String> outputString = javaObject.processInput(current, filePath);

        if (inputString != null && !inputString.isEmpty()) {
            outputString = javaObject.processInput(inputString, filePath);
            current = inputString;}
    %>

    <%-- HTML form to input the string --%>
    <form action="" method="post">
        <label for="inputString">Enter a string:</label>
        <input type="text" name="inputString" id="inputString" placeholder="<%= current %>">
        <input type="submit" value="Search">
    </form>

    <%
        out.println("<p>Input string: " + current + "</p>");
        out.println("<p>Output string: " + "</p>");
        for (String str : outputString) {
            out.println(str+ "<br>");
        }
        out.println("filePath " + filePath);
    %>




	<!--
		<form action="add">
		Change Query : <input type = "text" name = "num1">
		<input type = "submit">
		</form>
	 -->



</body>
</html>