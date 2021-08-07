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

# warm up sbt cache

COPY backend/project/build.properties /app/backend_src/project/
COPY backend/project/plugins.sbt /app/backend_src/project/
COPY backend/build.sbt /app/backend_src/

USER root
RUN chown -R web:web /app/backend_src

USER web
WORKDIR /app/backend_src/

# Let the sbt warm up its cache. This allows us reusing the docker layer.
RUN bash -c "source /home/web/.sdkman/bin/sdkman-init.sh && sbt shutdown"

# Copy the rest of the project & build dist.
USER root
COPY backend /app/backend_src
RUN chown -R web:web /app/backend_src

USER web
WORKDIR /app/backend_src

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

COPY entrypoint.sh .

# Needed by OAuth flow.
# Get these two from Google Cloud Console.
ENV GOOGLE_CLIENT_ID "fixme"
ENV GOOGLE_CLIENT_SECRET "fixme"
# Set this according to the host domain
ENV GOOGLE_REDIRECT_URL "http://localhost:9000/authenticate/google"

CMD bash entrypoint.sh
