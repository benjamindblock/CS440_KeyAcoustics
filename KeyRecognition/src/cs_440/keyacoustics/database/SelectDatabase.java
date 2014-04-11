package cs_440.keyacoustics.database;
import java.sql.*;
import java.util.ArrayList;


public class SelectDatabase {

	//STEP 1. Import required packages

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3306/DICTIONARY";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "";
	
	public ArrayList<String> queryWordMatches(String word) {
		ArrayList<String> matches = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.prepareStatement("SELECT * FROM WORD  WHERE word_length = "+word.length()); 
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				String id = rs.getString(0);
				matches.add(id);
			}
			rs.close();
		}catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if (stmt != null){
				try {
					stmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return matches;
	}
	
	

	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			//STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");

			//STEP 4: Execute a query
			stmt = conn.createStatement();
			System.out.println("Selecting from database...");
			String sql = 	" SELECT word_id FROM BIGRAM "+
							" WHERE word_length = 5 ";
			
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				String id = rs.getString("id");
//				testList.add(id);
			}
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					conn.close();
			}catch(SQLException se){
			}// do nothing
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try
//		System.out.println(testList.size());
		System.out.println("Goodbye!");
	}//end main
}
