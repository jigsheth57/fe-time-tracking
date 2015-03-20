Readme for Random Reply - Spring Boot
=====================================

##Description
This is a Spring Boot application to demonstrate basic Spring web app attributes.  The goal of this application is to demonstrate several Spring and Spring Boot features:

* 5 classes to create a data-driven REST service (including 2 pojo data objects and a main method class)
* Autoconfiguration for Spring Boot applications
* Spring Boot starter packs for JPA and REST services
* Spring test fixtures
* Spring Web Security automatically applied to REST services
* Explicit port binding and statelessness (ala 12-factor apps)
* Management and monitoring with the actuator module
* Run standalone or on Cloud Foundry

##Getting Started
To run the application standalone:

    mvn clean package
    java -jar target/fe-time-tracking-1.0.0-SNAPSHOT.jar

which starts an embedded Tomcat server listening on port 8080.

Open [http://localhost:8080/](http://localhost:8080/) in a browse


##Actuator
The [actuator module](http://docs.spring.io/spring-boot/docs/1.2.2.RELEASE/reference/htmlsingle/#production-ready) is also enabled, which exposes a number of management services.  For example, open [http://localhost:8080/info](http://localhost:8080/info) in a browser to see build information about the application.  See the documentation link above for more information about the endpoints exposed. 

##Testing
Unit and integration tests automatically start the embedded Tomcat server and / or embedded database as needed.  Using Spring's `SpringApplicationConfiguration` and `IntegrationTest` annotations on test cases automatically provide the required contexts for testing.
