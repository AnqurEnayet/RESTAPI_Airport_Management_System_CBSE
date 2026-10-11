# Jakarta EE - Calculator - Server - Example Application

This is a example server application for the Jakarta Exercises. It can be used as a starting point.

Run the Server with:

Maven:
```
mvn clean package wildfly:dev
```

or in Eclipse:
```
Project -> Run As -> (create a Maven run configuration with the same goal)
```
If you work in and start the server in Eclipse (maven run configuration with the same goal) code changes will be updated directly to the server.



## Access to the Web Interface

1. Run the server

2. python -m http.server 

FRONTEND: "http://localhost:8000/mainmenu.html"



USE THIS AS THE LINK NOW (PostMan Check)
This should be similar for everyone: "http://localhost:8080/st.cbse.logisticscenter.server/rest-api/"
Example: "http://localhost:8080/st.cbse.logisticscenter.server/rest-api/flights/all"


