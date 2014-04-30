package cs_440.keyacoustics.neuralnetwork;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.Perceptron;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.*;

import cs_440.keyacoustics.dictionary.KEYBOARD_SIDE;
import cs_440.keyacoustics.features.Letter;

import java.util.ArrayList;
import java.util.Vector;

/**
 * This class creates and trains a neural network that will determine
 * if a letter, when passed to the network, is on the left or right
 * side of the keyboard.
 * 
 * @author Ben Block and Walker Bohannan
 *
 */
public class LeftRightNeuralNetwork {

	private NeuralNetwork<SupervisedLearning> neuralNetwork;
	
	public LeftRightNeuralNetwork(NeuralNetwork<SupervisedLearning> nnet){
		neuralNetwork = nnet;
	}
	
	public LeftRightNeuralNetwork(String filePath){
		neuralNetwork = loadNetwork(filePath); 
	}
	
	public void trainNetwork(ArrayList<Letter> letters){
		/**
		 *
		 * We want our neural network to have two inputs and one output:
		 * 
		 * Inputs
		 * 1. Standard Deviation of MFCC
		 * 2. Standard Deviation of FFT
		 * 
		 * Output
		 * 1. Whether the letter is left or right. A returned 0 means the letter is on the left, and a
		 * returned 1 means the letter is on the right.
		 */
		
//		neuralNetwork = new Perceptron(2, 1);
		
		// Create our training set.
		DataSet trainingSet = new DataSet(2, 1); 
		for(int i = 0; i < letters.size(); i++){
			Letter let = letters.get(i);
			
			int leftOrRight = 0;
			if(let.ks.equals(KEYBOARD_SIDE.RIGHT)){
				leftOrRight = 1;
			}
			DataSetRow addTo = new DataSetRow(new double[]{let.fv[0], let.fv[1]}, new double[]{leftOrRight});
			trainingSet.addRow(addTo);
		}
		

		// Learn the training set
		System.out.println("Learning LR training set...");
		System.out.println("Training set size:" +trainingSet.size());
		neuralNetwork.learnInNewThread(trainingSet);
		System.out.println(neuralNetwork.toString());
		neuralNetwork.stopLearning();
		System.out.println("Learned LR training set.");

		// Test perceptron
//		testNeuralNetwork(myPerceptron, trainingSet); //we need to create a test method
	}
	
	/**
	 * Our method that runs our keypresses through the neural network.
	 * The network takes two inputs and gives one output.
	 * 
	 * @param inputs The Feature Vectors for each letter.
	 * @return An ArrayList with left or right values for every letter pair passed to the neural network.
	 */
	public ArrayList<Double> evaluateValues(ArrayList<double[]> inputs){
		
		ArrayList<Double> ret = new ArrayList<Double>();
		
		for(int i = 0; i < inputs.size(); i++){
			neuralNetwork.setInput(inputs.get(i));
			neuralNetwork.calculate();
			double[] output = neuralNetwork.getOutput();
			System.out.println(output[0]);
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
	public NeuralNetwork<SupervisedLearning> loadNetwork(String filePath){
		return neuralNetwork.createFromFile(filePath);
	}
	
}
