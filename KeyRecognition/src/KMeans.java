import java.util.ArrayList;
import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;

public class KMeans{
	
	private final int NO_OF_SEQUENCES = 10;
	private final int NO_OF_KEYS = 7; //Number of keys that we are processing
	private final int NO_OF_TESTS = 30; //Number of times each key is pressed in training data.
	
	private Hmm hmm;
	private Hmm[] allModels;
	
	public KMeans(ArrayList<double[]> mfccInput){
		
//		ArrayList< List <ObservationVector>> kMeansSequences = new ArrayList< List<ObservationVector>>();
//		ArrayList<ObservationVector> oV = new ArrayList<ObservationVector>();
//		for(int i = 0; i<mfccInput.size(); i++){
//			oV.add(new ObservationVector(mfccInput.get(i)));
//			System.out.println("Count: "+i);
//		}
//		for(int k = 0; k<NO_OF_SEQUENCES; k++){
//			kMeansSequences.add(oV);
//		}
//		
//		KMeansLearner kml = new KMeansLearner(5, new OpdfMultiGaussianFactory(2), kMeansSequences);
//		this.hmm = kml.learn();
		
		allModels = generateModels(mfccInput);
		
	}
	
	public Hmm[] generateModels(ArrayList<double[]> mfccInput){
		Hmm[] models = new Hmm[NO_OF_KEYS];
		ArrayList< List <ObservationVector>> kMeansSequences = new ArrayList< List<ObservationVector>>();
		ArrayList<ObservationVector> oV = new ArrayList<ObservationVector>();
		
		int count = 0;
		while(count < mfccInput.size()){
			for(int j = 0; j < NO_OF_KEYS; j++){
				
				Hmm addTo;
				
				for(int k = 0; k < NO_OF_TESTS; k++){
					oV.add(new ObservationVector(mfccInput.get(count)));
					count++;
				}
				
				for(int k = 0; k<NO_OF_SEQUENCES; k++){
					kMeansSequences.add(oV);
				}
				
				KMeansLearner kml = new KMeansLearner(5, new OpdfMultiGaussianFactory(2), kMeansSequences);
				addTo = kml.learn();
				
				models[j] = addTo;
			}
		}
	
		return models;
	}
	
	public Hmm[] getModels(){
		return allModels;
	}
}

