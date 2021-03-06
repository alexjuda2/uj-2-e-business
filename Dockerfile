FROM ubuntu:18.04

RUN apt update && apt upgrade

RUN apt install openjdk-8-jre-headless -y

RUN useradd web

USER web
