/**
 * @author			mikhail
 * Created			20.10.2016
 * Last modified	20.10.2016
 *
 */

import edu.princeton.cs.algs4.*;

public class PointSET
{
	SET<Point2D> set;
	
	/**
	 * Construct an empty set of points
	 */
	public PointSET()
	{
		set = new SET<Point2D>();
	}
	
	/**
	 * is the set empty?
	 * @return
	 */
	public boolean isEmpty()
	{		
		return set.isEmpty();
	}
	
	/**
	 * number of points in the set
	 * @return
	 */
	public int size()
	{		
		return set.size();
	}
	
	/**
	 * add the point to the set (if it is not already in the set) 
	 * @param p
	 */
	public void insert(Point2D p)
	{
		if (p == null)
			throw new java.lang.NullPointerException();
		
		if (!set.contains(p)) set.add(p);
	}
	
	/**
	 * does the set contain point p? 
	 * @param p
	 * @return
	 */
	public boolean contains(Point2D p)
	{
		if (p == null)
			throw new java.lang.NullPointerException();
		
		return set.contains(p);
	}
	
	/**
	 * draw all points to standard draw 
	 */
	public void draw()
	{
		for(Point2D p : set)
			StdDraw.point(p.x(), p.y());
	}
	
	/**
	 * all points that are inside the rectangle
	 * @param rect
	 * @return
	 */
	public Iterable<Point2D> range(RectHV rect)
	{
		if (rect == null)
			throw new java.lang.NullPointerException();
		
		SET<Point2D> out = new SET<Point2D>();
		
		for (Point2D p : set)
			if (rect.contains(p)) out.add(p);
		
		return out;
	}
	
	/**
	 * a nearest neighbor in the set to point p; null if the set is empty
	 * @param p
	 * @return
	 */
	public Point2D nearest(Point2D p)
	{
		if (p == null)
			throw new java.lang.NullPointerException();
		
		Point2D n = p;
		double min = Double.POSITIVE_INFINITY;
		for (Point2D q : set)
			if (p.distanceTo(p) < min) {
				min = p.distanceTo(p);
				n = q;
			}		
		return n;
	}

	/**
	 * unit testing of the methods (optional)
	 * @param args
	 */
	public static void main(String[] args)
	{
		
	}
}
