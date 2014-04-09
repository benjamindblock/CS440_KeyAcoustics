package cs_440.keyacoustics.neuralnetwork;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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

	public WordMatch(WordProfile wp){
		potentialMatches = new ArrayList<String>();
		predictionProfile = wp.getWordProfile();
		for(int i = 0; i < predictionProfile.size(); i++){
			for(int j = 0; j < predictionProfile.get(i).length; j++){
				System.out.println("Prediction profile"+predictionProfile.get(i)[j]);
			}
		}
		this.wp = wp;

		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/DICTIONARY", "root", "");
			getWords();
			foundWord = findWord();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String findWord(){
		int curScore = 0;
		int wordPos = 0;

		for(int i = 0; i < potentialMatches.size(); i++){
			int score = compareProfiles(predictionProfile, getWordProfile(potentialMatches.get(i)));
			if(curScore < score){
				curScore = score;
				wordPos = i;
			}else if(score == -1){
				//System.err.print("Words are not the same length. Uh oh.\n");
			}

		}

		return potentialMatches.get(wordPos);

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
				System.out.println("Word:"+word);
				potentialMatches.add(word);
			}
		} catch (SQLException se ) {
			con.close();
		} catch (Exception e){
			e.printStackTrace();
		}finally {
			if (stmt != null) { stmt.close();  }
		}
		
		System.out.println("List of potential matches for word length of "+wp.getWordLength()+": ");
		for(int i = 0; i < potentialMatches.size(); i++){
			System.out.println(potentialMatches.get(i));
		}
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
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			int bigramCount = 1;
			while (rs.next()) {
				String query2 = "SELECT charOneSide, charTwoSide, dist" +
								"FROM BIGRAM" +
								"WHERE BIGRAM.id = \'"+rs.getString("WORD.bigram_"+bigramCount)+"\'"+
								" AND WORD.bigram_"+bigramCount+" IS NOT NULL";
				ResultSet rs2 = stmt.executeQuery(query2);
				double[] addTo = new double[3];
				addTo[0] = rs2.getDouble("charOneSide");
				addTo[1] = rs2.getDouble("charTwoSide");
				addTo[2] = rs2.getDouble("dist");
				wordProfile.add(bigramCount-1, addTo);
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
