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
        .container1 {
            display: flex;
            justify-content: center;
            align-items: center;
        }
        .container2 {
          margin-left: 10%;
          margin-right: 10%;
        }
        .title {
          font-size: 6vh;;
          margin-top: 0;
          margin-bottom: 2px;
          text-align: center;
        }
        .input-box {
            width: 40vw;
            height: 5vh;
            border: 2px solid rgba(0, 0, 180, 0.7);
            border-radius: 15px 0 0 15px;
            animation: border-color-animation 5s infinite;
            box-shadow: 0 0 5px rgba(0, 0, 20, 0.3);
            margin-bottom: 2px;
        }
        #submitButton {
            background-color: rgba(0, 0, 255, 0.5) !important;
            border-radius: 0 15px 15px 0 !important;
            border: 2px solid rgba(0, 0, 180, 0.5) !important;
            animation: border-color-animation 3s infinite !important;
            height: 6vh !important;
            padding: 1vh 2vw !important;
            box-shadow: 0 0 5px rgba(0, 0, 20, 0.3) !important;
            cursor: pointer !important;
        }
        #submitButton:hover {
          background-color: #b3b3b3 !important;
          border: 2px solid rgba(0, 0, 0, 0.9) !important;
        }
        .optionContainer {
            position: fixed;
            top: 0;
            right: 0;
        }
        .box {
          background-color: #f0f0f0;
          border: 1px solid #ccc;
          margin: 10px;
          border-radius: 2vw;
          padding: 2vh 2vw !important;
        }
        .titleText {
            font-size: 1.5rem;
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
    <div class="container1">
        <div>
          <h1 class="title">Google</h1>
        </div>
    </div>
    <div class="container1">
        <form action="" method="post">
        <div>
            <input type="text" name="inputString" id="inputString" class="input-box" placeholder="<%= current %>" style="display: inline;">
            <button id="submitButton" type="submit" value="Search"> Search </button>
        </div>
    </div>
    <div class="container1">
        <div>
            <select name="option" id="option">
                <option value="2">Phase Length <= 2</option>
                <option value="3">Phase Length <= 3</option>
                <option value="-1">Phase Length <= Infinite</option>
            </select>
            &nbsp;&nbsp;
        <label for="checkboxId">Link based ranking</label>
        <input type="checkbox" name="checkboxName" id="checkboxId" value="checkboxValue">

        </div>
        </form>
    </div>
    <div class="container2">
    <%
        out.println("<p>Input string: " + current + "</p>");
        out.println("<p>Input option: " + currentoption + "</p>");
        out.println("<p>Checkbox option: " + currentcheckboxValue + "</p>");

        for (int i = 0; i < outputW.size(); i++){
           WebNode currentW = outputW.get(i);
           Double currentS = outputS.get(outputI.get(i));
    %>
            <div class="box">
            <span class="titleText"><%= TitleExtractor.extractTitle(currentW.getUrl()) %></span><br>
    <%
            out.println("score: " + currentS + "<br>");
            String currentURL = currentW.getUrl();
            out.println("URL: " + "<a href=\"" + currentURL + "\">" + currentURL + "</a><br>");

            out.println(currentW.getLastModifiedDate());
            out.println(" &nbsp; size: " + String.valueOf(PageSizeExtractor.extractPageSize(currentW.getUrl())) + "<br>");

            out.println(javaObject.nodeKeyWord(currentW) + "<br>");

            int count = 0;
            for (String parentLink : currentW.getParent()) {
                if (count >= 10) {
                    break;
                }
                count++;
                out.println("parent link: " + "<a href=\"" + parentLink + "\">" + parentLink + "</a><br>");
            }
            count = 0;
            for (String childLink : currentW.getChildren()) {
                if (count >= 10) {
                    break;
                }
                count++;
                out.println("children link: " + "<a href=\"" + childLink + "\">" + childLink + "</a><br>");
            }
            %>
            </div>
         <% } %>

    </div>

</body>
</html>