#!/usr/bin/env bash
# StockWise - Automated Test Suite Runner

if [ -z "$JAVA_HOME" ] && [ -d "/Library/Java/JavaVirtualMachines/jdk-26.jdk/Contents/Home" ]; then
    export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-26.jdk/Contents/Home"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

if [ ! -d "bin/com/stockwise" ]; then
    echo "Compiled classes not found. Building project first..."
    ./compile.sh || exit 1
fi

java -cp bin com.stockwise.TestRunner
