import java.util.ArrayList;
import java.util.List;

import org.oc.ocvolume.dsp.featureExtraction;

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
		short[] newInput = convert(converted);
		double[][] mfcc = computeMFCC(newInput);
		return mfcc;
	}
	
	/**
	 * Takes the inputed double[] of audio information and converts it to a short[],
	 * which is the required input for featureExtraction's "process" method.
	 * 
	 * @param input The double[] that we wish to convert
	 * @return The converted short[]
	 */
	private short[] convert(double[] input){
		int bufferSize = input.length;
		double[] buffer = new double[bufferSize];
		short[] transformed = new short[bufferSize];

		for (int j=0;j<bufferSize;j++) {
		    transformed[j] = (short)buffer[j];
		}
		return transformed;
	}
	
	/**
	 * This method calls the process function from featureExtraction.
	 * 
	 * @param audioData The short[] of audio data that we are getting the MFCC for.
	 * @return Returns a double[][] that contains the MFCC information.
	 */
	private double[][] computeMFCC(short[] audioData){
		double[][] audioOutput;
		featureExtraction fe = new featureExtraction();
		audioOutput = fe.process(audioData, 44100);
		return audioOutput;
	}

}
