管理员：admin 123456
读者：reader1 123456

# 图书管理系统（LibraryManager）

> **Java Servlet + JSP + JavaBean + MySQL**  
> 模拟 Oracle 特性（序列、存储过程、触发器、视图、大对象）  
> 符合《软件系统开发综合实践》课程要求

---

## 一、项目简介

本项目是一个完整的图书管理系统，支持**管理员**和**普通读者**两种角色，提供图书管理、借阅/归还、用户管理、组合条件模糊查询、排序、统计分析、CSV 导出、网页打印等功能。数据库使用 MySQL，但在设计中模拟了 Oracle 核心特性（表空间、序列、存储过程/函数、触发器、视图、大对象、索引等）。

---

## 二、技术栈

| 层面 | 技术 |
|------|------|
| 后端 | Java Servlet 3.0+, JSP, JavaBean |
| 数据库 | MySQL 8.0（模拟 Oracle 特性） |
| 服务器 | Apache Tomcat 9.x |
| 开发工具 | IntelliJ IDEA Community + Smart Tomcat 插件 |
| JDBC 驱动 | mysql-connector-java-8.0.xx.jar |

---

## 三、环境要求

- **JDK** 1.8 或以上
- **MySQL** 5.7+ / 8.0（已启动服务）
- **Apache Tomcat** 9.0.xx（解压可用）
- **IDEA** 社区版 + **Smart Tomcat** 插件
- 浏览器（推荐 Chrome / Edge）

---

## 四、快速开始

### 1. 数据库初始化

1. 启动 MySQL 服务，使用 root 或具有建库权限的账户连接：
   ```bash
   mysql -u root -p --default-character-set=utf8mb4
执行项目根目录下的 database.sql：

sql
source /你的路径/database.sql;
验证：

sql
USE TS_LIBRARY;
SHOW TABLES;
应显示 books, borrow_log, borrows, seq_table, users, v_borrow_detail。

2. 修改数据库连接配置
   打开 src/com/library/dao/DBHelper.java，修改以下值为你的实际环境：

java
private static final String USER = "root";       // MySQL 用户名
private static final String PASSWORD = "123456"; // MySQL 密码
3. 配置 IDEA 并运行
   用 IDEA 打开本项目文件夹（LibraryManager）。

安装 Smart Tomcat 插件（File → Settings → Plugins → 搜索 Smart Tomcat → 安装）。

添加 Tomcat 服务器依赖：

File → Project Structure → Modules → Dependencies → 点击 + → JARs or Directories

选择 tomcat安装目录/lib/servlet-api.jar 和 jsp-api.jar，作用域设为 Provided。

添加 MySQL 驱动：

将 mysql-connector-java-8.0.xx.jar 放在 WebContent/WEB-INF/lib/ 下（手动创建 lib 文件夹）。

配置 Smart Tomcat：

点击 Add Configuration → + → Smart Tomcat

Tomcat Server：选择本地 Tomcat 目录

Deployment Directory：选择项目中的 WebContent

Context Path：/LibraryManager

VM options：-Dfile.encoding=UTF-8

运行配置，控制台显示 Tomcat started 后访问：

text
http://localhost:8080/LibraryManager/login.jsp
4. 测试账号
   角色	用户名	密码
   管理员	admin	123456
   读者	reader1	123456
   五、功能概览
   管理员功能
   图书管理：增删改查图书，上传/查看封面（大对象存储）

用户管理：查看用户列表，启用/禁用账户，删除用户

借阅管理：查看所有用户的借阅记录，支持管理员代学生还书

统计分析：图书总量、当前借出数量、按类别统计

数据导出：导出全部图书清单 CSV、全部借阅记录 CSV

网页打印：图书列表页支持浏览器打印

读者功能
图书查询：按书名、作者、类别进行组合条件模糊查询，结果按列排序

图书借阅：借阅库存 >0 的图书（调用存储过程处理事务）

我的借阅：查看个人借阅记录，在线归还图书

数据导出：导出个人借阅记录为 CSV

网页打印：图书查询结果页打印

六、模拟 Oracle 特性说明
Oracle 特性	实现方式
表空间	使用独立数据库 TS_LIBRARY 模拟
用户/模式	注释中提供 lib_admin 用户授权语句
序列	通过 seq_table 表 + nextval 函数生成自增值
存储过程/函数	sp_borrow_book、sp_return_book、nextval
触发器	trg_borrow_after_insert 自动记录借阅日志
视图	v_borrow_detail 简化借阅信息查询
大对象	books.cover 使用 LONGBLOB 存储图书封面
索引	在 category、title、borrows.user_id 上建立索引
主键/外键	所有表均定义主键，借阅表定义外键约束
七、项目结构
text
LibraryManager/
├── src/com/library/
│   ├── bean/              # JavaBean 实体
│   │   ├── User.java
│   │   ├── Book.java
│   │   └── Borrow.java
│   ├── dao/               # 数据库操作工具
│   │   └── DBHelper.java
│   └── servlet/           # Servlet 控制器
│       ├── LoginServlet.java
│       ├── BookServlet.java
│       ├── UserServlet.java
│       └── BorrowServlet.java
├── WebContent/
│   ├── admin/             # 管理员页面
│   │   ├── bookManage.jsp
│   │   ├── addBook.jsp
│   │   ├── editBook.jsp
│   │   ├── userManage.jsp
│   │   ├── borrowManage.jsp
│   │   └── stats.jsp
│   ├── reader/            # 读者页面
│   │   ├── search.jsp
│   │   └── myBorrow.jsp
│   ├── login.jsp
│   ├── header.jsp
│   ├── error.jsp
│   └── WEB-INF/
│       ├── web.xml
│       └── lib/            # 需自行放入 mysql-connector
└── database.sql            # 数据库完整脚本
八、常见问题
问题	解决方法
页面中文乱码	① 检查 server.xml 的 Connector 是否含有 URIEncoding="UTF-8" ② 检查 Servlet 是否设置了 req.setCharacterEncoding("UTF-8") ③ JSP 页面是否有 <%@ page contentType="text/html;charset=UTF-8" %>
添加/编辑/删除后出现 404	检查 BookServlet 中重定向路径应为 resp.sendRedirect("list")，不要写成 "book/list"
借阅操作后跳转 404	修改 BorrowServlet 中 JavaScript 跳转路径为绝对路径或正确的相对路径
无法连接数据库	检查 MySQL 服务是否启动，DBHelper 中的用户名密码是否正确，mysql-connector 是否已加入 WEB-INF/lib
封面显示 404	该书未上传封面时返回 404，可自行添加默认图片替换逻辑
Smart Tomcat 无法启动	确认 Tomcat 目录无中文空格，端口 8080 未被占用
九、课程文档对照
本系统严格对照《软件系统开发综合实践》教学要求实现：

✅ 两种角色（管理员、普通用户）独立界面与权限

✅ 管理员可维护数据、为普通用户授权

✅ 组合条件模糊查询 + 按列排序 + 网页打印

✅ 统计分析 + CSV 导入/导出

✅ 使用 Java Servlet + JSP + JavaBean 技术栈，数据库 Oracle（模拟）

✅ 提交文档：需求分析、ER 图、关系表、详细设计说明、数据库对象截图、模块功能说明与截图、心得体会