package cs_440.keyacoustics.database;
import java.sql.*;


public class CreateTable {

	//STEP 1. Import required packages

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3306/DICTIONARY";

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "";

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
			System.out.println("Creating table in given database...");
			stmt = conn.createStatement();

			String sql = 	"CREATE TABLE WORD " +
					"(id VARCHAR(255) not NULL, " +
					" word_length INTEGER, " + 
					" frequency INTEGER, " +
					" bigram_1 VARCHAR(255), " +
					" bigram_2 VARCHAR(255), " +
					" bigram_3 VARCHAR(255), " +
					" bigram_4 VARCHAR(255), " +
					" bigram_5 VARCHAR(255), " +
					" bigram_6 VARCHAR(255), " +
					" bigram_7 VARCHAR(255), " +
					" bigram_8 VARCHAR(255), " +
					" bigram_9 VARCHAR(255), " +
					" bigram_10 VARCHAR(255), " +
					" bigram_11 VARCHAR(255), " +
					" bigram_12 VARCHAR(255), " +
					" bigram_13 VARCHAR(255), " +
					" bigram_14 VARCHAR(255), " +
					" bigram_15 VARCHAR(255), " +
					" bigram_16 VARCHAR(255), " +
					" bigram_17 VARCHAR(255), " +
					" bigram_18 VARCHAR(255), " +
					" bigram_19 VARCHAR(255), " +
					" bigram_20 VARCHAR(255), " +
					" bigram_21 VARCHAR(255), " +
					" bigram_22 VARCHAR(255), " +
					" bigram_23 VARCHAR(255), " +
					" bigram_24 VARCHAR(255), " +
					" bigram_25 VARCHAR(255), " +
					" bigram_26 VARCHAR(255), " +
					" bigram_27 VARCHAR(255), " +
					" bigram_28 VARCHAR(255), " +
					" bigram_29 VARCHAR(255), " +
					" bigram_30 VARCHAR(255), " +
					" PRIMARY KEY ( id ))";
			stmt.executeUpdate(sql);

			sql =	"CREATE TABLE BIGRAM " +
					"(id VARCHAR(255) not NULL, " +
					" charOneSide INTEGER, " +
					" charTwoSide INTEGER, " +
					" dist INTEGER, " +
					" PRIMARY KEY ( id ))";

			stmt.executeUpdate(sql);
			System.out.println("Created tables BIGRAM and WORD in given database...");
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
		System.out.println("Goodbye!");
	}//end main
}
