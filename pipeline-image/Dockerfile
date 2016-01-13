FROM ubuntu:trusty

ENV HOME /root
RUN \
  echo '' > $HOME/.bashrc && \
  echo "[ -f ~/.bashrc ] && source ~/.bashrc" >> $HOME/.bash_profile

RUN \
  echo "deb http://apt.gemnasium.com stable main" > /etc/apt/sources.list.d/gemnasium.list && \
  echo "deb http://archive.ubuntu.com/ubuntu trusty-backports main restricted universe multiverse" > /etc/apt/sources.list.d/backports.list && \
  apt-key adv --recv-keys --keyserver keyserver.ubuntu.com E5CEAB0AC5F1CA2A && \
  apt-get update && \
  apt-get -y install \
    bison \
    build-essential \
    curl \
    debconf-utils \
    gemnasium-toolbelt \
    git \
    jq/trusty-backports \
    libcurl4-openssl-dev \
    libmariadbclient-dev \
    libpq-dev \
    libreadline-dev \
    libsqlite3-dev \
    libssl-dev \
    libxml2-dev \
    libxslt-dev \
    libyaml-dev \
    mercurial \
    python-software-properties \
    python-pip \
    unzip \
    wget \
    zip \
    zlib1g-dev

RUN  \
  echo 'mysql-server mysql-server/root_password password password' | debconf-set-selections && \
  echo 'mysql-server mysql-server/root_password_again password password' | debconf-set-selections && \
  apt-get -y install \
    mysql-server \
    postgresql-contrib-9.3 \
    postgresql && \
  chmod 755 /var/lib/mysql && \
  sed -i 's/peer/trust/' /etc/postgresql/9.3/main/pg_hba.conf && \
  sed -i 's/md5/trust/' /etc/postgresql/9.3/main/pg_hba.conf && \
  service mysql restart && \
  service postgresql restart && \
  echo 'CREATE DATABASE cc_test;' | mysql -u root -ppassword && \
  createdb -U postgres cc_test

RUN \
  wget https://github.com/postmodern/ruby-install/archive/v0.4.1.tar.gz -P /tmp && \
  tar zxf /tmp/v0.4.1.tar.gz -C /tmp && \
  cd /tmp/ruby-install-0.4.1 && make install && \
  ruby-install ruby 2.1.8 && \
  ruby-install ruby 2.2.4 && \
  rm -rf /usr/local/src/ruby-2.1.8 && \
  rm -rf /usr/local/src/ruby-2.2.4 && \
  rm -rf /tmp/*

RUN \
  git clone https://github.com/postmodern/chruby /tmp/chruby && \
  cd /tmp/chruby && git reset --hard 310bd34d1fcbb3365814c85018114123cef5a41f && \
  cd /tmp/chruby && ./scripts/setup.sh && \
  echo 'source /usr/local/share/chruby/chruby.sh' >> $HOME/.bashrc && \
  echo 'chruby 2.2.4' >> $HOME/.bashrc && \
  rm -rf /tmp/*

RUN \
  bash -l -c "pip install awscli==1.6.6" && \
  bash -l -c "pip install s3cmd" && \
  bash -l -c "gem update --system" && \
  bash -l -c "gem install bundler --no-rdoc --no-ri" && \
  bash -l -c "gem install bosh_cli --version 1.3156.0 --no-rdoc --no-ri"

ENV GOPATH $HOME/go
ENV PATH $HOME/go/bin:/usr/local/go/bin:$PATH
RUN \
  wget https://storage.googleapis.com/golang/go1.4.3.linux-amd64.tar.gz -P /tmp && \
  tar xzvf /tmp/go1.4.3.linux-amd64.tar.gz -C /usr/local && \
  mkdir $GOPATH && \
  rm -rf /tmp/*

RUN \
  git clone https://github.com/cloudfoundry-incubator/spiff.git /tmp/go/src/github.com/cloudfoundry-incubator/spiff && \
  GOPATH=/tmp/go /tmp/go/src/github.com/cloudfoundry-incubator/spiff/scripts/build && \
  cp /tmp/go/src/github.com/cloudfoundry-incubator/spiff/spiff /usr/local/bin && \
  rm -rf /tmp/*

RUN \
  wget https://dl.bintray.com/mitchellh/vagrant/vagrant_1.6.5_x86_64.deb -P /tmp && \
  dpkg --install /tmp/vagrant_1.6.5_x86_64.deb && \
  vagrant plugin install vagrant-aws && \
  rm -rf /tmp/*

RUN \
  wget https://s3.amazonaws.com/go-cli/releases/v6.12.3/cf-linux-amd64.tgz -P /tmp && \
  tar xvf /tmp/cf-linux-amd64.tgz -C /usr/local/bin && \
  ln -s /usr/local/bin/cf /usr/local/bin/gcf && \
  rm -rf /tmp/*

RUN \
  wget https://s3.amazonaws.com/bosh-init-artifacts/bosh-init-0.0.70-linux-amd64 -P /tmp && \
  mv /tmp/bosh-init-0.0.70-linux-amd64 /usr/local/bin/bosh-init && \
  chmod +x /usr/local/bin/bosh-init && \
  rm -rf /tmp/*
