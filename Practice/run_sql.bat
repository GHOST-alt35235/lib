@echo off
mysql -u root -p123456 --default-character-set=utf8mb4 school < src/main/resources/sql/init.sql
echo init.sql executed successfully
mysql -u root -p123456 --default-character-set=utf8mb4 school < src/main/resources/sql/rich_data.sql
echo rich_data.sql executed successfully
pause