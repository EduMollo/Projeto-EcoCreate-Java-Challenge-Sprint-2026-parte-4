$ErrorActionPreference='Stop'
$env:MAVEN_USER_HOME=Join-Path $env:TEMP 'clyvovet-maven'
$repoCache=Join-Path $env:TEMP 'clyvovet-m2'
& "$PSScriptRoot/mvnw.cmd" "-Dmaven.repo.local=$repoCache" clean verify
exit $LASTEXITCODE
