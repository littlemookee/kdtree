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
		
	private class Node
	{
		public Node left;
		public Node right;		
		private Point2D point;
		public Node(boolean horisontal, Point2D point) {			
			this.point = point;
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
		if (node == null) return new Node(horisontal, p);
		
		double cmp;
		if (horisontal) cmp = p.x() - node.point.x();
		else cmp = p.y() - node.point.y();
		
		if (cmp < 0)	node.left = insert(node.left, p, !horisontal);
		else 			node.right = insert(node.right, p, !horisontal);
		
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
		
		if (!contains(p)) root = insert(root, p, true);
	}
	
	private boolean search(Node node, Point2D p, boolean horisontal)
	{
		if (node == null) return false;
		
		if (node.point.equals(p)) return true;
		
		double cmp;
		if (horisontal) cmp = p.x() - node.point.x();
		else 			cmp = p.y() - node.point.y();
		
		if (cmp < 0) return search(node.left, p, !horisontal);
		return search(node.right, p, !horisontal);
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
		
		if (horisontal) {
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.line(rect.xmin(), node.point.y(), rect.xmax(), node.point.y());
		} else {
			StdDraw.setPenColor(StdDraw.BLUE);
			StdDraw.line(node.point.x(), rect.ymin(), node.point.x(), rect.ymax());
		}
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.point(node.point.x(),  node.point.y());
 			
		// Left node
		RectHV newRect;
		if (horisontal)
			newRect = new RectHV(rect.xmin(), rect.ymin(), node.point.x(), rect.ymax());
		else
			newRect = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), node.point.y());
		draw(node.left, newRect, !horisontal);

		// Right node		
		if (horisontal)
			newRect = new RectHV(node.point.x(), rect.ymin(), rect.xmax(), rect.ymax());
		else
			newRect = new RectHV(rect.xmin(), node.point.y(), rect.xmax(), rect.ymax());
		draw(node.right, newRect, !horisontal);
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
		if (horisontal) toLeft = rect.xmin() < node.point.x();
		else			toLeft = rect.ymin() < node.point.y();
		if (toLeft) range(node.left, rect, points, !horisontal);
		
		boolean toRight;
		if (horisontal) toRight = rect.xmax() > node.point.x();
		else			toRight = rect.ymax() > node.point.y();
		if (toRight) range(node.right, rect, points, !horisontal);
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
	
	private Point2D nearest(Node node, Point2D query, Point2D n, double minDist, boolean horisontal)
	{
		if (node == null) return n;
		
		if (node.point.distanceTo(query) < minDist) {
			n = node.point;
			minDist = node.point.distanceTo(query);
		}
		
		boolean toBoth;
		if (horisontal)
			toBoth = query.distanceTo(new Point2D(node.point.x(), query.y())) < minDist;
		else
			toBoth = query.distanceTo(new Point2D(query.x(), node.point.y())) < minDist;
		
		if (toBoth) {
			n = nearest(node.left, query, n, minDist, !horisontal);
			n = nearest(node.right, query, n, minDist, !horisontal);
		} else {			
			boolean toLeft;
			if (horisontal) toLeft = query.x() < node.point.x();
			else 			toLeft = query.y() < node.point.y();

			if (toLeft)
				n = nearest(node.left, query, n, minDist, !horisontal);
			
			boolean toRight;
			if (horisontal) toRight = query.x() > node.point.x();
			else			toRight = query.y() > node.point.y();
			
			if (toRight)
				n = nearest(node.right, query, n, minDist, !horisontal);
		}
		return n;
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
		
		double minDist = Double.POSITIVE_INFINITY;		
		Point2D n = null;
		
		return nearest(root, p, n, minDist, true);
	}

	/**
	 * unit testing of the methods (optional)
	 * @param args
	 */
	public static void main(String[] args)
	{
		
	}
}
