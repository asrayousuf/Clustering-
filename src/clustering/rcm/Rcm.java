/*
 * Distance matrix format : Points X Clusters
 * Membership matrix format : clusters X Points 
 */
package clustering.rcm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import clustering.rcm.Cluster;
import clustering.rcm.DataPoint;

public class Rcm
{
    public static final int n= 500;
	public static final int noOfClusters =5;
	public static final float del = 1000.0f;
	public static final float wlower = 0.7f;
	public static final float wupper = 1 - wlower;
	public static ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	public static ArrayList<DataPoint> datapoints = new ArrayList<DataPoint>();
        public static ArrayList<DataPoint> dataCopy = new ArrayList<DataPoint>();
	public static float[][] distance = new float[n][noOfClusters];
	public static float[][] membership = new float[noOfClusters][n];
	public static float[][] oldMembership = new float[noOfClusters][n];
	public static int[][] pointCount = new int[noOfClusters][3];
        
	public static void main(String args[])
	{
		
		//read i/p from file
		try
		{
			fetchData();
			
		} catch (NumberFormatException e) {
			System.err.println("Invalid number entries in the file\nFile should only contan comma seperated numbers");
			return;
		}
                System.out.println("No of points: "+datapoints.size());
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
		
		//calculate initial set of distance of every point from every centroid
		calculateDistance();
		/*
		for(int i=0; i<datapoints.size() ; i++)
		{
			for(int j=0;j<noOfClusters;j++)
			{
				System.out.print(distance[i][j]+" ");  
			}
			System.out.println();
		}
		*/
		
		//allocate each point to upper or lower approx of the respective clusters
		allocateClusters();
		
		while(!stopSignal())
		{
			determineNewCentroid();
			calculateDistance();
                   /*     for(int i=0; i<datapoints.size() ; i++)
                        {
			for(int j=0;j<noOfClusters;j++)
			{
				System.out.print(distance[i][j]+" ");  
			}
			System.out.println();
                        }*/
			allocateClusters();
		}	
              
                System.out.println();
                for(int i=0;i<noOfClusters;i++)
                {
                    System.out.print("Cluster "+(i+1)+" : ");
                    for(int j=0;j<datapoints.size();j++)
                    {
                        if(membership[i][j]==1.0f||membership[i][j]==0.5f)
                        {
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
                                dataCopy.add(datapoint);
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
	private static void calculateDistance()
	{
              
		for(int i=0; i<datapoints.size() ; i++)
		{
			for(int j=0;j<noOfClusters;j++)
			{
				distance[i][j] = DataPoint.distanceBetween(datapoints.get(i),clusters.get(j).centroid );
			}
		}
	} 
	
	private static void allocateClusters()
	{
		System.out.println();
		System.out.println();
                 
                  
		
		//update old membership matrix to current membership matrix before it is recalculated
		for(int i=0; i<noOfClusters ; i++)
		{
			for(int j=0;j<datapoints.size();j++)
			{
				oldMembership[i][j]=membership[i][j]; 
			}
		}
		
		// set current membership matrix values to zero
		membership = new float[noOfClusters][n];
		
		//compute membership matrix based on distances
		for(int i=0;i<datapoints.size();i++)
		{
                    int min2Pos,minPos=0;
                    try
                    {
                        
			
			//finding closest jth cluster for ith point
			for(int j=0;j<noOfClusters;j++)
			{
				if(distance[i][j]<distance[i][minPos])
					minPos = j;
			}
			
			//finding 2nd closest cluster for jth point
			if(minPos==0)
			 min2Pos = 1;
			else min2Pos =0;
			for(int j=0;j<noOfClusters;j++)
			{
				if(j!=minPos && distance[i][j]<distance[i][min2Pos])
					min2Pos = j;
			}
			
			if(distance[i][min2Pos]-distance[i][minPos]>del)
			{
				membership[minPos][i]= 1;
			}
			else
			{
				membership[minPos][i]= 0.5f;
				membership[min2Pos][i]= 0.5f;
			}
                    }
                    catch(ArrayIndexOutOfBoundsException ex)
                    {
                        System.out.println("i="+i+"minPos="+minPos);
                    }
		}
                
                
		/*
		for(int i=0; i<noOfClusters ; i++)
		{
			for(int j=0;j<datapoints.size();j++)
			{
				System.out.print(membership[i][j]+" ");  
			}
			System.out.println();
		}*/
                
	}
	
	private static void determineNewCentroid()
	{
            System.out.println("\n Cluster Centroids:");
		for(int i=0;i<noOfClusters;i++)
		{
			
			ArrayList<Float> lowerApproxComponent = new ArrayList<Float>();
			ArrayList<Float> upperApproxComponent = new ArrayList<Float>();
			for(int k=0;k<datapoints.get(0).point.size();k++)
			{
				lowerApproxComponent.add(0.0f);
				upperApproxComponent.add(0.0f);
			}
			
			int lowerApproxCount=0,upperApproxCount=0;
			for(int j=0;j<datapoints.size();j++)
			{
				if(membership[i][j]==1)
				{
					lowerApproxCount++;
					for(int k=0;k<datapoints.get(j).point.size();k++)
					{
						lowerApproxComponent.set(k, lowerApproxComponent.get(k)+datapoints.get(j).point.get(k));
					}
				}
				else if(membership[i][j]==0.5)
				{
					upperApproxCount++;
					for(int k=0;k<datapoints.get(j).point.size();k++)
					{
						upperApproxComponent.set(k, upperApproxComponent.get(k)+datapoints.get(j).point.get(k));
					}
				}
			}
			
			ArrayList<Float> clusterCentroid = clusters.get(i).centroid.point;
			for(int k=0;k<datapoints.get(0).point.size();k++)
			{
                            
				clusterCentroid.set(k,0.0f);
				
			}
			pointCount[i][0]= lowerApproxCount;
                        pointCount[i][1]= upperApproxCount;
                        pointCount[i][2]=lowerApproxCount+upperApproxCount;
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
                        System.out.println(clusterCentroid);
                        
			
		}
                
	}
	
	private static boolean stopSignal()
	{
            
                   System.out.println();
		for(int i=0;i<noOfClusters;i++)
		{
			for(int j=0;j<datapoints.size();j++)
			{
				if(oldMembership[i][j]!=membership[i][j])
					return false;
			}
		}
		return true;
	}
	
}
