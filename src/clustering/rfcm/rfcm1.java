/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clustering.rfcm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import clustering.rfcm.Cluster;
import clustering.rfcm.Cluster;

public class rfcm1 {
   
	public static final int n = 500;
	public static final int noOfClusters = 5;
	public static final float epsolon = 0.01f;
	public static final float del =0.3f;
	public static final float wlower = 0.7f;
	public static final float wupper = 1 - wlower;
	public static final int m = 2;	
	public static ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	public static ArrayList<DataPoint> datapoints = new ArrayList<DataPoint>();
	public static float[][] membership = new float[noOfClusters][n];
	public static float[][] oldMembership = new float[noOfClusters][n];
	public static int[][] pointCount = new int[noOfClusters][3];
	
	public static void main(String[] args)
	{
		//read i/p from file
		try
		{
			fetchData();
			
		} catch (NumberFormatException e) {
			System.err.println("Invalid number entries in the file\nFile should only contan comma seperated numbers");
			return;
		}
		if(datapoints.size()<noOfClusters)
		{
			System.out.println("Insufficient points");
			return;
		}
		
		//allocate cluster centroids			
		for(int i=0;i < noOfClusters; i++)
		{
			Cluster c = new Cluster(datapoints.get(i));
			clusters.add(c);
		}		
		
		//calculate membership value of each point for each clusters
		calculateMembership();
		
		//allocate each point to upper or lower approx of the respective clusters
		allocateClusters();
		
		while(!stopSignal())
		{
			determineNewCentroid();
			calculateMembership();
			allocateClusters();
		}		
		//Final Output
              System.out.println("Cluster Output : \n");
                for(int i=0;i<noOfClusters;i++)
                {
                    System.out.print("Cluster "+(i+1)+" : ");
                    for(int j=0;j<datapoints.size();j++)
                    {
                        if(membership[i][j]!=0)
                        {
                       System.out.print(membership[i][j]*100+"% of ");
                            System.out.print("(");
                            for(int k=0; k<datapoints.get(j).point.size();k++)
                            {
                                System.out.print(datapoints.get(j).point.get(k)+ " ");
                                        }
                            System.out.print(")  ");
                        }        
                  
                    }
                    System.out.println();
	}
                System.out.println();
	
                 System.out.println("Cluster  Low   Upper   Total");
                 for(int i=0;i<noOfClusters;i++)
                {
                    System.out.println((i+1)+"\t"+pointCount[i][0]+"\t"+ pointCount[i][1]+"\t"+pointCount[i][2]);
                   
                }
	}
	
	
	private static void fetchData() throws NumberFormatException
	{		
		try
		{
			FileReader freader = new FileReader("Data.txt");
			BufferedReader breader = new BufferedReader(freader);	
			String dataLine = breader.readLine();
			while(dataLine!=null)
			{
				
				ArrayList<Float> dim = new ArrayList<Float>();
				
				String[] dimString = dataLine.split(",");				
				for (String string : dimString)
				{
					string = string.trim();
					dim.add(Float.parseFloat(string));					
				}
				DataPoint datapoint = new DataPoint(dim);
				datapoints.add(datapoint);
				dataLine = breader.readLine();
				
			}
			breader.close();
		}
		catch(NumberFormatException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	private static void calculateMembership()
	{
		for(int i=0; i < noOfClusters; i++)
		{
			for(int j=0; j<datapoints.size();j++)
			{
				oldMembership[i][j]= membership[i][j];
			}
		}
	
		
		for(int i = 0; i < noOfClusters; i++)
		{
			for(int j=0; j<datapoints.size();j++)
			{
				membership[i][j]=-1;
			}
		}
				 //System.out.println("Distance Matrix : \n");
		for(int i = 0; i < noOfClusters; i++)
		{
			for(int j=0; j<datapoints.size();j++)
			{
				float dij = DataPoint.distanceBetween(clusters.get(i).centroid,datapoints.get(j) );
				//System.out.print(dij+"  ");
				
				if(dij==0.0f)
				{
					membership[i][j]= 1;
					for(int h=0;h<noOfClusters;h++)
					{
						if(h!=i)
						{	
							membership[h][j]=0; 
						}
					}
				}
				else if(membership[i][j]==-1) 
				{
					membership[i][j]=0;
					for(int k = 0; k < noOfClusters; k++)
					{
						membership[i][j]+=Math.pow((dij/DataPoint.distanceBetween(datapoints.get(j),clusters.get(k).centroid)), 2.0/(m-1));
						
					}
                                        if(membership[i][j]!=0)
					membership[i][j] = 1/membership[i][j];
				}
				
			}
                   //     System.out.println();
		}
                /*
                System.out.println("Membership values after calculation: ");
                for(int i=0;i<noOfClusters;i++)
                {
                    for(int j=0;j<datapoints.size();j++)
                        System.out.print(membership[i][j]+ " ");
                    System.out.println();
                }*/
                 
	}

	private static void allocateClusters()
	{
		System.out.println();
		System.out.println();
		
		//compute membership matrix based on distances
		for(int i=0;i<datapoints.size();i++)
		{
			int max2Pos,maxPos=0;
			
			//finding closest jth cluster for ith point
			for(int j=0;j<noOfClusters;j++)
			{
				if(membership[j][i]>membership[maxPos][i])
					maxPos = j;
			}
			
			//finding 2nd closest cluster for jth point
			if(maxPos==0)
			 max2Pos = 1;
			else max2Pos =0;
			for(int j=0;j<noOfClusters;j++)
			{
				if(j!=maxPos && membership[j][i]>membership[max2Pos][i])
					max2Pos = j;
			}
			
                        
			if(membership[maxPos][i]-membership[max2Pos][i]>del)
			{
                                //System.out.println("i="+i);
				membership[maxPos][i]= 1;
				for(int k=0;k<noOfClusters;k++)
				{
					if(k!=maxPos)
						membership[k][i]=0.0f;
				}
			}	
		}
                
               /* System.out.println("Membership Values after allocation : \n");
                for(int i=0;i<noOfClusters;i++)
                {
                    for(int j=0;j<datapoints.size();j++)
                        System.out.print(membership[i][j]+ " ");
                    System.out.println();
                }*/
	}
	
	private static void determineNewCentroid()
	{
              System.out.println("Cluster Centroids: \n");
		for(int i=0;i<noOfClusters;i++)
		{
		ArrayList<Float> lowerApproxComponent = new ArrayList<Float>();
		ArrayList<Float> upperApproxComponent = new ArrayList<Float>();
                    for(int k=0;k<datapoints.get(0).point.size();k++)
		    {
                    lowerApproxComponent.add(0.0f);
		    upperApproxComponent.add(0.0f);
			}
               
			int lowerApproxCount=0,uc=0;
                        float upperApproxCount=0.0f;
			for(int j=0;j<datapoints.size();j++)
			{
				if(membership[i][j]==1.0f)
				{
					lowerApproxCount++;
					for(int k=0;k<datapoints.get(j).point.size();k++)
					{
					lowerApproxComponent.set(k, lowerApproxComponent.get(k)+datapoints.get(j).point.get(k));
					}
				}
				else if(membership[i][j]>0.0f && membership[i][j]<1.0f)
				{
                                    uc++;
					upperApproxCount+=(float)Math.pow(membership[i][j], m);
					for(int k=0;k<datapoints.get(j).point.size();k++)
					{
						upperApproxComponent.set(k, upperApproxComponent.get(k)+(float)Math.pow(membership[i][j], m)*datapoints.get(j).point.get(k));
					}
				}
			}
			pointCount[i][0]= lowerApproxCount;
                        pointCount[i][1]= uc;
                        pointCount[i][2]=lowerApproxCount+uc;
			ArrayList<Float> clusterCentroid = clusters.get(i).centroid.point;
			for(int k=0;k<datapoints.get(0).point.size();k++)
			{
				clusterCentroid.set(k,0.0f);	
			}
                if(lowerApproxCount==0&&upperApproxCount==0)
                {
                     for(int k = 0;k<clusterCentroid.size();k++)
				{
				clusterCentroid.set(k,999.0f);
				}  
                }
                else
                {
			if(lowerApproxCount==0)
			{
				for(int k = 0;k<clusterCentroid.size();k++)
				{
                                
					clusterCentroid.set(k,upperApproxComponent.get(k)/upperApproxCount);
				}
			}
			else if(upperApproxCount==0)
			{
				for(int k = 0;k<clusterCentroid.size();k++)
				{
                                   
					clusterCentroid.set(k,lowerApproxComponent.get(k)/lowerApproxCount);
				}
			}
			else
			{
				for(int k = 0;k<clusterCentroid.size();k++)
				{
				clusterCentroid.set(k,wlower*lowerApproxComponent.get(k)/lowerApproxCount+wupper*upperApproxComponent.get(k)/upperApproxCount);
				}
			}
                }
                
			System.out.println(clusterCentroid);
                        }
		
                System.out.println();
                }
	
	
	private static boolean stopSignal()
	{
            
		for(int i=0;i<noOfClusters;i++)
		{
			for(int j=0;j<datapoints.size();j++)
			{
		        if(Math.abs(oldMembership[i][j]-membership[i][j])>epsolon)
			return false;	
			}
		}
		return true;
	}
}
    

