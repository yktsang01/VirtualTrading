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
