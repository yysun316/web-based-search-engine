/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat/10.1.19
 * Generated at: 2024-04-26 16:05:07 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.jsp.*;
import hk.ust.comp4321.SearchEngine;
import hk.ust.comp4321.utils.WebNode;
import hk.ust.comp4321.extractors.TitleExtractor;
import hk.ust.comp4321.extractors.PageSizeExtractor;
import hk.ust.comp4321.extractors.LastModifiedDateExtractor;
import java.util.ArrayList;

public final class googleServer_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent,
                 org.apache.jasper.runtime.JspSourceImports,
                 org.apache.jasper.runtime.JspSourceDirectives {

  private static final jakarta.servlet.jsp.JspFactory _jspxFactory =
          jakarta.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private static final java.util.Set<java.lang.String> _jspx_imports_packages;

  private static final java.util.Set<java.lang.String> _jspx_imports_classes;

  static {
    _jspx_imports_packages = new java.util.LinkedHashSet<>(3);
    _jspx_imports_packages.add("jakarta.servlet");
    _jspx_imports_packages.add("jakarta.servlet.http");
    _jspx_imports_packages.add("jakarta.servlet.jsp");
    _jspx_imports_classes = new java.util.LinkedHashSet<>(6);
    _jspx_imports_classes.add("hk.ust.comp4321.extractors.PageSizeExtractor");
    _jspx_imports_classes.add("hk.ust.comp4321.extractors.LastModifiedDateExtractor");
    _jspx_imports_classes.add("hk.ust.comp4321.extractors.TitleExtractor");
    _jspx_imports_classes.add("hk.ust.comp4321.SearchEngine");
    _jspx_imports_classes.add("hk.ust.comp4321.utils.WebNode");
    _jspx_imports_classes.add("java.util.ArrayList");
  }

  private volatile jakarta.el.ExpressionFactory _el_expressionfactory;
  private volatile org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public java.util.Set<java.lang.String> getPackageImports() {
    return _jspx_imports_packages;
  }

  public java.util.Set<java.lang.String> getClassImports() {
    return _jspx_imports_classes;
  }

  public boolean getErrorOnELNotFound() {
    return false;
  }

  public jakarta.el.ExpressionFactory _jsp_getExpressionFactory() {
    if (_el_expressionfactory == null) {
      synchronized (this) {
        if (_el_expressionfactory == null) {
          _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
        }
      }
    }
    return _el_expressionfactory;
  }

  public org.apache.tomcat.InstanceManager _jsp_getInstanceManager() {
    if (_jsp_instancemanager == null) {
      synchronized (this) {
        if (_jsp_instancemanager == null) {
          _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
        }
      }
    }
    return _jsp_instancemanager;
  }

  public void _jspInit() {
  }

  public void _jspDestroy() {
  }

  public void _jspService(final jakarta.servlet.http.HttpServletRequest request, final jakarta.servlet.http.HttpServletResponse response)
      throws java.io.IOException, jakarta.servlet.ServletException {

    if (!jakarta.servlet.DispatcherType.ERROR.equals(request.getDispatcherType())) {
      final java.lang.String _jspx_method = request.getMethod();
      if ("OPTIONS".equals(_jspx_method)) {
        response.setHeader("Allow","GET, HEAD, POST, OPTIONS");
        return;
      }
      if (!"GET".equals(_jspx_method) && !"POST".equals(_jspx_method) && !"HEAD".equals(_jspx_method)) {
        response.setHeader("Allow","GET, HEAD, POST, OPTIONS");
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "JSPs only permit GET, POST or HEAD. Jasper also permits OPTIONS");
        return;
      }
    }

    final jakarta.servlet.jsp.PageContext pageContext;
    jakarta.servlet.http.HttpSession session = null;
    final jakarta.servlet.ServletContext application;
    final jakarta.servlet.ServletConfig config;
    jakarta.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    jakarta.servlet.jsp.JspWriter _jspx_out = null;
    jakarta.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html; charset=ISO-8859-1");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<!DOCTYPE html>\r\n");
      out.write("<html>\r\n");
      out.write("<head>\r\n");
      out.write("<meta charset=\"ISO-8859-1\">\r\n");
      out.write("<script src=\"jquery\"></script>\r\n");
      out.write("<title>Worse than Google</title>\r\n");
      out.write("    <style>\r\n");
      out.write("        .container1 {\r\n");
      out.write("            display: flex;\r\n");
      out.write("            justify-content: center;\r\n");
      out.write("            align-items: center;\r\n");
      out.write("        }\r\n");
      out.write("        .container2 {\r\n");
      out.write("          margin-left: 10%;\r\n");
      out.write("          margin-right: 10%;\r\n");
      out.write("        }\r\n");
      out.write("        .title {\r\n");
      out.write("          font-size: 6vh;;\r\n");
      out.write("          margin-top: 0;\r\n");
      out.write("          margin-bottom: 2px;\r\n");
      out.write("          text-align: center;\r\n");
      out.write("        }\r\n");
      out.write("        .input-box {\r\n");
      out.write("            width: 40vw;\r\n");
      out.write("            height: 5vh;\r\n");
      out.write("            border: 2px solid rgba(0, 0, 180, 0.7);\r\n");
      out.write("            border-radius: 15px 0 0 15px;\r\n");
      out.write("            animation: border-color-animation 5s infinite;\r\n");
      out.write("            box-shadow: 0 0 5px rgba(0, 0, 20, 0.3);\r\n");
      out.write("            margin-bottom: 2px;\r\n");
      out.write("        }\r\n");
      out.write("        #submitButton {\r\n");
      out.write("            background-color: rgba(0, 0, 255, 0.5) !important;\r\n");
      out.write("            border-radius: 0 15px 15px 0 !important;\r\n");
      out.write("            border: 2px solid rgba(0, 0, 180, 0.5) !important;\r\n");
      out.write("            animation: border-color-animation 3s infinite !important;\r\n");
      out.write("            height: 6vh !important;\r\n");
      out.write("            padding: 1vh 2vw !important;\r\n");
      out.write("            box-shadow: 0 0 5px rgba(0, 0, 20, 0.3) !important;\r\n");
      out.write("            cursor: pointer !important;\r\n");
      out.write("        }\r\n");
      out.write("        #submitButton:hover {\r\n");
      out.write("          background-color: #b3b3b3 !important;\r\n");
      out.write("          border: 2px solid rgba(0, 0, 0, 0.9) !important;\r\n");
      out.write("        }\r\n");
      out.write("        .optionContainer {\r\n");
      out.write("            position: fixed;\r\n");
      out.write("            top: 0;\r\n");
      out.write("            right: 0;\r\n");
      out.write("        }\r\n");
      out.write("        .box {\r\n");
      out.write("          background-color: #f0f0f0;\r\n");
      out.write("          border: 1px solid #ccc;\r\n");
      out.write("          margin: 10px;\r\n");
      out.write("          border-radius: 2vw;\r\n");
      out.write("          padding: 2vh 2vw !important;\r\n");
      out.write("        }\r\n");
      out.write("        .titleText {\r\n");
      out.write("            font-size: 1.5rem;\r\n");
      out.write("        }\r\n");
      out.write("    </style>\r\n");
      out.write("</head>\r\n");
      out.write("<body>\r\n");
      out.write("\r\n");

	String current = request.getParameter("txtname");
	String currentoption = request.getParameter("option");
	String currentcheckboxValue = request.getParameter("checkboxName");
	String inputString = current;
	String selectedOption = currentoption;
	String checkboxValue = currentcheckboxValue;

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");

    ServletContext servletContext = getServletContext();
    String filePath = servletContext.getRealPath("stopwords.txt");

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("    ");
      out.write("\r\n");
      out.write("    ");

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
    
      out.write("\r\n");
      out.write("    <div class=\"container1\">\r\n");
      out.write("        <div>\r\n");
      out.write("          <h1 class=\"title\">Google</h1>\r\n");
      out.write("        </div>\r\n");
      out.write("    </div>\r\n");
      out.write("    <div class=\"container1\">\r\n");
      out.write("        <form action=\"\" method=\"post\">\r\n");
      out.write("        <div>\r\n");
      out.write("            <input type=\"text\" name=\"inputString\" id=\"inputString\" class=\"input-box\" placeholder=\"");
      out.print( current );
      out.write("\" style=\"display: inline;\">\r\n");
      out.write("            <button id=\"submitButton\" type=\"submit\" value=\"Search\"> Search </button>\r\n");
      out.write("        </div>\r\n");
      out.write("    </div>\r\n");
      out.write("    <div class=\"container1\">\r\n");
      out.write("        <div>\r\n");
      out.write("            <select name=\"option\" id=\"option\">\r\n");
      out.write("                <option value=\"2\">Phase Length <= 2</option>\r\n");
      out.write("                <option value=\"3\">Phase Length <= 3</option>\r\n");
      out.write("                <option value=\"-1\">Phase Length <= Infinite</option>\r\n");
      out.write("            </select>\r\n");
      out.write("            &nbsp;&nbsp;\r\n");
      out.write("        <label for=\"checkboxId\">Link based ranking</label>\r\n");
      out.write("        <input type=\"checkbox\" name=\"checkboxName\" id=\"checkboxId\" value=\"checkboxValue\">\r\n");
      out.write("\r\n");
      out.write("        </div>\r\n");
      out.write("        </form>\r\n");
      out.write("    </div>\r\n");
      out.write("    <div class=\"container2\">\r\n");
      out.write("    ");

        out.println("<p>Input string: " + current + "</p>");
        out.println("<p>Input option: " + currentoption + "</p>");
        out.println("<p>Checkbox option: " + currentcheckboxValue + "</p>");

        for (int i = 0; i < outputW.size(); i++){
           WebNode currentW = outputW.get(i);
           Double currentS = outputS.get(outputI.get(i));
    
      out.write("\r\n");
      out.write("            <div class=\"box\">\r\n");
      out.write("            <span class=\"titleText\">");
      out.print( TitleExtractor.extractTitle(currentW.getUrl()) );
      out.write("</span><br>\r\n");
      out.write("    ");

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
            
      out.write("\r\n");
      out.write("            </div>\r\n");
      out.write("         ");
 } 
      out.write("\r\n");
      out.write("\r\n");
      out.write("    </div>\r\n");
      out.write("\r\n");
      out.write("</body>\r\n");
      out.write("</html>");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof jakarta.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try {
            if (response.isCommitted()) {
              out.flush();
            } else {
              out.clearBuffer();
            }
          } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
