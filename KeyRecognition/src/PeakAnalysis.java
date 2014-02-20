import jAudioFeatureExtractor.jAudioTools.FFT;
import java.math.*;

import java.util.ArrayList;
import java.util.List;


public class PeakAnalysis {

	double[] audioData; //This is our original audio data that is inputted to us.
	ArrayList<List<Double>> samples; //Our array list of lists. Each list contained within "samples"
									 //is a "window" of audio data. Each window is 100 samples long (~2ms).
	
	/**
	 * Constructor takes an input of audio data and initializes our global variables.
	 * 
	 * @param audioData Our audio data that we are analyzing
	 */
	public PeakAnalysis(double[] audioData){
		this.audioData = audioData;
		samples = new ArrayList<List<Double>>();
	}
	
	/**
	 * Runs the whole Peak Finder algorithm.
	 */
	public void run(){
		split();
		double[] normalized = normalize(doFFT());
		double[] vector = deltaVector(normalized);
		System.out.println(computeThreshold(vector));
	}
	
	/**
	 * The split method takes our audio data and splits it up into 100 sample chunks.
	 * Each of these chunks is a list of doubles, and then each of these lists is placed
	 * into our global sample bank called "samples."
	 */
	public void split(){
		int globalCounter = 0;
		int arrayCounter = 0;
		boolean needToPad = false;
		if(audioData.length % 100 < 4){
			needToPad = true;
		}
		
		while(globalCounter < audioData.length){
			List<Double> addTo = new ArrayList<Double>(); //Our ArrayList that we will add to samples.
			while(arrayCounter < 100 && globalCounter < audioData.length){
				addTo.add(audioData[globalCounter]); //Put our sample into the list to be added to samples.
				globalCounter = globalCounter + 1;
				arrayCounter = arrayCounter + 1;
			}
			samples.add(addTo); //Add our ArrayList (should contain 100 samples) to samples.
			arrayCounter = 0;
		}
		if(needToPad){
			samples.get(samples.size()-1).add((double) 0);
			samples.get(samples.size()-1).add((double) 0);
			System.err.println("Padding our array because it has less than 3 values.");
		}
		
		System.out.println("audioData's length is: "+audioData.length+"\nsamples's length is:"+samples.size());
		
	}
	
	/**
	 * This method performs the FFT operations needed for our algorithm. This takes two steps.
	 * 
	 * 1. Take 
	 * @return
	 */
	public ArrayList<Double> doFFT(){
		ArrayList<Double> energies = new ArrayList<Double>(); //This ArrayList holds the magnitude
															  //for each sample window. 
		
		for(int x = 0; x < samples.size(); x++){ //Looping through our entire list of windows of samples.
			List<Double> data = samples.get(x); //Get the next sample window from samples.
			
			//The following lines take the List<Double> and turn it into a double[].
			Double[] input = data.toArray(new Double[data.size()]); 
			double[] fftInput = new double[input.length];
			//Loop through to convert Double[] to double[]
			for(int y = 0; y < input.length; y++){
				fftInput[y] = input[y];
			}
			
			//Now that we have a double[] of our sample values, we send this to the FFT class.
			try {
				FFT fft = new FFT(fftInput, null, false, false);
				double value = sumFFT(fft); //Call our helper method.
				energies.add(value); //Put the summed FFT coefficient value into our energy ArrayList.
//				System.out.println(x+": "+value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("FFT failed in class PeakAnalysis.java");
				e.printStackTrace();
			}
		}
		
		return energies;
	}
	
	/**
	 * This method takes all of the FFT coefficients obtained for one sample window and sums them
	 * together to get the cumulative magnitude for that section of the audio.
	 * 
	 * @param input Our FFT coefficients
	 * @return A single value that is the summed value of all the FFT coefficients given to us.
	 */
	public double sumFFT(FFT input){
		double fftEnergy = 0;
		double[] realValues = input.getRealValues(); //Get all of the FFT coefficients.
		
		for(int x = 0; x < realValues.length; x++){
			fftEnergy = fftEnergy + realValues[x]; //Add 'em up.
		}
		
		return fftEnergy;
	}
	
	/**
	 * Takes an input of magnitude values and normalizes the array between 0 and 1.
	 * 
	 * @param input An ArrayList of magnitude values.
	 * @return A double[] of magnitude values normalized between 0 and 1.
	 */
	public double[] normalize(ArrayList<Double> input){
		double[] output = new double[input.size()];
		
		//Finding the max and min
		double max = input.get(0);
		int maxPos = 0;
		double min = input.get(0);
		int minPos = 0;
		
		for(int x = 0; x < input.size(); x++){
			if(input.get(x) > max){
				max = input.get(x);
				maxPos = x;
			}else if(input.get(x) < min){
				min = input.get(x);
				minPos = x;
			}
		}
		
		System.out.println("Max is "+max+" at position "+maxPos+" and Min is "+min+" at position "+minPos);
		
		//Now normalize with those results.
		for(int x = 0; x < input.size(); x++){
			output[x] = ((input.get(x)-min)/(max-min));
//			System.out.println(x+"'s normalized value is "+output[x]);
		}
		return output;
	}
	
	/**
	 * Takes the difference between each window and its predecessor. This will give us
	 * the  
	 * 
	 * @param input
	 * @return
	 */
	public double[] deltaVector(double[] input){
		double[] vector = new double[input.length];
		for(int x = 0; x < input.length; x++){
			if(x < input.length - 1){
				double difference = input[x] - input[x+1];
				difference = Math.abs(difference);
				vector[x] = difference;
				System.out.println("vector["+x+"] = "+vector[x]);
			}
		}
		return vector;
	}
	
	/**
	 * Takes a double[] and sets a threshold.
	 * 
	 * @param input
	 * @return A double that is the threshold, everything less than which we exclude.
	 */
	public double computeThreshold(double[] input){
		double threshold = 0.0;
		double max = 0.0;
		
		for(int x = 0; x < input.length; x++){
			if(input[x] > max){
				max = input[x];
			}
		}
		
		threshold = max/1.5;
		return threshold;
	}
}
