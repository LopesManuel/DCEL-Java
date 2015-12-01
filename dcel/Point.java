package dcel;


public class Point  implements Comparable<Point> {

		public enum Side {
			RIGHT, LEFT, UNKNOWN
		}
//		public enum Type {
//			START, END, MERGE, SPLIT, REGULARR, REGULARL, UNKNOWN
//		}
		public enum Type {
			CONVEX,REFLEX,UNKNOWN
		}
		public int x;
		public int y;
		public Side side;
		public Type type;
		public int pos;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
			this.side = Side.UNKNOWN;
			this.type = Type.UNKNOWN;
			this.pos = -1;
		}
	
		public Point() {
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean equals(Object obj) {
			Point p = (Point) obj;
			return p.x == x && p.y == y;
		}
		@Override
		public String toString() {
			return "(" + x + ", " + y + ", " + type + ")";
		}
		@Override
		public int compareTo(Point p) {
			if (y < p.y)
				return -1; 
			else if (y == p.y) {
				if (x < p.x)
					return -1;
				else if (x == p.x)
					return 0;
				else
					return 1;
			} 
			else
				return 1;
		}
		
		public Point sum(Point p) {
			return new Point(this.x + p.x, this.y + p.y);
		}
		public Point difference(Point p) {
			return new Point(this.x - p.x, this.y - p.y);
		}
		public int dot(Point p) {
			return (this.x * p.x + this.y * p.y);
		}
		/**
		 * This will equal zero if the point C is on the line formed by points A and
		 * B, and will have a different sign depending on the side.Which side this is
		 * depends on the orientation of your (x,y) coordinates, but you can plug test
		 * values for A,B and C into this formula to determine whether negative values
		 *  are to the left or to the right.
		 */
		public double cross(Point a, Point b, Point c){
			return ((b.x - a.x)*(c.y - a.y) - (b.y - a.y)*(c.x - a.x));
		}
		protected boolean isBelow( Point q) {  
		  if (y - q.y < 0.0) {
			  return true;
		  } else {
			  return false;
		  }
		}			
		public void calculateType(Point previous, Point next){
			double cross = cross(previous, this, next);
			
			if(cross > 0.0){
				type = Type.CONVEX;
			}
			else{
				type = Type.REFLEX;
			}
			
		}
}
