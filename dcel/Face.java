package dcel;

public class Face {
    // An edge of the edge list enclosing this face 
    protected HalfEdge edge;

    public Face() {
		this.edge = null;
	}
	public Face(HalfEdge e) {
		this.edge = e;
	}
	public HalfEdge getEdge() {
		return edge;
	}
	public void setEdge(HalfEdge edge) {
		 this.edge = edge;
	}
	/**
	 * Returns the number of edges on this face.
	 * @return int
	 */
	public int getEdgeCount() {
	        HalfEdge edge = this.edge;
	        int count = 0;
	        if (edge != null) {
	                count++;
	                while (edge.next != this.edge) {
	                        count++;
	                        edge = edge.next;
	                }
	        }
	        return count;
	}

}
