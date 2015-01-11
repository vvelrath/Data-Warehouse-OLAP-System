/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.inference.TTest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Vivekanandh
 */
public class Query8Servlet extends HttpServlet {

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
        String patientswithoutAllSqlStat = null;

        ResultSet rsetForALL = null;
        ResultSet rsetForNOTALL = null;
	    
    	String patientID = null;
        double expValue = 0;
        List<Double> exprValuesForPatientID = null;

        Map<String,List<Double>> patientToExpValueALL = new HashMap<String,List<Double>>();
        Map<String,List<Double>> patientToExpValueNOTALL = new HashMap<String,List<Double>>();

        //Variables for average correlation
        int i,j;
        double correlation = 0;
    	
        Set<String> patientsForALLSet = null;
    	String[] patientsForALL = null;
      	Set<String> patientsForNOTALLSet = null;
    	String[] patientsForNOTALL = null;

    	Set<String> patientsForTestCaseSet = null;
    	String[] patientsForTestCase = null;

  
    	List<Double> patientAllExprValues = null;
    	Double[] patientAllExprValuesArr = null;
        List<Double> patientNotAllExprValues = null;
        Double[] patientNotAllExprValuesArr = null;
       	List<Double> testPatientExprValues = null;
    	Double[] testPatientExprValuesArr = null;

    	JSONObject diseaseClassification = new JSONObject();
    	JSONArray testResults = new JSONArray();

        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            Class.forName("oracle.jdbc.driver.OracleDriver");

            connection = DriverManager.getConnection(
                            "jdbc:oracle:thin:@VIVEK:1521/ORCL", "system",
                            "vivek");

            stmt = connection.createStatement();


            patientswithAllSqlStat="SELECT C.P_ID, M.EXPRESSION FROM MICROARRAY_FACT M, PROBE P, "
                                + "CLINICAL_FACT C WHERE M.PB_ID = P.PB_ID AND M.S_ID = C.S_ID "
                                + "AND C.P_ID IN (SELECT P_ID FROM CLINICAL_FACT, DISEASE "
                                + "WHERE CLINICAL_FACT.DS_ID = DISEASE.DS_ID AND NAME = 'ALL') "
                                + "AND P.USER_ID IN(65772884,17144710,31997186,85557586,11333636,"
                                + "41333415,45926811,13947282,83398521,23552119,"
                                + "74496827,24984526,28863379,88257558,31308500,"
                                + "41464216,43866587,60661836,37998407,94113401,"
                                + "88596261,48199244,69156037,4826120,1433276,"
                                + "16073088,75492172,58672549,87592194,38422427,"
                                + "58792011,15295292,21633757,89697658,97606543,"
                                + "18493181,75434512,72920004,52948490,53478188,40567338)";

            patientswithoutAllSqlStat="SELECT C.P_ID, M.EXPRESSION FROM MICROARRAY_FACT M, PROBE P, "
                                    + "CLINICAL_FACT C WHERE M.PB_ID = P.PB_ID AND M.S_ID = C.S_ID "
                                    + "AND C.P_ID IN (SELECT P_ID FROM CLINICAL_FACT, DISEASE "
                                    + "WHERE CLINICAL_FACT.DS_ID = DISEASE.DS_ID AND NAME <> 'ALL') "
                                    + "AND P.USER_ID IN(65772884,17144710,31997186,85557586,11333636,"
                                    + "41333415,45926811,13947282,83398521,23552119,"
                                    + "74496827,24984526,28863379,88257558,31308500,"
                                    + "41464216,43866587,60661836,37998407,94113401,"
                                    + "88596261,48199244,69156037,4826120,1433276,"
                                    + "16073088,75492172,58672549,87592194,38422427,"
                                    + "58792011,15295292,21633757,89697658,97606543,"
                                    + "18493181,75434512,72920004,52948490,53478188,40567338)";
            

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
            
            //Executing the query for NOT ALL
            rsetForNOTALL = stmt.executeQuery(patientswithoutAllSqlStat);
            
            //Converting the result set for "NOT ALL" to the map
            while(rsetForNOTALL.next()){
            	patientID = rsetForNOTALL.getString(1);
                expValue = rsetForNOTALL.getDouble(2);
                
                if(patientToExpValueNOTALL.get(patientID) != null){
                	exprValuesForPatientID = patientToExpValueNOTALL.get(patientID);
                }else{
                	exprValuesForPatientID = new ArrayList<Double>();
                }
                
                exprValuesForPatientID.add(expValue);
                patientToExpValueNOTALL.put(patientID, exprValuesForPatientID);
            }

            
            //Getting the information of test cases from the file
            Map<String, List<Double>> testPatientsToExprValues = getTestCaseInformation();
            
