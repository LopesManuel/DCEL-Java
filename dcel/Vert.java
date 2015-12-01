package dcel;

public class Vert {
	protected Point point; //coordinates
	protected HalfEdge incidentEdge; // leaving edge
	
	public Vert() {
		this.point = null;
		this.incidentEdge = null;
	}
	public Vert(Point p, HalfEdge e) {
		this.point = p;
		this.incidentEdge = e;
	}
	public Vert(Point p) {
		this.point = p;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	public void setEdge(HalfEdge edge) {
		this.incidentEdge = edge;
	}
	public Point getPoint() {
		return point;
	}
	public HalfEdge getEdge() {
		return incidentEdge;
	}
	//Returns the edge from this node to the given node.
	public HalfEdge getEdgeTo(Vert node) {
        if (incidentEdge != null) {
                if (incidentEdge.twin.origin == node) {
                        return incidentEdge;
                } else {
                        HalfEdge edge = incidentEdge.twin.next;
                        while (edge != incidentEdge) {
                                if (edge.twin.origin == node) {
                                        return edge;
                                } else {
                                        edge = edge.twin.next;
                                }
                        }
                }
        }
        return null;
	}
    @Override
    public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("VERTEX[")
            .append(this.point)
            .append("]");
            return sb.toString();
    }
    
    public boolean equals(Vert v){
    	return((v.point.x == point.x) && (v.point.y == point.y));
    }
}
