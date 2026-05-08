package com.library.servlet;

import com.library.bean.User;
import com.library.dao.DBHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/user/*")
public class UserServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        String path = req.getPathInfo();
        if ("/list".equals(path)) {
            listUsers(req, resp);
        } else if ("/toggleStatus".equals(path)) {
            int userId = Integer.parseInt(req.getParameter("id"));
            int status = Integer.parseInt(req.getParameter("status"));
            toggleStatus(userId, status);
            resp.sendRedirect("list");   // 修复：改为 "list"
        } else if ("/delete".equals(path)) {
            int userId = Integer.parseInt(req.getParameter("id"));
            deleteUser(userId);
            resp.sendRedirect("list");   // 修复：改为 "list"
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");
    }

    private void listUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, role, email, status FROM users";
        try (Connection conn = DBHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                u.setRole(rs.getString("role"));
                u.setEmail(rs.getString("email"));
                u.setStatus(rs.getInt("status"));
                users.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        req.setAttribute("users", users);
        req.getRequestDispatcher("/admin/userManage.jsp").forward(req, resp);
    }

    private void toggleStatus(int userId, int status) {
        String sql = "UPDATE users SET status=? WHERE user_id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}