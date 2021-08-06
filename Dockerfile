FROM ubuntu:18.04

RUN apt update && apt upgrade

# install prerequisites
RUN apt-get install -y \
    zip \
    unzip \
    curl \
    gnupg2

# install node
# source: https://github.com/nodesource/distributions#installation-instructions
RUN curl -fsSL https://deb.nodesource.com/setup_15.x | bash -
RUN apt install -y nodejs


RUN useradd -ms /bin/bash web
# run adduser web sudo

USER web
WORKDIR /home/web/

RUN curl -s "https://get.sdkman.io" | bash
RUN chmod a+x ".sdkman/bin/sdkman-init.sh"
RUN bash -c "source .sdkman/bin/sdkman-init.sh && sdk install java 8.0.272.hs-adpt"
RUN bash -c "source .sdkman/bin/sdkman-init.sh && sdk install sbt 1.4.8"
RUN bash -c "source .sdkman/bin/sdkman-init.sh && sdk install scala 2.12.13"

# port for React app server
EXPOSE 8080

# port for Play backend app
EXPOSE 9000

VOLUME /home/web/projects

CMD /bin/bash
