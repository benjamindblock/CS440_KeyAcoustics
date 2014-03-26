import java.util.ArrayList;

public class StandardDeviationCalculator {
	private ArrayList<double[][]> constructorInput; 
	private final boolean USE_SAMPLE_DEVIATION = true;

	public StandardDeviationCalculator(ArrayList<double[][]> mfccInput){
		constructorInput = mfccInput;
	}
	
	public double [] standardDeviation(double [][] input){
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
		//System.out.print("Mean: "+ret[0]+"\n");
		//System.out.print("Standard Deviation: "+ret[1]+"\n");
		return ret;
	} 
	
	public ArrayList<double []> run(){
		ArrayList<double []> ret =  new ArrayList<double []>();
		for(int i = 0; i<constructorInput.size(); i++){
			ret.add(standardDeviation(constructorInput.get(i)));
		}
		return ret;
	}
}