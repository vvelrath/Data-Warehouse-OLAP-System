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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Vivekanandh
 */
public class Query6Servlet extends HttpServlet {

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

        String patientswithAllSqlStat = null;
        String patientswithAmlSqlStat = null;

        ResultSet rsetForALL = null;
        ResultSet rsetForAML = null;
	    
    	String patientID = null;
        double expValue = 0;
        List<Double> exprValuesForPatientID = null;

        Map<String,List<Double>> patientToExpValueALL = new HashMap<String,List<Double>>();
        Map<String,List<Double>> patientToExpValueAML = new HashMap<String,List<Double>>();

        //Variables for average correlation
        int i,j;
        double correlation = 0;
        double totalCorrelationALL = 0;
        double avgCorrelationAll = 0;
    	double numComForAll = 0;
        Set<String> patientsForALLSet = null;
    	String[] patientsForALL = null;

        double totalCorrelationAML = 0;
        double avgCorrelationAllAml = 0;
    	double numComForAml = 0;
        Set<String> patientsForAMLSet = null;
    	String[] patientsForAML = null;

    	List<Double> patientOneExprValues = null;
    	Double[] patientOneExprValuesArr = null;
        List<Double> patientTwoExprValues = null;
        Double[] patientTwoExprValuesArr = null;

        response.setContentType("application/json");
        
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            Class.forName("oracle.jdbc.driver.OracleDriver");

            connection = DriverManager.getConnection(
                            "jdbc:oracle:thin:@aos.acsu.buffalo.edu:1521/aos.buffalo.edu", "vvelrath",
                            "cse601");

            stmt = connection.createStatement();


            patientswithAllSqlStat="SELECT C.P_ID, EXPRESSION FROM MICROARRAY_FACT M, CLINICAL_FACT C "
                                                     + "WHERE M.S_ID = C.S_ID AND M.PB_ID IN (SELECT PB_ID FROM PROBE, GENE_FACT "
                                                     + "WHERE PROBE.USER_ID = GENE_FACT.GENE_UID AND "
                                                     + "GO_ID = 0007154) AND M.S_ID IN (SELECT S_ID FROM CLINICAL_FACT "
                                                     + "WHERE P_ID IN (SELECT P_ID FROM CLINICAL_FACT, DISEASE "
                                                     + "WHERE CLINICAL_FACT.DS_ID = DISEASE.DS_ID AND NAME = 'ALL'))";

            patientswithAmlSqlStat="SELECT C.P_ID, EXPRESSION FROM MICROARRAY_FACT M, CLINICAL_FACT C "
                                                     + "WHERE M.S_ID = C.S_ID AND M.PB_ID IN (SELECT PB_ID FROM PROBE, GENE_FACT "
                                                     + "WHERE PROBE.USER_ID = GENE_FACT.GENE_UID AND "
                                                     + "GO_ID = 0007154) AND M.S_ID IN (SELECT S_ID FROM CLINICAL_FACT "
                                                     + "WHERE P_ID IN (SELECT P_ID FROM CLINICAL_FACT, DISEASE "
                                                     + "WHERE CLINICAL_FACT.DS_ID = DISEASE.DS_ID AND NAME = 'AML'))";


            //Executing the query for ALL
            rsetForALL = stmt.executeQuery(patientswithAllSqlStat);
			
            //Converting the result set for "ALL" to the map
            while(rsetForALL.next()){
            	patientID = rsetForALL.getString(1);
                expValue = rsetForALL.getDouble(2);
                
                if(patientToExpValueALL.get(patientID) != null){
                	exprValuesForPatientID = patientToExpValueALL.get(patientID);
                }else{
                	exprValuesForPatientID = new ArrayList<Double>();
                }
                
                exprValuesForPatientID.add(expValue);
                patientToExpValueALL.put(patientID, exprValuesForPatientID);
            }
            
            //Executing the query for AML
            rsetForAML = stmt.executeQuery(patientswithAmlSqlStat);
            
            //Converting the result set for "AML" to the map
            while(rsetForAML.next()){
            	patientID = rsetForAML.getString(1);
                expValue = rsetForAML.getDouble(2);
                
                if(patientToExpValueAML.get(patientID) != null){
                	exprValuesForPatientID = patientToExpValueAML.get(patientID);
                }else{
                	exprValuesForPatientID = new ArrayList<Double>();
                }
                
                exprValuesForPatientID.add(expValue);
                patientToExpValueAML.put(patientID, exprValuesForPatientID);
            }
            
            //Finding average correlation for two patients with ALL and for between ALL and AML
            patientsForALLSet = patientToExpValueALL.keySet();
            patientsForALL = new String[patientsForALLSet.size()];
            patientsForALLSet.toArray(patientsForALL);
        	
            patientsForAMLSet = patientToExpValueAML.keySet();
            patientsForAML = new String[patientsForAMLSet.size()];
            patientsForAMLSet.toArray(patientsForAML);

            for(i = 0; i < patientsForALL.length; i++){
            	
            	patientOneExprValues = patientToExpValueALL.get(patientsForALL[i]);
            	patientOneExprValuesArr = new Double[patientOneExprValues.size()];
            	patientOneExprValues.toArray(patientOneExprValuesArr);
            	
            	//Calculating the correlation for between ALL
            	for(j = i + 1; j < patientsForALL.length; j++){
            	
            		patientTwoExprValues = patientToExpValueALL.get(patientsForALL[j]);
            		patientTwoExprValuesArr = new Double[patientTwoExprValues.size()];;
            		patientTwoExprValues.toArray(patientTwoExprValuesArr);
            		
            		correlation = new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(patientOneExprValuesArr),
            											  ArrayUtils.toPrimitive(patientTwoExprValuesArr));            	
            		totalCorrelationALL += correlation;
            		numComForAll++;
            	}
            	
            	//Calculating the correlation for between ALL and AML
            	for(j = 0; j < patientsForAML.length; j++){
            		patientTwoExprValues = patientToExpValueAML.get(patientsForAML[j]);
            		patientTwoExprValuesArr = new Double[patientTwoExprValues.size()];;
            		patientTwoExprValues.toArray(patientTwoExprValuesArr);
            		correlation = new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(patientOneExprValuesArr),
            											  ArrayUtils.toPrimitive(patientTwoExprValuesArr));
            		totalCorrelationAML += correlation;
            		numComForAml++;
            	}
            }

            avgCorrelationAll = totalCorrelationALL/numComForAll;            
            avgCorrelationAllAml = totalCorrelationAML/numComForAml;

            //Populating the JSON Object
            JSONObject avgCorr = new JSONObject();
            
            
            avgCorr.put("avgcorrall", avgCorrelationAll);
            avgCorr.put("avgcorrallaml", avgCorrelationAllAml);
            
            out.print(avgCorr);
            
            //Closing the statement and connection
            stmt.close();
            connection.close();
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
