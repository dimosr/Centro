Centro - Find the best meeting point
===================

Centro is a web-based application, used for getting suggestions about meeting places given several starting points and additional constraints.

----------
Requirements
----------

 - Java JDK (1.7 or above)
 - A Tomcat server
 - Maven (Help: https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html)
 - mySQL 

----------
Build with no IDE
----------
These instructions can guide someone to build & deploy the project without using any IDE, just using Maven. Execute the following commands in the root folder of the project.
- Download & install all maven dependencies:  
```
mvn clean install
```

- Execute all unit tests
```
mvn test
```

- Build and Create .war file (all unit tests will be executed):  
```
mvn package
```

- Get `Centro.war` file from `target` folder and copy it in Tomcat `webapps` folder. Startup Tomcat.

----------
Build using Eclipse
----------
- Download & extract Eclipse Enterprise Edition (http://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/keplerr)
- Generate the Eclipse project by executing the following command at the root of the project directory:
```
mvn eclipse:eclipse
```

- Import the project in Eclipse (click file -> import -> existing project into workspace)
- Configure a Tomcat 6 server (Tomcat 7 is incompatible with Java 1.7) (right click in the server tab -> new -> server)
  (If you don't have tomcat installed, after server -> new, click on add on the right of "server runtime environment", then click "Download and install")
- Click on "run" then choose "run on server" and pick the server you've just configured
- You're done =D

------------
Build using Netbeans
------------
- Install Netbeans from the [official site](https://netbeans.org/)
- Install a Tomcat Server and configure it with Netbeans
- Import this maven project by clicking File -> New Project -> Maven -> Project with Existing POM -> (browse to pom.xml file and select it)
- Configure the following properties of Netbeans project :
	1. Project Properties -> Run -> setup Tomcat as Server of this project
	2. Project Properties -> Actions -> Run Project -> Execute Goals (add "package")
	3. Project Properties -> Actions -> Build Project -> Execute Goals (add "package")
	4. Project Properties -> Actions -> Test Project -> Execute Goals (add "test")
- Select "Build Project" to build the project
- Select "Test Project" to execute all Unit Tests
- Select "Run Project" through Netbeans. Application is now deployed in Tomcat.

-------
Database versioning
-------
The database schema is also versioned, in folder `database` : 
- Execute `setup.sql` to create the database and the user
- Execute `schema-init.sql` to create the initial database schema, adding the first data

After changing the database schema, each developer should commit the changes in the schema in a separate script, and also add the necessary query in the script that adds the corresponding record in the table `schema_version`. This table is used to check which scripts have been executed in the current version and their order.