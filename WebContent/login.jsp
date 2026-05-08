<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>图书管理系统 - 登录</title>
    <style>
        body { font-family: Arial; background: #f0f2f5; display: flex; justify-content: center; align-items: center; height: 100vh; }
        .login-box { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); width: 350px; }
        h2 { text-align: center; margin-bottom: 20px; }
        input, select { width: 100%; padding: 10px; margin: 8px 0; border: 1px solid #ccc; border-radius: 4px; }
        button { width: 100%; padding: 12px; background: #1890ff; color: white; border: none; border-radius: 4px; cursor: pointer; }
        .msg { color: red; text-align: center; }
    </style>
</head>
<body>
<div class="login-box">
    <h2>图书管理系统</h2>
    <form action="${pageContext.request.contextPath}/login" method="post">
        <input type="text" name="username" placeholder="用户名" required>
        <input type="password" name="password" placeholder="密码" required>
        <select name="role">
            <option value="reader">读者</option>
            <option value="admin">管理员</option>
        </select>
        <button type="submit">登录</button>
        <div class="msg">${msg}</div>
    </form>
</div>
</body>
</html>