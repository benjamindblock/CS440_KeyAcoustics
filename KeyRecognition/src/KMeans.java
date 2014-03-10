import java.util.ArrayList;
import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;

public class KMeans{
	private final int NO_OF_SEQUENCES = 10;
	private Hmm hmm;
	
	
	public KMeans(ArrayList<double[]> mfccInput){
		
		ArrayList< List <ObservationVector>> kMeansSequences = new ArrayList< List<ObservationVector>>();
		ArrayList<ObservationVector> oV = new ArrayList<ObservationVector>();
		for(int i = 0; i<mfccInput.size(); i++){
			oV.add(new ObservationVector(mfccInput.get(i)));
			System.out.println("Count: "+i);
		}
		for(int k = 0; k<NO_OF_SEQUENCES; k++){
			kMeansSequences.add(oV);
		}
		
		KMeansLearner kml = new KMeansLearner(5, new OpdfMultiGaussianFactory(2), kMeansSequences);
		this.hmm = kml.learn();
		
	}
	
	public Hmm getHmm(){
		return hmm;
	}
}

