# 图书分发管理系统

一个现代化的图书管理系统，包含用户管理、图书管理、借阅管理和统计分析功能。

## 技术栈

- **前端**: Vue 3 + Element Plus + Vite + ECharts
- **后端** (迁移中): Spring Boot → Supabase Edge Functions (TypeScript)
- **数据库**: MySQL → Supabase PostgreSQL
- **部署**: Cloudflare Pages + Supabase

## 项目结构

```
.
├── Practice/                      # 原 Spring Boot 项目
│   ├── frontend/                  # Vue 3 前端
│   ├── src/main/java/             # Java 后端代码
│   └── pom.xml
├── supabase/                      # Supabase 相关代码
│   ├── migrations/                # 数据库迁移文件
│   └── functions/                 # Edge Functions
└── README.md
```

## 快速开始

### 1. 本地运行原项目（可选）

如果你想先测试原项目：

```bash
# 前端
cd Practice/frontend
npm install
npm run dev

# 后端（需要 MySQL 数据库）
cd Practice
mvn spring-boot:run
```

### 2. 部署到 Supabase

#### 创建 Supabase 项目

1. 访问 [supabase.com](https://supabase.com) 并创建新项目
2. 获取项目 URL 和 Service Role Key
3. 在 `SQL Editor` 中运行 `supabase/migrations/20240101000000_initial_schema.sql` 创建数据库表

#### 创建密码验证 SQL 函数

在 Supabase SQL Editor 中运行：

```sql
-- 密码加密函数
CREATE OR REPLACE FUNCTION hash_password(password text)
RETURNS text AS $$
BEGIN
  RETURN crypt(password, gen_salt('bf'));
END;
$$ LANGUAGE plpgsql;

-- 密码验证函数
CREATE OR REPLACE FUNCTION verify_password(password text, hash text)
RETURNS boolean AS $$
BEGIN
  RETURN crypt(password, hash) = hash;
END;
$$ LANGUAGE plpgsql;
```

#### 部署 Edge Functions

```bash
# 安装 Supabase CLI
# 然后部署所有 Edge Functions
```

### 3. 部署前端到 Cloudflare Pages

1. 访问 [pages.cloudflare.com](https://pages.cloudflare.com)
2. 连接 GitHub 仓库 `GHOST-alt35235/lib`
3. 设置构建命令和输出目录：
   - 构建命令: `cd Practice/frontend && npm install && npm run build`
   - 输出目录: `Practice/frontend/dist`
4. 等待部署完成

## 下一步

- [ ] 完成 Supabase Edge Functions 开发
- [ ] 更新前端 API 配置以指向 Supabase
- [ ] 配置 Cloudflare Pages 环境变量
- [ ] 完整系统测试

## 开发状态

- ✅ 代码上传到 GitHub
- ✅ 前端项目构建成功
- 🔄 Supabase 数据库设计中
- ⏳ Edge Functions 开发中
- ⏳ 前端 API 适配待完成
- ⏳ Cloudflare Pages 部署待完成
