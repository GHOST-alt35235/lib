$loginResponse = Invoke-WebRequest -Uri 'http://localhost:8080/auth/login' -Method Post -ContentType 'application/json' -Body '{"username":"admin","password":"123456"}'
$cookie = $loginResponse.Headers['Set-Cookie']

Write-Host "=== 月度借阅数据 ==="
$monthlyData = Invoke-WebRequest -Uri 'http://localhost:8080/statistics/monthly-borrow' -Headers @{Cookie=$cookie}
Write-Host $monthlyData.Content

Write-Host "`n=== 分类统计数据 ==="
$categoryData = Invoke-WebRequest -Uri 'http://localhost:8080/statistics/category-stats' -Headers @{Cookie=$cookie}
Write-Host $categoryData.Content

Write-Host "`n=== 状态分布数据 ==="
$statusData = Invoke-WebRequest -Uri 'http://localhost:8080/statistics/status-distribution' -Headers @{Cookie=$cookie}
Write-Host $statusData.Content