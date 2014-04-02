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
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Class that imports the text that corresponds with the audio for our training data.
 * 
 * @author Ben
 *
 */

public class TextStream {
	private static FileNameExtensionFilter filter = new FileNameExtensionFilter("Plain text files, (.txt)", "txt");
	private static JFileChooser fileChooser = new JFileChooser();
	private static File inFile;
	public static ArrayList<Character> characterList = new ArrayList<Character>();
	public static ArrayList<Word> wordList = new ArrayList<Word>();

	public static void textReader() throws IOException{
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


	private static void handleCharacters(File file, Charset encoding, int type) throws IOException {
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
	
	private static void createWordList(BufferedReader reader)
			throws IOException {
		String s;
		while ((s = reader.readLine()) != null) {
			String st = (String) s;
			System.out.println("Word: "+st);
			wordList.add(new Word(st));
		}
	}

	private static void createCharacterList(Reader reader)
			throws IOException {
		int r;
		while ((r = reader.read()) != -1) {
			char ch = (char) r;
			characterList.add(ch);
		}
	}
	
	public static ArrayList<Character> getArray(){
		return characterList;
	}
	
}
