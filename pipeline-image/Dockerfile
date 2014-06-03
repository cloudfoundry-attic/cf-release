FROM ubuntu:trusty

RUN apt-get update

RUN apt-get -y install libxml2-dev libxslt-dev libreadline-dev python-software-properties git

RUN apt-get -y install libmysqlclient-dev libpq-dev libsqlite3-dev

RUN apt-get -y install build-essential zlib1g-dev libssl-dev libyaml-dev curl wget

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
