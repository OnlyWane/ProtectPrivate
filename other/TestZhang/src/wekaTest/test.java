package wekaTest;

import java.util.List;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;


public class test {
	public static void main(String[] args) throws Exception{
		int[] querySize = {20,30,50,1000};
		for(int i = 2; i < 10; i++)
		System.out.println(PrivacyProtection.rawscheme("ant-1.3.csv", "ant-1.3_hide.csv", i, 0.4, 0.15, 0.35, querySize));
		calculateValue(10, 0.4, 0.15, 0.35, querySize, "ant-1.3.csv", "camel-1.0.csv");
//		System.out.println(PrivacyProtection.rawscheme("camel-1.0.csv", "camel-1.0_hide.csv", i, 0.4, 0.15, 0.35, querySize));
//		System.out.println(PrivacyProtection.rawscheme("poi-1.5.csv", "poi-1.5_hide.csv", i, 0.4, 0.15, 0.35, querySize));
//		System.out.println(PrivacyProtection.rawscheme("velocity-1.4.csv", "velocity-1.4_hide.csv", i, 0.4, 0.15, 0.35, querySize));
//		System.out.println(PrivacyProtection.rawscheme("xalan-2.4.csv", "xalan-2.4_hide.csv", i, 0.4, 0.15, 0.35, querySize));
//		System.out.println(PrivacyProtection.rawscheme("xerces-1.2.csv", "xerces-1.2_hide.csv", i, 0.4, 0.15, 0.35, querySize));
		
		/*// TODO Auto-generated method stub
		int binnum = 10; double p = 0.4; double alpha = 0.15; double beta = 0.35; int filenum = 10;
		int[] querynum={0,0,0,1000};
		String[] predictor = {"NB","SVM","NN"};
		String[] indicator = {"g_measure","AUC"};
		File[] file = new File[predictor.length * indicator.length];
		FileOutputStream[] out = new FileOutputStream[predictor.length * indicator.length];
		OutputStreamWriter[] osw = new OutputStreamWriter[predictor.length * indicator.length];
		BufferedWriter[] bw = new BufferedWriter[predictor.length * indicator.length];
		for(int i = 0; i < predictor.length; i ++)
			for(int j = 0; j < indicator.length; j ++){
				int index = i * indicator.length + j;
				file[index] = new File("result\\" + predictor[i] + "_" + indicator[j] + ".csv");
				out[index] = new FileOutputStream(file[index]);
				osw[index] =new OutputStreamWriter(out[index], "UTF8");
				bw[index] = new BufferedWriter(osw[index]);
			}
		
		File fileIPR=new File("result\\IPR.csv");
		FileOutputStream outIPR=new FileOutputStream(fileIPR);
		OutputStreamWriter oswIPR=new OutputStreamWriter(outIPR,"UTF8");
		BufferedWriter bwIPR=new BufferedWriter(oswIPR);
		
		double temp[];
		for(int datasetindex = 6; datasetindex < 7; datasetindex ++){
//			System.out.print(filenum);
			for(int i1 = 0; i1 < filenum; i1 ++){
//				System.out.print(p);
				temp = calculateValue(binnum, p, alpha, beta, querynum, i1 + ".csv", datasetindex + ".csv");
				System.out.print(temp);
				for(int j = 0; j <6; j ++)
					bw[j].write(temp[j] + ",");
				bwIPR.write(temp[6] + ",");
			}
		
			for(int i1 = 0; i1 < filenum - 1; i1 ++)
				for(int i2 = i1 + 1; i2 < filenum; i2 ++){
					temp = calculateValue(binnum, p, alpha, beta, querynum, i1 + "_" + i2 + ".csv", datasetindex + ".csv");
					for(int j = 0; j <6; j ++)
						bw[j].write(temp[j] + ",");
					bwIPR.write(temp[6] + ",");
				}
		
			for(int i1 = 0; i1 < filenum - 2; i1 ++)
				for(int i2 = i1 + 1; i2 < filenum - 1; i2 ++)
					for(int i3 = i2 + 1; i3 < filenum; i3 ++){
						temp = calculateValue(binnum, p, alpha, beta, querynum, i1 + "_" + i2 + "_" + i3 + ".csv", datasetindex + ".csv");
						for(int j = 0; j <6; j ++)
							bw[j].write(temp[j] + ",");
						bwIPR.write(temp[6] + ",");
					}
		
			for(int j = 0; j <6; j ++)
				bw[j].write("\n");
			bwIPR.write("\n");
		}

		for(int j = 0; j <6; j ++){
			bw[j].close();
			osw[j].close();
			out[j].close();
		}
		bwIPR.close();
		oswIPR.close();
		outIPR.close();*/
	
	}
	
	private static double[] calculateValue(int binnum, double p, double alpha, double beta, int[] querynum, String trainsetfile, String testsetfile) throws Exception{
		System.out.println(trainsetfile + "\t" + testsetfile);
		double[] result = new double[7]; double temp[];
		for(int i = 0; i < 10; i ++){
//			ipr隐私性
			result[6] += PrivacyProtection.rawscheme(trainsetfile,"DatasetAfterProcessing.csv", binnum, p, alpha, beta, querynum);
			temp = wekaTest.Prediction.NBprediction("DatasetAfterProcessing.csv", testsetfile);
//			g-m 和AUC表示准确性
			result[0] += temp[0];
			result[1] += temp[1];
			temp = wekaTest.Prediction.SVMprediction("DatasetAfterProcessing.csv", testsetfile);
			result[2] += temp[0];
			result[3] += temp[1];
			temp = wekaTest.Prediction.NNprediction("DatasetAfterProcessing.csv", testsetfile);
			result[4] += temp[0];
			result[5] += temp[1];
			System.out.print("turn " + i + "\t");
//			System.out.print(result[i] + "\t");
		}
		System.out.println();
		for(int i = 0; i < 7; i ++){
			result[i] /= 10;
			System.out.print(result[i] + "\t");
		}
		System.out.println();
		return result;
	}
}
