#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
exec bash scripts/gradle-bootstrap.sh test
