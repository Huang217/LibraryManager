package com.library.servlet;

import com.library.bean.User;
import com.library.dao.DBHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String role = req.getParameter("role");
        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt("status") == 0) {
                    req.setAttribute("msg", "账户已被禁用");
                    req.getRequestDispatcher("login.jsp").forward(req, resp);
                    return;
                }
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getInt("status"));
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                if ("admin".equals(user.getRole())) {
                    //resp.sendRedirect("admin/bookManage.jsp");
                    resp.sendRedirect("book/list");
                } else {
                    //resp.sendRedirect("reader/search.jsp");
                    resp.sendRedirect("book/search");
                }
            } else {
                req.setAttribute("msg", "用户名或密码错误");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("msg", "系统错误");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().invalidate();
        resp.sendRedirect("login.jsp");
    }
}