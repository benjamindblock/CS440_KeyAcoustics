import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.Perceptron;
import org.neuroph.*;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This class creates and trains a neural network that will determine
 * if a letter pair, when passed to the network, has a near or far
 * relationship
 * 
 * @author Ben Blocka and Walker Bohannan
 *
 */

public class NearFarNeuralNetwork {

	private NeuralNetwork neuralNetwork;
	
	//Create a new neural netowrk
	public NearFarNeuralNetwork(ArrayList<LetterPair> letterPairs){

		/**
		 *
		 * We want our neural network to have five inputs and one output.
		 * 
		 * Inputs
		 * 1. Standard Deviation of MFCC of the first letter in the pair.
		 * 2. Mean of MFCC of the first letter in the pair.
		 * 3. Standard Deviation of MFCC of the second letter in the pair.
		 * 4. Mean of MFCC of the second letter in the pair. 
		 * 
		 * Output
		 * 1. Whether the letter pair is near or far. A returned 0 means the letter pair is near, and a
		 * returned 1 means the letter pair is far.
		 */
		
		neuralNetwork = new Perceptron(4, 1);
		
		// Create our training set.
		DataSet trainingSet = new DataSet(4, 1); 
		for(int i = 0; i < letterPairs.size(); i++){
			LetterPair letPair = letterPairs.get(i);
			
			int nearOrFar = 0;
			if(letPair.ds.equals(DISTANCE.FAR)){
				nearOrFar = 1;
			}
			
			DataSetRow addTo = new DataSetRow(new double[]{letPair.fv[0], letPair.fv[1], 
					letPair.fv[2], letPair.fv[3]}, new double[]{nearOrFar});
			trainingSet.addRow(addTo);
		}
		

		// Learn the training set
		neuralNetwork.learn(trainingSet);

		// Test perceptron
//		testNeuralNetwork(myPerceptron, trainingSet); //we need to create a test method

		
	}
	
	/**
	 * Our method that runs our keypresses through the neural network.
	 * The network takes four inputs (two for each letter in the pair), and gives one output.
	 * 
	 * @param inputs The Feature Vectors for each letter.
	 * @return An ArrayList with near or far values for every letter pair passed to the neural network.
	 */
	public ArrayList<Double> evaluateValues(ArrayList<double[]> inputs){
		
		ArrayList<Double> ret = new ArrayList<Double>();
		
		for(int i = 0; i < inputs.size(); i++){
			neuralNetwork.setInput(inputs.get(i));
			neuralNetwork.calculate();
			double[] output = neuralNetwork.getOutput();
			ret.add(output[0]);
		}
		
		return ret;
	}

	
	/**
	 * Save our neural network at the file path designated.
	 */
	public void saveNetwork(String filePath){
		neuralNetwork.save(filePath);
	}
	
	/**
	 * Load a neural network from the file path designated.
	 * 
	 * @param filePath where our network is.
	 */
	public void loadNetwork(String filePath){
		NeuralNetwork loadedPerceptron = NeuralNetwork.load("mySamplePerceptron.nnet");
		neuralNetwork = loadedPerceptron;
	}
	
	
}
