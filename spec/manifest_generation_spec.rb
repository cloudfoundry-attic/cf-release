require 'support/yaml_eq'
require 'yaml'
require 'tempfile'
require 'erb'

describe "Manifest Generation" do
  shared_examples "generating manifests" do |infrastructure|
    it "builds the correct manifest for #{infrastructure}" do
      example_manifest = Tempfile.new("example-manifest.yml")
      `./scripts/generate_deployment_manifest #{infrastructure} spec/fixtures/#{infrastructure}/cf-stub.yml > #{example_manifest.path}`
      expect($?.exitstatus).to eq(0)

      expected = File.read("spec/fixtures/#{infrastructure}/cf-manifest.yml")
      actual = File.read(example_manifest.path)

      expect(actual).to yaml_eq(expected)
    end
  end

  context "aws" do
    it_behaves_like "generating manifests", "aws"
  end

  context "bosh-lite" do
    it_behaves_like "generating manifests", "bosh-lite"
  end

  context "openstack" do
    it_behaves_like "generating manifests", "openstack"
  end

  context "vsphere" do
    it_behaves_like "generating manifests", "vsphere"
  end

  context "azure" do
    it_behaves_like "generating manifests", "azure"
  end
end
