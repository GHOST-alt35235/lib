# 图书管理系统增强功能 API文档

## 概述

本文档描述了图书管理系统新增的增强功能，包括图书管理、读者管理、借阅管理和统计模块的完整实现。

---

## 一、图书管理模块

### 1.1 图书列表查询
- **接口**: `GET /book/list`
- **权限**: 所有登录用户
- **参数**: 可选
  - `bookName`: 图书名称（模糊搜索）
  - `author`: 作者（模糊搜索）
  - `categoryId`: 分类ID
  - `status`: 状态
- **返回**: 图书列表

### 1.2 添加图书
- **接口**: `POST /book/add`
- **权限**: SUPER_ADMIN, LIBRARIAN, TEACHER
- **参数** (Body JSON):
  ```json
  {
    "isbn": "978-7-111-11111-1",
    "bookName": "Java编程思想",
    "author": "Bruce Eckel",
    "publisher": "机械工业出版社",
    "publishDate": "2007-06-01",
    "categoryId": 1,
    "totalCount": 10,
    "availableCount": 10,
    "price": 108.00,
    "location": "A区01",
    "status": 1
  }
  ```

### 1.3 批量导入图书
- **接口**: `POST /book/import`
- **权限**: SUPER_ADMIN, LIBRARIAN
- **参数** (Body JSON数组):
  ```json
  [
    {
      "isbn": "978-7-111-11111-1",
      "bookName": "图书1",
      "author": "作者1",
      "publisher": "出版社1",
      "publishDate": "2023-01-01",
      "categoryId": 1,
      "description": "描述",
      "totalCount": 5,
      "price": 99.00,
      "location": "A区02"
    }
  ]
  ```
- **返回**: 导入结果（成功/失败数量）

### 1.4 库存调整
- **接口**: `PUT /book/stock/adjust`
- **权限**: SUPER_ADMIN, LIBRARIAN
- **参数** (Body JSON):
  ```json
  {
    "bookId": 1,
    "adjustCount": 5,
    "remark": "补充库存"
  }
  ```
  - `adjustCount`: 正数表示入库，负数表示出库

### 1.5 库存变更记录
- **接口**: `GET /book/stock/logs`
- **权限**: SUPER_ADMIN, LIBRARIAN
- **参数**:
  - `bookId`: 图书ID（可选）

---

## 二、读者管理模块

### 2.1 用户列表
- **接口**: `GET /user/list`
- **权限**: SUPER_ADMIN

### 2.2 添加用户
- **接口**: `POST /user/add`
- **权限**: SUPER_ADMIN

### 2.3 禁用/启用用户
- **接口**: `PUT /user/status/{userId}`
- **权限**: SUPER_ADMIN, LIBRARIAN
- **参数**:
  - `userId`: 路径参数，用户ID
  - `status`: 查询参数，1-启用，0-禁用

### 2.4 重置密码
- **接口**: `PUT /user/password/{userId}/reset`
- **权限**: SUPER_ADMIN, LIBRARIAN
- **参数** (Body JSON):
  ```json
  {
    "newPassword": "123456"
  }
  ```

### 2.5 设置借阅限制
- **接口**: `PUT /user/borrow-limit/{userId}`
- **权限**: SUPER_ADMIN, LIBRARIAN
- **参数**:
  - `userId`: 路径参数，用户ID
  - `limit`: 查询参数，借阅数量限制

---

## 三、借阅管理模块

### 3.1 借书
- **接口**: `POST /distribute/borrow`
- **权限**: SUPER_ADMIN, LIBRARIAN
- **参数** (Body JSON):
  ```json
  {
    "bookId": 1,
    "userId": 5,
    "dueDate": "2023-12-31T23:59:59",
    "remark": "教学用书"
  }
  ```

### 3.2 还书
- **接口**: `PUT /distribute/return/{id}`
- **权限**: SUPER_ADMIN, LIBRARIAN
- **参数**:
  - `id`: 路径参数，借阅记录ID
  - `remark`: 查询参数，备注（可选）

### 3.3 续借
- **接口**: `PUT /distribute/renew`
- **权限**: SUPER_ADMIN, LIBRARIAN
- **参数** (Body JSON):
  ```json
  {
    "recordId": 1,
    "extendDays": 30
  }
  ```
  - `extendDays`: 续借天数，默认30天
  - **说明**: 最多续借2次

### 3.4 逾期记录查询
- **接口**: `GET /distribute/overdue`
- **权限**: SUPER_ADMIN, LIBRARIAN
- **返回**: 所有逾期记录，包含逾期天数和罚款金额

### 3.5 计算罚款
- **接口**: `GET /distribute/fine/{recordId}`
- **权限**: SUPER_ADMIN, LIBRARIAN
- **参数**:
  - `recordId`: 路径参数，借阅记录ID
- **返回**:
  ```json
  {
    "code": 200,
    "msg": "查询成功",
    "data": {
      "overdueDays": 5,
      "fine": 2.50
    }
  }
  ```
  - **说明**: 每天罚款标准：0.5元/天

---

## 四、统计模块

### 4.1 借阅率统计
- **接口**: `GET /statistics/borrow-rate`
- **权限**: 所有登录用户

### 4.2 分类统计
- **接口**: `GET /statistics/category-stats`
- **权限**: 所有登录用户

### 4.3 月度借阅统计
- **接口**: `GET /statistics/monthly-borrow`
- **权限**: 所有登录用户

### 4.4 状态分布
- **接口**: `GET /statistics/status-distribution`
- **权限**: 所有登录用户

### 4.5 热门图书排行
- **接口**: `GET /statistics/top-borrowed-books`
- **权限**: 所有登录用户

---

## 五、数据库变更

### 5.1 新增表
- `book_stock_log`: 图书库存变更记录表

### 5.2 修改表
- `user` 新增字段:
  - `borrow_limit`: 借阅数量限制
- `distribute_record` 新增字段:
  - `renew_count`: 续借次数
  - `fine`: 罚款金额

### 5.3 新增视图
- `v_book_stock_summary`: 图书库存状态汇总
- `v_borrow_statistics`: 借阅统计

---

## 六、使用说明

### 6.1 数据库部署步骤
1. 执行 `src/main/resources/sql/enhanced_features.sql` 更新数据库
2. 重启后端服务

### 6.2 测试账号
| 角色 | 用户名 | 密码 |
|--------|---------|
| 超级管理员 | admin | 123456 |
| 图书管理员 | lib1 | 123456 |
| 教师 | teacher1 | 123456 |
| 学生 | student1 | 123456 |

### 6.3 角色权限说明
| 角色 | 图书管理 | 分类管理 | 借阅管理 | 用户管理 |
|--------|----------|----------|----------|----------|
| SUPER_ADMIN | ✅ | ✅ | ✅ | ✅ |
| LIBRARIAN | ✅ | ✅ | ✅ | ❌ |
| TEACHER | ✅ | ❌ | ❌ | ❌ |
| STUDENT | ❌ | ❌ | 查看自己的 | ❌ |
