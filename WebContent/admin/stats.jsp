<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../header.jsp"%>
<html>
<head><title>统计分析</title></head>
<body>
<h2>简单统计</h2>
<p>总图书数量：${totalBooks}</p>
<p>当前借出数量：${borrowedCount}</p>
<p>分类统计：${catStats}</p>
<a href="${pageContext.request.contextPath}/borrow/exportBorrow">导出借阅记录 CSV</a>
</body>
</html>