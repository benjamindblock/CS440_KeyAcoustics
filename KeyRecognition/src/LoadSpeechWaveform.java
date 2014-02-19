

import jAudioFeatureExtractor.ACE.DataTypes.FeatureDefinition;
import jAudioFeatureExtractor.AudioFeatures.MagnitudeSpectrum;
import jAudioFeatureExtractor.AudioFeatures.PeakFinder;

import java.io.File;
import java.io.FileNotFoundException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import java.io.File;
import java.io.FileNotFoundException;
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

	
	public static void main(String[] args) throws FileNotFoundException{
		double[] audioData = fileReader(); //Get our first audio file
		double[] audioData2 = fileReader(); //Get our second audio file (delete when done and we only have one)
		
		PeakAnalysis pa = new PeakAnalysis(audioData);
		pa.split();
		pa.normalize(pa.doFFT());
		
		//Find the peaks in our .wav file. We may need to tweak the PeakFinder class and adjust the threshold 
		//to get the proper amount of peaks that we need (ie. not too sensitive a threshold such that there are
		//an abundance of peaks and the data no longer is relevant).
		ComputePeakFinder cpf = new ComputePeakFinder(audioData);
		double[] audioPeaks = cpf.compute();
		
		//Now send the audioData and the peakData to our class that will split up the audio
		//and then send back a bunch of new .wav files that we will individually scan for
		//MFCC's
		
		//Find the mfcc in our .wav file.
		ComputeMFCC mfcc = new ComputeMFCC(audioData); //Create a new instance of ComputeMFCC and pass it our double array.
		ComputeMFCC mfcc2 = new ComputeMFCC(audioData2); 
		double[][] featureValues = mfcc.run(); //Compute MFCC for audio sample 1
		double[][] featureValues2 = mfcc2.run(); //Compute MFCC for audio sample 2
		boolean equal = Arrays.deepEquals(featureValues, featureValues2); //Compare
		System.out.println(equal);
	}
}
