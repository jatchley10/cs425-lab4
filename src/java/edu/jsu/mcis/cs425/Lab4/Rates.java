package edu.jsu.mcis.cs425.Lab4;

import com.opencsv.CSVReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import javax.naming.*;
public class Rates {
    
    public static final String RATE_FILENAME = "rates.csv";
    
    public static List<String[]> getRates(String path) {
        
        StringBuilder s = new StringBuilder();
        List<String[]> data = null;
        String line;
        
        try {
            
            /* Open Rates File; Attach BufferedReader */

            BufferedReader reader = new BufferedReader(new FileReader(path));
            
            /* Get File Data */
            
            while((line = reader.readLine()) != null) {
                s.append(line).append('\n');
            }
            
            reader.close();
            
            /* Attach CSVReader; Parse File Data to List */
            
            CSVReader csvreader = new CSVReader(new StringReader(s.toString()));
            data = csvreader.readAll();
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return List */
        
        return data;
        
    }
    
    public static String getRatesAsTable(List<String[]> csv) {
        
        StringBuilder s = new StringBuilder();
        String[] row;
        
        try {
            
            /* Create Iterator */
            
            Iterator<String[]> iterator = csv.iterator();
            
            /* Create HTML Table */
            
            s.append("<table>");
            
            while (iterator.hasNext()) {
                
                /* Create Row */
            
                row = iterator.next();
                s.append("<tr>");
                
                for (int i = 0; i < row.length; ++i) {
                    s.append("<td>").append(row[i]).append("</td>");
                }
                
                /* Close Row */
                
                s.append("</tr>");
            
            }
            
            /* Close Table */
            
            s.append("</table>");
            
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        /* Return Table */
        
        return (s.toString());
        
    }
    
    public static String getRatesAsJson(List<String[]> csv) {
        
        String results = "";
        String[] row;
        
        try {
            
            /* Create Iterator */
            
            Iterator<String[]> iterator = csv.iterator();
            
            /* Create JSON Containers */
            
            JSONObject json = new JSONObject();
            JSONObject rates = new JSONObject();            
            
            /* 
             * Add rate data to "rates" container and add "date" and "base"
             * values to "json" container.  See the "getRatesAsTable()" method
             * for an example of how to get the CSV data from the list, and
             * don't forget to skip the header row!
             */
            String[] record;
            int count = 0;
            while(iterator.hasNext()){
                
                if(count == 0){
                    //Skip the first row because it contains headings we don't need for our json data
                    record = iterator.next();
                    record = iterator.next();
                    rates.put(record[1],record[2]);
                    count += 1;
                }
                else{
                    record = iterator.next();
                    rates.put(record[1],record[2]);
                }
            }
            json.put("rates", rates);
            json.put("base", "USD");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            json.put("date", simpleDateFormat.format(new Date()));
            results = JSONValue.toJSONString(json);
        }
        catch (Exception e) { System.err.println( e.toString() ); }
        
        System.err.println(results);
        return (results.trim());
        
    }
    public static String getRatesAsJson(String code){
        
        try{
            String results = null;
            Context envContext = new InitialContext();
            Context initContext = (Context) envContext.lookup("java:/comp/env");
            DataSource ds = (DataSource) initContext.lookup("jdbc/db_pool");
            Connection conn = ds.getConnection();
            String query;
            PreparedStatement pstatement;
            String base;
            String date;
            JSONObject json = new JSONObject();
            JSONObject rates = new JSONObject();
            ResultSet rs;
            if(code == null){
                query = "SELECT code,rate,date FROM rates r";
                pstatement = conn.prepareStatement(query);
                rs = pstatement.executeQuery();
            }
            else{
                query = "SELECT code, rate, date FROM rates r where code like ?";
                pstatement = conn.prepareStatement(query);
                pstatement.setString(1, code);
                rs = pstatement.executeQuery();
            }
            
            while(rs.next()){
                rates.put(rs.getString("code"), rs.getString("rate"));
            }
            json.put("rates",rates);
            json.put("date","2019-09-30");
            json.put("base", "USD");
            results = JSONValue.toJSONString(json);
            
            return results;
        } catch (NamingException ex) {
            Logger.getLogger(Rates.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Rates.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Error";
    }
}