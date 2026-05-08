package com.library.dao;

import java.sql.*;

public class DBHelper {
    // 根据 MySQL 实际配置修改
    //private static final String URL = "jdbc:mysql://localhost:3306/TS_LIBRARY?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    //private static final String URL = "jdbc:mysql://localhost:3306/TS_LIBRARY?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private static final String URL = "jdbc:mysql://localhost:3306/TS_LIBRARY?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";      //MySQL 用户名
    private static final String PASSWORD = "123456"; //MySQL 密码

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 调用 nextval 函数
    public static long getNextval(String seqName) {
        String sql = "SELECT nextval(?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, seqName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // 调用借书存储过程
    public static String callBorrowBook(int userId, int bookId) {
        String sql = "{call sp_borrow_book(?,?,?)}";
        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, userId);
            cs.setInt(2, bookId);
            cs.registerOutParameter(3, Types.VARCHAR);
            cs.execute();
            return cs.getString(3);
        } catch (SQLException e) {
            e.printStackTrace();
            return "借阅异常: " + e.getMessage();
        }
    }

    // 调用还书存储过程
    public static String callReturnBook(long borrowId) {
        String sql = "{call sp_return_book(?,?)}";
        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {
            cs.setLong(1, borrowId);
            cs.registerOutParameter(2, Types.VARCHAR);
            cs.execute();
            return cs.getString(2);
        } catch (SQLException e) {
            e.printStackTrace();
            return "还书异常: " + e.getMessage();
        }
    }
}