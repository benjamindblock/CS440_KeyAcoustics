import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.Perceptron;
import org.neuroph.util.TransferFunctionType;


public class NeurophTest {
	NeuralNetwork neuralNetwork = new Perceptron(2, 1, TransferFunctionType.STEP);
	
	DataSet trainingSet = new DataSet(2, 1);
	
	public NeurophTest(){
		DataSetRow addTo = new DataSetRow(new double[] {0, 0}, new double [] {0});
		trainingSet.addRow(addTo);
		DataSetRow addTo1 = new DataSetRow(new double[] {0, 1}, new double [] {1});
		trainingSet.addRow(addTo1);
		DataSetRow addTo2 = new DataSetRow(new double[] {1, 0}, new double [] {1});
		trainingSet.addRow(addTo2);
		DataSetRow addTo3 = new DataSetRow(new double[] {1, 1}, new double [] {1});
		trainingSet.addRow(addTo3);
		
		neuralNetwork.learnInNewThread(trainingSet);
		neuralNetwork.save("/Users/walkerbohannan/Desktop/neurophTest_perceptron.nnent");
		
	}
	
	public static void main(String[] args){
		NeurophTest nt = new NeurophTest();
		NeuralNetwork nnet = NeuralNetwork.createFromFile("/Users/walkerbohannan/Desktop/neurophTest_perceptron.nnent");
		nnet.setInput(5, 1);
		nnet.calculate();
		double [] networkOutput = nnet.getOutput();		
		for(int i = 0; i < networkOutput.length; i++){
			System.out.println(networkOutput[i]);
		}
	}
}
