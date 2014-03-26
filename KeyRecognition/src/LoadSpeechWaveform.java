

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.AudioFeatures.MagnitudeSpectrum;
import jAudioFeatureExtractor.AudioFeatures.PeakFinder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.filechooser.*;


public class LoadSpeechWaveform{
	static FileNameExtensionFilter filter = new FileNameExtensionFilter("Speech waveform files, (.wav)", "wav");
	static JFileChooser fileChooser = new JFileChooser();
	static File inFile;
	private static final double MAX_16_BIT = Short.MAX_VALUE;     // 32,767

	
	public static double[] fileReader() throws FileNotFoundException{
		fileChooser.setFileFilter(filter);
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			throw new Error("Input file not selected");
		inFile = fileChooser.getSelectedFile();
		double[] audioData = read(inFile);
		System.out.println("Opened:" + inFile.getName());
		
		return audioData;
	} 
	 
	 /**
     * Read audio samples from a file (in .wav or .au format) and return them as a double array
     * with values between -1.0 and +1.0.
     * 
     * Code taken from: www.introcs.cs.princeton.edu/java/stdlib/StdAudio.java
     */
    public static double[] read(File file) {
        byte[] data = readByte(file);
        int N = data.length;
        System.out.println("N: "+N+"\n");
        double[] d = new double[N/2];
        for (int i = 0; i < N/2; i++) {
            d[i] = ((short) (((data[2*i+1] & 0xFF) << 8) + (data[2*i] & 0xFF))) / ((double) MAX_16_BIT);
        }
        return d;
    }
    
    /**
     * Return data as a byte array
     * 
     * Code taken from: www.introcs.cs.princeton.edu/java/stdlib/StdAudio.java
     * 
     * @param file Input file
     * @return
     */
    		
    private static byte[] readByte(File file) {
        byte[] data = null;
        AudioInputStream ais = null;
        try {

            // try to read from file
            if (file.exists()) {
                ais = AudioSystem.getAudioInputStream(file);
                data = new byte[ais.available()];
                ais.read(data);
            }

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Could not read " + file.getName());
        }

        return data;
    }


	
	public static void main(String[] args) throws IOException{
		TextStream.textReader();
		char [] textArray = TextStream.getArray();
		for(int i = 0; i < textArray.length; i++){
			System.out.println(textArray[i]);
		}
//		double[] trainingData = fileReader(); //Get our first audio file (training data).
//		double[] attackData = fileReader(); //Get our second audio file (audio we want to get text from).
//		
//		//Steps for our training data.
//		PeakAnalysis pa = new PeakAnalysis(trainingData); 
//		pa.run(); //Run our peak analysis.
//		ComputeMFCC cm = new ComputeMFCC(pa.getMFCC()); 
//		cm.run(); //Run our MFCC calculations
//		System.out.println(cm.getMFCCOutput().size());
//		ArrayList<double[][]> mfcc = cm.getMFCCOutput();
//		StandardDeviationCalculator sdc = new StandardDeviationCalculator(mfcc);
//		ArrayList<double[]> mfccData = sdc.run();
//		KMeans km = new KMeans(mfccData);
//		
//		//Steps for our attack data.
//		PeakAnalysis pa2 = new PeakAnalysis(attackData); 
//		pa2.run(); //Run our peak analysis.
//		ComputeMFCC cm2 = new ComputeMFCC(pa2.getMFCC()); 
//		cm2.run(); //Run our MFCC calculations
//		System.out.println(cm2.getMFCCOutput().size());
//		ArrayList<double[][]> mfcc2 = cm2.getMFCCOutput();
//		StandardDeviationCalculator sdc2 = new StandardDeviationCalculator(mfcc2);
//		ArrayList<double[]> mfccData2 = sdc2.run();
//		
//		TextRetrieval tr = new TextRetrieval(km.getModels(), mfccData2);
//		System.out.println("You typed: " + tr.getUtterance() + " ?");
//		
//		//Try jAudio's PeakFinder algorithm
////		ComputePeakFinder cpf = new ComputePeakFinder(audioData);
////		double[] peaks = cpf.compute();
////		System.out.println("Using jAudio's peak finder, the number of peaks is: "+peaks.length);
////		System.out.println("Peak locations: ");
////		for(int x = 0; x < peaks.length; x++){
////			System.out.println("Peak "+x+": "+peaks[x]);
////		}
//				
	}
}
