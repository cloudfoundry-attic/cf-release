#!/usr/bin/env ruby

puts "Please provide the tag currently deployed to production:"
production_tag = gets.chomp

puts "Please provide the sha for the Release Candidate commit you'd like to compare to:"
rc_sha = gets.chomp

puts "#{production_tag}..#{rc_sha}"

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

	def generate_html(f)
		f << "<H2>#{submodule_anchor}</H2>"
		f << "<H3>#{commit_anchor(@sha1[0..7])}..#{commit_anchor(@sha2[0..7])}</H3>"
		f << %Q{<div class="no-changes">No Changes</div>} if @commits.count == 0
		@commits.each do |commit|
			f << %Q{<div class="commit">}

			f << %Q{<div class="sha">#{commit_anchor(commit[:sha])}</div>}
			f << %Q{<div class="subject">#{commit[:subject]}</div>}
			f << %Q{<div class="body">#{commit[:body]}</div>}

			f << %Q{<div class="author">#{commit[:author]}</div>}
			f << %Q{<div class="date">#{commit[:date]}</div>}
			f << %Q{</div>}
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
		@submodules.each do |submodule, url|
			sub_sha1 = get_submodule_commit(@sha1, submodule)
			sub_sha2 = get_submodule_commit(@sha2, submodule)
			@submodule_logs << SubmoduleLog.new(submodule, url, sub_sha1, sub_sha2)
		end
	end

	def generate_html
		file = "#{ENV["HOME"]}/Dropbox/product/WhatsInTheDeploy.html"
		File.open(file, 'w') do |f|
			f << %Q{<html><head><link rel="stylesheet" type="text/css" href="WhatsInTheDeploy.css" /></head><body>}

			f << %Q{<h1>Changes in deploy from #{@sha1} to #{@sha2}</h1>}
			@submodule_logs.each do |submodule_log|
				submodule_log.generate_html(f)
			end
			f << "</body></html>"
		end
		file
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
		matches[1]
	end
end

whats_in_the_deploy = WhatsInTheDeploy.new(production_tag, rc_sha)
puts "Comparing submodules...."
whats_in_the_deploy.compare_submodules
puts "Generating HTML...."
file = whats_in_the_deploy.generate_html
puts "HTML Generated: #{file}"
`open #{file}`