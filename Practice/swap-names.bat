@echo off
echo 互换 admin 和 student1 的名称...
mysql -u root -p123456 < "d:\Jason\javaee期末作业\Practice\swap-names.sql"
if %errorlevel% equ 0 (
    echo 名称互换成功！
) else (
    echo 名称互换失败！
)
pause
