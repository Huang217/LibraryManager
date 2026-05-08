<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.library.bean.Book" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>图书管理</title>
    <style>
        .header { background: #001529; color: white; padding: 10px 20px; display: flex; justify-content: space-between; }
        .header a { color: white; margin-right: 15px; text-decoration: none; }
        .header .right { margin-left: auto; }
    </style>
    <script>
        function printPage() { window.print(); }
        function exportCSV() { window.location.href='${pageContext.request.contextPath}/book/export'; }
    </script>
</head>
<body>
<%@ include file="../header.jsp"%>
<h2>图书列表</h2>
<button onclick="printPage()">网页打印</button>
<button onclick="exportCSV()">导出 CSV</button>
<table border="1" cellpadding="5" style="border-collapse:collapse; width:100%">
    <tr><th>ID</th><th>ISBN</th><th>书名</th><th>作者</th><th>出版社</th><th>类别</th><th>总册</th><th>可借</th><th>操作</th></tr>
    <%
        List<Book> books = (List<Book>) request.getAttribute("books");
        if (books != null) for (Book b : books) {
    %>
    <tr>
        <td><%= b.getBookId() %></td>
        <td><%= b.getIsbn() %></td>
        <td><%= b.getTitle() %></td>
        <td><%= b.getAuthor() %></td>
        <td><%= b.getPress() %></td>
        <td><%= b.getCategory() %></td>
        <td><%= b.getTotal() %></td>
        <td><%= b.getRemain() %></td>
        <td>
            <a href="${pageContext.request.contextPath}/book/cover?id=<%= b.getBookId() %>">封面</a>
            <a href="${pageContext.request.contextPath}/admin/editBook.jsp?id=<%= b.getBookId() %>">编辑</a>
            <a href="${pageContext.request.contextPath}/book/delete?id=<%= b.getBookId() %>" onclick="return confirm('确认删除？')">删除</a>
        </td>
    </tr>
    <% } %>
</table>
<a href="${pageContext.request.contextPath}/admin/addBook.jsp">添加新书</a>
</body>
</html>