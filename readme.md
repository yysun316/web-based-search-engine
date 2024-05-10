# Team
| Name         | Student ID | Email                     |
|--------------|------------|---------------------------|
| Yeung Yu San | 20861929   | ysyeungad@connect.ust.hk |
| Choi Hei Ting| 20856508   | htchoiad@connect.ust.hk  |


# Project Set-Up
This README file provides step-by-step instructions to set up and run the project on your local machine.

# Prerequisites
Java Development Kit (JDK 17) installed <br>

# Procedures for Windows
1. Build the Project Build the project to generate the class files by pressing build.
2. Copy Class Files Copy the compiled class files to the Tomcat webapps directory in the command line.
```
cp -r target/classes /path/to/apache-tomcat-10.1.19-windows-x64/apache-tomcat-10.1.19/webapps/project/WEB-INF
```
3. Set Environment Variables CATALINA_HOME with value:
``` 
C:\Desktop\project_10-3\project_10-3\src\main\apache-tomcat-10.1.19-windows-x64\apache-tomcat-10.1.19
```
4. Create a folder to store the database files and open a command line interface in that folder.
5. Run %CATALINA_HOME%\bin\startup.bat in the command line.
6. Open https://localhost:8080/project/Google.html in your browser.
7. The initial query takes around 3 minutes because of the crawling and indexing process.
8. The run time for each process can be viewed in the console.

