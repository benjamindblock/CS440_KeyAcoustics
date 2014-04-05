package cs_440.keyacoustics.features;
import java.util.ArrayList;
import java.util.List;

import org.oc.ocvolume.dsp.mfccExtraction;

public class ComputeMFCC {

	private ArrayList<List<Double>> mfccInput;
	private ArrayList<double[][]> mfccOutput;
	
	/**
	 * Constructor for our class that will run the MFCC computation by accessing the jAudio
	 * library.
	 * 
	 * @param audioInput The audio input that we are getting from LoadSpeechWaveform, which
	 * automatically converts the inputed audio file into a double[].
	 */
	public ComputeMFCC(ArrayList<List<Double>> mfccInput){
		this.mfccInput = mfccInput;
		mfccOutput = new ArrayList<double[][]>();
	}
	
	/**
	 * Give the user our MFCC data.
	 * 
	 * @return Returns the ArrayList holding all of our MFCC's.
	 */
	public ArrayList<double[][]> getMFCCOutput(){
		return mfccOutput;
	}
	
	/**
	 * This method runs the MFCC operations and returns the data!
	 */
	public void run(){
		for(int x = 0; x < mfccInput.size(); x++){
			mfccOutput.add(compute(mfccInput.get(x)));
		}
	}
	
	/**
	 * 
	 * 
	 * @return This is a double[][] that contains the MFCC feature information, which is passed
	 * up from the computeMFCC method.
	 */
	private double[][] compute(List<Double> input){
		double[] converted = new double[input.size()];
		for(int x = 0; x < converted.length; x++){
			converted[x] = input.get(x);
		}
		double[][] mfcc = computeMFCC(converted);
		return mfcc;
	}
	
	
	/**
	 * This method calls the process function from featureExtraction.
	 * 
	 * @param audioData The short[] of audio data that we are getting the MFCC for.
	 * @return Returns a double[][] that contains the MFCC information.
	 */
	private double[][] computeMFCC(double[] audioData){
		double[][] audioOutput;
		mfccExtraction fe = new mfccExtraction();
		audioOutput = fe.process(audioData, 44100);
		return audioOutput;
	}

}
