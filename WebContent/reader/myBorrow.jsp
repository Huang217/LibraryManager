<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.library.bean.Borrow" %>
<%@ include file="../header.jsp"%>
<html>
<head><title>我的借阅</title></head>
<body>
<h2>我的借阅记录</h2>
<button onclick="window.location.href='${pageContext.request.contextPath}/borrow/exportBorrow'">导出 CSV</button>
<table border="1" cellpadding="5">
    <tr><th>借阅ID</th><th>书名</th><th>借出时间</th><th>归还时间</th><th>状态</th><th>操作</th></tr>
    <%
        List<Borrow> borrows = (List<Borrow>) request.getAttribute("borrows");
        if (borrows != null) for (Borrow b : borrows) {
    %>
    <tr>
        <td><%= b.getBorrowId() %></td>
        <td><%= b.getBookTitle() %></td>
        <td><%= b.getBorrowTime() %></td>
        <td><%= b.getReturnTime() == null ? "" : b.getReturnTime() %></td>
        <td><%= b.getState() %></td>
        <td>
            <% if ("借出".equals(b.getState())) { %>
                <a href="${pageContext.request.contextPath}/borrow/return?borrowId=<%= b.getBorrowId() %>" onclick="return confirm('确认还书？')">还书</a>
            <% } %>
        </td>
    </tr>
    <% } %>
</table>
</body>
</html>