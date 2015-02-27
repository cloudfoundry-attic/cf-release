FROM ubuntu:trusty

ENV HOME /root
RUN \
  echo '' > $HOME/.bashrc; \
  echo "[ -f ~/.bashrc ] && source ~/.bashrc" >> $HOME/.bash_profile

RUN \
  echo "deb http://apt.gemnasium.com stable main" > /etc/apt/sources.list.d/gemnasium.list; \
  apt-key adv --recv-keys --keyserver keyserver.ubuntu.com E5CEAB0AC5F1CA2A; \
  apt-get update; \
  apt-get -y install \
    bison \
    build-essential \
    curl \
    debconf-utils \
    gemnasium-toolbelt \
    git \
    libcurl4-openssl-dev \
    libmysqlclient-dev \
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
  echo 'mysql-server mysql-server/root_password password password' | debconf-set-selections; \
  echo 'mysql-server mysql-server/root_password_again password password' | debconf-set-selections; \
  apt-get -y install \
    mysql-server \
    postgresql-contrib-9.3 \
    postgresql; \
  chmod 755 /var/lib/mysql; \
  sed -i 's/peer/trust/' /etc/postgresql/9.3/main/pg_hba.conf; \
  sed -i 's/md5/trust/' /etc/postgresql/9.3/main/pg_hba.conf; \
  service mysql restart; \
  service postgresql restart; \
  echo 'CREATE DATABASE cc_test;' | mysql -u root -ppassword; \
  createdb -U postgres cc_test

RUN \
  wget https://github.com/postmodern/ruby-install/archive/v0.4.1.tar.gz -P /tmp; \
  tar zxf /tmp/v0.4.1.tar.gz -C /tmp; \
  cd /tmp/ruby-install-0.4.1 && make install; \
  ruby-install ruby 1.9.3-p547; \
  ruby-install ruby 2.1.4; \
  rm -rf /usr/local/src/ruby-1.9.3-p547; \
  rm -rf /usr/local/src/ruby-2.1.4; \
  rm -rf /tmp/*

RUN \
  git clone https://github.com/postmodern/chruby /tmp/chruby; \
  cd /tmp/chruby && git reset --hard 310bd34d1fcbb3365814c85018114123cef5a41f; \
  cd /tmp/chruby && ./scripts/setup.sh; \
  echo 'source /usr/local/share/chruby/chruby.sh' >> $HOME/.bashrc; \
  echo 'chruby 2.1.4' >> $HOME/.bashrc; \
  rm -rf /tmp/*

RUN \
  bash -l -c "pip install awscli==1.6.6"; \
  bash -l -c "pip install s3cmd"; \
  bash -l -c "gem install bundler --no-rdoc --no-ri"; \
  bash -l -c "gem install bosh_cli --no-rdoc --no-ri"; \
  bash -l -c "chruby 1.9.3-p547; gem install bundler --no-rdoc --no-ri"; \
  bash -l -c "chruby 1.9.3-p547; gem install bosh_cli --no-rdoc --no-ri"

ENV GOPATH $HOME/go
ENV PATH $HOME/go/bin:/usr/local/go/bin:$PATH
RUN \
  wget https://storage.googleapis.com/golang/go1.3.1.linux-amd64.tar.gz -P /tmp; \
  tar xzvf /tmp/go1.3.1.linux-amd64.tar.gz -C /usr/local; \
  mkdir $GOPATH; \
  rm -rf /tmp/*

RUN \
  git clone https://github.com/cloudfoundry-incubator/spiff.git /tmp/go/src/github.com/cloudfoundry-incubator/spiff; \
  GOPATH=/tmp/go /tmp/go/src/github.com/cloudfoundry-incubator/spiff/scripts/build; \
  cp /tmp/go/src/github.com/cloudfoundry-incubator/spiff/spiff /usr/local/bin; \
  rm -rf /tmp/*

RUN \
  wget https://dl.bintray.com/mitchellh/vagrant/vagrant_1.6.5_x86_64.deb -P /tmp; \
  dpkg --install /tmp/vagrant_1.6.5_x86_64.deb; \
  vagrant plugin install vagrant-aws; \
  rm -rf /tmp/*

RUN \
  wget https://s3.amazonaws.com/go-cli/releases/v6.9.0/cf-linux-amd64.tgz -P /tmp; \
  tar xvf /tmp/cf-linux-amd64.tgz -C /usr/local/bin; \
  ln -s /usr/local/bin/cf /usr/local/bin/gcf; \
  rm -rf /tmp/*
