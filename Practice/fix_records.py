#!/usr/bin/env python
# -*- coding: utf-8 -*-
import subprocess
import sys

print("正在修复借阅记录...")

# 获取所有有效的图书ID
get_books_sql = "SELECT id, book_name FROM book ORDER BY id"
proc = subprocess.Popen(
    ['mysql', '-u', 'root', '-p123456', '--default-character-set=utf8mb4', '-N', 'school'],
    stdin=subprocess.PIPE,
    stdout=subprocess.PIPE,
    stderr=subprocess.PIPE,
    text=True,
    encoding='utf-8'
)
books_output, _ = proc.communicate(input=get_books_sql)
book_ids = []
for line in books_output.strip().split('\n'):
    if line:
        parts = line.split('\t', 1)
        if parts:
            try:
                book_ids.append(int(parts[0]))
            except:
                pass

print(f"找到 {len(book_ids)} 本有效图书")

# 修复借阅记录
if book_ids:
    # 要修复的记录ID和新的book_id映射
    # 我们从有效ID列表中按顺序分配
    fix_sql = "USE school;\n"
    
    # 获取所有有问题的记录
    get_bad_sql = "SELECT id, book_id FROM distribute_record WHERE book_id NOT IN (SELECT id FROM book) ORDER BY id"
    proc = subprocess.Popen(
        ['mysql', '-u', 'root', '-p123456', '--default-character-set=utf8mb4', '-N', 'school'],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        encoding='utf-8'
    )
    bad_output, _ = proc.communicate(input=get_bad_sql)
    
    bad_records = []
    for line in bad_output.strip().split('\n'):
        if line:
            parts = line.split('\t')
            if len(parts) >= 2:
                try:
                    rec_id = int(parts[0])
                    bad_records.append(rec_id)
                except:
                    pass
    
    print(f"找到 {len(bad_records)} 条有问题的记录")
    
    # 分配有效ID
    for i, rec_id in enumerate(bad_records):
        new_book_id = book_ids[i % len(book_ids)]
        fix_sql += f"UPDATE distribute_record SET book_id = {new_book_id} WHERE id = {rec_id};\n"
    
    # 执行修复
    if bad_records:
        proc = subprocess.Popen(
            ['mysql', '-u', 'root', '-p123456', '--default-character-set=utf8mb4', 'school'],
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            encoding='utf-8'
        )
        _, stderr = proc.communicate(input=fix_sql)
        
        if proc.returncode == 0:
            print("修复成功！")
        else:
            print(f"修复失败: {stderr}")
    
    # 验证
    verify_sql = "SELECT dr.id, dr.book_id, b.book_name FROM distribute_record dr LEFT JOIN book b ON dr.book_id = b.id LIMIT 10"
    print("\n验证结果 (前10条):")
    proc = subprocess.Popen(
        ['mysql', '-u', 'root', '-p123456', '--default-character-set=utf8mb4', 'school'],
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        encoding='utf-8'
    )
    verify_output, _ = proc.communicate(input=verify_sql)
    print(verify_output)
