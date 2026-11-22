#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
VERSION="8.14.3"
GRADLE="$ROOT/.tools/gradle-$VERSION/bin/gradle"
TASK="${1:-bootRun}"

if ! command -v java >/dev/null 2>&1; then
  echo 'ОШИБКА: Java не найдена. Установите JDK 21: https://adoptium.net/temurin/releases/?version=21'
  exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -n 1)
if [[ ! "$JAVA_VERSION" =~ \"21([.\"]|$) ]]; then
  echo "ОШИБКА: нужна Java 21, обнаружено: $JAVA_VERSION"
  exit 1
fi

if [ ! -f "$GRADLE" ] && [ -f "$ROOT/../Student_Exam_Results/.tools/gradle-$VERSION/bin/gradle" ]; then
  GRADLE="$ROOT/../Student_Exam_Results/.tools/gradle-$VERSION/bin/gradle"
fi

if [ ! -f "$GRADLE" ]; then
  echo 'Первый запуск: загружаю Gradle 8.14.3 (потребуется интернет)...'
  command -v curl >/dev/null || { echo 'Для загрузки нужен curl'; exit 1; }
  command -v unzip >/dev/null || { echo 'Для распаковки нужен unzip'; exit 1; }
  mkdir -p "$ROOT/.tools"
  curl -fL --retry 3 --connect-timeout 20 "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip" -o "$ROOT/.tools/gradle-$VERSION-bin.zip"
  unzip -q "$ROOT/.tools/gradle-$VERSION-bin.zip" -d "$ROOT/.tools"
  rm -f "$ROOT/.tools/gradle-$VERSION-bin.zip"
fi

mkdir -p "$ROOT/.data"
cd "$ROOT"
exec "$GRADLE" --no-daemon "$TASK"
