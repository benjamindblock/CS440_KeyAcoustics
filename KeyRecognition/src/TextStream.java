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

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;



public class TextStream {
	static FileNameExtensionFilter filter = new FileNameExtensionFilter("Plain text files, (.txt)", "txt");
	static JFileChooser fileChooser = new JFileChooser();
	static File inFile;
	static char[] array = new char[633];

	public static void textReader() throws IOException{
		fileChooser.setFileFilter(filter);
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			throw new Error("Input file not selected");
		inFile = fileChooser.getSelectedFile();
		Charset encoding = Charset.defaultCharset();
		handleFile(inFile, encoding);
		System.out.println("Opened:" + inFile.getName());
	} 


	private static void handleFile(File file, Charset encoding) throws IOException {
		try (InputStream in = new FileInputStream(file);
				Reader reader = new InputStreamReader(in, encoding);
				// buffer for efficiency
				Reader buffer = new BufferedReader(reader)) {
			handleCharacters(buffer);
		}
	}

	private static void handleCharacters(Reader reader)
			throws IOException {
		int r;
		int counter = 0;
		while ((r = reader.read()) != -1) {
			char ch = (char) r;
			array[counter] = ch;
			counter++;
		}
	}
	
	public static char[] getArray(){
		return array;
	}
	
}
