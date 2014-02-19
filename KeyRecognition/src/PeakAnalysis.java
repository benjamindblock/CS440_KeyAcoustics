import jAudioFeatureExtractor.jAudioTools.FFT;

import java.util.ArrayList;
import java.util.List;


public class PeakAnalysis {

	double[] audioData;
	ArrayList<List<Double>> samples;
	
	public PeakAnalysis(double[] audioData){
		this.audioData = audioData;
		samples = new ArrayList<List<Double>>();
	}
	
	public void split(){
		int globalCounter = 0;
		int arrayCounter = 0;
		
		while(globalCounter < audioData.length){
			List<Double> addTo = new ArrayList<Double>();
			while(arrayCounter < 100 && globalCounter < audioData.length){
				addTo.add(audioData[globalCounter]);
				globalCounter = globalCounter + 1;
				arrayCounter = arrayCounter + 1;
			}
			samples.add(addTo);
			arrayCounter = 0;
		}
		
		System.out.println("audioData's length is: "+audioData.length+"\nsamples's length is:"+samples.size());
		
	}
	
	public ArrayList<Double> doFFT(){
		ArrayList<Double> energies = new ArrayList<Double>();
		
		for(int x = 0; x < samples.size(); x++){
			List<Double> data = samples.get(x);
			Double[] input = data.toArray(new Double[data.size()]);
			double[] fftInput = new double[input.length];
			//Loop through to convert Double[] to double[]
			for(int y = 0; y < input.length; y++){
				fftInput[y] = input[y];
			}
			
			try {
				FFT fft = new FFT(fftInput, null, false, false);
				double value = sumFFT(fft);
				energies.add(value);
				System.out.println(x+": "+value);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("FFT failed in class PeakAnalysis.java");
				e.printStackTrace();
			}
		}
		
		return energies;
	}
	
	public double sumFFT(FFT input){
		double fftEnergy = 0;
		double[] realValues = input.getRealValues();
		
		for(int x = 0; x < realValues.length; x++){
			fftEnergy = fftEnergy + realValues[x];
		}
		
		return fftEnergy;
	}
	
	public double[] normalize(ArrayList<Double> input){
		double[] output = new double[input.size()];
		
		//Finding the max and min
		double max = input.get(0);
		double min = input.get(0);
		
		for(int x = 0; x < input.size(); x++){
			if(input.get(x) > max){
				max = input.get(x);
			}else if(input.get(x) < min){
				min = input.get(x);
			}
		}
		
		System.out.println("Max is "+max+" and Min is "+min);
		
		//Now normalize with those results.
		for(int x = 0; x < input.size(); x++){
			output[x] = ((input.get(x)-min)/(max-min));
			System.out.println(x+"'s normalized value is "+output[x]);
		}
		return output;
	}
	
	public double[] deltaVector(double[] input){
		double[] vector = new double[input.length];
		return vector;
	}
}