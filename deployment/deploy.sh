#!/bin/bash -ex
set -x
set -e

pushd app/
npm install
cdk bootstrap
cdk deploy --all --require-approval never