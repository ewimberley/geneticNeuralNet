package ewimberley.ml.gnn;

import java.util.Comparator;

public class NeuralNetworkErrorComparator<H> implements Comparator<GenticNeuralNetwork<H>> {

	public int compare(GenticNeuralNetwork<H> c1, GenticNeuralNetwork<H> c2) {
		if(c1.getAverageError() < c2.getAverageError()) {
			return -1;
		} else if(c1.getAverageError() > c2.getAverageError()) {
			return 1;
		} else {
			return 0;
		}
	}

}
