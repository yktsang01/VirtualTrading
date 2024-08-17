# Virtual Trading

Java web application simulating online stock trading.<br>
First developed in Y2008, revamped in Y2024.<br>
Compatible with Java 17.<br>

The available features can be found <a href="docs/features.txt">here</a>.<br>

The application follows a 3-tiered architecture:<br>
MySQL database for the data layer. Database scripts can be found in <a href="db">db folder</a>.<br>
REST APIs for the service (business logic) layer. The available endpoints can be found <a href="docs/endpoints.txt">here</a>.<br>
Freemarker for the user interface (UI) layer.<br>

Spring Boot v3 as base with embedded Tomcat server.<br>
A &quot;modified&quot; Yahoo Finance API library for fetching stock prices. (Changes <a href="https://github.com/yktsang01/YahooFinanceAPI">here</a>.)<br>
OpenAPI v3 for Swagger.<br>
JSON web token (JWT) for API authentication &amp; authorization.<br>

Domain model:<br>
<img src="docs/domain_model.png">


### Non-Java 8 Runtime

If you do NOT have Java 8 runtime on your machine, you may encounter the below error in the logs when running the project:<br>
PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

The reason why this happens because the data is fetched from the <a href="stocks.json">stocks.json</a> file on the yktsang.com website specified under the key &quot;yahoo.stock.json&quot; in the <a href="src/main/resources/application.properties">application.properties</a> file.

There are several approaches to rectify this:
1. Import the yktsang.com SSL cert to your Java runtime
2. Amend the application.properties "yahoo.stock.json" key to point to a trusted website (requires recompilation)
3. Refactor the application to fetch the data locally or within the application (under classpath inside the JAR/WAR)


Ensure you have admin rights before importing or deleting the SSL cert. These commands will prompt you to enter the keystore password accordingly.

To import the SSL cert to your Java runtime<br>
keytool -importcert -alias yktsang.com -keystore /path/to/cacerts -file /path/to/sslcert

To view the imported entry<br>
keytool -list -v -keystore /path/to/cacerts | grep yktsang.com

To delete the imported entry<br>
keytool -delete -alias yktsang.com -keystore /path/to/cacerts

