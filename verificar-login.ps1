$ErrorActionPreference = 'Stop'
$arquivoJar = Join-Path $PSScriptRoot 'target/clyvovet-web-4.0.0.jar'
$fonte = Join-Path $PSScriptRoot 'scripts/VerificarLogin.java'
if (-not (Test-Path -LiteralPath $arquivoJar)) {
    throw 'JAR nao encontrado. Execute .\verificar.ps1 antes deste diagnostico.'
}
$java = if (Test-Path -LiteralPath 'C:\Program Files\Java\jdk-21\bin\java.exe') {
    'C:\Program Files\Java\jdk-21\bin\java.exe'
} elseif ($env:JAVA_HOME -and (Test-Path -LiteralPath (Join-Path $env:JAVA_HOME 'bin/java.exe'))) {
    Join-Path $env:JAVA_HOME 'bin/java.exe'
} else {
    (Get-Command java.exe -ErrorAction Stop).Source
}
Add-Type -AssemblyName System.IO.Compression.FileSystem
$pastaTemporaria = Join-Path ([IO.Path]::GetTempPath()) ('clyvovet-login-' + [Guid]::NewGuid().ToString('N'))
[IO.Directory]::CreateDirectory($pastaTemporaria) | Out-Null
$extraidos = @()
$zip = $null
$resultado = 0
try {
    $zip = [IO.Compression.ZipFile]::OpenRead($arquivoJar)
    foreach ($entrada in $zip.Entries) {
        if ($entrada.FullName -match '^BOOT-INF/lib/(postgresql-|spring-security-crypto-|spring-core-|commons-logging-|spring-jcl-)[^/]+\.jar$') {
            $destino = Join-Path $pastaTemporaria $entrada.Name
            [IO.Compression.ZipFileExtensions]::ExtractToFile($entrada, $destino)
            $extraidos += $destino
        }
    }
    if (-not ($extraidos | Where-Object { [IO.Path]::GetFileName($_) -like 'postgresql-*' })) {
        throw 'O JAR nao inclui o driver PostgreSQL. Execute .\verificar.ps1.'
    }
    & $java -cp (Join-Path $pastaTemporaria '*') $fonte
    $resultado = $LASTEXITCODE
}
finally {
    if ($null -ne $zip) { $zip.Dispose() }
    foreach ($arquivo in $extraidos) { Remove-Item -LiteralPath $arquivo -Force }
    Remove-Item -LiteralPath $pastaTemporaria -Force
}
if ($resultado -ne 0) { throw 'O diagnostico nao foi concluido. Execute em um terminal PowerShell interativo.' }
