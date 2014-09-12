require 'yaml'
require 'tempfile'

describe "Manifest Generation" do
  shared_examples "generating manifests" do |infrastructure|
    it "builds the correct manifest for #{infrastructure}" do
      example_manifest = Tempfile.new("example-manifest.yml")
      `./generate_deployment_manifest #{infrastructure} spec/fixtures/#{infrastructure}/cf-stub.yml > #{example_manifest.path}`
      expect($?.exitstatus).to eq(0)

      expected = YAML.load_file("spec/fixtures/#{infrastructure}/cf-manifest.yml")
      actual = YAML.load_file(example_manifest.path)

      expect(actual.keys).to eq(expected.keys)
      actual.each do |key, value|
        expect(value).to eq(expected[key])
      end
    end
  end

  context "aws" do
    it_behaves_like "generating manifests", "aws"
  end

  context "warden" do
    it_behaves_like "generating manifests", "warden"
  end

  context "openstack" do
    it_behaves_like "generating manifests", "openstack"
  end

  context "vsphere" do
    it_behaves_like "generating manifests", "vsphere"
  end
end
