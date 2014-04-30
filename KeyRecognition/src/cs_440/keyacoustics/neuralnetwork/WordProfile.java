package cs_440.keyacoustics.neuralnetwork;

import java.util.ArrayList;

public class WordProfile {
	
	private ArrayList <double []> wordProfile;
	private int wordLength;
	
	public WordProfile(ArrayList<double []> input, LeftRightNeuralNetwork lrnnet, NearFarNeuralNetwork nfnnet) {
		
		wordProfile = new ArrayList<double []>();
		wordLength = input.size();
		System.out.println("Input's contents: ");
		for(int i = 0; i<input.size(); i++){
			for(int j = 0; j<input.get(i).length; j++){
				System.out.println(i + ".) Position " + j + ": "+input.get(i)[j]);
			}
		}
		
		ArrayList<Double> keyPositions = lrnnet.evaluateValues(input);
		ArrayList<Double> keyDistances = nfnnet.evaluateValues(input);
		
		for(int i = 0; i < input.size()-1; i++){
			double[] temp = new double[3];
			//System.out.println("Key positions:" + keyPositions.toString());
			System.out.println(keyDistances.toString());
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
