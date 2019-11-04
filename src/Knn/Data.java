package Knn;
import weka.core.Instance;

public class Data {
	private String type;// 样本类型
	private double[] att;// 样本数据
	private double distance;// 距离

	public Data(Instance instance, int mols[]) {
		att = new double[mols.length];
		type = instance.stringValue(instance.numAttributes()-1);
		double max = 0;
		for (int i = 0; i < mols.length; i++) {
//			if(instance.value(mols[i])==0.0){
//				att[i]=0;
//			}
				att[i] = instance.value(mols[i]);
				if(att[i]!=att[i])
					att[i]=0;
				if (Math.abs(att[i]) > max)
					max = Math.abs(att[i]);
		}
		for (int i = 0; i < att.length; i++) {
			att[i] /= max;
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double[] getAtt() {
		return att;
	}

	public void setAtt(double[] att) {
		this.att = att;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}


}
