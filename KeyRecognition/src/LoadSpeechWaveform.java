

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
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.*;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.Perceptron;
import org.neuroph.util.TransferFunctionType;

import cs_440.keyacoustics.dictionary.TextStream;
import cs_440.keyacoustics.features.ComputeMFCC;
import cs_440.keyacoustics.features.PeakAnalysis;
import cs_440.keyacoustics.features.StandardDeviationCalculator;
import cs_440.keyacoustics.features.TrainNetworks;
import cs_440.keyacoustics.neuralnetwork.LeftRightNeuralNetwork;
import cs_440.keyacoustics.neuralnetwork.NearFarNeuralNetwork;
import cs_440.keyacoustics.neuralnetwork.WordMatch;
import cs_440.keyacoustics.neuralnetwork.WordProfile;


public class LoadSpeechWaveform{
	static FileNameExtensionFilter filter = new FileNameExtensionFilter("Speech waveform files, (.wav)", "wav");
	static JFileChooser fileChooser = new JFileChooser();
	static File inFile;
	private static final double MAX_16_BIT = Short.MAX_VALUE;     // 32,767
	private final static boolean USESAVEDNETWORKS = true;
	private static final String FILE_PATH_LR = "/Users/walkerbohannan/Documents/GitHub/CS440_KeyAcoustics/NeuralNetworks/perceptron_lr.nnet";
	private static final String FILE_PATH_NF = "/Users/walkerbohannan/Documents/GitHub/CS440_KeyAcoustics/NeuralNetworks/perceptron_nf.nnet";


