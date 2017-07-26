#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    mvn clean deploy coveralls:report -DrepoToken=$COVERALLS_REPO_TOKEN -P sign,build-extras --settings cd/mvnsettings.xml
fi