package cs_440.keyacoustics.neuralnetwork;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.omg.CORBA.TIMEOUT;

/**
 * Word Match is used to go through the dictionary and match predictions to actual words. 
 * 
 * Adaptation from pseudocode in Georgia Tech paper by Traynor.
 * @author Walker
 *
 */


public class WordMatch {

	private ArrayList<String> potentialMatches;
	private ArrayList<double[]> predictionProfile;
	private WordProfile wp;
	private String foundWord;
	Connection con;
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3306/DICTIONARY";
	static final int COLUMN_MAX = 34;

	//  Database credentials
	static final String USER = "root";
	static final String PASS = "";
	
	
	public WordMatch(WordProfile wp){
		potentialMatches = new ArrayList<String>();
		predictionProfile = wp.getWordProfile();
		for(int i = 0; i < predictionProfile.size(); i++){
			for(int j = 0; j < predictionProfile.get(i).length; j++){
				System.out.println("Prediction profile: "+predictionProfile.get(i)[j]);
			}
		}
		this.wp = wp;

		ArrayList<String> wordMatches = queryWordMatches(wp.getWordLength());
		int maxScore = 0;
		int finalWordPos = 0;
		for(int i = 0; i < wordMatches.size(); i++){
//			System.out.println(i+": "+wordMatches.get(i));
			ArrayList<String> bigrams = queryFindBigrams(wordMatches.get(i));
			ArrayList<double[]> wordProfile = new ArrayList<double[]>();
			for(int j = 0; j < bigrams.size(); j++){
				double[] bigramProfile = queryFindBigramProfile(bigrams.get(j));
				wordProfile.add(bigramProfile);
			}
			int score = compareProfiles(predictionProfile, wordProfile);
			if(score > maxScore){
				maxScore = score;
				finalWordPos = i;
			}
		}
		
		System.out.println("Best word is: "+wordMatches.get(finalWordPos)+" with a score of: "+maxScore);

	}


	/**
	 * Compares the profiles of two words and returns the score, that is, the number of values
	 * that they have in common
	 * 
	 * @param wordOne
	 * @param wordTwo
	 * @return
	 */
	public int compareProfiles(ArrayList<double[]> wordOne, ArrayList<double[]> wordTwo){
		int score = 0;
		//System.out.println("WordOne size: " +wordOne.size()+"\nWordTwo size: "+wordTwo.size());
		if(wordOne.size() == wordTwo.size()){
			for(int i = 0; i < wordOne.size(); i++){
				for(int j = 0; j < 3; j++){
					if(wordOne.get(i)[j] == wordTwo.get(i)[j]){
						score++;
					}
				}
			}
		}else{
			return -1;
		}

		return score;
	}

	public ArrayList<String> queryWordMatches(int wordLength) {
		ArrayList<String> matches = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.prepareStatement("SELECT * FROM WORD  WHERE word_length = "+wordLength); 
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				String id = rs.getString(1);
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
	
	public ArrayList<String> queryFindBigrams(String word) {
		ArrayList<String> matches = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement stmt = null;
		try{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.prepareStatement("SELECT * FROM WORD WHERE id = \'"+word+"\'"); 
			ResultSet rs = stmt.executeQuery();
			int column = 4; 								// Bigrams start at column 4.
			rs.next();
			while(column < COLUMN_MAX) {
				if(rs.getString(column) != null){
					String id = rs.getString(column);
					matches.add(id);
				}
				column++;
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
	
	public double[] queryFindBigramProfile(String bigramInput) {
		Connection conn = null;
		PreparedStatement stmt = null;
		double[] bigramProfile = new double[3];
		try{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.prepareStatement("SELECT * FROM BIGRAM WHERE id = \'"+bigramInput+"\'"); 
			ResultSet rs = stmt.executeQuery();
			int column = 2; 								// Bigrams start at column 4.
			rs.next();
			while(column < 5) {
				if(rs.getString(column) != null){
					double info = (double) rs.getInt(column);
					bigramProfile[column-2] = info;
				}
				column++;
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
		
		return bigramProfile;
	}
	
	
	public String getFoundWord(){
		return foundWord;
	}
}
