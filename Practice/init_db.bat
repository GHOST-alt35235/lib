@echo off
chcp 65001 >nul
echo 正在初始化数据库...
mysql -u root -p123456 --default-character-set=utf8mb4 < "d:\Jason\javaee期末作业\Practice\src\main\resources\sql\rich_data.sql"
if %errorlevel% equ 0 (
    echo 数据库初始化成功！
) else (
    echo 数据库初始化失败！
)
pause
