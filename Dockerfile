FROM ubuntu:18.04

RUN apt update && apt upgrade

# install java 8
RUN apt install openjdk-8-jre-headless -y

# install prerequisites
RUN apt-get install \
    curl \
    gnupg2 \
    -y

# install sbt
RUN echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list
RUN curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add
RUN apt-get update && apt-get install sbt -y

# install scala
RUN apt-get install scala -y

# install node
# source: https://github.com/nodesource/distributions#installation-instructions
RUN curl -fsSL https://deb.nodesource.com/setup_15.x | bash -
RUN apt-get install -y nodejs

RUN useradd -ms /bin/bash web

USER web
WORKDIR /home/web

# port for React app server
EXPOSE 8100

# port for Play backend app
EXPOSE 8200

VOLUME /home/web/projects

CMD /bin/bash
