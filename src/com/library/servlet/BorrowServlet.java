package com.library.servlet;

import com.library.bean.Borrow;
import com.library.bean.User;
import com.library.dao.DBHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/borrow/*")
public class BorrowServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        String path = req.getPathInfo();
        if ("/my".equals(path)) {
            myBorrows(req, resp);
        } else if ("/return".equals(path)) {
            returnBook(req, resp);
        } else if ("/stats".equals(path)) {
            stats(req, resp);
        } else if ("/exportBorrow".equals(path)) {
            exportBorrowCSV(req, resp);
        } else if ("/adminList".equals(path)) {   // 新增管理员借阅列表
            adminList(req, resp);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        String path = req.getPathInfo();
        if ("/borrow".equals(path)) {
            borrowBook(req, resp);
        }
    }

    private void borrowBook(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect("../login.jsp");
            return;
        }
        int bookId = Integer.parseInt(req.getParameter("bookId"));
        String result = DBHelper.callBorrowBook(user.getUserId(), bookId);
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.println("<script>alert('"+result+"'); window.location.href='"+ req.getContextPath() + "/book/search';</script>");
    }

    private void returnBook(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long borrowId = Long.parseLong(req.getParameter("borrowId"));
        String result = DBHelper.callReturnBook(borrowId);
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        // 修复：还书后根据角色跳转，管理员回到借阅管理，读者回到我的借阅
        User user = (User) req.getSession().getAttribute("user");
        String redirectUrl = req.getContextPath() + ("admin".equals(user.getRole()) ? "/borrow/adminList" : "/borrow/my");
        out.println("<script>alert('"+result+"'); window.location.href='"+ redirectUrl +"';</script>");
    }

    private void myBorrows(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect("../login.jsp");
            return;
        }
        List<Borrow> list = new ArrayList<>();
        String sql = "SELECT * FROM v_borrow_detail WHERE username=? ORDER BY borrow_time DESC";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Borrow b = new Borrow();
                b.setBorrowId(rs.getLong("borrow_id"));
                b.setBookTitle(rs.getString("title"));
                b.setIsbn(rs.getString("isbn"));
                b.setBorrowTime(rs.getTimestamp("borrow_time"));
                b.setReturnTime(rs.getTimestamp("return_time"));
                b.setState(rs.getString("state"));
                list.add(b);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        req.setAttribute("borrows", list);
        req.getRequestDispatcher("/reader/myBorrow.jsp").forward(req, resp);
    }

    private void adminList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        List<Borrow> list = new ArrayList<>();
        String sql = "SELECT * FROM v_borrow_detail ORDER BY borrow_time DESC";
        try (Connection conn = DBHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Borrow b = new Borrow();
                b.setBorrowId(rs.getLong("borrow_id"));
                b.setUsername(rs.getString("username"));
                b.setBookTitle(rs.getString("title"));
                b.setIsbn(rs.getString("isbn"));
                b.setBorrowTime(rs.getTimestamp("borrow_time"));
                b.setReturnTime(rs.getTimestamp("return_time"));
                b.setState(rs.getString("state"));
                list.add(b);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        req.setAttribute("borrows", list);
        req.getRequestDispatcher("/admin/borrowManage.jsp").forward(req, resp);
    }

    private void stats(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int totalBooks = 0, borrowedCount = 0;
        String catStats = "";
        try (Connection conn = DBHelper.getConnection();
             Statement st = conn.createStatement()) {
            ResultSet rs1 = st.executeQuery("SELECT COUNT(*) FROM books");
            if (rs1.next()) totalBooks = rs1.getInt(1);
            ResultSet rs2 = st.executeQuery("SELECT COUNT(*) FROM v_borrow_detail WHERE state='借出'");
            if (rs2.next()) borrowedCount = rs2.getInt(1);
            ResultSet rs3 = st.executeQuery("SELECT category, COUNT(*) cnt FROM books GROUP BY category");
            StringBuilder sb = new StringBuilder();
            while (rs3.next()) {
                sb.append(rs3.getString("category")).append(":").append(rs3.getInt("cnt")).append(" ");
            }
            catStats = sb.toString();
        } catch (SQLException e) { e.printStackTrace(); }
        req.setAttribute("totalBooks", totalBooks);
        req.setAttribute("borrowedCount", borrowedCount);
        req.setAttribute("catStats", catStats);
        req.getRequestDispatcher("/admin/stats.jsp").forward(req, resp);
    }

    private void exportBorrowCSV(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 权限检查：仅管理员可导出全站数据
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (!"admin".equals(user.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        resp.setContentType("text/csv;charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=borrows_export.csv");
        PrintWriter pw = resp.getWriter();
        pw.println("\uFEFF借阅ID,读者,书名,借出时间,归还时间,状态");
        String sql = "SELECT * FROM v_borrow_detail";
        try (Connection conn = DBHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                pw.printf("%d,%s,%s,%s,%s,%s\n",
                        rs.getLong("borrow_id"),
                        rs.getString("username"),
                        rs.getString("title"),
                        rs.getTimestamp("borrow_time"),
                        rs.getTimestamp("return_time"),
                        rs.getString("state"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        pw.flush();
    }
}