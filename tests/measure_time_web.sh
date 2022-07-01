#!/usr/bin/env bash

# Params
testStart=15
testEnd=18
endpoint="http://localhost:8080/check"
outputFile="runtimes.csv"


TIMEFORMAT=%R


# Run tests
#echo "test,seconds" > "$outputFile"
for i in $(seq $testStart $testEnd) ; do
    inFile="test-$i.in.txt"
    content=$(cat "$inFile")
    echo "$inFile: $content"

    url="$endpoint?text=$content"
    url="${url//[ ]/%20}"

    echo "url: $url"

    runtime=$(echo "%{time_total}" |  curl -w @- -o "output-$i.txt" -s "$url")
    echo "runtime: $runtime"
    echo "$i,$runtime" >> "$outputFile"
done

echo "Running times saved to: $outputFile"



