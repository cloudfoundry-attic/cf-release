# cf-registrar for Identity Components

This directory exists because currently Identity Components (login, pivotal_login, saml_login, uaa)
do not currently have `src/` directories.

Ths directory replaces a submodule linked to the [vcap-common](https://github.com/cloudfoundry/vcap-common).

This submodule reference is being replaced so that the Identity team can manage their `cf-registrar`
dependencies via the `Gemfile` + `Gemfile.lock` process.
