package cro;

import java.awt.List;
import java.util.ArrayList;

import Knn.Knn;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class Molecule {
	public int attCount=0;
	private int numAttribute;// 分子维数
	/**
	 * 碰撞次数
	 */
	private int numHit;
	/**
	 * 最小碰撞次数
	 */
	private int minHit;
	/**
	 * 分子结构，二进制数据
	 */
	private int molS[];
	/**
	 * Two temporary structures.
	 */
	private int t1[], t2[];
	/**
	 * 动能、势能
	 */
	private double PE, KE;
	/**
	 * 分子能到达的局部最优解
	 */
	private double localMin;
	// public double acc;//分类正确率

	public Problem problem;

	Container container;
	
	public Molecule(Problem problem, Container container) {
		this.problem = problem;
		this.container = container;
		this.numAttribute = problem.getInstance().numAttributes() - 1;
		int t[] = new int[numAttribute];
		double b;
		
		int j=0;
		int weighlen = problem.weigh.length;
		
		for (int i = 0; i < t.length; i++) {// 在规定范围内随机产生分子结构
			while(j<weighlen && i ==problem.weigh[j]) {
				++j;
				b = Util.r.nextDouble();
				if(b<0.1)
					t[i]=1;
			}
			
		}
		setMolS(t);
		t1 = new int[numAttribute];
		t2 = new int[numAttribute];
		numHit = 0;
		minHit = 0;
	}
	
	public double PEmol(int[] molS) {// 分子势能
		double pe = 0;
		int n = 0;// 所选特征数
		double acc = new Knn().getAcc(problem.getInstance(), molS);
		if (container.maxAcc < acc)
			container.maxAcc = acc;
		//System.out.println(acc);
		for (int i = 0; i < molS.length; i++) {
			if (molS[i] == 1)
				n++;
		}
		attCount=n;
		if (container.minCount > n)
			container.minCount = n;
		pe = 1 * acc + 0.001 * (1 - n / (double) molS.length);
		// pe=Math.pow(acc, Math.pow(n, 0.5));
		return 0- pe;
	}
	
	public void upDate() {

		if (PE > localMin) {
			localMin = PE;
			minHit = numHit;
		}

	}

	public boolean decCheck() {
		return (numHit - minHit) > container.getDecThres();
	}

	public boolean synCheck() {
		return KE < container.getSynThres();
	}

	public boolean decomposition(Molecule newMolecule) {
		// 分解反应
		numHit++;
		Util.copySln(getMolS(), t1);
		Util.copySln(getMolS(), t2);

		for (int i = 0; i < t1.length / 2; i++) {
			container.neighbor0(t1);
			container.neighbor0(t2);
			//container.neighbor1(t1, 0, t1.length/2);
			//container.neighbor1(t2, t2.length/2, t2.length-1);
		}
		double tempPE1 = PEmol(t1);
		double tempPE2 = PEmol(t2);
		// Energy check.
		double tempBuff = PE + KE - tempPE1 - tempPE2;
		if ((tempBuff >= 0) || (tempBuff + container.getEnergyBuffer() >= 0)) {
			if (tempBuff >= 0) {
				KE = tempBuff * Util.r.nextDouble();
				newMolecule.KE = tempBuff - KE;
			} else {
				container.setEnergyBuffer(container.getEnergyBuffer()
						+ tempBuff);
				KE = container.getEnergyBuffer() * Util.r.nextDouble()
						* Util.r.nextDouble();
				container.setEnergyBuffer(container.getEnergyBuffer() - KE);
				newMolecule.KE = container.getEnergyBuffer()
						* Util.r.nextDouble() * Util.r.nextDouble();
				container.setEnergyBuffer(container.getEnergyBuffer()
						- newMolecule.KE);
			}
			minHit = 0;
			numHit = 0;
			PE = tempPE1;
			setMolS(t1);
			upDate();
			newMolecule.PE = tempPE2;
			newMolecule.setMolS(t2);
			newMolecule.upDate();
			return true;
		}
		return false;
	}

	public double onWallIneffective() {
		// 单分子无效碰撞
		double result = 0;
		numHit++;
		Util.copySln(getMolS(), t1);
		container.exchange(t1);// 两两交换
		double tempPE = PEmol(t1);// 新的分子势能
		double tempBuff = PE + KE - tempPE;
		if (tempBuff >= 0) {// 条件满足（能量守恒）
			PE = tempPE;
			KE = tempBuff
					* (Util.r.nextDouble() * (1.0 - container.getLossRate()) + container
							.getLossRate());
			setMolS(t1);
			upDate();// 根据分子势能改变碰撞次数（是否分解）
			result = tempBuff - KE;
		}
		return result;
	}

	public boolean synthesis(Molecule otherMolecule) {
		// 合成反应
		numHit++;
		Util.copySln(getMolS(), t1);
		Util.copySln(otherMolecule.getMolS(), t2);
		for (int i = 0; i < t1.length; i++) {
			if (Util.r.nextDouble() > 0.5 ) {
				t1[i] = t2[i];
			}
		}
		double tempPE = PEmol(t1);
		double tempBuff = PE + KE + otherMolecule.PE + otherMolecule.KE
				- tempPE;
		if (tempBuff >= 0) {
			PE = tempPE;
			KE = tempBuff;
			setMolS(t1);
			upDate();
			minHit = 0;
			numHit = 0;
			return true;
		}
		return false;
	}

	public void interMolecular(Molecule otherMolecule) {
		// 多分子无效碰撞
		numHit++;
		otherMolecule.numHit++;
		Util.copySln(getMolS(), t1);
		Util.copySln(otherMolecule.getMolS(), t2);
		container.exchange(t1);
		container.exchange(t2);
		//container.exchange1(t1, t2);
	
		double tempPE1 = PEmol(t1);
		double tempPE2 = PEmol(t2);
		// Energy Check.
		double tempBuff = PE + KE + otherMolecule.PE + otherMolecule.KE
				- tempPE1 - tempPE2;
		if (tempBuff >= 0) {
			PE = tempPE1;
			otherMolecule.PE = tempPE2;
			KE = tempBuff * Util.r.nextDouble();
			otherMolecule.KE = tempBuff - KE;
			setMolS(t1);
			otherMolecule.setMolS(t2);
			upDate();
			otherMolecule.upDate();
		}
	}
	
	public int getNumAttribute() {
		return numAttribute;
	}
	public void setNumAttribute(int numAttribute) {
		this.numAttribute=numAttribute;
	}

	public double getPE() {
		return PE;
	}

	public void setPE(double pE) {
		PE = pE;
	}

	public double getKE() {
		return KE;
	}

	public void setKE(double kE) {
		KE = kE;
	}

	public int[] getMolS() {
		return molS;
	}

	public void setMolS(int molS[]) {
		this.molS = molS;
	}

}
