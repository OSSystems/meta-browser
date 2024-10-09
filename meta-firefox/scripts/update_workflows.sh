#!/bin/bash

help(){
  echo "This script is used to update the GitHub workflow files for meta-firefox after version update."
  echo "Most probably you don't need it."
  echo "But if you think you do, use it like this: "
  echo "$0 old_version new_version"
  echo
  echo "old_ version = the version of workflow files that need to be updated"
  echo "new_version = the version to update the workflow files to"
  exit 0
}

if [ "$#" -ne 2 -o "$1" = "-h" -o "$1" = "--help" ]; then
  help
fi

WORKFLOWS_DIR="../../.github/workflows"

if [ ! -d ../../.github/workflows ]; then
  echo "$WORKFLOWS_DIR folder doesn't exist!"
  exit 1
fi

OLD_VERSION=$1
NEW_VERSION=$2

for workflow in `ls ${WORKFLOWS_DIR}/*$OLD_VERSION*`; do
  sed -i "s/$OLD_VERSION/$NEW_VERSION/g" $workflow
  mv $workflow ${workflow/"$OLD_VERSION"/"$NEW_VERSION"}
done
