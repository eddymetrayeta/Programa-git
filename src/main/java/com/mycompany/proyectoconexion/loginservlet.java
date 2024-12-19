package com.mycompany.proyectoconexion;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "loginservlet", urlPatterns = {"/loginservlet"})
public class loginservlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Datos de conexión a la base de datos
        String usuarioDB = "root";
        String passwordDB = "";
        String url = "jdbc:mysql://localhost:3306/productos";

        // Obtener los datos del formulario
        String user = request.getParameter("user");
        String password = request.getParameter("password");

        // Objetos de conexión y consulta
        Connection conexion = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // Carga el driver JDBC para MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establece una conexión a la base de datos
            conexion = DriverManager.getConnection(url, usuarioDB, passwordDB);

            // Prepara una consulta SQL para buscar un usuario por nombre y contraseña
            String query = "SELECT * FROM clientes WHERE Usuario=? AND Contraseña=?";
            ps = conexion.prepareStatement(query);

            // Establece los valores de los parámetros en la consulta
            ps.setString(1, user);
            ps.setString(2, password);

            // Ejecuta la consulta y obtiene un ResultSet
            rs = ps.executeQuery();

            if (rs.next()) {
                // Usuario autenticado correctamente
                request.setAttribute("Usuario", user);
                RequestDispatcher dispatcher = request.getRequestDispatcher("panel.jsp");
                dispatcher.forward(request, response);
            } else {
                // Si las credenciales son incorrectas
                request.setAttribute("mensajeError", "Usuario o contraseña incorrecta😔");
                RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
                dispatcher.forward(request, response);
            }

        } catch (SQLException | ClassNotFoundException ex) {
            // Si ocurre un error al conectar con la base de datos
            request.setAttribute("mensajeError", "Error al conectar con la base de datos");
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet para manejo de login de usuarios";
    }
}
