package opennlp.maxent;

import opennlp.model.MaxentModel;

public class RandomModel implements MaxentModel {

	@Override
	public double[] eval(String[] context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] eval(String[] context, double[] probs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] eval(String[] context, float[] values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBestOutcome(double[] outcomes) {
		int r=(int) (Math.random()*100);
		String[] ss={"S","B","M","E"};
		return ss[r%4];
	}

	@Override
	public String getAllOutcomes(double[] outcomes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOutcome(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getIndex(String outcome) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object[] getDataStructures() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumOutcomes() {
		// TODO Auto-generated method stub
		return 0;
	}

}
