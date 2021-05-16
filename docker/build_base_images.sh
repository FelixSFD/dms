#!/usr/bin/env bash

BASEDIR=$(dirname "$0")
echo "$BASEDIR"

docker build -t dms-base-runtime:dev $BASEDIR/dev-runtime/