package wekaTest;
//package wekaTest;

import java.util.List;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class PrivacyProtection {
	//CLIFF+MORPH������IPR��ֵ
	public static double rawscheme(String sourcefilename,String destinationfilename,int binnum,double remainrate,double lowerborder,double upperborder,int[] querynum) throws Exception{
		List<String> attributeset=getAttributeFromFile(sourcefilename);
		List<List<Double>> dataset = loaddata(sourcefilename);
		int[][] binindexofrawdataset = devideIntoBins(dataset,binnum);
		double[][] power=calculatePower(dataset,binindexofrawdataset,binnum);
		prunRecord(dataset,power,remainrate);
		int[] binindexofsensitiveattribute=new int[dataset.size()];
		for(int i=0;i<dataset.size();i++)
			binindexofsensitiveattribute[i]=binindexofrawdataset[i][0];
		dataset=MORPH(dataset,lowerborder,upperborder);
		writeData(destinationfilename,attributeset,dataset);
		dataset = loaddata(destinationfilename);
		int[][] binindexofdatasetafterprotection = devideIntoBins(dataset,binnum);
		double IPR=calculateIPR(binindexofrawdataset,binindexofdatasetafterprotection,binindexofsensitiveattribute,querynum,binnum);
		return IPR;
		//return 0;
	}
	
	//��ȡ���е�����ֵ
	private static List<String> getAttributeFromFile(String filename) throws Exception{
		CSVFileUtil dataset = new CSVFileUtil(filename);
		String attributeLine = dataset.readLine();
		List<String> attribute = new ArrayList<String>();
		attribute = CSVFileUtil.fromCSVLinetoArray(attributeLine);
		return attribute;
	}
	
	//�������ݣ������ݷ��ڶ�ά�б���
	private static List<List<Double>> loaddata(String filename) throws Exception{
		CSVFileUtil trainset = new CSVFileUtil(filename);
		String attributeLine = trainset.readLine();
		List<String> attribute = new ArrayList<String>();
		attribute = CSVFileUtil.fromCSVLinetoArray(attributeLine);
		String line = new String();
		List<List<Double>> dataset = new ArrayList<List<Double>>();
		
		List<String> recordread = new ArrayList<String>();
		while(true) {
			line = trainset.readLine();
			if(line == null) break;
			recordread = CSVFileUtil.fromCSVLinetoArray(line);
			List<Double> record = new ArrayList<Double>();
			for(int i=0;i<recordread.size();i++){
				//if(attribute.get(i).equals("name") || attribute.get(i).equals("version") || attribute.get(i).equals("packagename"))
					//continue;
				if(attribute.get(i).equals("bug"))
					if(recordread.get(i).equals("Y"))
						record.add((double) 1);
					else
						record.add((double) 0);
				else
					record.add(Double.parseDouble(recordread.get(i)));
			}
			dataset.add(record);
		}
		
		return dataset;
	}
	
	//��������ֵ�����������䣬�������ű�ʾ
	//��1�α�ʾΪ0����2�α�ʾΪ1����������n�α�ʾΪn-1
	private static int[][] devideIntoBins(List<List<Double>> dataset,int binnum) throws Exception{
		double binborderindex;
		int[][] binindex = new int[dataset.size()][dataset.get(0).size()-1];//binindex������������
		double[] attributevalue = new double[dataset.size()];//binindex���ĳһ�������е�ֵ��һ�����ݣ�
		double[] binupborder = new double[binnum];//binupborder���ÿһ��������Ҷ˵�ֵ
		for(int j=0;j<dataset.get(0).size()-1;j++){
			//���潫��j������д��attributevalue�в���������
			for(int i=0;i<dataset.size();i++)
				attributevalue[i] = dataset.get(i).get(j);//�����ݼ��е�ֵд��attributevalue
			Arrays.sort(attributevalue);//��attributevalue�е�����ֵ��������
			//����Ѱ������˵�ֵ����i������=attributevalue[����Ԫ�ظ���/�ܶ���*(i+1)-1]��i��0��ʼ
			for(int k=0;k<binnum-1;k++)
			{
				binborderindex=(dataset.size()+0.0)/binnum*(k+1)-1;
				binupborder[k]=attributevalue[(int)binborderindex];//��������attributevalue���ȷֳ�10�Σ�ȡ��ÿһ�����ұߵ�ֵ
			}
			binupborder[binnum-1]=attributevalue[dataset.size()-1];//������һ�е�������±�������ȡ����Ϊ��ʹ���һ�ε��Ҷ˵���attributevalue�е����ֵ���˴���������
			for(int i=0;i<dataset.size();i++){//��ѭ���������е�ֵ���������Ѽ�����Ķ˵�ֵ�����ǹ��ൽ��ͬ��������
				int k;
				for(k=0;k<binnum;k++)
					if(dataset.get(i).get(j)<=binupborder[k])
						break;
				binindex[i][j]=k;
			}
		}		
		return binindex;
	}
	
	//����Ȩ�أ�Ҫ��class��List�����һ��
	private static double[][] calculatePower(List<List<Double>> dataset,int[][] binindex,int binnum) throws Exception{
		double[][] power = new double[dataset.size()][dataset.get(0).size()-1];//power����������ݵ�Ȩ��
		int[][] bincount = new int[binnum][2];//��һ�д�Ÿ������������class=0���������ڶ��д�Ÿ������������class=1������
		double[][] binfrequency = new double[binnum][2];//binfrequency���������ݵ�ֵ������bincount�ж�Ӧ��ֵ����bincount���������ݵĺ�
		for(int j=0;j<dataset.get(0).size()-1;j++){//��ѭ�������ݼ��е�ÿһ�����ݷֱ���м���
			for(int k=0;k<binnum;k++){//��ѭ����bincount�����е����ݳ�ʼ��Ϊ0�����˻��ɸ�ѭ��Ӧ�÷����������j��ѭ����
				bincount[k][0]=0;
				bincount[k][1]=0;
			}
			//���濪ʼ��bincount��binfrequency��ֵ
			for(int i=0;i<dataset.size();i++)
				bincount[binindex[i][j]][dataset.get(i).get(dataset.get(0).size()-1).intValue()]++;
			for(int k=0;k<binnum;k++){
				binfrequency[k][0]=(double)bincount[k][0]/dataset.size();
				binfrequency[k][1]=(double)bincount[k][1]/dataset.size();
			}
			//�������Ȩ��
			for(int i=0;i<dataset.size();i++){//����һ�������е���������
				power[i][j]=binfrequency[binindex[i][j]][dataset.get(i).get(dataset.get(0).size()-1).intValue()];
				power[i][j]*=power[i][j];
				power[i][j]/=(binfrequency[binindex[i][j]][0]+binfrequency[binindex[i][j]][1]);
			}
		}
		return power;
	}
	
	//ɾ��Ȩ��С������
	private static void prunRecord(List<List<Double>> dataset,double[][] power,double remainrate) throws Exception{
		//����ֱ����dataset��class=0��class=1�ļ�¼�������Լ�����Ȩ�صĳ˻�
		int sum=0;//sum���class=1�ļ�¼����
		double[] powerproduct = new double[dataset.size()];//powerproduct���ÿһ����¼�������Ե�Ȩ�صĳ˻�
		for(int i=0;i<dataset.size();i++){
			sum+=dataset.get(i).get(dataset.get(0).size()-1).intValue();//��class�����е�����ֵ��Ӽ��ɵõ�sum
			//System.out.println(dataset.get(i).get(dataset.get(0).size()-1).intValue());
			powerproduct[i]=1;
			for(int j=0;j<dataset.get(0).size()-1;j++)//��ѭ�����ڼ���˻�
				powerproduct[i]*=power[i][j];
		}
		//���潫class=0��class=1�ļ�¼�ֵ���ͬ��������
		double[] powerproductclass1 = new double[sum];//powerproductclass1���class=1��ÿһ����¼�������Ե�Ȩ�صĳ˻�
		double[] powerproductclass0 = new double[dataset.size()-sum];//powerproductclass1���class=0��ÿһ����¼�������Ե�Ȩ�صĳ˻�
		int pointer1=0,pointer0=0;//pointer1��pointer0�ֱ���ʵ��д��ĳ˻�����
		for(int i=0;i<dataset.size();i++){//��ѭ���Գ˻����й�����
			if(dataset.get(i).get(dataset.get(0).size()-1).intValue()==1)
				powerproductclass1[pointer1++]=powerproduct[i];
			else
				powerproductclass0[pointer0++]=powerproduct[i];
		}
		//��������Ƿ�ɾ�����ٽ�ֵ
		Arrays.sort(powerproductclass1);//���˻���С�����������
		Arrays.sort(powerproductclass0);
		double thresholdclass1 = powerproductclass1[(int)((1-remainrate)*sum)];//����ɾ����¼���ٽ�ֵ
		double thresholdclass0 = powerproductclass0[(int)((1-remainrate)*(dataset.size()-sum))];
		//����ɾ��Ȩ��С���ٽ�ֵ�ļ�¼
		for(int i=dataset.size()-1;i>=0;i--)
			if(dataset.get(i).get(dataset.get(0).size()-1).intValue()==1){
				if(powerproduct[i]<=thresholdclass1)
					dataset.remove(i);
			}
			else{
				if(powerproduct[i]<=thresholdclass0)
					dataset.remove(i);
			}
	}
	
	//MORPHģ��������
	private static List<List<Double>> MORPH(List<List<Double>> dataset,double lowerborder,double upperborder) throws Exception{
		List<List<Double>> datasetafterMORPH=new ArrayList<List<Double>>();//datasetafterMORPH���ģ����֮��Ľ��
		double mindistance,distance,randomwithsign,rate;//mindistance���������¼�����С���룬randomwithsign�ڽ�0��1�������ӳ�䵽�涨��Χʱʹ��
		int mindistanceindex,classcolindex=dataset.get(0).size()-1;//mindistanceindex����뵱ǰ��¼��������ļ�¼���±꣬classcolindex���class���������к�
		for(int i=0;i<dataset.size();i++){
			//�����ҳ����i����¼�в���ͬ��class��ֵ�����i����¼��������ļ�¼������Ӧ���±걣����mindistanceindex��
			int ianother=0;//ianother����ָʾ��i��ͬ��ļ�¼�±�
			for(;ianother<dataset.size();ianother++)//�ҵ����ݼ�����class��ͬ��ĵ�һ��Ԫ��ͣ��
				if(dataset.get(i).get(classcolindex)!=dataset.get(ianother).get(classcolindex))
					break;
			mindistance=PrivacyProtection.calculateDistance(dataset.get(i),dataset.get(ianother),classcolindex);//����ǰ��¼����class��ͬ�ĵ�һ����¼��ľ�����Ϊ��ʼʱ����̾���
			mindistanceindex=ianother;
			for(;ianother<dataset.size();ianother++)//����̾��룬�Ż�mindistance�Ͷ�Ӧ��mindistanceindex
				if(dataset.get(i).get(classcolindex)!=dataset.get(ianother).get(classcolindex)){
					distance=PrivacyProtection.calculateDistance(dataset.get(i),dataset.get(ianother),classcolindex);
					if(distance<mindistance){
						mindistance=distance;
						mindistanceindex=ianother;
					}
				}
			//����ģ����֮���ֵ�����ӵ�datasetafterMORPH��
			List<Double> recordafterMORPH=new ArrayList<Double>();//recordafterMORPH��ż�����
			recordafterMORPH.add(dataset.get(i).get(0));//��ģ����ǰ�����ݷ���recordafterMORPH����Ϊ��ʼֵ
			for(int j=1;j<classcolindex;j++){
				do
					randomwithsign=java.lang.Math.random()-0.5;//randomwithsignΪ-0.5��0.5֮����������������0��whileѭ�����ڹ��˵�0��
				while(randomwithsign==0.0);
				rate=(2*(upperborder-lowerborder)*java.lang.Math.abs(randomwithsign)+lowerborder)*java.lang.Math.signum(randomwithsign);//rateΪ-beta��-alpha��alpha��beta��������
				double valueafterMORPH=dataset.get(i).get(j)+(dataset.get(i).get(j)-dataset.get(mindistanceindex).get(j))*rate;//����ģ����֮���ֵ
				recordafterMORPH.add(valueafterMORPH);//��������д��recordafterMORPH��
			}
			recordafterMORPH.add(dataset.get(i).get(classcolindex));//д��class��ֵ
			datasetafterMORPH.add(recordafterMORPH);
		}
		return datasetafterMORPH;
	}
	
	//����������¼���ŷ�Ͼ��룬classcolnumΪ�����������ά��
	private static double calculateDistance(List<Double> record1,List<Double> record2,int classcolindex) throws Exception{
		double distance=0;
		if(record1.size()!=record2.size())//������������ά����ͬ������
			throw new Exception("Two records don't have the same attributes!");
		for(int i=0;i<record1.size();i++)
			if(i!=classcolindex)
				distance+=((record1.get(i)-record2.get(i))*(record1.get(i)-record2.get(i)));
		distance=java.lang.Math.sqrt(distance);
		return distance;
	}
	
	//����¼д���ļ�
	private static void writeData(String filename,List<String> attributeset,List<List<Double>> dataset) throws Exception{
		File file=new File(filename);
		FileOutputStream out=new FileOutputStream(file);
		OutputStreamWriter osw=new OutputStreamWriter(out,"UTF8");
		BufferedWriter bw=new BufferedWriter(osw);
		String temp;
		for(int j=0;j<attributeset.size();j++){
			bw.write(attributeset.get(j));
			if(j!=attributeset.size()-1)
				bw.write(",");
		}
		bw.write("\r\n");
		for(int i=0;i<dataset.size();i++){
			ArrayList rowtowrite=new ArrayList();
			for(int j=0;j<dataset.get(0).size();j++){
				temp=dataset.get(i).get(j).toString();
				if(j==dataset.get(0).size()-1){
					if(temp.equals("1.0"))
						rowtowrite.add("Y");
					else
						rowtowrite.add("N");
				}
				else
					rowtowrite.add(temp);
			}
			String csvrowtowrite=CSVFileUtil.ListtoCSVLine(rowtowrite);
			bw.write(csvrowtowrite+"\r\n");
		}
		bw.close();
		osw.close();
		out.close();
	}
	
	//����IPR��ֵ��Ҫ���0�д����������
	private static double calculateIPR(int[][] binindexofrawdataset,int[][] binindexofdatasetafterprotection,int[] binindexofsensitiveattribute,int[] querynum,int binnum) throws Exception{
		int sum=0,differentnum=0,protecteddataresult;//sum���������Ч��ѯ�Ĵ�����differentnum�����˽����ǰ���ѯ�����ͬ�Ĵ�����protecteddataresult������˽������Ĳ�ѯ���
		int[] rawdataresult=new int[2];//rawdataresult�ĵ�һ��Ԫ�ش�ŷ��ؼ�¼�����ڶ���Ԫ�ش����Щ��¼����˽���Գ�������������
		List<int[]> queryset=new ArrayList<int[]>();//queryset����Ѿ�ʹ�ù��Ĳ�ѯ
		for(int k=0;k<querynum.length;k++){
			for(int m=0;m<querynum[k];m++){
				int[] query=queryGenerator(binindexofrawdataset[0].length,k+1,binnum);//query�ĳ���Ϊ���Ը�����query�ĵ�i��Ԫ�ش����ò�ѯҪ���¼�е�i�����Ե�ֵ���ڵ�i����������
				if(queryset.contains(query)){//������ɵĲ�ѯ�ѳ��ֹ��������ѭ������
					m--;//��ô�ѭ����m++�������ʹm��ֵ�������κα仯
					continue;
				}
				rawdataresult=mostSensitiveIndex(binindexofrawdataset,binindexofsensitiveattribute,query,binnum);//����ԭʼ���ݼ���ѯ�󷵻ص���������
				protecteddataresult=mostSensitiveIndex(binindexofdatasetafterprotection,binindexofsensitiveattribute,query,binnum)[1];//������˽����������ݼ���ѯ�󷵻ص���������
				//System.out.println(rawdataresult[0]);
				if(rawdataresult[0]<2){////���ԭʼ���ݼ��з��صļ�¼��С�������������ѭ������
					m--;
					continue;
				}
				sum++;
				if(rawdataresult[1]!=protecteddataresult)//��˽����ǰ���ѯ�����ͬʱdifferentnum��ֵ��1
					differentnum++;
			}
		}
		double IPR=(double)differentnum/sum;
		return IPR;
	}
	
	//�����ѯ��������Ҫ���0�д���������ԣ���������ڳ���Ϊattributenum�������У�-1��ʾ��Ӧ����û������
	private static int[] queryGenerator(int attributenum,int querysize,int binnum) throws Exception{
		int[] query=new int[attributenum];//query�ĳ���Ϊ���Ը�����query�ĵ�i��Ԫ�ش����ò�ѯҪ���¼�е�i�����Ե�ֵ���ڵ�i����������
		//����Ҫ���Ĺ����������query�е�querysize����ѯ���ȣ���ֵ�����Ϊĳ��������ı�ţ�����Ԫ�ض���ֵΪ-1
		for(int i=0;i<attributenum;i++)
			query[i]=-1;
		int index;
		Random r=new Random();
		for(int i=0;i<querysize;i++){
			do
				index=r.nextInt(attributenum-1)+1;
			while(query[index]!=-1);
			query[index]=r.nextInt(binnum);
		}
		return query;
	}
	
	//���ݸ����Ĳ�ѯҪ����в�ѯ��ͳ������Ҫ��ļ�¼�����ͳ���������������,�ó���Ϊ2�����鱣��������ֵ,��������Ϊ-1��ʾ��ѯ�޽��
	private static int[] mostSensitiveIndex(int[][] binindex,int binindexofsensitiveattribute[],int[] query,int binnum) throws Exception{
		//recordnuminindex��Ų�ѯ�������������ֵ�ڸ����������еļ�¼����
		int[] recordnuminindex=new int[binnum];//recordnuminindex��������ѯҪ��ļ�¼��������������ÿһ���������ϵļ�¼����
		int[] result=new int[2];//result[0]�������Ҫ��ļ�¼������result[1]��ų�������������������������
		result[0]=result[1]=0;
		boolean flag;
		for(int i=0;i<binindex.length;i++){//��ѭ���������ݼ��е�ÿһ����¼
			flag=true;//��¼�Ƿ������ѯҪ��ı�־����ʼΪtrue������ĳ�����Բ������ѯҪ��ʱ��Ϊfalse
			for(int j=1;j<binindex[0].length;j++)//������¼���������ԣ�����Ƿ������ѯҪ��
				if(query[j]!=-1)
					if(binindex[i][j]!=query[j])
						flag=false;
			if(flag==false)
				continue;
			recordnuminindex[binindex[i][0]]++;
			result[0]++;
		}
		//�ҳ�recordnuminindex���Ԫ�ض�Ӧ���±����result[1]��
		int maxvalue=0,k;
		for(k=0;k<binnum;k++)
			if(recordnuminindex[k]>maxvalue){
				maxvalue=recordnuminindex[k];
				result[1]=k;
			}
		if(result[0]==0)
			result[1]=-1;
		return result;
	}
}