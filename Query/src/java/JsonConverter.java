
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Vivekanandh
 */
public class JsonConverter {
    
    /*Code adapted from http://biercoff.blogspot.com/2013/11/nice-and-simple-converter-of-java.html starts here*/
    public static JSONArray convertToJSON(ResultSet resultSet) {
        JSONArray jsonArray = new JSONArray();
        try{
	        while (resultSet.next()) {
	            int total_rows = resultSet.getMetaData().getColumnCount();
	            JSONObject obj = new JSONObject();
	            for (int i = 0; i < total_rows; i++) {
	                obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
	                        .toLowerCase(), resultSet.getObject(i + 1));
	                
	            }
	            jsonArray.put(obj);
	        }
        }catch(SQLException se){
        	System.out.println("SQL problem");
        }catch(JSONException se){
        	System.out.println("JSON problem");
        }
        return jsonArray;
    }
    /*Code adapted from http://biercoff.blogspot.com/2013/11/nice-and-simple-converter-of-java.html starts here*/
}
