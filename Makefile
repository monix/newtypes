RUBY_VERSION := $(shell cat "./site/.ruby-version")

build-microsite:
	rbenv install -s "$(RUBY_VERSION)"
	ln -sf "./site/.ruby-version" "./.ruby-version"
	bundle install --gemfile=site/Gemfile
	sbt site/publishMicrosite
