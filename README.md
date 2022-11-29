# thundra-demo-todo-app-java (demo)

## Running todo app locally
Todo app is a [Spring Boot](https://spring.io/guides/gs/spring-boot) application built using [Maven](https://spring.io/guides/gs/maven/). You can build a jar file and run it from the command line:
 

```
git clone https://github.com/thundra-io/thundra-demo-todo-app-java.git
cd thundra-demo-todo-app-java
./mvnw package
java -jar target/*.jar
```

You can then access todo app here: http://localhost:8080/


Or you can run it from Maven directly using the Spring Boot Maven plugin. If you do this it will pick up changes that you make in the project immediately (changes to Java source files require a compile as well - most people use an IDE for this):

```
./mvnw spring-boot:run
```
