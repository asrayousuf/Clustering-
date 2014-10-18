package clustering.rcm;

import java.util.ArrayList;

public class Cluster
{
	public DataPoint centroid;
	public ArrayList<DataPoint> lowerApprox;
	public ArrayList<DataPoint> upperApprox;
	
	Cluster(DataPoint centroid)
	{
		this.centroid = new DataPoint();
                for(int i=0;i<centroid.point.size();i++)
                {
                    this.centroid.point.add(centroid.point.get(i));
                }
		lowerApprox = new ArrayList<DataPoint>();
		upperApprox = new ArrayList<DataPoint>();
	}
	
	
}