            //Getting the data structures ready for calculating P value
            patientsForALLSet = patientToExpValueALL.keySet();
            patientsForALL = new String[patientsForALLSet.size()];
            patientsForALLSet.toArray(patientsForALL);
        	
            patientsForNOTALLSet = patientToExpValueNOTALL.keySet();
            patientsForNOTALL = new String[patientsForNOTALLSet.size()];
            patientsForNOTALLSet.toArray(patientsForNOTALL);

            patientsForTestCaseSet = testPatientsToExprValues.keySet();
            patientsForTestCase = new String[patientsForTestCaseSet.size()];
            patientsForTestCaseSet.toArray(patientsForTestCase);

            //Iterating through the test patients to calculate ra's and rb's
            for(i = 0; i < patientsForTestCase.length; i++){
            	
            	testPatientExprValues = testPatientsToExprValues.get(patientsForTestCase[i]);
            	testPatientExprValuesArr = new Double[testPatientExprValues.size()];
            	testPatientExprValues.toArray(testPatientExprValuesArr);
            	
            	//Initializing the test patient's correlation array with the two groups
            	double[] correlation_ra = new double[patientsForALL.length];
            	double[] correlation_rb = new double[patientsForNOTALL.length];
            	
            	//Calculating the correlation for between ALL
            	for(j = 0; j < patientsForALL.length; j++){
            	
                    patientAllExprValues = patientToExpValueALL.get(patientsForALL[j]);
                    patientAllExprValuesArr = new Double[patientAllExprValues.size()];;
                    patientAllExprValues.toArray(patientAllExprValuesArr);

                    correlation = new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(patientAllExprValuesArr),
                                                                        ArrayUtils.toPrimitive(testPatientExprValuesArr));            	
                    correlation_ra[j] = correlation;
            	}
            	
            	//Calculating the correlation for between ALL and AML
            	for(j = 0; j < patientsForNOTALL.length; j++){
                    patientNotAllExprValues = patientToExpValueNOTALL.get(patientsForNOTALL[j]);
                    patientNotAllExprValuesArr = new Double[patientNotAllExprValues.size()];;
                    patientNotAllExprValues.toArray(patientNotAllExprValuesArr);
                    correlation = new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(patientNotAllExprValuesArr),
                                                                        ArrayUtils.toPrimitive(testPatientExprValuesArr));
                    correlation_rb[j] = correlation;            	
            	}
            	
            	//Calculating the p value of the two correlation arrays ra and rb
            	double p_value = new TTest().tTest(correlation_ra, correlation_rb);
            	
            	if(p_value < 0.01){
            		diseaseClassification.put(patientsForTestCase[i], "ALL");
            	}else{
            		diseaseClassification.put(patientsForTestCase[i], "NOT ALL");
            	}
            }
            
            testResults.put(diseaseClassification);
            out.print(diseaseClassification);
                        
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
        }catch (JSONException e) {
                System.out.println("Where is your Oracle JDBC Driver?");
                e.printStackTrace();
                return;
        }
    }
    
    //Getting information from the test_patients file
    public static Map<String,List<Double>> getTestCaseInformation(){

            List<String> patientNames = new ArrayList<String>();
            Map<String, List<Double>> testPatientToExprValues = new HashMap<String, List<Double>>();
            List<Double> exprValues = null;

            BufferedReader reader = null;
            String line = null;
            String[] parts = null;

            try {
                    reader = new BufferedReader(new FileReader("C://Users//Vivekanandh//Desktop//Data Mining//Data Files//test_samples.txt"));
                    line = reader.readLine().trim();
                    parts = line.split("\\s");

                    //Adding the patient names to the list
                    for(String patientName : parts){
                            patientNames.add(patientName);
                    }

                    while ((line = reader.readLine()) != null) {

                            parts = line.split("\\s");
                            int geneID = Integer.parseInt(parts[0]);

                            //Adding the expression values only from the informative genes
                            if(GeneConstants.informativeGenes.contains(geneID)){
                                    //Iterating through the expression values for different patients to add them to the map
                                    for(int j = 0; j < patientNames.size(); j++){
                                            if(testPatientToExprValues.containsKey(patientNames.get(j)))
                                                    exprValues = testPatientToExprValues.get(patientNames.get(j));
                                            else
                                                    exprValues = new ArrayList<Double>();

                                            exprValues.add(Double.parseDouble(parts[j+1]));
                                            testPatientToExprValues.put(patientNames.get(j), exprValues);
                                    }	
                            }
                    }
                    reader.close();
            } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }		

            return testPatientToExprValues;
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
