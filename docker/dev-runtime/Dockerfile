FROM openjdk:11-jre-slim
RUN apt-get update && apt-get install curl -y
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash -
RUN apt-get install nodejs -y

ENTRYPOINT ["/bin/bash"]