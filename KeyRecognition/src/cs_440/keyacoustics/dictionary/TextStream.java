package cs_440.keyacoustics.dictionary;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Class that imports the text that corresponds with the audio for our training data.
 * 
 * @author Ben
 *
 */

public class TextStream {
	private FileNameExtensionFilter filter = new FileNameExtensionFilter("Plain text files, (.txt)", "txt");
	private JFileChooser fileChooser = new JFileChooser();
	private File inFile;
	public ArrayList<Character> characterList = new ArrayList<Character>();
	public ArrayList<Word> wordList = new ArrayList<Word>();
	private int numOfCharacters;

	public void textReader() throws IOException{
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Select file for dictionary.");
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			throw new Error("Input file not selected");
		inFile = fileChooser.getSelectedFile();
		System.out.println("Opened:" + inFile.getName());
		Charset encoding = Charset.defaultCharset();
		handleCharacters(inFile, encoding, 0);
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Select file for character training.");
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			throw new Error("Input file not selected");
		inFile = fileChooser.getSelectedFile();
		System.out.println("Opened:" + inFile.getName());
		handleCharacters(inFile, encoding, 1);
	} 


	private void handleCharacters(File file, Charset encoding, int type) throws IOException {
		try (InputStream in = new FileInputStream(file);
				Reader reader = new InputStreamReader(in, encoding);
				// buffer for efficiency
				Reader wordReader = new InputStreamReader(in);
				BufferedReader wordBuffer = new BufferedReader(wordReader);
				Reader buffer = new BufferedReader(reader)) {
			if(type == 0){
				createWordList(wordBuffer);
			} else if (type == 1) {
				createCharacterList(buffer);
			}
		}
	}

	private void createWordList(BufferedReader reader)
			throws IOException {
		String s;
		int max = 0;
		while ((s = reader.readLine()) != null) {
			String st = (String) s;
			int length = st.length();
			if (length > max) {
				max = length;
			}
			wordList.add(new Word(st));
		}
		System.out.println("Max is: "+ max);
	}

	private void createCharacterList(Reader reader)
			throws IOException {
		int r;
		int counter = 0;
		while ((r = reader.read()) != -1) {
			char ch = (char) r;
			characterList.add(ch);
			counter++;
		}
		numOfCharacters = counter;
	}
	
	public int getNumOfCharacters(){
		System.out.println("Number of Characters: "+numOfCharacters);
		return numOfCharacters;
	}

	public ArrayList<Character> getArray(){
		return characterList;
	}

	public void insertIntoDatabase(){

		Statement stmt = null;
		Connection conn = null;
		try{
			String sql;

			// JDBC driver name and database URL
			final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
			final String DB_URL = "jdbc:mysql://localhost:3306/DICTIONARY";

			//  Database credentials
			final String USER = "root";
			final String PASS = "";

			//STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			//STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");

			//STEP 4: Execute a query
			System.out.println("Creating table in given database...");
			stmt = conn.createStatement();
			Set <String> uniqueBigrams = new HashSet<String>();
			for(int i = 0; i < wordList.size(); i++){
				String insertWord = "INSERT INTO WORD " +
						" VALUES ( \'"+wordList.get(i).getWordName()+"\', " +
						wordList.get(i).getWordLength() + ", " +
						i + ",";
				Bigram[] bigrams = wordList.get(i).getBigrams();
				for(int j = 0; j <= 29; j++){
					if( j == 29) {
						insertWord = insertWord + "null ";
					} else if (j < bigrams.length){
//						System.out.println("j is: "+j);
//						System.out.println("word: "+wordList.get(i).getWordName());
						insertWord = insertWord + "\'"+bigrams[j].getPair()+"\', ";
						if(uniqueBigrams.add(bigrams[j].getPair())){
							int charSideOne;
							if(bigrams[j].getCharOneSide().equals(KEYBOARD_SIDE.LEFT)){
								charSideOne = 0;
							} else {
								charSideOne = 1;
							}
							int charSideTwo;
							if((bigrams[j].getPair().length() == 2) && (bigrams[j].getCharTwoSide().equals(KEYBOARD_SIDE.LEFT))){
								charSideTwo = 0;
							} else {
								charSideTwo = 1;
							}
							int distance;
							if((bigrams[j].getPair().length() == 2) && (bigrams[j].getDistance().equals(DISTANCE.NEAR))){
								distance = 0;
							} else {
								distance = 1;
							} 
							String insertBigram = "INSERT INTO BIGRAM " +
									" VALUES ( \'" + bigrams[j].getPair()+ "\', " +
									charSideOne + ", " +
									charSideTwo + ", " +
									distance + " )";
							//System.out.println(insertBigram);
							stmt.executeUpdate(insertBigram);
						}
						
//						System.out.println("bigram is: "+bigrams[j].getPair());
					}else{
						insertWord = insertWord + "null, ";
					}
				}
				sql = insertWord + " )";
				System.out.println(sql);
				stmt.executeUpdate(sql);
			}

		}catch(SQLException se){
			se.printStackTrace();
		}catch(Exception e){
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
		}
		System.out.println("Records added. Goodbye!");

	}

}
