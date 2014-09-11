FROM ubuntu:trusty

RUN apt-get update && \
  apt-get -y install \
  libxml2-dev libxslt-dev libreadline-dev python-software-properties git \
  libmysqlclient-dev libpq-dev libsqlite3-dev \
  build-essential zlib1g-dev libssl-dev libyaml-dev \
  curl libcurl4-openssl-dev wget unzip mercurial bison

RUN git clone https://github.com/postmodern/chruby /tmp/chruby
RUN cd /tmp/chruby && git reset --hard 310bd34d1fcbb3365814c85018114123cef5a41f
RUN cd /tmp/chruby && ./scripts/setup.sh
RUN echo 'source /usr/local/share/chruby/auto.sh' >> $HOME/.bashrc

ADD https://github.com/postmodern/ruby-install/archive/v0.4.1.tar.gz /tmp/
RUN tar zxf /tmp/v0.4.1.tar.gz -C /tmp
RUN cd /tmp/ruby-install-0.4.1 && make install
RUN rm -rf /tmp/ruby-install-0.4.1

RUN ruby-install ruby 1.9.3-p547

RUN echo 'chruby 1.9.3-p547' >> $HOME/.bash_profile
RUN bash -l -c "gem install bundler --no-rdoc --no-ri"
RUN bash -l -c "gem install bosh_cli --no-rdoc --no-ri"

RUN curl -s -S -L https://raw.githubusercontent.com/moovweb/gvm/master/binscripts/gvm-installer -o /tmp/gvm-install.sh
RUN chmod 744 /tmp/gvm-install.sh
RUN bash -l -c "GVM_NO_UPDATE_PROFILE=false /tmp/gvm-install.sh"
RUN bash -l -c "source $HOME/.gvm/scripts/gvm && gvm install go1.3.1"

RUN git clone https://github.com/cloudfoundry-incubator/spiff.git /tmp/go/src/github.com/cloudfoundry-incubator/spiff
RUN bash -l -c "source $HOME/.gvm/scripts/gvm && gvm use go1.3.1 && GOPATH=/tmp/go /tmp/go/src/github.com/cloudfoundry-incubator/spiff/scripts/build"
RUN cp /tmp/go/src/github.com/cloudfoundry-incubator/spiff/spiff /usr/local/bin
RUN rm -rf /tmp/go

RUN wget https://dl.bintray.com/mitchellh/vagrant/vagrant_1.6.5_x86_64.deb
RUN dpkg --install vagrant_1.6.5_x86_64.deb && rm vagrant_1.6.5_x86_64.deb
RUN vagrant plugin install vagrant-aws
