<%@ page import="my_package.Test" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>

<html>
    <body>
        <%
            if(request.getParameter("txtname1")!="")
            {
                out.println("You input "+request.getParameter("txtname1"));
            }
            else
            {
                out.println("You input nothing");
            }
            out.println(Test.hello_world());
        %>
    </body>



</html>
