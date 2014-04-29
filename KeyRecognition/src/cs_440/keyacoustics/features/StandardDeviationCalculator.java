package cs_440.keyacoustics.features;
import java.util.ArrayList;
import java.util.List;

public class StandardDeviationCalculator {
	private ArrayList<double[][]> mfccConstructorInput; 
	private ArrayList<List<Double>> fftConstructorInput;
	private final boolean USE_SAMPLE_DEVIATION = true;

	public StandardDeviationCalculator(ArrayList<double[][]> mfccInput){
		mfccConstructorInput = mfccInput;
	}
	
	/*
	 * This is the constructor for when we want both mfcc and fft values being considered.
	 */
	
	public StandardDeviationCalculator(ArrayList<double[][]> mfccInput, ArrayList<List<Double>> fftInput) {
		mfccConstructorInput = mfccInput;
		fftConstructorInput = fftInput;
	}
	
	
	
	public double [] mfccStandardDeviation(double [][] input){
		double [] ret = new double [2];
		double sum = 0;
		double count = 0;
		for(int i = 0; i<input.length; i++){
			for(int k = 0; k<input[i].length; k++){
				sum = sum + input[i][k];
				count++;
			}
		}
		double mean = sum/count;
		double sqDiffSum = 0;
		for(int i = 0; i<input.length; i++){
			for(int k = 0; k<input[i].length; k++){
				double diff = (mean - input[i][k]);
				diff = diff * diff;
				sqDiffSum = sqDiffSum + diff;
			}
		}
		double variance;
		if(USE_SAMPLE_DEVIATION){
			variance = sqDiffSum/(count-1);
			//System.out.println(sqDiffSum +"/"+ (count-1));
			//System.out.println("Variance: "+variance);
			
		}else{
			variance = sqDiffSum/(count);
			//System.out.println(sqDiffSum +"/"+ (count));
			//System.out.println("Variance: "+variance);
		}
		ret[0] = mean;
		ret[1] = Math.sqrt(variance);
//		System.out.print("Mean: "+ret[0]+"\n");
//		System.out.print("Standard Deviation: "+ret[1]+"\n");
		return ret;
	} 
	
	public double [] fftStandardDeviation(List <Double> input){
		double [] ret = new double [2];
		double sum = 0;
		double count = 0;
		for(int i = 0; i<input.size(); i++){
			sum = sum + input.get(i);
			count++;
		}
		double mean = sum/count;
		double sqDiffSum = 0;
		for(int i = 0; i<input.size(); i++){
			double diff = (mean - input.get(i));
			diff = diff * diff;
			sqDiffSum = sqDiffSum + diff;
			
		}
		double variance;
		if(USE_SAMPLE_DEVIATION){
			variance = sqDiffSum/(count-1);
			//System.out.println(sqDiffSum +"/"+ (count-1));
			//System.out.println("Variance: "+variance);
			
		}else{
			variance = sqDiffSum/(count);
			//System.out.println(sqDiffSum +"/"+ (count));
			//System.out.println("Variance: "+variance);
		}
		ret[0] = mean;
		ret[1] = Math.sqrt(variance);
//		System.out.print("Mean: "+ret[0]+"\n");
//		System.out.print("Standard Deviation: "+ret[1]+"\n");
		return ret;
	} 
	
	public ArrayList<double []> runMFCCStandardDeviation(){
		ArrayList<double []> ret =  new ArrayList<double []>();
		for(int i = 0; i<mfccConstructorInput.size(); i++){
			ret.add(mfccStandardDeviation(mfccConstructorInput.get(i)));
		}
		return ret;
	}
	
	public ArrayList<double []> runFFTStandardDeviation(){
		ArrayList<double []> ret =  new ArrayList<double []>();
		for(int i = 0; i<fftConstructorInput.size(); i++){
			ret.add(fftStandardDeviation(fftConstructorInput.get(i)));
		}
		return ret;
	}
}