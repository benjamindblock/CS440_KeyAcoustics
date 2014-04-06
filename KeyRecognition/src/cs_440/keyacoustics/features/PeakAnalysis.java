package cs_440.keyacoustics.features;
import jAudioFeatureExtractor.jAudioTools.FFT;

import java.math.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class PeakAnalysis {

	double[] audioData; //This is our original audio data that is inputted to us.
	ArrayList<List<Double>> samples; //Our array list of lists. Each list contained within "samples"
									 //is a "window" of audio data. Each window is 100 samples long (~2ms).
	private ArrayList<List<Double>> mfcc; //Our array list of lists. Each list contained within "mfcc" is a 40ms
								  //window of audio data that contains the information for the hit peak of
								  // the audio signal, which we can then pass to our ComputeMFCC class.
	
	/**
	 * Declaring variables to allow for fine-tuning of peak-analysis characteristics.
	 */
	private final int PEAK_WIDTH = -10; //This describes the number of windows on either side of the peak to MFCC.
	private double thresh_value = 1.2; //How sensitive we want our threshold. A lower THRESH_VALUE will mean
											  //a tighter threshold, but this could exclude peaks that we need.
	private final int KEY_PRESS_SPACE = 100; //How much space between peaks we want to assert. Each increase +1 in the
											//int is ~2ms in audio data. So a value of 50 is 100ms.
	private final boolean USE_DELTA_VECTORS = true; //Do we want to use delta vectors or just the normalized values?
	
	/**
	 * Potential class to use to avoid using HashMaps. Not yet implemented.
	 * 
	 * @author Ben
	 *
	 */
	private class Peak{
		
		private int location;
		private double magnitude;
		
		private Peak(int location, double magnitude){
			this.location = location;
			this.magnitude = magnitude;
		}
		
		private int getLocation(){
			return location;
		}
		
		private double getMagnitude(){
			return magnitude;
		}
		
		
	}
	
	/**
	 * Constructor takes an input of audio data and initializes our global variables.
	 * 
	 * @param audioData Our audio data that we are analyzing
	 */
	public PeakAnalysis(double[] audioData){
		this.audioData = audioData;
		samples = new ArrayList<List<Double>>();
		mfcc = new ArrayList<List<Double>>();
	}
	
	public void setThreshold(double newThresh){
		thresh_value = newThresh;
	}
	
	
	/**
	 * Runs the whole Peak Finder algorithm.
	 * 
	 * Returns the number of peaks.
	 */
	public int run(){
		HashMap<Integer, Double> peaks;
		if(USE_DELTA_VECTORS){ //Use delta vectors to find peaks and such.
			split();
			double[] normalized = normalize(doFFT());
			double[] vector = deltaVector(normalized);
			double thresh = computeThreshold(vector);
			peaks = findPeaks(thresh, vector);
			setMFCC(peaks);
//			ArrayList<Peak> peaks = getPeaks(thresh, vector);
//			setUpMFCC(peaks);

			System.out.println("Threshold is "+thresh);
			System.out.println("Number of peaks is "+peaks.size());
			//System.out.println("Peaks are: "+peaks.toString());
			//System.out.println(mfcc.size());
		}else{ //Do not use delta vectors, just normalized data.
			split();
			double[] normalized = normalize(doFFT());
			double thresh = computeThreshold(normalized);
			peaks = findPeaks(thresh, normalized);
			setMFCC(peaks);
//			ArrayList<Peak> peaks = getPeaks(thresh, normalized);
//			setUpMFCC(peaks);
			
			System.out.println("Threshold is "+thresh);
			System.out.println("Number of peaks is "+peaks.size());
//			System.out.println("Peaks are: "+peaks.toString());
//			System.out.println(mfcc.size());
//			
		}
		return peaks.size();
	}
	
	/**
	 * The split method takes our audio data and splits it up into 100 sample chunks (~2ms).
	 * Each of these chunks is a list of doubles, and then each of these lists is placed
	 * into our global sample bank called "samples."
	 */
	private void split(){
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
		
		//System.out.println("audioData's length is: "+audioData.length+"\nsamples's length is:"+samples.size());
		
	}
	
	/**
	 * This method performs the FFT operations needed for our algorithm. This takes two steps.
	 * 
	 * 1. Take 
	 * @return
	 */
	private ArrayList<Double> doFFT(){
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
	private double sumFFT(FFT input){
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
	private double[] normalize(ArrayList<Double> input){
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
		
		//System.out.println("Max is "+max+" at position "+maxPos+" and Min is "+min+" at position "+minPos);
		
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
	private double[] deltaVector(double[] input){
		double[] vector = new double[input.length];
		for(int x = 0; x < input.length; x++){
			if(x < input.length - 1){
				if(input[x] - input[x+1] < 0){
					double difference = input[x] - input[x+1];
					difference = Math.abs(difference);
					vector[x] = difference;
				}
			}
		}
		return vector;
	}
	
	/**
	 * Takes a double[] and sets a threshold.
	 * To do this, we take the input of audio data that has been normalized between 0 and 1, 
	 * find the max, and compute a threshold that is a ratio of that peak as defined by THRESH_VALUE.
	 * 
	 * @param input
	 * @return A double that is the threshold, everything less than which we exclude.
	 */
	private double computeThreshold(double[] input){
		double threshold = 0.0;
		double max = 0.0;
		
		for(int x = 0; x < input.length; x++){
			if(input[x] > max){
				max = input[x];
			}
		}
		
		threshold = max/thresh_value;
		return threshold;
	}
	
	/**
	 * A version that does not use the new Peak class and uses a HashMap instead.
	 * 
	 * @param threshold The threshold we will use to roughly detect peaks.
	 * @param input The normalized audio data that we are picking the peaks out of.
	 * @return A HashMap that maps the peaks position its energy value
	 */
	private HashMap<Integer, Double> findPeaks(double threshold, double[] input){
		HashMap<Integer, Double> potentialPeaks = new HashMap<Integer, Double>();
		for(int x = 0; x < input.length; x++){
			if(input[x] >= threshold){
				potentialPeaks.put(x, input[x]);
			}
		}
		
		//Put in here a check to see if any two values (positions in the audioData array) are within
		//100ms of each other, if they are, then pick the highest one because this is the real hit peak.
		//As shown in "Keyboard Acoustic Emanations Revisited" (p. 3:5), key presses typically occur 
		//over the course of 100ms, and so there is generally 100ms or more between key presses. Thus,
		//we want to make sure we do not get more than one "peak" in the space of 100ms, otherwise we
		//would be computing the MFCC values for two peaks that are really part of the same keypress.
		//KEY_PRESS_SPACE allows us to change the value of 100ms to whatever we want.
		Set<Integer> positions = potentialPeaks.keySet();
		ArrayList<Integer> removals = new ArrayList<Integer>();
		for(Integer pos : positions){
			for(Integer pos2 : positions){
				if(pos != pos2){
					int pDifference = Math.abs(pos - pos2);
					if(pDifference < KEY_PRESS_SPACE){
						if(potentialPeaks.get(pos) > potentialPeaks.get(pos2)){
							removals.add(pos2); //Make a "note" to remove this non-peak from the peak hashmap
						}else{
							removals.add(pos); 
						}
					}
				}
			}
		}
		
		for(Integer remove : removals){
			potentialPeaks.remove(remove);
		}
		
		return potentialPeaks;
	}
	
	/**
	 * A version that uses the new Peak class.
	 * 
	 * @param threshold The threshold we will use to roughly detect peaks.
	 * @param input The normalized audio data that we are picking the peaks out of.
	 * @return A HashMap that maps the peaks position its energy value
	 */
//	private ArrayList<Peak> getPeaks(double threshold, double[] input){
//		ArrayList<Peak> potentialPeaks = new ArrayList<Peak>();
//		
//		for(int x = 0; x < input.length; x++){
//			if(input[x] >= threshold){
//				potentialPeaks.add(new Peak(x, input[x]));
//			}
//		}
//		
//		ArrayList<Peak> removals = new ArrayList<Peak>();
//		
//		for(int x = 0; x < potentialPeaks.size()-1; x++){
//			int location1 = potentialPeaks.get(x).getLocation();
//			int location2 = potentialPeaks.get(x+1).getLocation();
//			if(location1 != location2){
//				int difference = Math.abs(location1 - location2);
//				if(difference < KEY_PRESS_SPACE){
//					if(potentialPeaks.get(x).getMagnitude() > potentialPeaks.get(x+1).getMagnitude()){
//						removals.add(potentialPeaks.get(x+1));
//					}else{
//						removals.add(potentialPeaks.get(x));
//					}
//				}
//			}
//		}
//		
//		for(Peak remove : removals){
//			potentialPeaks.remove(remove);
//		}
//		
//		return potentialPeaks;
//	}
	
	/**
	 * From the point of the peak, which is in the middle of the entire push peak, we want to calculate
	 * the MFCC from 20ms before the peak and 20ms after, which should cover the entire push peak.
	 * 
	 * 1. Get the sample chunks out of "samples" for the location of the peak.
	 * 2. Get the sample chunks for every window 10 locations before and 10 locations after
	 * the peak, which will get us the entire push peak.
	 * 3. Recombine these into a single List<Double> that is the raw audio data for one keypress's peak.
	 * 4. Put this List into the larger list that contains all the peak's for the whole input file.
	 * 
	 * @param input
	 */
	private void setMFCC(HashMap<Integer, Double> input){
		Set<Integer> locations = input.keySet();
		for(Integer loc : locations){ //Our very outer loop that will go through all the peak positions.
			List<Double> addTo = new ArrayList<Double>(); //Create the list that will hold all of the data for these
														  //twenty windows of audio
			
			for(int x = PEAK_WIDTH; x < Math.abs(PEAK_WIDTH); x++){ //Our loop to go to ten windows before and ten after
				for(int y = 0; y < 100; y++){ //Our loop to get all 100 samples from each window
					addTo.add(samples.get(loc+x).get(y)); //Put each sample into our ArrayList that holds all data
				}	
			}
			
			mfcc.add(addTo); //Put this peak data into the master MFCC "to do" array that we will then give to 
							 //our MFCC class
		}
		
	}
	
	/**
	 * From the point of the peak, which is in the middle of the entire push peak, we want to calculate
	 * the MFCC from 20ms before the peak and 20ms after, which should cover the entire push peak.
	 * 
	 * 1. Get the sample chunks out of "samples" for the location of the peak.
	 * 2. Get the sample chunks for every window 10 locations before and 10 locations after
	 * the peak, which will get us the entire push peak.
	 * 3. Recombine these into a single List<Double> that is the raw audio data for one keypress's peak.
	 * 4. Put this List into the larger list that contains all the peak's for the whole input file.
	 * 
	 * @param input
	 */
//	private void setUpMFCC(ArrayList<Peak> input){
//		for(int x = 0; x < input.size(); x++){ //Our very outer loop that will go through all the peak positions.
//			List<Double> addTo = new ArrayList<Double>(); //Create the list that will hold all of the data for these
//														  //twenty windows of audio
//			
//			for(int y = PEAK_WIDTH; y < Math.abs(PEAK_WIDTH); y++){ //Our loop to go to ten windows before and ten after
//				for(int z = 0; z < 100; z++){ //Our loop to get all 100 samples from each window
//					addTo.add(samples.get(input.get(x).getLocation()+y).get(z)); //Put each sample into our ArrayList that holds all data
//				}	
//			}
//			
//			mfcc.add(addTo); //Put this peak data into the master MFCC "to do" array that we will then give to 
//							 //our MFCC class
//		}
//		
//	}
	
	public ArrayList<List<Double>> getMFCC(){
		return mfcc;
	}
}
