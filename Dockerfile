FROM ubuntu:18.04

RUN apt update && apt upgrade

# install java 8
RUN apt install openjdk-8-jre-headless -y

# install scala 2.12
RUN apt-get install \
    curl \
    gnupg2 \
    -y
RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add
RUN apt-get update && apt-get install sbt -y

RUN useradd web

USER web
