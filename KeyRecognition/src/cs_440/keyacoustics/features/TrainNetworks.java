package cs_440.keyacoustics.features;

import java.util.ArrayList;

import cs_440.keyacoustics.neuralnetwork.LeftRightNeuralNetwork;
import cs_440.keyacoustics.neuralnetwork.NearFarNeuralNetwork;

public class TrainNetworks {

	private char[] characterStream;
	private ArrayList<double[]> characterFV;
	
	public TrainNetworks(char[] characterStream, ArrayList<double[]> characterFV){
		if(characterStream.length != characterFV.size()){
			System.err.println("Number of inputted characters and found feature vectors do not match. Uh oh. Probably a "
					+ "threshold problem.");
		}
		
		this.characterStream = characterStream;
		this.characterFV = characterFV;

		
	}
	
	public void trainLeftRightNeuralNetwork(){
		ArrayList<Letter> letters = new ArrayList<Letter>();
		for(int i = 0; i < characterStream.length; i++){		
			Letter addTo = new Letter(Character.toString(characterStream[i]), characterFV.get(i));
			letters.add(addTo);
		}
		LeftRightNeuralNetwork.trainNetwork(letters);
	}
	
	public void trainNearFarNeuralNetwork(){
		ArrayList<LetterPair> letterPairs = new ArrayList<LetterPair>();
		for(int i = 0; i < characterStream.length-1; i++){
			Letter charOne = new Letter(Character.toString(characterStream[i]), characterFV.get(i));
			Letter charTwo = new Letter(Character.toString(characterStream[i+1]), characterFV.get(i+1));
			LetterPair addTo = new LetterPair(charOne, charTwo);
			letterPairs.add(addTo);
		}
		NearFarNeuralNetwork.trainNetwork(letterPairs);
	}
	
}
