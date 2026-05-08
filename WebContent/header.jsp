<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.library.bean.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<div class="header">
    <div>
        <% if ("admin".equals(user.getRole())) { %>
            <a href="${pageContext.request.contextPath}/book/list">图书管理</a>
            <a href="${pageContext.request.contextPath}/user/list">用户管理</a>
            <a href="${pageContext.request.contextPath}/borrow/adminList">借阅管理</a>
            <a href="${pageContext.request.contextPath}/borrow/stats">统计分析</a>
        <% } else { %>
            <a href="${pageContext.request.contextPath}/book/search">图书查询</a>
            <a href="${pageContext.request.contextPath}/borrow/my">我的借阅</a>
        <% } %>
    </div>
    <div class="right">
        欢迎，<%= user.getUsername() %> (<%= user.getRole() %>)
        <a href="${pageContext.request.contextPath}/login">退出</a>
    </div>
</div>