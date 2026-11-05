param(
    [string]$BaseUrl = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"
$BaseUrl = $BaseUrl.TrimEnd("/")
$repoRoot = Split-Path -Parent $PSScriptRoot
$outputDirectory = Join-Path $repoRoot "docs\evidencias"
$temporaryFiles = @()
$password = "Tp3-evidencia-2026!"
$firstEmail = "tp3-" + [guid]::NewGuid().ToString("N") + "@example.com"
$secondEmail = "tp3-" + [guid]::NewGuid().ToString("N") + "@example.com"

New-Item -ItemType Directory -Force -Path $outputDirectory | Out-Null

function New-JsonFile {
    param([hashtable]$Value)

    $path = Join-Path $env:TEMP ("api-blank-" + [guid]::NewGuid().ToString("N") + ".json")
    $json = ConvertTo-Json -InputObject $Value -Compress
    [System.IO.File]::WriteAllText($path, $json, [System.Text.UTF8Encoding]::new($false))
    $script:temporaryFiles += $path
    return $path
}

function Invoke-Api {
    param(
        [string]$FileName,
        [string]$ExpectedStatus,
        [string[]]$Arguments
    )

    $responsePath = Join-Path $outputDirectory $FileName
    $status = & curl.exe -s -i -o $responsePath -w "%{http_code}" @Arguments
    if ($LASTEXITCODE -ne 0) {
        throw "curl.exe falló al solicitar $FileName (código $LASTEXITCODE)"
    }
    if ("$status" -ne $ExpectedStatus) {
        throw "Se esperaba HTTP $ExpectedStatus para $FileName y se recibió HTTP $status"
    }
    Write-Host "${FileName}: HTTP $status"
    return [System.IO.File]::ReadAllText($responsePath)
}

function Get-ResponseBody {
    param([string]$Response)

    $parts = [regex]::Split($Response, "\r?\n\r?\n", 2)
    if ($parts.Length -lt 2) {
        throw "La respuesta HTTP no contiene un body"
    }
    return $parts[1]
}

try {
    Invoke-Api "01-health.txt" "200" @("$BaseUrl/health") | Out-Null
    Invoke-Api "02-listas-sin-token-401.txt" "401" @("$BaseUrl/api/listas") | Out-Null

    $firstRegistrationFile = New-JsonFile @{ email = $firstEmail; password = $password }
    Invoke-Api "03-registro-201.txt" "201" @(
        "-X", "POST", "$BaseUrl/api/auth/registro",
        "-H", "Content-Type: application/json",
        "--data-binary", "@$firstRegistrationFile"
    ) | Out-Null

    $firstLoginFile = New-JsonFile @{ email = $firstEmail; password = $password }
    $firstLoginResponse = Invoke-Api "04-login-200.txt" "200" @(
        "-X", "POST", "$BaseUrl/api/auth/login",
        "-H", "Content-Type: application/json",
        "--data-binary", "@$firstLoginFile"
    )
    $firstToken = (Get-ResponseBody $firstLoginResponse | ConvertFrom-Json).token
    if ([string]::IsNullOrWhiteSpace($firstToken)) {
        throw "El login no devolvió un token"
    }
    $loginEvidencePath = Join-Path $outputDirectory "04-login-200.txt"
    $redactedLogin = $firstLoginResponse -replace '"token"\s*:\s*"[^"]+"', '"token":"<REDACTED>"'
    [System.IO.File]::WriteAllText($loginEvidencePath, $redactedLogin)

    $listName = "Lista-" + [guid]::NewGuid().ToString("N").Substring(0, 8)
    $listFile = New-JsonFile @{ nombre = $listName }
    $createdListResponse = Invoke-Api "05-crear-lista-201.txt" "201" @(
        "-X", "POST", "$BaseUrl/api/listas",
        "-H", "Content-Type: application/json",
        "-H", "Authorization: Bearer $firstToken",
        "--data-binary", "@$listFile"
    )
    $listId = (Get-ResponseBody $createdListResponse | ConvertFrom-Json).id

    Invoke-Api "06-listas-con-token-200.txt" "200" @(
        "-H", "Authorization: Bearer $firstToken",
        "$BaseUrl/api/listas"
    ) | Out-Null

    $secondRegistrationFile = New-JsonFile @{ email = $secondEmail; password = $password }
    Invoke-Api "registro-segundo-usuario.txt" "201" @(
        "-X", "POST", "$BaseUrl/api/auth/registro",
        "-H", "Content-Type: application/json",
        "--data-binary", "@$secondRegistrationFile"
    ) | Out-Null
    Remove-Item -LiteralPath (Join-Path $outputDirectory "registro-segundo-usuario.txt")
    $secondLoginFile = New-JsonFile @{ email = $secondEmail; password = $password }
    $secondLoginResponse = Invoke-Api "login-segundo-usuario.txt" "200" @(
        "-X", "POST", "$BaseUrl/api/auth/login",
        "-H", "Content-Type: application/json",
        "--data-binary", "@$secondLoginFile"
    )
    $secondToken = (Get-ResponseBody $secondLoginResponse | ConvertFrom-Json).token
    Remove-Item -LiteralPath (Join-Path $outputDirectory "login-segundo-usuario.txt")

    Invoke-Api "07-lista-ajena-403.txt" "403" @(
        "-H", "Authorization: Bearer $secondToken",
        "$BaseUrl/api/listas/$listId"
    ) | Out-Null

    $invalidLoginFile = New-JsonFile @{ email = $firstEmail; password = "incorrecta" }
    Invoke-Api "08-login-invalido-401.txt" "401" @(
        "-X", "POST", "$BaseUrl/api/auth/login",
        "-H", "Content-Type: application/json",
        "--data-binary", "@$invalidLoginFile"
    ) | Out-Null
}
finally {
    foreach ($path in $temporaryFiles) {
        Remove-Item -LiteralPath $path -ErrorAction SilentlyContinue
    }
}
