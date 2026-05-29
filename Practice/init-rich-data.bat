@echo off
echo Initializing database with rich data...
mysql -u root -p123456 < "d:\Jason\javaee期末作业\Practice\src\main\resources\sql\rich_data.sql"
if %errorlevel% equ 0 (
    echo Database initialized successfully!
) else (
    echo Failed to initialize database!
)
pause
