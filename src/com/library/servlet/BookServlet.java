package com.library.servlet;

import com.library.bean.Book;
import com.library.dao.DBHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/book/*")
@MultipartConfig(maxFileSize = 1024*1024*5)
public class BookServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        String path = req.getPathInfo();
        if (path == null) path = "/list";
        switch (path) {
            case "/list": listBooks(req, resp); break;
            case "/search": searchBooks(req, resp); break;
            case "/delete": deleteBook(req, resp); break;
            case "/cover": getCover(req, resp); break;
            case "/export": exportCSV(req, resp); break;
            default: resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        String path = req.getPathInfo();
        if ("/add".equals(path)) addBook(req, resp);
        else if ("/update".equals(path)) updateBook(req, resp);
    }

    private void listBooks(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT book_id, isbn, title, author, press, category, total, remain, status FROM books ORDER BY book_id";
        try (Connection conn = DBHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Book b = new Book();
                b.setBookId(rs.getInt("book_id"));
                b.setIsbn(rs.getString("isbn"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setPress(rs.getString("press"));
                b.setCategory(rs.getString("category"));
                b.setTotal(rs.getInt("total"));
                b.setRemain(rs.getInt("remain"));
                b.setStatus(rs.getInt("status"));
                list.add(b);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        req.setAttribute("books", list);
        req.getRequestDispatcher("/admin/bookManage.jsp").forward(req, resp);
    }

    private void searchBooks(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String category = req.getParameter("category");
        String sort = req.getParameter("sort");
        StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE status=1 ");
        if (title != null && !title.trim().isEmpty())
            sql.append(" AND title LIKE '%").append(title).append("%' ");
        if (author != null && !author.trim().isEmpty())
            sql.append(" AND author LIKE '%").append(author).append("%' ");
        if (category != null && !category.trim().isEmpty())
            sql.append(" AND category='").append(category).append("' ");
        if (sort != null && !sort.trim().isEmpty())
            sql.append(" ORDER BY ").append(sort);
        else
            sql.append(" ORDER BY book_id");

        List<Book> list = new ArrayList<>();
        try (Connection conn = DBHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql.toString())) {
            while (rs.next()) {
                Book b = new Book();
                b.setBookId(rs.getInt("book_id"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setPress(rs.getString("press"));
                b.setCategory(rs.getString("category"));
                b.setRemain(rs.getInt("remain"));
                list.add(b);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        req.setAttribute("books", list);
        req.setAttribute("qTitle", title);
        req.setAttribute("qAuthor", author);
        req.setAttribute("qCategory", category);
        req.getRequestDispatcher("/reader/search.jsp").forward(req, resp);
    }

    private void addBook(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String isbn = req.getParameter("isbn");
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String press = req.getParameter("press");
        String category = req.getParameter("category");
        int total = Integer.parseInt(req.getParameter("total"));
        Part coverPart = req.getPart("cover");
        InputStream coverStream = null;
        if (coverPart != null && coverPart.getSize() > 0) {
            coverStream = coverPart.getInputStream();
        }
        String sql = "INSERT INTO books (isbn, title, author, press, category, cover, total, remain) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setString(4, press);
            ps.setString(5, category);
            if (coverStream != null) ps.setBlob(6, coverStream);
            else ps.setNull(6, Types.BLOB);
            ps.setInt(7, total);
            ps.setInt(8, total);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        resp.sendRedirect("list");   // 修复：改为相对路径 "list"
    }

    private void updateBook(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int bookId = Integer.parseInt(req.getParameter("bookId"));
        String title = req.getParameter("title");
        String author = req.getParameter("author");
        String press = req.getParameter("press");
        String category = req.getParameter("category");
        int total = Integer.parseInt(req.getParameter("total"));
        Part coverPart = req.getPart("cover");
        InputStream coverStream = null;
        if (coverPart != null && coverPart.getSize() > 0) {
            coverStream = coverPart.getInputStream();
        }
        String sql = "UPDATE books SET title=?, author=?, press=?, category=?, total=?, remain=remain+?";
        if (coverStream != null) sql += ", cover=?";
        sql += " WHERE book_id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, press);
            ps.setString(4, category);
            ps.setInt(5, total);
            int oldTotal = getBookTotal(bookId);
            ps.setInt(6, total - oldTotal);
            int paramIndex = 7;
            if (coverStream != null) {
                ps.setBlob(paramIndex++, coverStream);
            }
            ps.setInt(paramIndex, bookId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        resp.sendRedirect("list");   // 修复：改为相对路径 "list"
    }

    private int getBookTotal(int bookId) {
        String sql = "SELECT total FROM books WHERE book_id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private void deleteBook(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int bookId = Integer.parseInt(req.getParameter("id"));
        String sql = "DELETE FROM books WHERE book_id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
        resp.sendRedirect("list");   // 修复：改为相对路径 "list"
    }

    private void getCover(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String sql = "SELECT cover FROM books WHERE book_id=?";
        try (Connection conn = DBHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Blob blob = rs.getBlob("cover");
                if (blob != null) {
                    resp.setContentType("image/jpeg");
                    InputStream in = blob.getBinaryStream();
                    OutputStream out = resp.getOutputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    in.close();
                    out.close();
                    return;
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void exportCSV(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/csv;charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=books_export.csv");
        PrintWriter pw = resp.getWriter();
        pw.println("\uFEFF书名,作者,出版社,类别,总册数,可借册数");
        String sql = "SELECT title, author, press, category, total, remain FROM books";
        try (Connection conn = DBHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",%d,%d\n",
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("press"),
                        rs.getString("category"),
                        rs.getInt("total"),
                        rs.getInt("remain"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        pw.flush();
    }
}