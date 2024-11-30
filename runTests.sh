#!/bin/bash

# Stop the script if any command fails
set -e

# Function to run tests for a specific configuration
run_tests() {
    local libBuildType="$1"
    shift
    local tasks=("$@")

    if [ ${#tasks[@]} -eq 0 ]; then
      echo "No specific tasks provided, running all default tests..."
     # tasks=("jvmTest" "testDebugUnitTest" "testReleaseUnitTest" "jsTest" "wasmJsTest" "iosX64Test" "iosSimulatorArm64Test" "macosX64Test" "macosArm64Test" "tvosX64Test" "tvosSimulatorArm64Test")
    tasks=("jvmTest" "jsTest" "wasmJsTest" "macosX64Test" "macosArm64Test")
   fi

     echo "Running tests with libBuildType=$libBuildType and tasks=${tasks[*]}..."

    # Remove build directories if they exist
    echo "clean build"
    ./gradlew :ksoup-test:clean -PlibBuildType="$libBuildType" --quiet --warning-mode=none

    for task in "${tasks[@]}"; do
      start_time=$(date +%s)
      echo "Running $task... $libBuildType"
      ./gradlew "$task" -PlibBuildType="$libBuildType" --quiet --warning-mode=none
      end_time=$(date +%s)
      duration=$((end_time - start_time))
      echo "Task $task completed in $duration seconds."
    done
}

# Supported parameters
SUPPORTED_PARAMS=("core" "korlibs" "okio" "kotlinx")

# Function to check if the provided parameter is supported
is_supported_param() {
    local param="$1"
    for supported_param in "${SUPPORTED_PARAMS[@]}"; do
        if [ "$supported_param" == "$param" ]; then
            return 0
        fi
    done
    return 1
}

# Main script logic
if [ "$#" -ge 1 ]; then
    libBuildType="$1"
    shift

    if is_supported_param "$libBuildType"; then
        run_tests "$libBuildType" "$@"
    else
        echo "Error: Unsupported parameter '$libBuildType'. Supported parameters are: ${SUPPORTED_PARAMS[*]}"
        exit 1
    fi
else
    for param in "${SUPPORTED_PARAMS[@]}"; do
        run_tests "$param"
    done
fi

echo "All tests ran successfully!"
