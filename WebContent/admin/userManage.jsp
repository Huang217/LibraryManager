<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.library.bean.User" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户管理</title>
    <style>
        .header { background: #001529; color: white; padding: 10px 20px; display: flex; justify-content: space-between; }
        .header a { color: white; margin-right: 15px; text-decoration: none; }
        .header .right { margin-left: auto; }
    </style>
</head>
<body>
<%@ include file="../header.jsp"%>
<h2>用户列表</h2>
<table border="1" cellpadding="5">
    <tr><th>ID</th><th>用户名</th><th>角色</th><th>邮箱</th><th>状态</th><th>操作</th></tr>
    <%
        List<User> users = (List<User>) request.getAttribute("users");
        if (users != null) for (User u : users) {
    %>
    <tr>
        <td><%= u.getUserId() %></td>
        <td><%= u.getUsername() %></td>
        <td><%= u.getRole() %></td>
        <td><%= u.getEmail() %></td>
        <td><%= u.getStatus() == 1 ? "正常" : "禁用" %></td>
        <td>
            <% if (u.getStatus() == 1) { %>
                <a href="${pageContext.request.contextPath}/user/toggleStatus?id=<%= u.getUserId() %>&status=0">禁用</a>
            <% } else { %>
                <a href="${pageContext.request.contextPath}/user/toggleStatus?id=<%= u.getUserId() %>&status=1">启用</a>
            <% } %>
            <a href="${pageContext.request.contextPath}/user/delete?id=<%= u.getUserId() %>" onclick="return confirm('确认删除？')">删除</a>
        </td>
    </tr>
    <% } %>
</table>
</body>
</html>