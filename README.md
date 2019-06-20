# myretail-service
myRetail Restful Service

## Technologies
Java JDK 1.8<br>
Spring 2.1.6 Release<br>
Maven 3.6.1<br>
MonogoDB 4.0.3<br>
Tomcat 8.5.3<br>

## Database
database=products<br>
collections=products<br>
mongodb host=localhost<br>
mongodb port=27017<br>

## Local Build
Launch Mongodb<br>
Built using IntelliJ, Run As Spring Boot App<br>
http://localhost:8080/ for Application<br>
http://localhost:27017/ for DB<br>

## Directory
http://localhost:8080/products/ - GET - Shows all products<br>
http://localhost:8080/products/{1} - GET - Get a product based on ID from Mongo DB and also gets external API Info, Shows External API ID, Name, Local DB Price<br>
http://localhost:8080/products{1} - PUT - Updates Price By ID<br>

## Swagger
http://localhost:8080/swagger-ui.html