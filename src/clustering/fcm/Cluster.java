package clustering.fcm;

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
