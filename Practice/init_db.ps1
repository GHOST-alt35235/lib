$ErrorActionPreference = "Stop"

$sqlFile = "d:\Jason\javaee期末作业\Practice\src\main\resources\sql\rich_data.sql"
$mysqlPath = "mysql"
$mysqlArgs = @("-u", "root", "-p123456", "--default-character-set=utf8mb4")

Write-Host "正在初始化数据库..."

# 使用 .NET Process 来运行 mysql 并重定向输入
$process = New-Object System.Diagnostics.Process
$process.StartInfo.FileName = $mysqlPath
$process.StartInfo.Arguments = $mysqlArgs
$process.StartInfo.UseShellExecute = $false
$process.StartInfo.RedirectStandardInput = $true
$process.StartInfo.RedirectStandardOutput = $true
$process.StartInfo.RedirectStandardError = $true
$process.StartInfo.WorkingDirectory = Split-Path -Parent $sqlFile

try {
    $process.Start() | Out-Null
    
    # 读取 SQL 文件内容并写入标准输入
    $sqlContent = Get-Content $sqlFile -Raw -Encoding UTF8
    $process.StandardInput.Write($sqlContent)
    $process.StandardInput.Close()
    
    # 等待进程结束
    $process.WaitForExit()
    
    # 获取输出
    $output = $process.StandardOutput.ReadToEnd()
    $errorOutput = $process.StandardError.ReadToEnd()
    
    if ($process.ExitCode -eq 0) {
        Write-Host "数据库初始化成功！"
        if ($output) { Write-Host $output }
    } else {
        Write-Host "数据库初始化失败！"
        if ($errorOutput) { Write-Host "错误信息: $errorOutput" }
        exit 1
    }
} finally {
    $process.Dispose()
}
