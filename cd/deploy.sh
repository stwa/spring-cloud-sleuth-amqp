#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    mvn clean deploy coveralls:report -P sign,build-extras --settings cd/mvnsettings.xml -DrepoToken=$COVERALLS_REPO_TOKEN
fi