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
public class Query5Servlet extends HttpServlet {

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
        JSONObject fstat_obj = new JSONObject();
		
        String patientsWithAllSqlQuery = null;
        String patientsWithAmlSqlQuery = null;
        String patientsWithCTumorSqlQuery = null;
        String patientsWithBTumorSqlQuery = null;

        ResultSet expWithAllResultSet = null;
        ResultSet expWithAmlResultSet = null;
        ResultSet expWithCTumorResultSet = null;
        ResultSet expWithBTumorResultSet = null;
		
        //Declaring the list and variables for individual groups
        List<Integer>  expWithAllList = new ArrayList<Integer>();
        double expressionWithAllTotal = 0;
        int n1 = 0;
        double expressionWithAllMean = 0;

        List<Integer>  expWithAmlList = new ArrayList<Integer>();
        double expressionWithAmlTotal = 0;
        int n2 = 0;
        double expressionWithAmlMean = 0;
	    
        List<Integer>  expWithCTumorList = new ArrayList<Integer>();
        double expressionWithCTumorTotal = 0;
        int n3 = 0;
        double expressionWithCTumorMean = 0;

        List<Integer>  expWithBTumorList = new ArrayList<Integer>();
        double expressionWithBTumorTotal = 0;
        int n4 = 0;
        double expressionWithBTumorMean = 0;

        int n = 0;
        double totalMean = 0;

        double ss_cond1 = 0;
        double ss_cond2 = 0;
        double ss_cond3 = 0;
        double ss_cond4 = 0;
        double ss_cond = 0;

        double ss_err1 = 0;
        double ss_err2 = 0;
        double ss_err3 = 0;
        double ss_err4 = 0;
        double ss_err = 0;

        double ms_cond = 0;
        double ms_err = 0;
        double f_stat = 0;


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
                                                            + "GO_ID = 0007154) AND S_ID IN (SELECT S_ID FROM CLINICAL_FACT "
                                                            + "WHERE P_ID IN (SELECT P_ID FROM CLINICAL_FACT, DISEASE "
                                                            + "WHERE CLINICAL_FACT.DS_ID = DISEASE.DS_ID AND NAME = 'ALL'))";

            patientsWithAmlSqlQuery = "SELECT EXPRESSION FROM MICROARRAY_FACT "
                                                            + "WHERE PB_ID IN (SELECT PB_ID FROM PROBE, GENE_FACT "
                                                            + "WHERE PROBE.USER_ID = GENE_FACT.GENE_UID AND "
                                                            + "GO_ID = 0007154) AND S_ID IN (SELECT S_ID FROM CLINICAL_FACT "
                                                            + "WHERE P_ID IN (SELECT P_ID FROM CLINICAL_FACT, DISEASE "
                                                            + "WHERE CLINICAL_FACT.DS_ID = DISEASE.DS_ID AND NAME = 'AML'))";

            patientsWithCTumorSqlQuery = "SELECT EXPRESSION FROM MICROARRAY_FACT "
                                                            + "WHERE PB_ID IN (SELECT PB_ID FROM PROBE, GENE_FACT "
                                                            + "WHERE PROBE.USER_ID = GENE_FACT.GENE_UID AND "
                                                            + "GO_ID = 0007154) AND S_ID IN (SELECT S_ID FROM CLINICAL_FACT "
                                                            + "WHERE P_ID IN (SELECT P_ID FROM CLINICAL_FACT, DISEASE "
                                                            + "WHERE CLINICAL_FACT.DS_ID = DISEASE.DS_ID AND NAME = 'Colon tumor'))";

            patientsWithBTumorSqlQuery = "SELECT EXPRESSION FROM MICROARRAY_FACT "
                                                            + "WHERE PB_ID IN (SELECT PB_ID FROM PROBE, GENE_FACT "
                                                            + "WHERE PROBE.USER_ID = GENE_FACT.GENE_UID AND "
                                                            + "GO_ID = 0007154) AND S_ID IN (SELECT S_ID FROM CLINICAL_FACT "
                                                            + "WHERE P_ID IN (SELECT P_ID FROM CLINICAL_FACT, DISEASE "
                                                            + "WHERE CLINICAL_FACT.DS_ID = DISEASE.DS_ID AND NAME = 'Breast tumor'))";


