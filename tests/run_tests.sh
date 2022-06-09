#!/usr/bin/env sh

testStart=1
testEnd=18
program="factcheck"
version="1.0-SNAPSHOT"
tarPath="../fact-checker-cli/build/distributions/$program-$version.tar"
testDir="tests"
exe="$program-$version/bin/$program"
knowledgeBase="../ontos/fact.owl"

# Setup
cd ..
# build CLI
./gradlew fact-checker-cli:build
# start verbalizer
docker-compose  up -d owl-verbalizer
cd "$testDir" || exit 1
# extract cli
tar xvf "$tarPath"





# Run tests
for i in $(seq $testStart $testEnd) ; do
    inFile="test-$i.in.txt"
    outFile="test-$i.out.txt"
    owlFile="test-$i.owl"

    echo "$inFile, $owlFile -> $outFile"

    export DUMMY="$owlFile"
    export KNOWLEDGE_BASE="$knowledgeBase"
    $exe check -i "$inFile" > "$outFile"
done


# Cleanup
git clean -dfX .
