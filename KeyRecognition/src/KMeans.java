import java.util.ArrayList;

import be.ac.ulg.montefiore.run.jahmm.*;

public class KMeans {
	
	public KMeans(ArrayList<double[][]> mfccInput){
		
		KMeansCalculator kmeans = new KMeansCalculator(50, mfccInput);
	}
	
}
