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
		
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DICTIONARY", "root", "");
//			getWords();
//			foundWord = findWord(wordMatches);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String findWord(ArrayList<String> wordMatches){
		int curScore = 0;
		int wordPos = 0;

		for(int i = 0; i < wordMatches.size(); i++){
			int score = compareProfiles(predictionProfile, getWordProfile(wordMatches.get(i)));
			if(curScore < score){
				curScore = score;
				wordPos = i;
			}else if(score == -1){
				//System.err.print("Words are not the same length. Uh oh.\n");
			}

		}

		return wordMatches.get(wordPos);

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
	
	/**
	 * Gets all words from our database with the same length as our potential word.
	 * 
	 * @throws SQLException
	 */
	public void getWords() throws SQLException {

		Statement stmt = null;
		String query = 	" SELECT * FROM WORD "+
						" WHERE word_length = "+wp.getWordLength();
		try {
			System.out.println("I'm here");
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				String word = rs.getString("WORD.id");
				//System.out.println("Word:"+word);
				potentialMatches.add(word);
			}
			rs.close();
			stmt.close();
			con.close();
			//System.out.println("potential matches: "+potentialMatches.size());
		} catch (SQLException se ) {
			con.close();
		} catch (Exception e){
			e.printStackTrace();
		}finally {
			if (stmt != null) { stmt.close();  }
		}
		
//		System.out.println("List of potential matches for word length of "+wp.getWordLength()+": ");
//		for(int i = 0; i < potentialMatches.size(); i++){
//			System.out.println(potentialMatches.get(i));
//		}
	}

	/**
	 * Given a potential word, this method returns an ArrayList<double[]> that is the word profile 
	 * for this potential word, which we then check against the output from the neural networks.
	 *  
	 * @param word
	 * @return wordProfile
	 */
	public ArrayList<double[]> getWordProfile(String word){

		ArrayList<double[]> wordProfile = new ArrayList<double[]>();
		Statement stmt = null;
		String query = "SELECT *" +
				   "FROM WORD" +
				   "WHERE WORD.id = \'"+word+"\'";
		try {
			int bigramCount = 1;
			
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			for (int i = 0; i < potentialMatches.size(); i ++) {
				String query2 = "SELECT *" +
								"FROM BIGRAM, WORD" +
								"WHERE BIGRAM.id = \'"+rs.getString("WORD.bigram_"+bigramCount)+"\'"+
								" AND WORD.bigram_"+bigramCount+" IS NOT NULL";
				ResultSet rs2 = stmt.executeQuery(query2);
				double[] addTo = new double[3];
				addTo[0] = (double)(rs2.getInt("charOneSide"));
				addTo[1] = (double)(rs2.getInt("charTwoSide"));
				addTo[2] = (double)(rs2.getInt("dist"));
				wordProfile.add(addTo);
				System.out.println("Size of word profile: "+wordProfile.size()+"\n"+
									"addTo charOneSide: "+addTo[0]);
				bigramCount++;
			}
		} catch (SQLException se ) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e){
			e.printStackTrace();
		}finally {
			if (stmt != null) { try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} }
		}
		
		

		return wordProfile;
	}
	
	public String getFoundWord(){
		return foundWord;
	}
}
