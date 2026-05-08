<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>统计分析</title>
    <style>
        .header { background: #001529; color: white; padding: 10px 20px; display: flex; justify-content: space-between; }
        .header a { color: white; margin-right: 15px; text-decoration: none; }
        .header .right { margin-left: auto; }
    </style>
</head>
<body>
<%@ include file="../header.jsp"%>
<h2>简单统计</h2>
<p>总图书数量：${totalBooks}</p>
<p>当前借出数量：${borrowedCount}</p>
<p>分类统计：${catStats}</p>
<a href="${pageContext.request.contextPath}/borrow/exportBorrow">导出借阅记录 CSV</a>
</body>
</html>