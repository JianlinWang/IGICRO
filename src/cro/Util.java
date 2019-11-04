package cro;

import java.util.Random;

public class Util {
	/**
	 * This variable is a randomizer.
	 */
	public static Random r = new Random();

	static void sort(double[] R, int low, int high) {
		// 建堆、排序,R指堆，调整low和high之间的顺序
		int i = low, j = 2 * i;
		double temp = R[i - 1];
		while (j <= high) {
			if (j < high && R[j - 1] < R[j]) {
				j++;
			}
			if (temp < R[j - 1]) {
				R[i - 1] = R[j - 1];
				i = j;
				j = 2 * i;
			} else
				break;
		}
		R[i - 1] = temp;

	}

	/*
	 * static double[] bestAtt(double[] sol){//堆排序 double[] best=new
	 * double[Main.numBestAtt]; double[] alt=sol; for(int
	 * i=sol.length/2;i>=1;i--){//建立初始堆 sort(alt,i,sol.length); } for(int
	 * j=0;j<best.length;j++){//排序 best[j]=alt[0]; alt[0]=alt[alt.length-j-1];
	 * sort(alt,1,alt.length-1-j); }
	 * 
	 * return best; }
	 */

	/**
	 * This function calculates the mean of a vector.
	 * 
	 * @param inputVector
	 *            The input vector whose mean value is to be calculated.
	 * @return The mean value of the input vector.
	 */
	static double mean(double inputVector[]) {
		double sum = 0;
		for (int i = 0; i < inputVector.length; i++) {
			sum += inputVector[i];
		}
		return sum / inputVector.length;
	}

	/**
	 * This function calculates the standard deviation of a vector.
	 * 
	 * @param inputVector
	 *            The input vector whose standard deviation is to be calculated.
	 * @return The standard deviation of the input vector.
	 */
	static double std(double inputVector[]) {
		double avg = mean(inputVector);
		double std = 0;
		for (int i = 0; i < inputVector.length; i++) {
			std += (avg - inputVector[i]) * (avg - inputVector[i]);
		}
		return Math.sqrt(std / inputVector.length);
	}

	/**
	 * This function gets the best, or smallest, value from a vector.
	 * 
	 * @param inputVector
	 *            The input vector whose best value is to be calculated.
	 * @return The best value of the input vector.
	 */
	static double best(double inputVector[]) {
		double best = inputVector[0];
		for (int i = 0; i < inputVector.length; i++) {
			if (inputVector[i] < best) {
				best = inputVector[i];
			}
		}
		return best;
	}

	/**
	 * This function gets the worst, or largest, value from a vector.
	 * 
	 * @param inputVector
	 *            The input vector whose worse value is to be calculated.
	 * @return The worse value of the input vector.
	 */
	static double worst(double inputVector[]) {
		double worst = inputVector[0];
		for (int i = 0; i < inputVector.length; i++) {
			if (inputVector[i] > worst) {
				worst = inputVector[i];
			}
		}
		return worst;
	}

	/**
	 * This function copies a vector to another one. Please note that there is
	 * no length checking mechanism in this function, so make sure that the
	 * length and data type of both input vectors are identical.
	 * 
	 * @param t1
	 *            The source vector.
	 * @param is
	 *            The destination vector.
	 */
	static void copySln(int[] t1, int[] is) {
		System.arraycopy(t1, 0, is, 0, t1.length);
	}

}
