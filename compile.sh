#!/usr/bin/env bash
# StockWise - Compile Script

if [ -z "$JAVA_HOME" ] && [ -d "/Library/Java/JavaVirtualMachines/jdk-26.jdk/Contents/Home" ]; then
    export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-26.jdk/Contents/Home"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

mkdir -p bin
echo "Compiling StockWise Java 17 application and automated tests..."

javac --release 17 -d bin $(find src test -name "*.java")

if [ $? -eq 0 ]; then
    echo "Compilation successful! Bytecode generated in bin/"
else
    echo "[ERROR] Compilation failed."
    exit 1
fi
