package cs_440.keyacoustics.neuralnetwork;

import java.util.ArrayList;

public class WordProfile {
	
	private ArrayList <double []> wordProfile;
	private int wordLength;
	
	public WordProfile(ArrayList<double []> input) {
		
		wordProfile = new ArrayList<double []>();
		wordLength = input.size();
		
		ArrayList<Double> keyPositions = LeftRightNeuralNetwork.evaluateValues(input);
		ArrayList<Double> keyDistances = NearFarNeuralNetwork.evaluateValues(input);
		
		for(int i = 0; i < input.size()-1; i++){
			double[] temp = new double[3];
			temp[0] = keyPositions.get(i);
			temp[1] = keyPositions.get(i+1);
			temp[2] = keyDistances.get(i);
			wordProfile.add(temp);
		}
	}
	
	public ArrayList<double[]> getWordProfile(){
		return wordProfile;
	}
	
	public int getWordLength(){
		return wordLength;
	}
}
