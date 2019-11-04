package Knn;
import java.util.ArrayList;
import java.util.Random;

import weka.core.Instances;


public class Knn {
	// public int k=5;//ÿ��ѡ�����ڵ�k��ֵ��
		// public double weight[]={1.0,0.8,0.6,0.4,0.2};//���ü�ȥ��������Ȩ��
		ArrayList<Data> daTest = new ArrayList<Data>();// ��������
		ArrayList<Data> daTrain = new ArrayList<Data>();// ѵ������
		Instances instance;// ʵ������
		public int[] molS;
		public int molCount=0;//��ѡ����������

		public double getAcc(Instances instance,int[] m){
			this.instance=instance;
			for(int i=0;i<m.length;i++){
				if(m[i]==1){
					++molCount;
				}
			}
			molS=new int[molCount];
			molCount=0;
			for(int i=0;i<m.length;i++){
				if(m[i]==1){
					molS[molCount]=i;
					++molCount;
				}
			}
			Linked[] link = new Linked[10];
			for (int i = 0; i < link.length; ++i)
				link[i] = new Linked();
			int correct = 0;
			int num=instance.numInstances();//ʵ������
			for (int i = 0; i < 10; ++i) {
				int[] a = new int[num];
				int index;
				for (int j = 0; j < num; ++j) {// ÿ��������䣬10�۽�����֤
					index =new Random().nextInt(num);
					while (a[index] == 1)
						index = new Random().nextInt(num);
					a[index] = 1;
					link[j % 10].link.add(instance.instance(index));
				}
				String result=null;
				for (int j = 0; j < 10; ++j) {// �ֱ�ȡÿһ��������Ϊ���Լ�
					readyData(link, j);
					Data da; //ȡ����һ��
					for (int k = 0; k < daTest.size(); ++k) {
						 da= daTest.get(k);
						 result= getResult(da);// �õ�����
						try{
						if (result.equals(da.getType())) 
							correct++;
						}catch(Exception e){
//							System.out.println(result);
//							System.out.println(daTrain.size());
						}
					}
					daTest.clear();
					daTrain.clear();
				}
				for (int j = 0; j < 10; ++j)
					link[j].link.clear();
			}
			return correct / (double) (10 * num);
		}
	/*	
		public double getAcc(ArrayList<Molecule> population, int[] molS) {
			instance = population.get(0).problem.getInstance();
			this.molS = molS;
			Linked[] link = new Linked[10];
			for (int i = 0; i < link.length; i++)
				link[i] = new Linked();
			int correct = 0;
			for (int i = 0; i < 10; i++) {
				int[] a = new int[population.size()];
				int index;
				for (int j = 0; j < population.size(); j++) {// ÿ��������䣬10�۽�����֤
					index = Util.r.nextInt(population.size());
					while (a[index] == 1)
						index = Util.r.nextInt(population.size());
					a[index] = 1;
					link[j % 10].link.add(population.get(index));
				}
				for (int j = 0; j < 10; j++) {// �ֱ�ȡÿһ��������Ϊ���Լ�
					readyData(link, j);
					for (int k = 0; k < daTest.size(); k++) {
						Data da = daTest.get(k);
						String result = getResult(da);// �õ�����
						try {
							if (result.equals(da.getType())) {
								correct++;
							}
						} catch (Exception w) {
							System.out.println(population.size());
						}
					}
					daTest.clear();
					daTrain.clear();
				}
				for (int j = 0; j < 10; j++)
					link[j].link.clear();
			}
			return correct / (double) (10 * population.size());
		}*/

		void readyData(Linked[] link, int index) {
			int testSize = link[index].link.size();
			for (int i = 0; i < testSize; ++i) {
				Data data = new Data(link[index].link.get(i), molS);
				daTest.add(data);
			}
			for (int i = 0; i < 10; ++i) {
				if (i != index) {
					testSize = link[i].link.size();
					for (int j = 0; j < testSize; ++j) {
						Data data = new Data(link[i].link.get(j), molS);
						daTrain.add(data);
					}
				}
			}
		}

		private String getResult(Data data) {
			double min = 1E200;
			String result = null;
			for (int i = 0; i < daTrain.size(); ++i) {// �õ�data��ÿ��ѵ�����ݵľ���
				double distance = 0;
				double a;
				for (int j = 0; j < data.getAtt().length; ++j) {
					a = data.getAtt()[j] - daTrain.get(i).getAtt()[j];
					distance += a * a;
				}
				distance = Math.sqrt(distance);
				if (min > distance) {
					min = distance;
					result = daTrain.get(i).getType();
				}
				//daTrain.get(i).setDistance(distance);
			}
			/*
			 * Collections.sort(daTrain,new Comparator<Data>(){//���� public int
			 * compare(Data o1, Data o2) { double
			 * s=o1.getDistance()-o2.getDistance(); if(s<0) return 1; else if(s>0)
			 * return -1; else return 0; } });
			 * 
			 * //�õ��ò��Լ���ѵ�����е����� Attribute
			 * strClass=instance.attribute(instance.numAttributes()-1); double[]
			 * disWeight=new double[strClass.numValues()]; int index=0; double
			 * max=0; for(int i=0;i<k&&i<daTrain.size();i++){//k�����ڽ����� for(int
			 * j=0;j<strClass.numValues();j++){
			 * if(daTrain.get(i).getType().equals(strClass.value(j))){
			 * disWeight[j]+=weight[i]; if(max<disWeight[j]){ index=j;
			 * max=disWeight[j]; } break; } } } return strClass.value(index);
			 */
			return result;
		}

}
