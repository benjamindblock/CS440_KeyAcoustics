package cs_440.keyacoustics.features;

import java.util.ArrayList;

import cs_440.keyacoustics.neuralnetwork.LeftRightNeuralNetwork;
import cs_440.keyacoustics.neuralnetwork.NearFarNeuralNetwork;

public class TrainNetworks {

	private ArrayList<Character> characterStream;
	private ArrayList<double[]> characterFV;
	
	public TrainNetworks(ArrayList<Character> characterStream, ArrayList<double[]> characterFV){
		if(characterStream.size() != characterFV.size()){
			System.out.println("Character stream length is: "+characterStream.size()+" and there are "+characterFV.size()+" feature vectors");
			System.err.println("Number of inputted characters and found feature vectors do not match. Uh oh. Probably a "
					+ "threshold problem.");
		}
		this.characterStream = characterStream;
		this.characterFV = characterFV;	
	}
	
	public void trainLeftRightNeuralNetwork(){
		ArrayList<Letter> letters = new ArrayList<Letter>();
		for(int i = 0; i < characterStream.size(); i++){		
			Letter addTo = new Letter(Character.toString(characterStream.get(i)), characterFV.get(i));
			letters.add(addTo);
		}
		LeftRightNeuralNetwork.trainNetwork(letters);
	}
	
	public void trainNearFarNeuralNetwork(){
		ArrayList<LetterPair> letterPairs = new ArrayList<LetterPair>();
		for(int i = 0; i < characterStream.size()-1; i++){
			Letter charOne = new Letter(Character.toString(characterStream.get(i)), characterFV.get(i));
			Letter charTwo = new Letter(Character.toString(characterStream.get(i+1)), characterFV.get(i+1));
			LetterPair addTo = new LetterPair(charOne, charTwo);
			letterPairs.add(addTo);
		}
		NearFarNeuralNetwork.trainNetwork(letterPairs);
	}
	
}
