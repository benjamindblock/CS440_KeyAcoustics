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



public class TextStream {
	private static FileNameExtensionFilter filter = new FileNameExtensionFilter("Plain text files, (.txt)", "txt");
	private static JFileChooser fileChooser = new JFileChooser();
	private static File inFile;
	public static ArrayList<Character> characterList = new ArrayList<Character>();
	public static ArrayList< ArrayList<String>> wordCharacterPairs = new ArrayList< ArrayList<String>>();

	public static void textReader() throws IOException{
		fileChooser.setFileFilter(filter);
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			throw new Error("Input file not selected");
		inFile = fileChooser.getSelectedFile();
		System.out.println("Opened:" + inFile.getName());
		Charset encoding = Charset.defaultCharset();
		handleCharacters(inFile, encoding);
	} 


	private static void handleCharacters(File file, Charset encoding) throws IOException {
		try (InputStream in = new FileInputStream(file);
				Reader reader = new InputStreamReader(in, encoding);
				// buffer for efficiency
				Reader buffer = new BufferedReader(reader)) {
			createCharacterList(buffer);
		}
	}

	private static void createCharacterList(Reader reader)
			throws IOException {
		int r;
		int counter = 0;
		while ((r = reader.read()) != -1) {
			char ch = (char) r;
			characterList.add(ch);
			counter++;
		}
	}
	
	public static ArrayList<Character> getArray(){
		return characterList;
	}
	
}
