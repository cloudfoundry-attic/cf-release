require 'yaml'

describe "Stack Receipts" do

  context "cflinux" do
    it "has the right sha" do
     compare_sha('spec/fixtures/receipts/cflinuxfs2_receipt', 'rootfs/cflinuxfs2.tar.gz')
    end
  end

  def compare_sha(expected_filename, blob_key)
     expected_sha = File.read(expected_filename).lines.first[/[a-z0-9]{40}/]
     actual_sha = YAML.load_file('config/blobs.yml')[blob_key]['sha']
     expect(actual_sha).to(eq(expected_sha))
  end
end
