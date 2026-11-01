$ErrorActionPreference = 'Stop'
$nomes = @('SPRING_PROFILES_ACTIVE','DB_URL','DB_USERNAME','DB_PASSWORD','ADMIN_USERNAME','ADMIN_PASSWORD','COOKIE_SECURE','PORT','MAVEN_USER_HOME','JAVA_HOME')
$anteriores = @{}
foreach ($nome in $nomes) { $anteriores[$nome] = [Environment]::GetEnvironmentVariable($nome,'Process') }
$senhaBanco = $null
$senhaAdmin = $null
$resultado = 0
Push-Location $PSScriptRoot
try {
    $senhaBanco = Read-Host 'Senha do banco definida ao criar o projeto Supabase' -AsSecureString
    if ($senhaBanco.Length -eq 0) { throw 'A senha do banco nao pode estar vazia.' }
    $senhaAdmin = Read-Host 'Escolha a senha do ADMIN do site (minimo 12 caracteres; diferente da senha do banco)' -AsSecureString
    if ($senhaAdmin.Length -lt 12) { throw 'A senha do ADMIN precisa de pelo menos 12 caracteres.' }
    $env:SPRING_PROFILES_ACTIVE = 'postgres'
    $env:DB_URL = 'jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require'
    $env:DB_USERNAME = 'postgres.zznnuwevoeuxaznplxhy'
    $env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $senhaBanco).Password
    $env:ADMIN_USERNAME = 'admin'
    $env:ADMIN_PASSWORD = [System.Net.NetworkCredential]::new('', $senhaAdmin).Password
    $env:COOKIE_SECURE = 'false'
    $env:PORT = '8080'
    $env:MAVEN_USER_HOME = Join-Path $env:TEMP 'clyvovet-maven'
    $cacheDependencias = Join-Path $env:TEMP 'clyvovet-m2'
    if (-not $env:JAVA_HOME -and (Test-Path 'C:\Program Files\Java\jdk-21\bin\javac.exe')) {
        $env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
    }
    Write-Host 'Aguarde Started ClyvoVetWebApplication; depois abra http://localhost:8080. Ctrl+C encerra.'
    Write-Host 'No primeiro inicio o Flyway cria as tabelas e o login admin e cadastrado.'
    & "$PSScriptRoot/mvnw.cmd" "-Dmaven.repo.local=$cacheDependencias" spring-boot:run
    $resultado = $LASTEXITCODE
}
finally {
    foreach ($nome in $nomes) { [Environment]::SetEnvironmentVariable($nome,$anteriores[$nome],'Process') }
    if ($null -ne $senhaBanco) { $senhaBanco.Dispose() }
    if ($null -ne $senhaAdmin) { $senhaAdmin.Dispose() }
    Pop-Location
}
if ($resultado -ne 0) { throw 'A aplicacao encerrou com erro. Confira a mensagem acima; nao compartilhe senhas.' }
