FROM ubuntu:trusty

# docker defaults to using '/' as $HOME, we have to setup bash this way to get ruby and go in the path
# when doing 'bash -l -c'
RUN touch $HOME/.bashrc
RUN echo "[ -f ~/.bashrc ] && source ~/.bashrc" >> $HOME/.bash_profile

RUN apt-get update
RUN apt-get -y install \
  bison \
  build-essential \
  curl \
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
  unzip \
  wget \
  zlib1g-dev

RUN git clone https://github.com/postmodern/chruby /tmp/chruby
RUN cd /tmp/chruby && git reset --hard 310bd34d1fcbb3365814c85018114123cef5a41f
RUN cd /tmp/chruby && ./scripts/setup.sh
RUN echo 'source /usr/local/share/chruby/chruby.sh' >> $HOME/.bashrc

ADD https://github.com/postmodern/ruby-install/archive/v0.4.1.tar.gz /tmp/
RUN tar zxf /tmp/v0.4.1.tar.gz -C /tmp
RUN cd /tmp/ruby-install-0.4.1 && make install
RUN ruby-install ruby 1.9.3-p547
RUN rm -rf /usr/local/src/ruby-1.9.3-p547

RUN echo 'chruby 1.9.3-p547' >> $HOME/.bashrc

RUN bash -l -c "gem install bundler --no-rdoc --no-ri"
RUN bash -l -c "gem install bosh_cli --no-rdoc --no-ri"

ADD https://storage.googleapis.com/golang/go1.3.1.linux-amd64.tar.gz /tmp/
RUN tar xzvf /tmp/go1.3.1.linux-amd64.tar.gz -C /usr/local
ENV GOPATH $HOME/go
ENV PATH $HOME/go/bin:/usr/local/go/bin:$PATH

RUN git clone https://github.com/cloudfoundry-incubator/spiff.git /tmp/go/src/github.com/cloudfoundry-incubator/spiff
RUN bash -l -c "GOPATH=/tmp/go /tmp/go/src/github.com/cloudfoundry-incubator/spiff/scripts/build"
RUN cp /tmp/go/src/github.com/cloudfoundry-incubator/spiff/spiff /usr/local/bin

ADD https://dl.bintray.com/mitchellh/vagrant/vagrant_1.6.5_x86_64.deb /tmp/
RUN dpkg --install /tmp/vagrant_1.6.5_x86_64.deb
RUN vagrant plugin install vagrant-aws

ADD https://s3.amazonaws.com/go-cli/releases/v6.6.0/cf-linux-amd64.tgz /tmp/
RUN tar xvf /tmp/cf-linux-amd64.tgz -C /usr/local/bin
RUN ln -s /usr/local/bin/cf /usr/local/bin/gcf

RUN rm -rf /tmp/*
