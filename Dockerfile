FROM openjdk:8
RUN mkdir -p /app
COPY target/thundra-demo-todo-app-java-1.0.0.jar /app/thundra-demo-todo-app-java.jar
COPY thundra-agent-bootstrap.jar /app/thundra-agent-bootstrap.jar
WORKDIR /app
EXPOSE 8080
ENV THUNDRA_AGENT_APPLICATION_NAME=thundra-demo-todo-app-java
ENV THUNDRA_AGENT_APPLICATION_TAG_THUNDRA_DEBUGGER_ONBOARDING=true
ENV THUNDRA_AGENT_APPLICATION_VERSION=1.0.0
ENV THUNDRA_AGENT_APPLICATION_STAGE=onboarding
ENTRYPOINT [ "java", "-javaagent:thundra-agent-bootstrap.jar", "-jar", "thundra-demo-todo-app-java.jar" ]
