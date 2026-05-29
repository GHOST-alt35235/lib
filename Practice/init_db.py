#!/usr/bin/env python
# -*- coding: utf-8 -*-
import subprocess
import sys

sql_file = r"d:\Jason\javaee期末作业\Practice\src\main\resources\sql\rich_data.sql"

print("正在初始化数据库...")

try:
    # 用 subprocess 运行 mysql 并通过管道传递 SQL
    with open(sql_file, 'r', encoding='utf-8') as f:
        sql_content = f.read()
    
    # 运行 mysql 命令
    proc = subprocess.Popen(
        ['mysql', '-u', 'root', '-p123456', '--default-character-set=utf8mb4'],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        encoding='utf-8'
    )
    
    stdout, stderr = proc.communicate(input=sql_content)
    
    if proc.returncode == 0:
        print("数据库初始化成功！")
        if stdout:
            print(stdout)
    else:
        print("数据库初始化失败！")
        if stderr:
            print("错误信息:", stderr)
        sys.exit(1)
        
except Exception as e:
    print(f"发生错误: {e}")
    import traceback
    traceback.print_exc()
    sys.exit(1)
