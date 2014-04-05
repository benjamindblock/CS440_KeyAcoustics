package cs_440.keyacoustics.hmmTests;
import java.util.ArrayList;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.ViterbiCalculator;


public class TextRetrieval {

	private Hmm[] models;
	private ArrayList<double[]> mfccInput;
	private String utterance;
	
	public TextRetrieval(Hmm[] models, ArrayList<double[]> mfccInput){
		this.models = models;
		this.mfccInput = mfccInput;
		
		utterance = runTextRetrieval();
	}
	
	private String runTextRetrieval(){
		String ret = "";
		
		for(int i = 0; i < mfccInput.size(); i++){
			String character = getChar(getPos(mfccInput.get(i)));
			ret = ret + character;
		}
		System.out.println("Interpreted text is: "+ret);
		return ret;
	}
	
	private String getChar(int pos){
		String character = "";
		pos = pos + 97;
		character = Character.toString ((char) pos);
		return character;
	}
	
	private int getPos(double[] mfccData){

		int pos = 0;
		double probability = 0.0;
		
		for(int i = 0; i < models.length; i++){
			double newProb = getProbability(models[i], mfccData);
			System.out.println("getPos() probability is: "+probability);
			if(probability < newProb){
				probability = newProb;
				pos = i;
			}
		}
		
		return pos;
	}
	
	private double getProbability(Hmm model, double[] mfccData){
		
		
		double probability = 0.0;
		ArrayList<ObservationVector> oSeq = new ArrayList<ObservationVector>();
		
		oSeq.add(new ObservationVector(mfccData));
		
		ViterbiCalculator vc = new ViterbiCalculator(oSeq, model);
		probability = vc.lnProbability();
		//probability = model.probability(oSeq);
		//System.out.println("To string: \n" + model.toString());
		//System.out.println("getProbability() probability is: "+probability);
		return probability;
	}
	
	public String getUtterance(){
		return utterance;
	}
	
}
