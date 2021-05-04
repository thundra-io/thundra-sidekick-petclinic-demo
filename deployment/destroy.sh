#!/bin/bash -ex
set -x
set -e

pushd app/
cdk destroy --all --force
