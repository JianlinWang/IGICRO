package cro;

import java.util.ArrayList;

public class Main extends Thread{
	private static int numDiease=10;
	private static int numRepeat=3;
	
	static int numCount=0;
	static String diseaseName[]= {"Colon"};
			//"Colon",
			//"CNS","Leukemia","Leukemia_3c","Lymhhoma",
			//"Leukemia_4c","SRBCT","MLL"};
	static int S[] = new int[] { 10,10,10,10, 10, 10, 10, 10, 10, 10, 10, 10,10,10 };// ��Ӧÿ�����ݼ����еĴ���
	static ArrayList<Double> Fits= new ArrayList<Double>();
	static ArrayList<Integer> Atts= new ArrayList<Integer>();
	static ArrayList<Double> Accs= new ArrayList<Double>();
	public static void main(String[] args) throws Exception {
		double times[] = new double[diseaseName.length];
		for (int g=0;g<diseaseName.length;g++) {
			long start=System.currentTimeMillis();
			Problem problem = new Problem(diseaseName[g]);
			System.out.println(".............");
			double fit,acc,avgFit=0;
			double maxFit=0,minFit=2,avgAtt=0,std;
			double globaMinAcc=2;//��С��������
			double globaMaxAcc=0,avgAcc=0;//�����������
			double avgFit2=0,avgAtt2=0,avgAcc2=0;
			
			
			for(int j=0;j<10;j++) {
				Container container = new Container(problem);
				container.run();
				int count=0;
				int c[] = container.bestMole.getMolS();
				for(int k=0;k<c.length;k++) {
					if(c[k]==1) {
						count++;
					}
				}
				
				
				if(maxFit<(-container.bestMole.getPE())) {
					maxFit=(-container.bestMole.getPE());
				}
				if(minFit>(-container.bestMole.getPE())) {
					minFit=(-container.bestMole.getPE());
				}
				
				if(globaMaxAcc<container.maxAcc) {
					globaMaxAcc=container.maxAcc;
				}
				if(globaMinAcc>container.maxAcc) {
					globaMinAcc=container.maxAcc;
				}
				
				avgAtt+=count;
				fit=(-container.bestMole.getPE());
				avgFit+=fit;
				acc=container.maxAcc;
				avgAcc+=acc;
				Fits.add(fit);
				Atts.add(count);
				Accs.add(acc);
				System.out.println("��������"+count);
				System.out.println("���ྫ��"+acc);
				System.out.println("��Ӧ��ֵ"+fit);
			}
			
			int t=S[1];
			for (int j=0;j<(t);j++) {
				avgFit2+=((avgFit/(t))-Fits.get(j))*((avgFit/(t))-Fits.get(j));
				avgAtt2+=((avgAtt/(t))-Atts.get(j))*((avgAtt/(t))-Atts.get(j));
				avgAcc2+=((avgAcc/(t))-Accs.get(j))*((avgAtt/(t))-Accs.get(j));
			}
			
			
			
			System.out.println("�����Ӧ��ֵ��"+maxFit+"\n��С��Ӧ��ֵ��"+minFit+"\nƽ����Ӧ��ֵ��"+avgFit/10);
			System.out.println("��Ӧ�ȱ�׼�"+Math.sqrt(avgFit2/10));
			System.out.println("�����ྫ��"+globaMaxAcc+"\n��С���ྫ�ȣ�"+globaMinAcc+"\nƽ����ȷ�ȣ�"+avgAcc/10);
			System.out.println("���ྫ�ȱ�׼�"+Math.sqrt(avgAcc2/10));
			System.out.println("ƽ����������"+avgAtt/10);
			System.out.println("���Ը�����׼�"+Math.sqrt(avgAtt2/10));
			long end=System.currentTimeMillis();
			times[g]=(float)(end-start)/1000;
			System.out.println("ִ��ʱ�䣺"+times[g]+"s");
		}
		
	}

}
