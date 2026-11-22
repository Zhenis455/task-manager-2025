#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"
echo 'Task Manager is starting on http://localhost:8081/tasks'
exec bash scripts/gradle-bootstrap.sh bootRun
