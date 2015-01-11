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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.inference.TTest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Vivekanandh
 */
public class Query7Servlet extends HttpServlet {

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

    	String geneID = null;
        double expValue = 0;
        List<Double> exprValuesForGeneID = null;

        Map<String,List<Double>> geneToExpValueALL = new HashMap<String,List<Double>>();
        Map<String,List<Double>> geneToExpValueNOTALL = new HashMap<String,List<Double>>();

        Set<String> geneIDSet = null;
        String[] geneIDs = null;
    	List<Double> listOneForGeneId = null;
    	List<Double> listTwoForGeneId = null;
        Double[] listOneForGeneIdArr = null;
        Double[] listTwoForGeneIdArr = null;
        
        
        double p_value = 0;
        JSONArray informativeGenes = new JSONArray();
        JSONObject informativeGene = null;

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            connection = DriverManager.getConnection(
                            "jdbc:oracle:thin:@VIVEK:1521/ORCL", "system",
                            "vivek");

            stmt = connection.createStatement();


            patientsWithAllSqlQuery = "SELECT P.USER_ID, M.EXPRESSION FROM MICROARRAY_FACT M, PROBE P, CLINICAL_FACT C "
                                                            + "WHERE M.PB_ID = P.PB_ID "
                                                            + "AND M.S_ID = C.S_ID "
                                                            + "AND C.P_ID IN (SELECT P_ID FROM CLINICAL_FACT, DISEASE "
                                                            + "WHERE CLINICAL_FACT.DS_ID = DISEASE.DS_ID AND NAME = 'ALL')";


            patientsWithoutAllSqlQuery = "SELECT P.USER_ID, M.EXPRESSION FROM MICROARRAY_FACT M, PROBE P, CLINICAL_FACT C "
                                                            + "WHERE M.PB_ID = P.PB_ID "
                                                            + "AND M.S_ID = C.S_ID "
                                                            + "AND C.P_ID IN (SELECT P_ID FROM CLINICAL_FACT, DISEASE "
                                                            + "WHERE CLINICAL_FACT.DS_ID = DISEASE.DS_ID AND NAME <> 'ALL')";

			
            //Expression Values for patients with "ALL"
            expWithAllResultSet = stmt.executeQuery(patientsWithAllSqlQuery);

            //Converting the result set for "ALL" to the map
            while(expWithAllResultSet.next()){
            	geneID = expWithAllResultSet.getString(1);
                expValue = expWithAllResultSet.getDouble(2);
                
                if(geneToExpValueALL.get(geneID) != null){
                	exprValuesForGeneID = geneToExpValueALL.get(geneID);
                }else{
                	exprValuesForGeneID = new ArrayList<Double>();
                }
                
                exprValuesForGeneID.add(expValue);
                geneToExpValueALL.put(geneID, exprValuesForGeneID);
            }

            //Expression Values for patients without "ALL"
            expWithoutAllResultSet = stmt.executeQuery(patientsWithoutAllSqlQuery);
			
            while(expWithoutAllResultSet.next()){
            	geneID = expWithoutAllResultSet.getString(1);
                expValue = expWithoutAllResultSet.getDouble(2);
                
                if(geneToExpValueNOTALL.get(geneID) != null){
                	exprValuesForGeneID = geneToExpValueNOTALL.get(geneID);
                }else{
                	exprValuesForGeneID = new ArrayList<Double>();
                }
                
                exprValuesForGeneID.add(expValue);
                geneToExpValueNOTALL.put(geneID, exprValuesForGeneID);
            }
			
            //Calculating the p value for genes between two groups
            geneIDSet = geneToExpValueALL.keySet();
            geneIDs = new String[geneIDSet.size()];
            geneIDSet.toArray(geneIDs);
            
            for(int i = 0; i < geneIDs.length; i++){
            	listOneForGeneId = geneToExpValueALL.get(geneIDs[i]);
            	listTwoForGeneId = geneToExpValueNOTALL.get(geneIDs[i]);

            	listOneForGeneIdArr = new Double[listOneForGeneId.size()];
            	listTwoForGeneIdArr = new Double[listTwoForGeneId.size()];
            	
            	listOneForGeneId.toArray(listOneForGeneIdArr);
            	listTwoForGeneId.toArray(listTwoForGeneIdArr);
            	
            	p_value = new TTest().tTest(ArrayUtils.toPrimitive(listOneForGeneIdArr), ArrayUtils.toPrimitive(listTwoForGeneIdArr));

            	if(p_value < 0.01){
                    informativeGene = new JSONObject();
                    informativeGene.put("infogene", geneIDs[i]);
                    informativeGenes.put(informativeGene);
            	}
            }

            out.print(informativeGenes);
            //Closing the statement and connection
            stmt.close();
            connection.close();
        }catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return;
        } catch (JSONException e) {
            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return;
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
