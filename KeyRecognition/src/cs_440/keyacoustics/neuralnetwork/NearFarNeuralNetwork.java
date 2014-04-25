package cs_440.keyacoustics.neuralnetwork;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.Perceptron;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.*;

import cs_440.keyacoustics.dictionary.DISTANCE;
import cs_440.keyacoustics.features.LetterPair;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This class creates and trains a neural network that will determine
 * if a letter pair, when passed to the network, has a near or far
 * relationship
 * 
 * @author Ben Block and Walker Bohannan
 *
 */

public class NearFarNeuralNetwork {

//	private static NeuralNetwork<SupervisedLearning> neuralNetwork = new Perceptron(4, 1, TransferFunctionType.STEP);
	private static NeuralNetwork<SupervisedLearning> neuralNetwork = new Perceptron(2, 1, TransferFunctionType.STEP);

	
	public static void trainNetwork(ArrayList<LetterPair> letterPairs){
		
		/**
		 *
		 * We want our neural network to have four inputs and one output.
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
		
//		neuralNetwork = new Perceptron(4, 1);
		
		// Create our training set.
		DataSet trainingSet = new DataSet(2, 1); 
		for(int i = 0; i < letterPairs.size(); i++){
			LetterPair letPair = letterPairs.get(i);
			
			int nearOrFar = 0;
			if(letPair.ds.equals(DISTANCE.FAR)){
				nearOrFar = 1;
			}
			
			DataSetRow addTo = new DataSetRow(new double[]{letPair.fv[1], 
					letPair.fv[3]}, new double[]{nearOrFar});
			trainingSet.addRow(addTo);
			System.out.println(i+" "+addTo);
		}
		

		// Learn the training set
		System.out.println("Learning NF training set...");

		neuralNetwork.learnInNewThread(trainingSet);
		neuralNetwork.stopLearning();
		System.out.println(neuralNetwork.toString());
		System.out.println("Learned NF training set.");
		
//		// Create our training set.
//				DataSet trainingSet = new DataSet(2, 1); 
//				for(int i = 0; i < letterPairs.size(); i++){
//					LetterPair letPair = letterPairs.get(i);
//					
//					int nearOrFar = 0;
//					if(letPair.ds.equals(DISTANCE.FAR)){
//						nearOrFar = 1;
//					}
//					
//					DataSetRow addTo = new DataSetRow(new double[]{letPair.fv[1], letPair.fv[3]}, new double[]{nearOrFar});
//					trainingSet.addRow(addTo);
//					System.out.println(i+" "+addTo);
//				}
//				
//
//				// Learn the training set
//				System.out.println("Learning NF training set...");
//
//				neuralNetwork.learnInNewThread(trainingSet);
//				System.out.println("Learned NF training set.");


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
	public static ArrayList<Double> evaluateValues(ArrayList<double[]> inputs){
		
		ArrayList<Double> ret = new ArrayList<Double>();
		
		for(int i = 0; i < inputs.size()-1; i++){
			double[] input = new double[2];
//			input[0] = inputs.get(i)[0];
			input[0] = inputs.get(i)[1];
//			input[2] = inputs.get(i+1)[0];
			input[1] = inputs.get(i+1)[1];
			
			neuralNetwork.setInput(input);
			neuralNetwork.calculate();
			double[] output = neuralNetwork.getOutput();
			ret.add(output[0]);
		}
		System.out.println("ret : "+ret);
		return ret;
	}

	
	/**
	 * Save our neural network at the file path designated.
	 */
	public static void saveNetwork(String filePath){
		neuralNetwork.save(filePath);
	}
	
	/**
	 * Load a neural network from the file path designated.
	 * 
	 * @param filePath where our network is.
	 */
	public static void loadNetwork(String filePath){
		neuralNetwork = NeuralNetwork.createFromFile(filePath);
	}
	
	
}
