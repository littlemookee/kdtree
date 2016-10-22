/**
 * @author			mikhail
 * Created			20.10.2016
 * Last modified	20.10.2016
 *
 */

import edu.princeton.cs.algs4.*;

public class KdTree
{
	private Node root;
	private int size;
	private Point2D closest;
	private double minDist;
		
	private class Node
	{
		public Node left;
		public Node right;		
		private Point2D point;
		public Node(Point2D p) {			
			point = p;
			left = null;
			right = null;
		}		
	}
	
	/**
	 * Construct an empty set of points
	 */
	public KdTree()
	{
		root = null;
		size = 0;
	}
	
	/**
	 * is the set empty?
	 * @return
	 */
	public boolean isEmpty()
	{		
		return size == 0;
	}
	
	/**
	 * number of points in the set
	 * @return
	 */
	public int size()
	{		
		return size;
	}
	
	private Node insert(Node node, Point2D p, boolean horisontal)
	{		
		if (node == null) return new Node(p);
		
		boolean goLeft;
		if (horisontal) goLeft = p.x() < node.point.x();
		else 			goLeft = p.y() < node.point.y();
		
		if (goLeft)	node.left = insert(node.left, p, !horisontal);
		else 		node.right = insert(node.right, p, !horisontal);
		
		return node;
	}
	
	/**
	 * add the point to the set (if it is not already in the set) 
	 * @param p
	 */
	public void insert(Point2D p)
	{
		if (p == null)
			throw new java.lang.NullPointerException();
		
		if (!contains(p)) {
			root = insert(root, p, true);
			size++;
		} 
	}
	
	private boolean search(Node node, Point2D p, boolean horisontal)
	{
		if (node == null) return false;
		
		if (node.point.equals(p)) return true;
		
		boolean goLeft;
		if (horisontal) goLeft = p.x() < node.point.x();
		else 			goLeft = p.y() < node.point.y();
		
		if (goLeft)	return search(node.left, p, !horisontal);
		else 		return search(node.right, p, !horisontal);
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
		
		return search(root, p, true);
	}
	
	private void draw(Node node, RectHV rect, boolean horisontal)
	{
		if (node == null) return;
		
		RectHV leftRect,rightRect;
		if (horisontal) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.line(node.point.x(),rect.ymin(),node.point.x(),rect.ymax());
			leftRect = new RectHV(rect.xmin(),rect.ymin(),node.point.x(),rect.ymax());
			rightRect = new RectHV(node.point.x(),rect.ymin(),rect.xmax(),rect.ymax());
		} else {
			StdDraw.setPenColor(StdDraw.BLUE);
			StdDraw.line(rect.xmin(),node.point.y(),rect.xmax(),node.point.y());
			leftRect = new RectHV(rect.xmin(),rect.ymin(),rect.xmax(),node.point.y());
			rightRect = new RectHV(rect.xmin(),node.point.y(),rect.xmax(),rect.ymax());
		}

		StdDraw.setPenColor(StdDraw.BLACK);
		double penRadius = StdDraw.getPenRadius();
		StdDraw.setPenRadius(0.01);
		StdDraw.point(node.point.x(), node.point.y());
		StdDraw.setPenRadius(penRadius);
 		
		draw(node.left,leftRect,!horisontal);
		draw(node.right,rightRect,!horisontal);
	}
	
	/**
	 * draw all points to standard draw 
	 */
	public void draw()
	{		
		draw(root, new RectHV(0.0, 0.0, 1.0, 1.0), true);
	}
	
	private void range(Node node, RectHV rect, SET<Point2D> points, boolean horisontal)
	{
		if (node == null) return;
		
		if (rect.contains(node.point)) points.add(node.point);
		
		boolean toLeft;
		
		if (horisontal) toLeft = rect.xmin() <= node.point.x();
		else			toLeft = rect.ymin() <= node.point.y();
		
		if (toLeft) 	range(node.left, rect, points, !horisontal);
		
		boolean toRight;
		
		if (horisontal) toRight = rect.xmax() >= node.point.x();
		else			toRight = rect.ymax() >= node.point.y();
		
		if (toRight) 	range(node.right, rect, points, !horisontal);
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
		
		SET<Point2D> points = new SET<Point2D>();

		range(root, rect, points, true);
		
		return points;
	}
	
	private void nearest(Node node, RectHV rect, Point2D query, boolean horisontal)
	{
		if (node == null) return;
		
		if (query.distanceTo(node.point) < minDist) {
			closest = node.point;
			minDist = query.distanceTo(closest);
		}
		
		boolean left; // Determine in what rect is the query relative to the node point
		RectHV leftRect, rightRect;
		if (horisontal) {
			left = query.x() < node.point.x();
			leftRect = new RectHV(rect.xmin(),rect.ymin(),node.point.x(),rect.ymax());
			rightRect = new RectHV(node.point.x(),rect.ymin(),rect.xmax(),rect.ymax());
		} else {
			left = query.y() < node.point.y();
			leftRect = new RectHV(rect.xmin(),rect.ymin(),rect.xmax(),node.point.y()); 
			rightRect = new RectHV(rect.xmin(),node.point.y(),rect.xmax(),rect.ymax());			
		}
		
		if (left) {			
			nearest(node.left, leftRect, query, !horisontal);
			if (rightRect.distanceTo(query) < minDist)
				nearest(node.right, rightRect, query, !horisontal);
		} else {
			nearest(node.right, rightRect, query, !horisontal);
			if (leftRect.distanceTo(query) < minDist)
				nearest(node.left, leftRect, query, !horisontal);
		}
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
		
		minDist = Double.POSITIVE_INFINITY;
		nearest(root, new RectHV(0.0, 0.0, 1.0, 1.0), p, true);
		return closest;
	}

	/**
	 * unit testing of the methods (optional)
	 * @param args
	 */
	public static void main(String[] args)
	{
        String filename = args[0];
        In in = new In(filename);

        // initialize the two data structures with point from standard input
        KdTree kdtree = new KdTree();
        //Point2D n = kdtree.nearest(new Point2D(0.5,0.75));
        int N = 0;
        while (N++ < 1000) {
            double x = StdRandom.uniform();
            double y = StdRandom.uniform();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        int size = kdtree.size();
        kdtree.draw();
        kdtree.nearest(new Point2D(0.5,0.75));
	}
}
