# ------------------- toolchain step -------------------

FROM ubuntu:18.04 AS toolchain

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

# VOLUME /home/web/projects

CMD /bin/bash


# ------------------- build step -------------------

FROM toolchain AS build

COPY backend /app/backend_src

USER root
RUN chown -R web:web /app/backend_src

USER web
WORKDIR /app/backend_src

# just let the sbt warm up cache
RUN bash -c "source /home/web/.sdkman/bin/sdkman-init.sh && sbt shutdown"

RUN bash -c "source /home/web/.sdkman/bin/sdkman-init.sh && sbt dist"


# ------------------- main step -------------------

FROM toolchain

USER root
RUN apt install -y vim

COPY --from=build /app/backend_src/target/universal/backend-1.0.zip /app/backend_dist/backend-1.0.zip
RUN cd /app/backend_dist && unzip backend-1.0.zip


WORKDIR /app/backend_dist/backend-1.0/
RUN chown -R web:web .

USER web

# CMD source .sdkman/bin/sdkman-init.sh && target/universal/backend-1.0/bin/backend
# CMD ./bin/backend -Dplay.evolutions.db.default.autoApply=true

COPY entrypoint.sh .
CMD bash entrypoint.sh
