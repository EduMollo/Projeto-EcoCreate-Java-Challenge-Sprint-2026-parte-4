param([switch]$SomentePreparar,[switch]$Verificar)
$ErrorActionPreference='Stop'
$jarPath=Join-Path $PSScriptRoot 'target\clyvovet-web-4.0.0.jar'
if(-not (Test-Path -LiteralPath $jarPath)) { throw 'Compile primeiro o projeto Java. O JAR validado e necessario para reutilizar a carga.' }
$jdkRoot=$env:JAVA_HOME
if(-not $jdkRoot -and (Test-Path -LiteralPath 'C:\Program Files\Java\jdk-21\bin\javac.exe')) {
    $jdkRoot='C:\Program Files\Java\jdk-21'
}
if(-not $jdkRoot -or -not (Test-Path -LiteralPath (Join-Path $jdkRoot 'bin\javac.exe'))) { throw 'Configure JAVA_HOME com um JDK 17 ou superior.' }
$cacheRoot=Join-Path $PSScriptRoot 'target\carga-deploy'
$libRoot=Join-Path $cacheRoot 'lib'
$classesRoot=Join-Path $cacheRoot 'classes'
$runnerRoot=Join-Path $cacheRoot 'runner'
foreach($folder in @($libRoot,$classesRoot,$runnerRoot)) { New-Item -ItemType Directory -Force -Path $folder | Out-Null }
Add-Type -AssemblyName System.IO.Compression.FileSystem
$archive=[IO.Compression.ZipFile]::OpenRead($jarPath)
try {
    foreach($entry in $archive.Entries) {
        $destination=$null
        if($entry.FullName.StartsWith('BOOT-INF/lib/') -and $entry.Name.EndsWith('.jar')) {
            $destination=Join-Path $libRoot $entry.Name
        } elseif($entry.FullName.StartsWith('BOOT-INF/classes/') -and $entry.Name) {
            $relative=$entry.FullName.Substring('BOOT-INF/classes/'.Length).Replace('/',[IO.Path]::DirectorySeparatorChar)
            $destination=[IO.Path]::GetFullPath((Join-Path $classesRoot $relative))
            if(-not $destination.StartsWith($classesRoot+[IO.Path]::DirectorySeparatorChar,[StringComparison]::OrdinalIgnoreCase)) {
                throw 'Caminho inesperado no JAR.'
            }
        }
        if($destination) {
            [IO.Directory]::CreateDirectory([IO.Path]::GetDirectoryName($destination)) | Out-Null
            [IO.Compression.ZipFileExtensions]::ExtractToFile($entry,$destination,$true)
        }
    }
} finally { $archive.Dispose() }
if(-not (Test-Path -LiteralPath (Join-Path $classesRoot 'br\com\fiap\clyvovet\demo\CargaDemonstracaoService.class'))) {
    throw 'O JAR nao contem a carga demonstrativa. Execute o build atualizado antes de continuar.'
}
$classpath="$runnerRoot;$classesRoot;$libRoot\*"
& (Join-Path $jdkRoot 'bin\javac.exe') -encoding UTF-8 -cp $classpath -d $runnerRoot (Join-Path $PSScriptRoot 'scripts\PopularDeploy.java')
if($LASTEXITCODE -ne 0) { throw 'Falha ao preparar o executor da carga. Nenhum banco foi alterado.' }
if($SomentePreparar) { Write-Host 'Executor compilado, usando a carga e as dependencias do JAR validado. Nenhuma conexao realizada.'; return }
$previousPassword=[Environment]::GetEnvironmentVariable('CLYVOVET_DEPLOY_DB_PASSWORD','Process')
$password=$null
try {
    if(-not $previousPassword) {
        $password=Read-Host 'Senha do BANCO Supabase usado pelo deploy (nao e a senha Oracle nem a senha do login do site)' -AsSecureString
        if($password.Length -eq 0) { throw 'A senha do banco nao pode estar vazia.' }
        $env:CLYVOVET_DEPLOY_DB_PASSWORD=[Net.NetworkCredential]::new('', $password).Password
    }
    $action=if($Verificar){'check'}else{'seed'}
    $resultPath=Join-Path $PSScriptRoot 'docs\DADOS-DEMO-DEPLOY-RESULTADO.json'
    & (Join-Path $jdkRoot 'bin\java.exe') -cp $classpath PopularDeploy $action $resultPath
    if($LASTEXITCODE -ne 0) { throw 'A carga nao foi concluida. Confira o erro acima; nao compartilhe senhas.' }
} finally {
    [Environment]::SetEnvironmentVariable('CLYVOVET_DEPLOY_DB_PASSWORD',$previousPassword,'Process')
    if($password) { $password.Dispose() }
}
