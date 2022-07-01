#!/usr/bin/env sh

testStart=13
testEnd=13
program="factcheck"
version="1.0-SNAPSHOT"
cliModule="cli"
tarPath="../$cliModule/build/distributions/$program-$version.tar"
testDir="tests"
exe="$program-$version/bin/$program"
knowledgeBase="../ontos/fact.owl"

# Setup
cd ..
# build CLI
./gradlew "$cliModule:build" || exit 1
# start verbalizer
docker-compose  up -d owl-verbalizer || exit 1
cd "$testDir" || exit 1
# extract cli
tar xvf "$tarPath"





# Run tests
for i in $(seq $testStart $testEnd) ; do
    inFile="test-$i.in.txt"
    outFile="test-$i.out.txt"
    owlFile="test-$i.owl"

    echo "$inFile -> $outFile, $owlFile"

    export FACE_KB="$knowledgeBase"
    $exe check -i "$inFile" > "$outFile"
    mv unprocessed.owl "$owlFile"
done


# Cleanup
git clean -dfX .
