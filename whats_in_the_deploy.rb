#!/usr/bin/env ruby

class SubmoduleLog
  def initialize(submodule, url, sha1, sha2)
    @submodule = submodule
    @url = url
    @sha1 = sha1
    @sha2 = sha2
    @commits = get_commits    
  end

  def get_commits
    short_log = Dir.chdir(@submodule) do
      `git log #{@sha1}..#{@sha2} --pretty="%H|||%an|||%ad|||%s|||%bSPLITHERE"`
    end
    commit_chunks = short_log.split("SPLITHERE")
    commits = []
    commit_chunks.each do |chunk|
      segments = chunk.split("|||")
      next if segments.size < 3
      commits << {
        sha: segments[0],
        author: segments[1],
        date: segments[2],
        subject: segments[3],
        body: segments[4] ? segments[4].gsub("\n", "<br>") : ""
      }
    end
    commits
  end

  def submodule_anchor
    %Q{<a href="#{@url}/commits/#{@sha2}" target="_blank">#{@submodule}</a>}
  end

  def commit_anchor(sha)
    %Q{<a href="#{@url}/commit/#{sha}" target="_blank">#{sha}</a>}
  end

  def comparison_anchor
    %Q{<a href="#{@url}/compare/#{@sha1}...#{@sha2}" target="_blank">compare</a>}
  end

  def generate_html(f)
    f << "<details>"
    f << "<summary><h2>#{submodule_anchor} (#{@commits.count} changes)</h2></summary>"
    f << "<H3>#{commit_anchor(@sha1[0..7])}..#{commit_anchor(@sha2[0..7])} (#{comparison_anchor})</H3>"
    f << %Q{<div class="no-changes">No Changes</div>} if @commits.count == 0
    @commits.each do |commit|
      f << %Q{<details class="commit">}
      f << %Q{<summary class="subject">#{commit[:subject]}</summary>}
      
      f << %Q{<div class="sha">#{commit_anchor(commit[:sha])}</div>}
      f << %Q{<div class="body">#{linkify(commit[:body])}</div>}
      f << %Q{<div class="author">#{commit[:author]}</div>}
      f << %Q{<div class="date">#{commit[:date]}</div>}
      f << %Q{</details>}
    end
    f << "</details>"    
  end
  
  private
  
  AUTO_LINK_RE = %r{
      (?: ([\w+.:-]+:)// | www\. )
      [^\s<]+
    }x
    
    BRACKETS = { ']' => '[', ')' => '(', '}' => '{' }
    
  
  def linkify(text)    
    text.gsub(AUTO_LINK_RE) do
      scheme, href = $1, $&
      punctuation = []

        # don't include trailing punctuation character as part of the URL
        while href.sub!(/[^\w\/-]$/, '')
          punctuation.push $&
          if opening = BRACKETS[punctuation.last] and href.scan(opening).size > href.scan(punctuation.last).size
            href << punctuation.pop
            break
          end
        end

        href = 'http://' + href unless scheme

        %Q{<a href="#{href}">#{href}</a>}
    end
  end
end

class WhatsInTheDeploy
  def initialize(sha1, sha2)
    @submodules = find_submodules
    @submodule_logs = []
    @sha1 = sha1
    @sha2 = sha2
  end

  def compare_submodules
    @submodule_logs << SubmoduleLog.new(".", "http://github.com/cloudfoundry/cf-release", @sha1, @sha2)
    
    @submodules.each do |submodule, url|
      sub_sha1 = get_submodule_commit(@sha1, submodule)
      sub_sha2 = get_submodule_commit(@sha2, submodule)
      if sub_sha1 && sub_sha2
        @submodule_logs << SubmoduleLog.new(submodule, url, sub_sha1, sub_sha2)
      else
        puts "Skipping #{submodule} (couldn't find one of the SHAs.  This is probably a new submodule in the release.)"
      end
    end
  end

  def generate_html(path)
    File.open(path, 'w') do |f|
      f << %Q{<html><head><style>#{DATA.read}</style></head><body>}

      f << %Q{<h1>Changes in deploy from #{@sha1} to #{@sha2}</h1>}
      @submodule_logs.each do |submodule_log|
        submodule_log.generate_html(f)
      end
      f << "</body></html>"
    end
  end

  private

  def find_submodules
    submodules = {}
    gitmodules = File.read('.gitmodules')
    gitmodules.scan(/path = (.+)\n\s+url = (.+)\n/) do |match|
      path = match[0]
      url = match[1].chomp(".git")
      if Dir.glob("#{path}/*").length > 0
        submodules[path] = url
      end
    end
    submodules
  end

  def get_submodule_commit(tree_identifier, submodule)
    ls_tree_output = `git ls-tree #{tree_identifier} #{submodule}`
    matches = /commit (.+)\s+#{submodule}/.match(ls_tree_output)
    matches[1] if matches
  end
end

if __FILE__ == $0
  if ARGV
    production_tag, rc_sha = ARGV
  else
    puts "Please provide the tag currently deployed to production:"
    production_tag = gets.chomp

    puts "Please provide the sha for the Release Candidate commit you'd like to compare to:"
    rc_sha = gets.chomp
  end
  
  puts "#{production_tag}..#{rc_sha}"

  whats_in_the_deploy = WhatsInTheDeploy.new(production_tag, rc_sha)
  puts "Comparing submodules...."
  whats_in_the_deploy.compare_submodules
  puts "Generating HTML...."

  output_path = ENV.fetch("WITD_OUT", "#{ENV["HOME"]}/Dropbox/product/WhatsInTheDeploy.html")
  whats_in_the_deploy.generate_html(output_path)
  puts "HTML Generated: #{output_path}"
  `open #{output_path}`
end

__END__
body {
	font-family: "helvetica neue";
	font-size:14px;
}

h2 {
  display:inline;
}

a {
	text-decoration: none;
}

a:hover {
	text-decoration: underline;
}

.no-changes {
	margin:10px;
	padding:10px;
	background-color: #efe;
}

.commit {
	margin:10px;
	padding:10px;
	background-color: #f5f5f5;
}

.commit:nth-child(odd) {
	background-color: #ddd;
}

.sha {
	margin-bottom:5px;
	font-size:12px;
}

.sha a {
	color:#777;
}

.subject {
	font-weight:bold;
}

.body {
	padding:10px;
	font-size:;
}

.author {
	font-style:italic;
}

.date {
	color:#777;
	font-size:12px;
}
