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
			// 1.读入训练,在此我们将训练样本和测试样本是由weka提供的segment数据集构成的
			File file = new File("F:\\data1\\"+fileName+".arff");//"F://data1//"  + ".arff" File file = new File("e:\\data2\\" + fileName + ".arff");
			//File file = new File("C://Program Files//Weka-3-8//data//breast-cancer.arff");
			ArffLoader loader = new ArffLoader();
			loader.setFile(file);
			trainIns = loader.getDataSet();// trainIns中有62个实例
  // 在使用样本之前一定要首先设置instances的classIndex，否则在使用instances对象是会抛出异常
			int m=trainIns.numAttributes() - 1;
			trainIns.setClassIndex(trainIns.numAttributes() - 1);
			/*
			 * * 2.初始化搜索算法（search method）及属性评测算法（attribute evaluator） Ranker rank = new
			 * Ranker(); InfoGainAttributeEval eval = new InfoGainAttributeEval(); /* *
			 * 3.根据评测算法评测各个属性 eval.buildEvaluator(trainIns);
			 * //System.out.println(rank.search(eval, trainIns)); /*4.按照特定搜索算法对属性进行筛选
			 * 在这里使用的Ranker算法仅仅是属性按照InfoGain的大小进行排序
			 */
			// 2.初始化搜索算法（search method）及属性评测算法（attribute evaluator）
			Ranker rank = new Ranker();
			InfoGainAttributeEval eval = new InfoGainAttributeEval();
			//ChiSquaredAttributeEval eval = new ChiSquaredAttributeEval();
			 //ReliefFAttributeEval eval = new ReliefFAttributeEval();
			// 3.根据评测算法评测各个属性
			eval.buildEvaluator(trainIns);  //wdbc数据出现异常
			
			//System.out.println(rank.search(eval, trainIns));//输出地址的地方

			int[] attrIndex = rank.search(eval, trainIns); //下标
			/*
			 * 5.打印结果信息 在这里我们了属性的排序结果同时将每个属性的InfoGain信息打印出来
			 */
			StringBuffer attrIndexInfo = new StringBuffer();
			StringBuffer attrInfoGainInfo = new StringBuffer();
			/* attrIndexInfo.append("Selected attributes:\n"); */
			attrInfoGainInfo.append("Ranked attributes:\n");
			for (int i = 0; i < 50; i++) {// attrIndex.length
				attrIndexInfo.append(attrIndex[i]); // 自己加1的地方，可加可不加
				attrIndexInfo.append(",");
				// System.out.println("第"+(i+1)+"个属性");
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
