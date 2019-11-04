package cro;

import java.util.ArrayList;
import java.util.Random;

public class Container {
	private Problem problem;
	
	private int FE;
	
	private int initPopSize;// ��ʼ�˿�����
	static Molecule bestMole;
	Molecule worstMole;

//	private double globalBest;// ȫ�����Ž�
//	private int[] globalBestStruct;// ȫ�����ŷ��ӽṹ
	private double energyBuffer;

	private double initKE;// ��ʼ����

	private double collRate;
	/**
	 * ��������Ч��ײ������ʧ��
	 */
	private double lossRate;

	/**
	 * �ֽⷢ���ٽ�ֵ
	 */
	private int decThres;

	/**
	 * �ϳɷ����ٽ�ֵ
	 */
	private double synThres;

	/**
	 * ������������
	 */
	private double stepSize;
	public int minCount = 7000;
	public double maxAcc = 0;
	
	static ArrayList<Molecule> population = new ArrayList<Molecule>();
	public Container(Problem problem) {
		this.problem= problem;
		//globalBest = 1E100;
		initContainer();
		initPopulaction();
	}
	
	public void initContainer() {
		this.setInitPopSize(20);// ��Ⱥ��ģ
		this.setCollRate(0.5);// �������ײ����
		// this.setStepSize(0.2);//����
		this.setEnergyBuffer(1000);// ����������
		this.setInitKE(5000);// ��ʼ����
		this.setLossRate(0.2);// ��������Ч��ײ������ʧ��
		this.setDecThres(500);// �ֽ��ٽ�ֵ
		this.setSynThres(10);// �ϳ��ٽ�ֵ

	}
	
	public void initPopulaction() {
		//FE = initPopSize;
		bestMole =new Molecule(problem,this);
		bestMole.setPE(0);
		// ����ʵ������Ŀ
		for (int i = 0; i < initPopSize; i++) {// ���ѡ��һ������ʵ����Ϊ��ʼ��Ⱥ
		/*	population.add(new Molecule(problem, this));
			Molecule m = (Molecule) population.get(i);
			m.setPE(m.PEmol(m.getMolS()));
			m.setKE(initKE);
			upDate(m);*/
			
			Molecule m= new Molecule(problem,this);
			m.setPE(m.PEmol(m.getMolS()));
			upDate(m);
			m=search(m);
			population.add(i,m);
			m=(Molecule)population.get(i);
		//	System.out.println("��"+i+"����"+m.getPE()+"....."+m.attCount);
		//	System.out.println("��"+i+"����"+m.getPE()+"....."+m.attCount);
		}
	//	bestMole= getBest();
	//	worstMole= getWorst();
	}
	
	public Molecule getBest() {
		double bestfit=0;
		int bestpos=0;
		for(int i=0;i<initPopSize; i++) {
			Molecule m=(Molecule) population.get(i);
			double fit =m.getPE();
			if(fit>bestfit) {
				bestpos=i;
				bestfit=fit;
			}
		}
		return (Molecule) population.get(bestpos);
	}
	
	public Molecule getWorst() {
		double worstfit=2;
		int worstpos=0;
		
		for(int i=0;i<initPopSize; i++) {
			Molecule m=(Molecule) population.get(i);
			double fit =m.getPE();
			if(fit< worstfit) {
				worstpos=i;
				worstfit=fit;
			}
		}
		return (Molecule) population.get(worstpos);
	}
	
	public void upDate(Molecule newMolecule) {
		if (newMolecule.getPE() < bestMole.getPE()) {
			// Update the global record.
			//globalBest = newMolecule.getPE();
			//globalBestStruct = newMolecule.getMolS();
			bestMole.setPE(newMolecule.getPE());
			bestMole.setMolS(newMolecule.getMolS());
		}
	}
	public void upDateH(Molecule newMolecule) {
		if (newMolecule.getPE() > worstMole.getPE()) {
			// Update the global record.
			//globalBest = newMolecule.getPE();
			//globalBestStruct = newMolecule.getMolS();
			population.remove(worstMole);
		    population.add(newMolecule);
		    worstMole=getWorst();
		    bestMole=getBest();
			//bestMole.setPE(newMolecule.getPE());
			//bestMole.setMolS(newMolecule.getMolS());
		}
	}
	
	
	public double run() {

		while (FE < problem.getFELimit() ) {
			if ((Util.r.nextDouble() > collRate) ) {
				int pos = Util.r.nextInt(population.size());
				Molecule p = (Molecule) population.get(pos);
				if (p.decCheck()) {// �ﵽ�ֽ��ٽ�ֵʱ
					Molecule q = new Molecule(problem, this);
					if (p.decomposition(q)) {
						upDate(p);
						upDate(q);
						population.add(q);
						search(p);
						search(q);
					}
					FE ++;
					
				} // On-wall.
				else {
					
					energyBuffer += p.onWallIneffective();// ������Ч��ײ����������յĻ���������
					//onWallIneffective(p);
					upDate(p);
					search(p);
					FE++;
					
					
				}
			} else {
				// Synthesis.
				int pos1 = Util.r.nextInt(population.size());
				int pos2 = pos1;
				while (pos2 == pos1) {
					pos2 = Util.r.nextInt(population.size());
				}
				Molecule p = (Molecule) population.get(pos1);
				Molecule q = (Molecule) population.get(pos2);
			//	if (p.synCheck() && q.synCheck()) {
					if (p.synthesis(q)) {
						upDate(p); // ��������q���Ƴ�������P
						//population.remove(pos2);
						search(p);
					}
					FE ++;
					// Inter-Molecular.
			//	}
		//	else {
				//	interMolecule(p,q);
					p.interMolecular(q);
					upDate(p);
					upDate(q);
					search(p);
					search(q);
					FE++;
		//		}
			}
		//	System.out.println("��"+FE+"�ε�����Ӧ��ֵ"+bestMole.getPE()+"....."+bestMole.attCount);
		}
		return 0- bestMole.getPE();
	}
/*	public void onWallIneffective(Molecule p) {
		int attnum=bestMole.getNumAttribute();
		int[] t1=p.getMolS();
		exchange(t1);
		Molecule newMolecule = new Molecule(problem,this);
		newMolecule.setMolS(t1);
		newMolecule.setPE(newMolecule.PEmol(t1));
		
		if(worstMole.getPE()<newMolecule.getPE()) {
			population.remove(worstMole);
			population.add(worstMole);
			worstMole=getWorst();
			bestMole= getBest();
		}
	}
	
	
	public void interMolecule(Molecule p, Molecule q) {
		int attnum=bestMole.getNumAttribute();
		int[] t1= new int[attnum];
		t1=p.getMolS();
		exchange(t1);
		
		int[] t2= new int[attnum];
		t2=q.getMolS();
		exchange(t2);
		
		Molecule newMolecule1=new Molecule(problem, this);
		newMolecule1.setMolS(t1);
		newMolecule1.setPE(newMolecule1.PEmol(t1));
		
		Molecule newMolecule2=new Molecule(problem, this);
		newMolecule2.setMolS(t2);
		newMolecule2.setPE(newMolecule1.PEmol(t2));
		
		if(worstMole.getPE()<newMolecule1.getPE()&& worstMole.getPE()<newMolecule2.getPE()) {
			population.remove(worstMole);
			population.add(newMolecule1);
			worstMole=getWorst();
			bestMole= getBest();
			
			population.remove(worstMole);
			population.add(newMolecule2);
			worstMole=getWorst();
			bestMole= getBest();
		}
		
	}
	
	*/
	

