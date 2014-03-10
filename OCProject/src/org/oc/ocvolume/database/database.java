/*
OC Volume - Java Speech Recognition Engine
Copyright (c) 2002-2004, OrangeCow organization
All rights reserved.

Redistribution and use in source and binary forms,
with or without modification, are permitted provided
that the following conditions are met:

* Redistributions of source code must retain the
  above copyright notice, this list of conditions
  and the following disclaimer.
* Redistributions in binary form must reproduce the
  above copyright notice, this list of conditions
  and the following disclaimer in the documentation
  and/or other materials provided with the
  distribution.
* Neither the name of the OrangeCow organization
  nor the names of its contributors may be used to
  endorse or promote products derived from this
  software without specific prior written
  permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

Contact information:
Please visit http://ocvolume.sourceforge.net.
*/

package org.oc.ocvolume.database;

import java.sql.*;
import java.io.*;
import java.util.*;

/**
 * last updated on June 15, 2002<br>
 * <b>description:</b> class used to manipulate database<br>
 * <b>calls:</b> none<br>
 * <b>called by:</b> volume<br>
 * <b>input:</b> data to be stored in database<br>
 * <b>output:</b> data read from database
 * @author Danny Su
 */
public class database{
    /**
     * data type DOUBLE
     */
    public static final String DOUBLE = "DOUBLE";
    /**
     * statement - used to send SQL commands to db
     */
    private Statement stmt;
    /**
     * connection to database
     */
    private Connection con;
    /**
     * get number of rows in a table<br>
     * calls: none<br>
     * called by: volume
     * @param tableName table to get row count from
     * @return number of rows
     */
    public int getRowCount(String tableName){
        int rows = 0;

        try{
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
            if ( rs != null ){
                rs.next();
                rows = rs.getInt(1);
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
        return rows;
    }
    /**
     * retrieve data from entire table<br>
     * calls: none<br>
     * called by: volume
     * @param tableName table to get data from
     * @return result set
     */
    public ResultSet retrieve(String tableName){
        tableName = tableName.replace(' ', '_');
        ResultSet rs = null;
        try{
            rs = stmt.executeQuery("SELECT * FROM " + tableName);
        }
        catch(SQLException SQLE) {
            System.out.println(SQLE.toString());
        }
        
        return rs;
    }
    
    /**
     * inserts a row to the specified table<br>
     * calls: none<br>
     * called by: chgToDB
     * @param tableName table to add the new row to
     * @param values values for all the columns in the table
     */
    public void insertRow(String tableName, double values[]){
        tableName = tableName.replace(' ', '_');
        String sqlcmd = "INSERT INTO " + tableName + " VALUES(";
        for (int i = 0; i < values.length; i++){
            sqlcmd += values[i] + "";
            if (i != values.length - 1){
                sqlcmd += ", ";
            }
        }
        sqlcmd += ")";
        
        System.out.println(sqlcmd);
        
        try{
            stmt.execute(sqlcmd);
        }
        catch(SQLException SQLE) {
            System.out.println(SQLE.toString());
        }
    }
    
    /**
     * creates a table in database<br>
     * calls: none<br>
     * called by: chgToDB
     * @param tableName name of table to be created
     * @param columns column names
     * @param dataTypes data types for the columns
     */
    public void createTable(String tableName, String columns[], String dataTypes[]){
        tableName = tableName.replace(' ', '_');
        String sqlcmd = "CREATE TABLE " + tableName + " ( ";
        for (int i = 0; i < columns.length; i++){
            sqlcmd += columns[i] + " " + dataTypes[i];
            if (i != columns.length - 1){
                sqlcmd += ", ";
            }
        }
        sqlcmd += " )";
        
        System.out.println(sqlcmd);
        
        try{
            stmt.execute(sqlcmd);
        }
        catch(SQLException SQLE) {
            System.out.println(SQLE.toString());
        }
    }
    
    /**
     * close connection to database<br>
     * calls: none<br>
     * called by: volume, chgToDB
     */
    public void close(){
        try{
            stmt.close();
            con.close();
        }
        catch(Exception e){
        }
    }
    
    /**
     * constructor to load database<br>
     * calls: none<br>
     * called by: volume, chgToDB
     * @param dataSourceName database source
     */
    public database(String dataSourceName){
        try{           
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            con = DriverManager.getConnection("jdbc:odbc:" + dataSourceName);
            
            stmt = con.createStatement();
        }
        catch(SQLException SQLE) {
            System.out.println(SQLE.toString());
        }
        catch(Exception e){
            System.out.println(e.toString());
        }
    }
}