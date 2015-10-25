Centro - Find the best meeting point
===================

Centro is a software engineering project on which 5 Imperial College students are working.

----------
Requirements
----------

 - Java JDK (1.7 or above)
 - A Tomcat server
 - Maven (Help: https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)

----------
Build
----------
- Download & install all maven dependencies:  
```
mvn clean install
```

- Build and Create .war file:  
```
mvn package
```

----------
Getting started (w/ Eclipse)
----------
- Download & extract Eclipse Enterprise Edition (http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/keplerr)
- Generate the Eclipse project:
```
mvn eclipse:eclipse
```
- Import the project in Eclipse and choose "Run on server" then configure a Tomcat 6 server (Tomcat 7 is incompatible with Java 1.7)
- You're done =D