	Molecule search (Molecule m) {
		Molecule m1=m;
		Molecule bestM=m;
		int t[]=m.getMolS();
		int[] t1=new int[t.length];
		double pe=m.getPE();
		for(int i=0;i<20;i++) {
			neighbor(t,t1);
			m1.setMolS(t1);
			m1.setPE(m1.PEmol(m1.getMolS()));
			upDate(m1);
			if(m1.getPE()<pe) {
				bestM=m1;
				pe=m1.getPE();
			}
			
		}
		return bestM;
	}
	
	void neighbor(int[] mol, int[] tempMole) {
		for (int j=0;j<mol.length;j++) {
			tempMole[j]=mol[j];
			
		}
		int index=Util.r.nextInt(tempMole.length-1);
		if(tempMole[index]==0) {
			tempMole[index]=1;
		}else {
			tempMole[index]=0;
		}
				
	}
	
	
	
	
	
	
	
	void neighbor0(int[] t1) {// ����ı�һ�����ӵ�ĳγ��ֵ
		int pos = Util.r.nextInt(t1.length);
		int temp = Util.r.nextInt(2);
		t1[pos] = temp;
	}
	
	void neighbor1(int[] t1,int start,int end) {
		int pos= start+Util.r.nextInt(end-start);
		double temp=Util.r.nextDouble();
		if(temp<0.007) {
			t1[pos]=1;
		}
	}

	void exchange(int[] t1) {// ��������
		for(int i=0;i<10;i++) {
			int a = Util.r.nextInt(t1.length);
			int b = Util.r.nextInt(t1.length);
			while (a == b)
				b = Util.r.nextInt(t1.length);
			int temp = t1[a];
			t1[a] = t1[b];
			t1[b] = temp;
		}
		
	}
	
	void exchange1(int[] t1,int []t2) {
		int a=	Util.r.nextInt(t1.length/2);
		int b= t2.length/2+Util.r.nextInt(t2.length-t2.length/2-1);
		while(a==b)
		  b= t2.length/2+Util.r.nextInt(t2.length-t2.length/2-1);
		int temp=t1[a];
		t1[a]=t2[b];
		t2[b]=temp;
		
		 a=t1.length/2+Util.r.nextInt(t1.length-t1.length/2-1);
		 b= Util.r.nextInt(t2.length/2);
		while(a==b)
		  b=Util.r.nextInt(t2.length/2);
		temp=t1[a];
		t1[a]=t2[b];
		t2[b]=temp;
	}
	
	public int getInitPopSize() {
		return initPopSize;
	}

	public void setInitPopSize(int initPopSize) {
		this.initPopSize = initPopSize;
	}

	public double getEnergyBuffer() {
		return energyBuffer;
	}

	public void setEnergyBuffer(double energyBuffer) {
		this.energyBuffer = energyBuffer;
	}

	public double getInitKE() {
		return initKE;
	}

	public void setInitKE(double initKE) {
		this.initKE = initKE;
	}

	public double getCollRate() {
		return collRate;
	}

	public void setCollRate(double collRate) {
		this.collRate = collRate;
	}

	public double getLossRate() {
		return lossRate;
	}

	public void setLossRate(double lossRate) {
		this.lossRate = lossRate;
	}

	public int getDecThres() {
		return decThres;
	}

	public void setDecThres(int decThres) {
		this.decThres = decThres;
	}

	public double getSynThres() {
		return synThres;
	}

	public void setSynThres(double synThres) {
		this.synThres = synThres;
	}

	public double getStepSize() {
		return stepSize;
	}

	public void setStepSize(double stepSize) {
		this.stepSize = stepSize;
	}

/*	public int[] getGlobalBestStruct() {
		return globalBestStruct;
	}

	public void setGlobalBestStruct(int[] globalBestStruct) {
		this.globalBestStruct = globalBestStruct;
	}
*/

}
