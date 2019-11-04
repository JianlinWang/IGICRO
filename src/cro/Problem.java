package cro;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSource;

public class Problem {
	public Instances instance;//shili
	public int FELimit;
	public String fileName;
	public int[] weigh= {0};
	
	public Problem (String dieaseName) {
		this.fileName=dieaseName;
		setInstance(getInstances(fileName));
		setFELimit(300);
		
		weigh=Filter(dieaseName,50);
		Arrays.sort(weigh);
	}
	
	public int[] Filter(String fileName, int num) {
		Instances trainIns = null;
		int[] a = null;
		try {
			// 1.����ѵ��,�ڴ����ǽ�ѵ�������Ͳ�����������weka�ṩ��segment���ݼ����ɵ�
			File file = new File("F:\\data1\\"+fileName+".arff");//"F://data1//"  + ".arff" File file = new File("e:\\data2\\" + fileName + ".arff");
			//File file = new File("C://Program Files//Weka-3-8//data//breast-cancer.arff");
			ArffLoader loader = new ArffLoader();
			loader.setFile(file);
			trainIns = loader.getDataSet();// trainIns����62��ʵ��
  // ��ʹ������֮ǰһ��Ҫ��������instances��classIndex��������ʹ��instances�����ǻ��׳��쳣
			int m=trainIns.numAttributes() - 1;
			trainIns.setClassIndex(trainIns.numAttributes() - 1);
			/*
			 * * 2.��ʼ�������㷨��search method�������������㷨��attribute evaluator�� Ranker rank = new
			 * Ranker(); InfoGainAttributeEval eval = new InfoGainAttributeEval(); /* *
			 * 3.���������㷨����������� eval.buildEvaluator(trainIns);
			 * //System.out.println(rank.search(eval, trainIns)); /*4.�����ض������㷨�����Խ���ɸѡ
			 * ������ʹ�õ�Ranker�㷨���������԰���InfoGain�Ĵ�С��������
			 */
			// 2.��ʼ�������㷨��search method�������������㷨��attribute evaluator��
			Ranker rank = new Ranker();
			InfoGainAttributeEval eval = new InfoGainAttributeEval();
			//ChiSquaredAttributeEval eval = new ChiSquaredAttributeEval();
			 //ReliefFAttributeEval eval = new ReliefFAttributeEval();
			// 3.���������㷨�����������
			eval.buildEvaluator(trainIns);  //wdbc���ݳ����쳣
			
			//System.out.println(rank.search(eval, trainIns));//�����ַ�ĵط�

			int[] attrIndex = rank.search(eval, trainIns); //�±�
			/*
			 * 5.��ӡ�����Ϣ ���������������Ե�������ͬʱ��ÿ�����Ե�InfoGain��Ϣ��ӡ����
			 */
			StringBuffer attrIndexInfo = new StringBuffer();
			StringBuffer attrInfoGainInfo = new StringBuffer();
			/* attrIndexInfo.append("Selected attributes:\n"); */
			attrInfoGainInfo.append("Ranked attributes:\n");
			for (int i = 0; i < 50; i++) {// attrIndex.length
				attrIndexInfo.append(attrIndex[i]); // �Լ���1�ĵط����ɼӿɲ���
				attrIndexInfo.append(",");
				// System.out.println("��"+(i+1)+"������");
				attrInfoGainInfo.append((trainIns.attribute(attrIndex[i]).name()));
				// attrInfoGainInfo.append(eval.evaluateAttribute(attrIndex[i]));
				attrInfoGainInfo.append(",");//
				attrInfoGainInfo.append(eval.evaluateAttribute(attrIndex[i]) + " ");
				// attrInfoGainInfo.append((trainIns.attribute(attrIndex[i]).name())+" ");
				// attrInfoGainInfo.append("/n");
			}

			String[] temp = attrIndexInfo.toString().split(",");
			List<Integer> list = new ArrayList<Integer>();
			for (String x : temp) {
				list.add(Integer.parseInt(x));
			}
			a = new int[list.size()];
			for (int i = 0; i < list.size(); i++) {
				a[i] = list.get(i);
			}
			
			//System.out.println(attrInfoGainInfo.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;
	}
	
	
	private Instances getInstances(String fileName) {
		Instances instance = null;
		try {
			instance = DataSource.read("F:\\data1\\" + fileName+".arff");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}
	public Instances getInstance() {
		return instance;
	}

	public void setInstance(Instances instance) {
		this.instance = instance;
	}

	public int getFELimit() {
		return FELimit;
	}

	public void setFELimit(int fELimit) {
		FELimit = fELimit;
	}

}
