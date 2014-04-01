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
	
	public NearFarNeuralNetwork(ArrayList<LetterPair> letterPairs){

		// create training set (logical AND function)
		DataSet trainingSet = new DataSet(2, 1);
		trainingSet.addRow(new DataSetRow(new double[]{0, 0}, new double[]{0}));
		trainingSet.addRow(new DataSetRow(new double[]{0, 1}, new double[]{0}));
		trainingSet.addRow(new DataSetRow(new double[]{1, 0}, new double[]{0}));
		trainingSet.addRow(new DataSetRow(new double[]{1, 1}, new double[]{1}));

		// create perceptron neural network with two inputs and one output
		neuralNetwork = new Perceptron(2, 1);

		// learn the training set
		neuralNetwork.learn(trainingSet);

		// test perceptron
		System.out.println("Testing trained perceptron");
		//				testNeuralNetwork(myPerceptron, trainingSet);

		// save trained perceptron
		neuralNetwork.save("mySamplePerceptron.nnet");

		// load saved neural network
		NeuralNetwork loadedPerceptron = NeuralNetwork.load("mySamplePerceptron.nnet");
		
	}
}