	public static double[] fileReader(String audioType) throws FileNotFoundException{
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle("Select file for "+audioType+".");
		if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
			throw new Error("Input file not selected");
		inFile = fileChooser.getSelectedFile();
		double[] audioData = read(inFile);
		System.out.println("Opened: " + inFile.getName());

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
//		System.out.println("Size in bytes of file: "+N+"\n");
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

	 public static double determineThreshold(int numOfCharacters, double[] trainingData){
	    	System.out.println("Setting threshold (this may take a while)...");
	    	double increment = 10;
	    	boolean addConsecutive = true;
	    	boolean minusConsecutive = true;
	    	double newThresh = 0;
	    	PeakAnalysis pa = new PeakAnalysis();
	    	pa.setThreshold(newThresh);
	    	pa.run(trainingData);
	    	while(numOfCharacters != pa.getNumOfPeaks()){
	    		if(numOfCharacters > pa.getNumOfPeaks()){
	    			if(addConsecutive) {
	    				newThresh = newThresh + increment;
	    			} else {
	    				increment = increment/2;
	    				//System.out.println("Addition increment changed! New increment is "+increment);
	    				newThresh = newThresh + increment;
	    			}
	    			pa = new PeakAnalysis();
	        		pa.setThreshold(newThresh);
	        		pa.run(trainingData);
	        		minusConsecutive = false;
	        		addConsecutive = true;
	        		System.out.println(newThresh);
	        		
	    		} else if (numOfCharacters < pa.getNumOfPeaks()){
	    			if(minusConsecutive && (newThresh - increment) > 0) {
	    				newThresh = newThresh - increment;
	    			} else {
	    				increment = increment/2;
	    				//System.out.println("Minus increment changed! New increment is "+increment);
	    				newThresh = newThresh - increment;
	    			}
	    			pa = new PeakAnalysis();
	        		pa.setThreshold(newThresh);
	        		pa.run(trainingData);
	        		minusConsecutive = true;
	        		addConsecutive = false;
	        		System.out.println(newThresh);	
	    		}
	    		//}
	    	}
	    	System.out.println("Success! The threshold was set at "+newThresh+" for "+numOfCharacters+" number of characters.");
	    	return newThresh;
	    }

	public static void main(String[] args) throws IOException{
		//		Text Stream call
		TextStream ts = new TextStream();
		ts.textReader();
		//Get our first audio file (training data).
		
		//Steps for our training data.
		double[] trainingData = fileReader("training data"); 
		int numOfCharacters = ts.getNumOfCharacters();
		double threshold = determineThreshold(numOfCharacters, trainingData);
		PeakAnalysis pa = new PeakAnalysis();
		pa.setThreshold(threshold);
		pa.run(trainingData);
		ArrayList <List <Double>> fft = pa.getMFCC();
		ComputeMFCC cm = new ComputeMFCC(fft); 
		cm.run(); //Run our MFCC calculations
		ArrayList<double[][]> mfcc = cm.getMFCCOutput();
		StandardDeviationCalculator sdc = new StandardDeviationCalculator(mfcc, fft);
		ArrayList<double[]> mfccData = sdc.runMFCCStandardDeviation();
		ArrayList<double[]> fftData = sdc.runFFTStandardDeviation();
		
		ArrayList<double[]> masterData = new ArrayList<double[]>();
		//START HERE
		if(mfccData.size() == fftData.size()){
			for(int i = 0; i < mfccData.size(); i++){
				masterData.add(new double []{mfccData.get(i)[1], fftData.get(i)[1]});
			}
		}
		
		
		System.out.println("Training neural networks...");
		if(USESAVEDNETWORKS){
			System.out.println("Using previously saved networks :\n\t"+FILE_PATH_LR+"\n\t"+FILE_PATH_NF);
			TrainNetworks tn = new TrainNetworks(ts.getArray(), masterData);
			LeftRightNeuralNetwork lrnnet = new LeftRightNeuralNetwork(FILE_PATH_LR);
			NearFarNeuralNetwork nfnnet = new NearFarNeuralNetwork(FILE_PATH_NF);
			tn.trainLeftRightNeuralNetwork(lrnnet);
			tn.trainNearFarNeuralNetwork(nfnnet);
			lrnnet.saveNetwork(FILE_PATH_LR);
			nfnnet.saveNetwork(FILE_PATH_NF);
		}else{
			System.out.println("Creating a new network :\n\t"+FILE_PATH_LR+"\n\t"+FILE_PATH_NF);
			TrainNetworks tn = new TrainNetworks(ts.getArray(), masterData);
			NeuralNetwork<SupervisedLearning> lrNeuralNetwork = new Perceptron(2, 1, TransferFunctionType.STEP);
			NeuralNetwork<SupervisedLearning> nfNeuralNetwork = new Perceptron(4, 1, TransferFunctionType.STEP);
			LeftRightNeuralNetwork lrnnet = new LeftRightNeuralNetwork(lrNeuralNetwork);
			NearFarNeuralNetwork nfnnet = new NearFarNeuralNetwork(nfNeuralNetwork);
			tn.trainLeftRightNeuralNetwork(lrnnet);
			tn.trainNearFarNeuralNetwork(nfnnet);
			lrnnet.saveNetwork(FILE_PATH_LR);
			nfnnet.saveNetwork(FILE_PATH_NF);
		}
		
		//Steps for our attack data.
		double[] attackData = fileReader("attack data"); //Get our second audio file (audio we want to get text from).
		//threshold = determineThreshold(5, attackData);
		PeakAnalysis pa2 = new PeakAnalysis(); 
		//double hello = 1.31;
		//double dad = ;
		//double aged = ;
		pa2.setThreshold(1.31);
		pa2.run(attackData);
		ComputeMFCC cm2 = new ComputeMFCC(pa2.getMFCC()); 
		cm2.run(); //Run our MFCC calculations
		ArrayList<double[][]> mfcc2 = cm2.getMFCCOutput();
		StandardDeviationCalculator sdc2 = new StandardDeviationCalculator(mfcc2, pa2.getMFCC());
		ArrayList<double[]> mfccData2 = sdc2.runMFCCStandardDeviation();
		ArrayList<double[]> fftData2 = sdc2.runFFTStandardDeviation();
		
		ArrayList<double[]> masterData2 = new ArrayList<double[]>();
		//START HERE
		if(mfccData2.size() == fftData2.size()){
			for(int i = 0; i < mfccData2.size(); i++){
				masterData2.add(new double []{mfccData2.get(i)[1], fftData2.get(i)[1]});
			}
		}
		
		System.out.println(mfccData2.get(0)[0]);
		WordMatch wm = new WordMatch(new WordProfile(masterData2, new LeftRightNeuralNetwork(FILE_PATH_LR), new NearFarNeuralNetwork(FILE_PATH_NF)));
		System.out.println("All done.");
		
		//System.out.println("Predicted word is :"+wm.findWord());
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
