package clustering.rcm;

import java.util.ArrayList;

public class DataPoint {
	public ArrayList<Float> point;
	public static int dim ;
	
	public DataPoint()
	{
		this.point = new ArrayList<Float>();
	}
	
	public DataPoint(ArrayList<Float> point)
	{
		this.point = point;
	}

	public ArrayList<Float> getPoint() {
		return point;
	}

	public void setPoint(ArrayList<Float> point)
	{
		this.point = point;
	}
	
	public void displayPoint()
	{
		for (Float dim : point) {
			System.out.print(dim+",");
		}
		System.out.println();
	}
	
	public static float distanceBetween(DataPoint dp1, DataPoint dp2)
	{
		float distance = 0;
				
		for(int i=0;i<dp1.point.size();i++)
		{
			distance += (dp1.point.get(i)-dp2.point.get(i))*(dp1.point.get(i)-dp2.point.get(i));
		}
		distance = (float) Math.sqrt(distance);	
		
		return distance;
	}
	
	
}