            //Expression Values for patients with "ALL"
            expWithAllResultSet = stmt.executeQuery(patientsWithAllSqlQuery);

            while(expWithAllResultSet.next()){
                int expValue = expWithAllResultSet.getInt(1);
                expressionWithAllTotal += expValue;
                expWithAllList.add(expValue);
            }

            //Expression Values for patients with "AML"
            expWithAmlResultSet = stmt.executeQuery(patientsWithAmlSqlQuery);

            while(expWithAmlResultSet.next()){
                int expValue = expWithAmlResultSet.getInt(1);
                expressionWithAmlTotal += expValue;
                expWithAmlList.add(expValue);
            }

            //Expression Values for patients with "Colon Tumor"
            expWithCTumorResultSet = stmt.executeQuery(patientsWithCTumorSqlQuery);

            while(expWithCTumorResultSet.next()){
                int expValue = expWithCTumorResultSet.getInt(1);
                expressionWithCTumorTotal += expValue;
                expWithCTumorList.add(expValue);
            }

            //Expression Values for patients with "Breast Tumor"
            expWithBTumorResultSet = stmt.executeQuery(patientsWithBTumorSqlQuery);

            while(expWithBTumorResultSet.next()){
                int expValue = expWithBTumorResultSet.getInt(1);
                expressionWithBTumorTotal += expValue;
                expWithBTumorList.add(expValue);
            }
		    
            //Number of measurements for the groups
            n1 = expWithAllList.size();
            n2 = expWithAmlList.size();
            n3 = expWithCTumorList.size();
            n4 = expWithBTumorList.size();
            n = n1 + n2 + n3 + n4;

            //Calculating mean values for each group
            expressionWithAllMean = expressionWithAllTotal/n1;
            expressionWithAmlMean = expressionWithAmlTotal/n2;
            expressionWithCTumorMean = expressionWithCTumorTotal/n3;
            expressionWithBTumorMean = expressionWithBTumorTotal/n4;
            totalMean = (expressionWithAllTotal +
                                         expressionWithAmlTotal +
                                         expressionWithCTumorTotal +
                                         expressionWithBTumorTotal)
                                         /n;

            //Calculating sum of squares for the conditions
            ss_cond1 = n1 * Math.pow((expressionWithAllMean - totalMean),2);
            ss_cond2 = n2 * Math.pow((expressionWithAmlMean - totalMean),2);
            ss_cond3 = n3 * Math.pow((expressionWithCTumorMean - totalMean),2);
            ss_cond4 = n4 * Math.pow((expressionWithBTumorMean - totalMean),2);
            ss_cond = ss_cond1 + ss_cond2 + ss_cond3 + ss_cond4;
		    
            //Calculating sum of squares for the errors		    
            for(int i=0;i<expWithAllList.size();i++){
                ss_err1 += Math.pow((expWithAllList.get(i) - expressionWithAllMean),2);
            }
            for(int i=0;i<expWithAmlList.size();i++){
                ss_err2 += Math.pow((expWithAmlList.get(i) - expressionWithAmlMean),2);
            }
            for(int i=0;i<expWithCTumorList.size();i++){
                ss_err3 += Math.pow((expWithCTumorList.get(i) - expressionWithCTumorMean),2);
            }
            for(int i=0;i<expWithBTumorList.size();i++){
                ss_err4 += Math.pow((expWithBTumorList.get(i) - expressionWithBTumorMean),2);
            }

            ss_err = ss_err1 + ss_err2 + ss_err3 + ss_err4;


            //Conditional mean squares and Error mean squares
            ms_cond = ss_cond/3;
            ms_err = ss_err/(n-4);

            //F Statistic
            f_stat = ms_cond/ms_err;

            //Populating the JSON Object
            fstat_obj.put("fstat", f_stat);

            //Closing the statement and connection
            stmt.close();
            connection.close();

            out.print(fstat_obj);
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
