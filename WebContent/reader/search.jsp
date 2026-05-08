<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, com.library.bean.Book" %>
<%@ include file="../header.jsp"%>
<html>
<head><title>图书查询</title>
    <script>
        function sortBy(col) {
            let params = new URLSearchParams(window.location.search);
            params.set('sort', col);
            window.location.search = params.toString();
        }
        function printPage() { window.print(); }
    </script>
</head>
<body>
<h2>图书检索</h2>
<form action="${pageContext.request.contextPath}/book/search" method="get">
    书名：<input type="text" name="title" value="${qTitle}">
    作者：<input type="text" name="author" value="${qAuthor}">
    类别：<input type="text" name="category" value="${qCategory}">
    <input type="submit" value="搜索">
    <input type="hidden" name="sort" id="sortField" value="">
</form>
<button onclick="printPage()">打印本页</button>
<table border="1" cellpadding="5" style="width:100%">
    <tr>
        <th onclick="sortBy('title')">书名</th>
        <th onclick="sortBy('author')">作者</th>
        <th>出版社</th>
        <th>类别</th>
        <th>可借</th>
        <th>借阅</th>
    </tr>
    <%
        List<Book> books = (List<Book>) request.getAttribute("books");
        if (books != null) for (Book b : books) {
    %>
    <tr>
        <td><%= b.getTitle() %></td>
        <td><%= b.getAuthor() %></td>
        <td><%= b.getPress() %></td>
        <td><%= b.getCategory() %></td>
        <td><%= b.getRemain() %></td>
        <td>
            <form action="${pageContext.request.contextPath}/borrow/borrow" method="post" onsubmit="return confirm('确定借阅？')">
                <input type="hidden" name="bookId" value="<%= b.getBookId() %>">
                <button type="submit">借阅</button>
            </form>
        </td>
    </tr>
    <% } %>
</table>
</body>
</html>