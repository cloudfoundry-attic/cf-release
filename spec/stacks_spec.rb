describe "Stack Receipts" do

  context "cflinux" do
    it "has the right sha" do
     compare_sha('spec/fixtures/receipts/cflinuxfs2_receipt', 'blobs/rootfs/cflinuxfs2.tar.gz')
    end
  end

  def compare_sha(expected_filename, actual_filename)
     expected_sha = File.read(expected_filename).lines.first[/[a-z0-9]{40}/]
     actual_sha = `shasum #{actual_filename}`[0..39]
     expect(actual_sha).to(eq(expected_sha))
  end
end
