#!/bin/bash

# Stop the script if any command fails
set -e

# Default publishing task
PUBLISH_TASK="publishToMavenLocal"

# Check for the --remote flag
if [ "$1" == "--remote" ]; then
  PUBLISH_TASK="publishAllPublicationsToMavenCentralRepository"
  shift
fi

# Default build types if none are passed
default_build_types=("common" "lite" "korlibs" "kotlinx" "okio" "ktor2")

# If build types are passed, use them; otherwise, use the default list
if [ "$#" -ge 1 ]; then
  build_types=("$@")
else
  build_types=("${default_build_types[@]}")
fi

# Function to add projects based on the key
add_projects_based_on_key() {
  local key="$1"
  case "$key" in
    "common")
      projects=("ksoup-common")
      ;;
    "lite")
      projects=("ksoup")
      ;;
    "kotlinx")
      projects=("ksoup-kotlinx" "ksoup-network")
      ;;
    "korlibs")
      projects=("ksoup-korlibs" "ksoup-network-korlibs")
      ;;
    "ktor2")
      projects=("ksoup-network-ktor2")
      ;;
    "okio")
      projects=("ksoup-okio")
      ;;
    *)
      echo "Unknown key: $key"
      exit 1
      ;;
  esac
}

# Function to safely remove a directory if it exists
safe_remove_dir() {
    local dir="$1"
    if [ -d "$dir" ]; then
        rm -rf "$dir"
    fi
}

# Loop through all projects and publish them
for buildType in "${build_types[@]}"; do
  add_projects_based_on_key "$buildType"

  # clean build
  echo "clean build"
  safe_remove_dir "kotlin-js-store"
  ./gradlew clean --quiet --warning-mode=none

  for projectName in "${projects[@]}"; do
    echo "*****buildType: $buildType, project: $projectName"
    echo "Publishing $projectName with libBuildType=$buildType"
    ./gradlew ":$projectName:$PUBLISH_TASK" -PlibBuildType="$buildType" --quiet --warning-mode=none --no-configuration-cache
  done

done

echo "Publishing completed successfully."
