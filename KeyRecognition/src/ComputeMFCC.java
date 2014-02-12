import org.oc.ocvolume.dsp.featureExtraction;

public class ComputeMFCC {

	public double[] audioInput; //Holds our audio data.
	
	/**
	 * Constructor for our class that will run the MFCC computation by accessing the jAudio
	 * library.
	 * 
	 * @param audioInput The audio input that we are getting from LoadSpeechWaveform, which
	 * automatically converts the inputed audio file into a double[].
	 */
	public ComputeMFCC(double[] audioInput){
		this.audioInput = audioInput;
	}
	
	/**
	 * This method runs the MFCC operations and returns the data!
	 * 
	 * @return This is a double[][] that contains the MFCC feature information, which is passed
	 * up from the computeMFCC method.
	 */
	public double[][] run(){
		short[] newInput = convert(audioInput);
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
	public short[] convert(double[] input){
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
	public double[][] computeMFCC(short[] audioData){
		double[][] audioOutput;
		featureExtraction fe = new featureExtraction();
		audioOutput = fe.process(audioData, 44100);
		return audioOutput;
	}

}
