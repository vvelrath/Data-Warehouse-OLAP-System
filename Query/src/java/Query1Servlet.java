/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
/**
 *
 * @author Vivekanandh
 */
public class Query1Servlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Connection connection = null;
        Statement stmt = null;
        String sqlStatement = null;

        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            Class.forName("oracle.jdbc.driver.OracleDriver");

            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@aos.acsu.buffalo.edu:1521/aos.buffalo.edu", "vvelrath",
                    "cse601");

            stmt = connection.createStatement();

            sqlStatement = "SELECT COUNT(*) AS \"COUNT\" FROM PATIENT, "
                    + "DISEASE, CLINICAL_FACT "
                    + "WHERE DISEASE.DS_ID = CLINICAL_FACT.DS_ID AND "
                    + "PATIENT.P_ID = CLINICAL_FACT.P_ID AND "
                    + "(DISEASE.DESCRIPTION = 'tumor' OR "
                    + "DISEASE.TYPE = 'leukemia' OR "
                    + "DISEASE.NAME = 'ALL')";

            ResultSet rset = stmt.executeQuery(sqlStatement);

            JSONArray outputJsonArray = JsonConverter.convertToJSON(rset);

            //Closing the statement and connection
            stmt.close();
            connection.close();

            /*out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Query1Servlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Query1Servlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");*/
            out.print(outputJsonArray);
        }
        catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
        } catch (ClassNotFoundException e) { 
            System.out.println("Where is your Oracle JDBC Driver?");
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
