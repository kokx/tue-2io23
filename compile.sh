#!/bin/bash
path=".:protobuf-java-2.4.1.jar"
protoc --java_out . ChatProto.proto
javac -classpath "$path" *.java
