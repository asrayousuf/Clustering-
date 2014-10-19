package clustering.rfcm;

import java.util.ArrayList;

public class Cluster
{
	public DataPoint centroid;
	
	Cluster(DataPoint centroid)
	{
		this.centroid = new DataPoint();
                for(int i=0;i<centroid.point.size();i++)
                {
                    this.centroid.point.add(centroid.point.get(i));
                }
		
	}
	
	
}
