generate_ca() {
  common_name=$1
  depot_path=$2
  output_path=$3
  certstrap --depot-path "${depot_path}" init --passphrase '' --common-name "${common_name}"
  mv -f "${depot_path}/$common_name.crt" "${depot_path}/$output_path.crt"
  mv -f "${depot_path}/$common_name.key" "${depot_path}/$output_path.key"
}

generate_end_entity_certs() {
  common_name=$1
  domain=$2
  ca_name=$3
  depot_path=$4
  output_name=$5

  domain_argument=""

  if [ "${domain}" != "" ]; then
    domain_argument="--domain ${domain}"
  fi

  common_name_path=$(echo $common_name | tr ' ' _)

  certstrap --depot-path "${depot_path}" request-cert --passphrase '' --common-name "${common_name}" ${domain_argument}
  certstrap --depot-path "${depot_path}" sign "${common_name_path}" --CA "${ca_name}"
  mv -f "${depot_path}/${common_name_path}.key" "${depot_path}/${output_name}.key"
  mv -f "${depot_path}/${common_name_path}.csr" "${depot_path}/${output_name}.csr"
  mv -f "${depot_path}/${common_name_path}.crt" "${depot_path}/${output_name}.crt"
}
