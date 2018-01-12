package ewimberley.gnn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public abstract class NeuralNetwork extends Classifier {

	protected Map<String, Neuron> neurons;
	protected Set<InputNeuron> inputs;
	protected Map<Integer, InputNeuron> featureToInputMap;
	protected List<Set<String>> layers;
	protected Map<OutputNeuron, String> outputs;
	protected int numHiddenLayers;
	protected int numNeuronsPerLayer;
	private double learningRate;
	private double annealingRate;

	public NeuralNetwork() {
		rand = new Random();
		layers = new ArrayList<Set<String>>();
		inputs = new HashSet<InputNeuron>();
		outputs = new HashMap<OutputNeuron, String>();
		featureToInputMap = new HashMap<Integer, InputNeuron>();
	}

	public void test(double[][] inputData, String[] expected, int[][] confusionMatrix, List<Integer> testingIndices,
			Map<String, Integer> classLabelConfusionMatrixIndices) {
		for (Integer testingIndex : testingIndices) {
			setupForPredict(inputData[testingIndex]);
			double highestProb = 0.0;
			String highestProbClass = null;
			for (Neuron output : outputs.keySet()) {
				double prob = output.activation();
				if(prob > highestProb) {
					highestProb = prob;
					highestProbClass = outputs.get(output);
				}
			}
			String expectedClass = expected[testingIndex];
			System.out.println("Expected " + expectedClass + ", predicted " + highestProbClass + " with probability " + highestProb);
			confusionMatrix[classLabelConfusionMatrixIndices.get(expectedClass)][classLabelConfusionMatrixIndices.get(highestProbClass)]++;
		}
	}

	public double error(double[] inputData, String expected) {
		double totalError = 0.0;
		setupForPredict(inputData);
		for (Neuron output : outputs.keySet()) {
			double prob = output.activation();
			double error = 0.0;
			if (outputs.get(output).equals(expected)) {
				error = Math.abs(1.0 - prob);
			} else {
				error = Math.abs(0.0 - prob);
			}
			totalError += error;
		}
		return totalError;
	}

	// FIXME figure out how to return class and probability
	public String predict(double[] inputData) {
		double highestProb = 0.0;
		Neuron highestProbNeuron = null;
		setupForPredict(inputData);
		for (Neuron output : outputs.keySet()) {
			double prob = output.activation();
			if (prob > highestProb) {
				highestProbNeuron = output;
				highestProb = prob;
			}
		}
		System.out.println("Predicted class is " + outputs.get(highestProbNeuron) + " with prob value " + highestProb);
		return outputs.get(highestProbNeuron);
	}

	private void setupForPredict(double[] inputData) {
		for (Map.Entry<String, Neuron> neuronEntry : getNeurons().entrySet()) {
			neuronEntry.getValue().resetMemoization();
		}
		for (int i = 0; i < inputData.length; i++) {
			InputNeuron in = featureToInputMap.get(i);
			in.setInput(inputData[i]);
		}
	}

	protected void createInputLayer() {
		int numInputs = data[0].length;
		inputs = new HashSet<InputNeuron>();
		featureToInputMap = new HashMap<Integer, InputNeuron>();
		for (int i = 0; i < numInputs; i++) {
			InputNeuron input = new InputNeuron(this);
			featureToInputMap.put(i, input);
			addInput(input);
		}
	}

	protected void addInput(InputNeuron input) {
		inputs.add(input);
		neurons.put(input.getUuid(), input);
	}

	protected Neuron createNewRandomNeuron() {
		Neuron n = new HiddenNeuron(this);
		neurons.put(n.getUuid(), n);
		return n;
	}

	protected void addOutput(String classLabel, OutputNeuron output) {
		outputs.put(output, classLabel);
		neurons.put(output.getUuid(), output);
	}

	public void scramble() {
		for (Neuron neuron : getNeurons().values()) {
			neuron.scramble();
		}
	}

	public void printNetwork() {
		System.out.println("***********************************");
		System.out.println("Input Layer:");
		for (InputNeuron input : inputs) {
			System.out.println(" " + input.toString());
		}
		for (int i = 1; i < layers.size(); i++) {
			Set<String> layer = layers.get(i);
			System.out.println("Hidden Layer " + i + ":");
			for (String neuronId : layer) {
				Neuron n = neurons.get(neuronId);
				System.out.println(" " + n.toString());
			}
		}
		System.out.println("Output Layer:");
		for (Neuron output : outputs.keySet()) {
			System.out.println(" " + output.toString());
		}
	}

	public Map<String, Neuron> getNeurons() {
		return neurons;
	}

	public Set<InputNeuron> getInputs() {
		return inputs;
	}

	public Map<OutputNeuron, String> getOutputs() {
		return outputs;
	}

	public int getNumHiddenLayers() {
		return numHiddenLayers;
	}

	public void setNumHiddenLayers(int numHiddenLayers) {
		if (numHiddenLayers < 1) {
			throw new IllegalArgumentException("Number of hidden layers must be at least 1.");
		}
		this.numHiddenLayers = numHiddenLayers;
	}

	public int getNumNeuronsPerLayer() {
		return numNeuronsPerLayer;
	}

	public void setNumNeuronsPerLayer(int numNeuronsPerLayer) {
		if (numNeuronsPerLayer < 1) {
			throw new IllegalArgumentException("Number of neurons per layer must be at least 1.");
		}
		this.numNeuronsPerLayer = numNeuronsPerLayer;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public double getAnnealingRate() {
		return annealingRate;
	}

	public void setAnnealingRate(double annealingRate) {
		this.annealingRate = annealingRate;
	}

	public Map<Integer, InputNeuron> getFeatureToInputMap() {
		return featureToInputMap;
	}

}