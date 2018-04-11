FROM rabbitmq
MAINTAINER Jignesh Sheth "jsheth@pivotal.io"

# Keep upstart from complaining
RUN dpkg-divert --local --rename --add /sbin/initctl
RUN ln -sf /bin/true /sbin/initctl

# Let the conatiner know that there is no tty
ENV DEBIAN_FRONTEND noninteractive

# Avoid ERROR: invoke-rc.d: policy-rc.d denied execution of start.
RUN echo "#!/bin/sh\nexit 0" > /usr/sbin/policy-rc.d

RUN cat /proc/version

# make sure the package repository is up to date
RUN apt-get update
RUN apt-get -y upgrade

#
RUN mkdir -p /usr/share/man/man1
RUN apt-get install -y wget curl default-jdk maven net-tools
RUN wget -O 'cf.deb' https://s3.amazonaws.com/pcf-softwares-57/cf-cli-installer_6.36.1_x86-64.deb
RUN dpkg --install cf.deb
RUN cf -v
RUN java -version
RUN mvn -v
