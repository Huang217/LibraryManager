<%@ page import="java.sql.*, com.library.dao.DBHelper" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../header.jsp"%>
<html>
<head><title>编辑图书</title></head>
<body>
<%
    int bookId = Integer.parseInt(request.getParameter("id"));
    String title = "", author = "", press = "", category = "", isbn = "";
    int total = 0;
    boolean hasCover = false;
    try (Connection conn = DBHelper.getConnection();
         PreparedStatement ps = conn.prepareStatement("SELECT * FROM books WHERE book_id=?")) {
        ps.setInt(1, bookId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            isbn = rs.getString("isbn");
            title = rs.getString("title");
            author = rs.getString("author");
            press = rs.getString("press");
            category = rs.getString("category");
            total = rs.getInt("total");
            hasCover = rs.getBlob("cover") != null;
        }
    } catch (SQLException e) { e.printStackTrace(); }
%>
<h2>编辑图书</h2>
<form action="${pageContext.request.contextPath}/book/update" method="post" enctype="multipart/form-data">
    <input type="hidden" name="bookId" value="<%= bookId %>">
    ISBN：<input type="text" name="isbn" value="<%= isbn %>" required><br><br>
    书名：<input type="text" name="title" value="<%= title %>" required><br><br>
    作者：<input type="text" name="author" value="<%= author %>" required><br><br>
    出版社：<input type="text" name="press" value="<%= press %>" required><br><br>
    类别：<input type="text" name="category" value="<%= category %>" required><br><br>
    总册数：<input type="number" name="total" value="<%= total %>" min="1" required><br><br>
    当前封面：<%= hasCover ? "<a href='" + request.getContextPath() + "/book/cover?id=" + bookId + "'>查看封面</a>" : "无" %>
    更换封面：<input type="file" name="cover" accept="image/*"> （留空则不修改）<br><br>
    <input type="submit" value="保存修改">
    <a href="bookManage.jsp">返回</a>
</form>
</body>
</html>