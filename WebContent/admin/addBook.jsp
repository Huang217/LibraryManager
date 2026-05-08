<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../header.jsp"%>
<html>
<head><title>添加新书</title></head>
<body>
<h2>添加新书</h2>
<form action="${pageContext.request.contextPath}/book/add" method="post" enctype="multipart/form-data">
    ISBN：<input type="text" name="isbn" required><br><br>
    书名：<input type="text" name="title" required><br><br>
    作者：<input type="text" name="author" required><br><br>
    出版社：<input type="text" name="press" required><br><br>
    类别：<input type="text" name="category" required><br><br>
    总册数：<input type="number" name="total" min="1" required><br><br>
    封面图片：<input type="file" name="cover" accept="image/*"><br><br>
    <input type="submit" value="添加">
    <a href="bookManage.jsp">返回</a>
</form>
</body>
</html>