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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Vivekanandh
 */
public class Query4Servlet extends HttpServlet {

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
        String patientsWithAllSqlQuery = null;
        String patientsWithoutAllSqlQuery = null;

        ResultSet expWithAllResultSet = null;
        ResultSet expWithoutAllResultSet = null;

        List<Integer> expWithAllList = new ArrayList<Integer>();
        double expressionWithAllTotal = 0;
        int n1 = 0;
        double expressionWithAllMean = 0;

        List<Integer> expWithoutAllList = new ArrayList<Integer>();
        double expressionWithoutAllTotal = 0;
        int n2 = 0;
        double expressionWithoutAllMean = 0;

        double expWithAllTotalVariance = 0;
        double expWithAllVariance = 0;
        double expWithoutAllTotalVariance = 0;
        double expWithoutAllVariance = 0;

        double pooledSampleVariance = 0;
        double t_stat = 0;
        JSONObject tstat_obj = new JSONObject();


        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            Class.forName("oracle.jdbc.driver.OracleDriver");

            connection = DriverManager.getConnection(
                    "jdbc:oracle:thin:@aos.acsu.buffalo.edu:1521/aos.buffalo.edu", "vvelrath",
                    "cse601");

            stmt = connection.createStatement();

            patientsWithAllSqlQuery = "SELECT EXPRESSION FROM MICROARRAY_FACT "
                                    + "WHERE PB_ID IN (SELECT PB_ID FROM PROBE, GENE_FACT "
                                    + "WHERE PROBE.USER_ID = GENE_FACT.GENE_UID AND "
                                    + "GO_ID = 0012502) AND S_ID IN (SELECT S_ID FROM CLINICAL_FACT "
                                    + "WHERE P_ID IN (SELECT P_ID FROM CLINICAL_FACT, DISEASE "
                                    + "WHERE CLINICAL_FACT.DS_ID = DISEASE.DS_ID AND NAME = 'ALL'))";


            patientsWithoutAllSqlQuery = "SELECT EXPRESSION FROM MICROARRAY_FACT "
                                        + "WHERE PB_ID IN (SELECT PB_ID FROM PROBE, GENE_FACT "
                                        + "WHERE PROBE.USER_ID = GENE_FACT.GENE_UID AND "
                                        + "GO_ID = 0012502) AND S_ID IN (SELECT S_ID FROM CLINICAL_FACT "
                                        + "WHERE P_ID IN (SELECT P_ID FROM CLINICAL_FACT, DISEASE "
                                        + "WHERE CLINICAL_FACT.DS_ID = DISEASE.DS_ID AND NAME <> 'ALL'))";


                //Expression Values for patients with "ALL"
            expWithAllResultSet = stmt.executeQuery(patientsWithAllSqlQuery);

            while(expWithAllResultSet.next()){
                int expValue = expWithAllResultSet.getInt(1);
                expressionWithAllTotal += expValue;
                expWithAllList.add(expValue);
            }

            n1 = expWithAllList.size();


            //Expression Values for patients without "ALL"
            expWithoutAllResultSet = stmt.executeQuery(patientsWithoutAllSqlQuery);

            while(expWithoutAllResultSet.next()){
                int expValue = expWithoutAllResultSet.getInt(1);
                expressionWithoutAllTotal += expValue;
                expWithoutAllList.add(expValue);
            }

            n2 = expWithoutAllList.size();


            //Calculating mean values
            expressionWithAllMean = expressionWithAllTotal/n1;
            expressionWithoutAllMean = expressionWithoutAllTotal/n2;


            //Calculating the variance of the patients with "ALL" samples
            for(int i = 0; i < expWithAllList.size(); i++){
                expWithAllTotalVariance += Math.pow((expWithAllList.get(i) - expressionWithAllMean), 2);
            }
            expWithAllVariance = expWithAllTotalVariance/(n1-1);

            //Calculating the variance of the patients without "ALL" samples
            for(int i = 0; i < expWithoutAllList.size(); i++){
                expWithoutAllTotalVariance += Math.pow((expWithoutAllList.get(i) - expressionWithoutAllMean), 2);
            }
            expWithoutAllVariance = expWithoutAllTotalVariance/(n2-1);

            //Calculating the pooled sample variance for calculating the t test with equal variance
            pooledSampleVariance = (((n1 - 1)*expWithAllVariance) + ((n2 - 1)*expWithoutAllVariance))/(n1 + n2 - 2);


            //Calculating the t test
            t_stat = (expressionWithAllMean - expressionWithoutAllMean)/Math.sqrt(((pooledSampleVariance/n1) + (pooledSampleVariance/n2)));
            
            //Populating the JSON Object
            tstat_obj.put("tstat", t_stat);

            //Closing the statement and connection
            stmt.close();
            connection.close();

            out.print(tstat_obj);
        }
        catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
        } catch (ClassNotFoundException e) { 
            System.out.println("Where is your Oracle JDBC Driver?");
        } catch (JSONException ex) {
            Logger.getLogger(Query4Servlet.class.getName()).log(Level.SEVERE, null, ex);
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
