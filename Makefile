RUBY_VERSION := $(shell cat "./site/.ruby-version")

build-microsite:
	rbenv install -s "$(RUBY_VERSION)"
	ln -sf "./site/.ruby-version" "./.ruby-version"
	bundle install --gemfile=site/Gemfile
	sbt site/publishMicrosite

dependency-updates:
	@mkdir -p ~/.sbt/1.0/plugins && \
	( test -f ~/.sbt/1.0/plugins/sbt-updates.sbt || echo 'addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.6.4")' > ~/.sbt/1.0/plugins/sbt-updates.sbt ) && \
	sbt dependencyUpdatesReport 1>/dev/null && \
	sbt ";reload plugins;dependencyUpdatesReport" 1>/dev/null && \
	find . -name "dependency-updates.txt" -type f
