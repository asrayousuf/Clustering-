/*
 * 1. Read i/p from  file 
 * 2. Assign cluster centroids
 * 3. Calculate distance --> membership
 * 4. Calculate new centroid
 * 5. Check if u(n)-u(n-1)<del
 * 	a. if yes then print results and quit
 *  b. else repeat step 3 to 4
 *  
 */


package clustering.fcm;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;

public class Fcm {
	public static final int n = 500;
	public static final int noOfClusters = 5;
	public static final float epsolon = 0.01f;
	public static final int m = 2;	
	public static ArrayList<Cluster> clusters = new ArrayList<Cluster>();
	public static ArrayList<DataPoint> datapoints = new ArrayList<DataPoint>();
	public static float[][] membership = new float[noOfClusters][n];
	public static float[][] oldMembership = new float[noOfClusters][n];
	
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
				
		//recalculate centroid
		while(!stopSignal())
		{
			determineNewCentroid();
			calculateMembership();
		}
                //Final Output
                 System.out.println();
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
		for(int i = 0; i < noOfClusters; i++)
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
				
		for(int i = 0; i < noOfClusters; i++)
		{
			for(int j=0; j<datapoints.size();j++)
			{
				float dij = DataPoint.distanceBetween(clusters.get(i).centroid,datapoints.get(j) );
				//System.out.println(dij);
				
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
					membership[i][j] = 1/membership[i][j];
				}
				
			}
		}
		/*
		for(int i = 0; i < noOfClusters; i++)
		{
			for(int j=0; j<datapoints.size();j++)
			{
				System.out.print(oldMembership[i][j] + " ");
			}
			System.out.println();
		}
		
		for(int i = 0; i < noOfClusters; i++)
		{
			for(int j=0; j<datapoints.size();j++)
			{
				System.out.print(membership[i][j] + " ");
			}
			System.out.println();
		}*/
		
		
	}
	
	private static void determineNewCentroid()
	{
		System.out.println("Cluster Centroids: \n");
		for(int i=0;i<noOfClusters;i++)
		{
			ArrayList<Float> dp = new ArrayList<Float>();
			for(int k=0;k<datapoints.get(0).point.size();k++)
			{
				dp.add(0.0f);
			}
			
			float denominator = 0;
			for(int j=0;j<datapoints.size();j++)
			{
				denominator +=Math.pow(membership[i][j], m) ;
				for(int k=0;k<datapoints.get(j).point.size();k++)
				{
					dp.set(k, dp.get(k)+(float)Math.pow(membership[i][j], m)*datapoints.get(j).point.get(k));
				}
			}
			for(int k = 0;k<dp.size();k++)
			{
				dp.set(k, dp.get(k)/denominator);
			}
			clusters.get(i).centroid = new DataPoint(dp);
			System.out.println(clusters.get(i).centroid.point);
			
		}
	}
	
	private static boolean stopSignal()
	{
            System.out.println();
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
