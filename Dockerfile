FROM openjdk:17-alpine
ARG JAR_FILE=target/comrade-*.jar
ARG NGROK_CONFIG_FILE=src/main/resources/ngrok/ngrok.yml
ARG ATTACHMENTS_DIR=src/main/resources/attachments
COPY ${JAR_FILE} app/comrade.jar
COPY ${NGROK_CONFIG_FILE} app/ngrok/ngrok.yml
COPY ${ATTACHMENTS_DIR} app/attachments
ENTRYPOINT ["java","-jar","app/comrade.jar"]