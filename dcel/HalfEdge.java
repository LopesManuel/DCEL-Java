package dcel;

/**
 * Represents a half edge of the Doubly-Connected Edge List.
 * - We consider an edge to be open (its endpoint are not a part of it)
 * - A half-edge bounds only one face. 
 * - The next half-edge of a given half-edge with respect to a
 *   counterclockwise traversal of a face induces an orientation on 
 *   each half-edge: it is oriented such that the face that it bounds 
 *   lies to its left for an observer walking along the edge.
 * @author Manuel Lopes
 */
public class HalfEdge implements Comparable<HalfEdge>{
	
	public enum EdgeType {
		VERTICAL, HORIZONTAL, UNKNOWN
	}
	boolean isArtificial = false;
	//The half-edge origin
	Vert origin; 
	//The adjacent face of this half edge 
	Face incidentFace;
	// The adjacents edges in the list having the same face
	HalfEdge next;
	//The adjacent twin of this half edge 
	HalfEdge twin;
	
	EdgeType type;
	
	public HalfEdge(Face incidentFace,	Vert origin, HalfEdge next,	HalfEdge prev,	HalfEdge twin, boolean isArtificial,EdgeType type){
		 this.origin = origin;
		 this.twin = twin;
		 this.incidentFace = incidentFace;
		 this.next = next;
		 this.type = type;
		 this.isArtificial = isArtificial;

	}
	//pre initialization
	public HalfEdge(HalfEdge twin, boolean isArtificial,EdgeType type,Vert origin){
		 this.twin = twin;
		 this.type = type;
		 this.isArtificial = isArtificial;
		 this.origin = origin;
	}
	public HalfEdge() {
	}
	public HalfEdge(HalfEdge n) {
		 this.origin = n.origin;
		 this.twin = n.twin;
		 this.incidentFace = n.incidentFace;
		 this.next = n.next;
		 this.type = n.type;
	}
	public Vert getOrigin() {
		return origin;
	}
	public void setOrigin(Vert origin) {
		this.origin = origin;
	}
	public HalfEdge getTwin() {
		return twin;
	}
	public Face getFace() {
		return incidentFace;
	}
	public HalfEdge getNext() {
		return next;
	}
	public void setNext(HalfEdge next) {
		this.next = next;
	}
    @Override
    public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("HALF_EDGE[")
            .append(this.origin.toString()).append("|")
            .append(this.next.origin.toString())
            .append("] - " + this.type);
            return sb.toString();
    }
    public void calculateType(){
    	Point destination = twin.origin.getPoint();
    	if(origin.getPoint().y == destination.y)
    		type = EdgeType.HORIZONTAL;
    	else if(origin.getPoint().x == destination.x)
    		type = EdgeType.VERTICAL;
    	else
    		type = EdgeType.UNKNOWN;
    }
    public boolean equal(HalfEdge edge){
    	return (edge.origin.equals(origin) && edge.twin.origin.equals(twin.origin));
    }
    public boolean sameUndirectedEdge(HalfEdge edge){
    	return ((edge.origin.equals(origin) && edge.twin.origin.equals(twin.origin)) 
    			|| (edge.origin.equals(twin.origin) && edge.twin.origin.equals(origin)));
    }
    protected boolean isFace(){
    	HalfEdge currenEdge = this;
    	for (int i = 0; i < 4; i++) {
			currenEdge = currenEdge.next;
			if(currenEdge.equal(this))
				return true;
		}
    	return false;
    }
	@Override
	public int compareTo(HalfEdge o) {
		return  o.origin.point.y - origin.point.y ;
	}
}
