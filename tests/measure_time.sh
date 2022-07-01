#!/usr/bin/env bash

# Params
testStart=16
testEnd=18
program="factcheck"
version="1.0-SNAPSHOT"
cliModule="cli"
tarPath="../$cliModule/build/distributions/$program-$version.tar"
testDir="tests"
exe="$program-$version/bin/$program"
knowledgeBase="../ontos/fact.owl"
outputFile="runtimes.csv"

# Setup
cd ..
# build CLI
./gradlew "$cliModule:build" || exit 1
# start verbalizer
docker-compose  up -d owl-verbalizer || exit 1
cd "$testDir" || exit 1
# extract cli
tar xvf "$tarPath"





export FACE_KB="$knowledgeBase"
TIMEFORMAT=%R

runtimes=()

# Run tests
for i in $(seq $testStart $testEnd) ; do
    inFile="test-$i.in.txt"
    owlFile="test-$i.owl"

    echo "$inFile, $owlFile:"

    #export FACE_FRED_DUMMY="$owlFile"

    runtime=$( { time "$exe" check -i "$inFile" > "output-$i.txt" 2> "err-$i.txt"; } 2>&1 )
    echo "runtime: $runtime"
    runtimes+=( "$runtime" )
done

# Cleanup
git clean -dfX .

# Save runtimes to file system
#echo "test,seconds" > "$outputFile"
for i  in "${!runtimes[@]}" ; do
    idx=$(( "$i" + "$testStart" ))
    echo "$idx,${runtimes[${i}]}" >> "$outputFile"
done

echo "Running times saved to: $outputFile"



