package dcel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import dcel.HalfEdge.EdgeType;
import dcel.Point.Type;

public class DoublyConnectedEdgeList {
	 /** The list of nodes */
    protected List<Vert> vertices = new ArrayList<Vert>();
   
    /** The list of half edges */
    protected List<HalfEdge> edges = new ArrayList<HalfEdge>();
   
    /** The list of faces */
    protected List<Face> faces = new ArrayList<Face>();

    /** The list of points that will cause events */
    protected List<HalfEdge> events = new ArrayList<HalfEdge>();

    public DoublyConnectedEdgeList(Point[] points) {
        this.initialize(points);
    }
    
    // Initializes the DCEL class given the points of the polygon.
    protected void initialize(Point[] points) {
        int size = points.length;
        
        // we will always have exactly one face at the beginning
        Face face = new Face();
        faces.add(face);
       
        HalfEdge prevLeftEdge = null;
        HalfEdge prevRightEdge = null;
       
        // loop over the points creating the vertices and
        // half edges for the data structure
        for (int i = 0; i < size; i++) {
            Point point = points[i];
            
            Vert vertex = new Vert();
            HalfEdge left = new HalfEdge();
            HalfEdge right = new HalfEdge();
            
            // create and populate the left
            // and right half edges
            left.incidentFace = face;
            left.next = null;
            left.origin = vertex;
            left.twin = right;
           
            right.incidentFace = null;
            right.next = prevRightEdge;
            right.origin = null;
            right.twin = left;
           
            // add the edges the edge list
            edges.add(left);
            edges.add(right);
           
            // populate the veroutgoingtex
            vertex.incidentEdge = left;
            vertex.point = point;
           
            // add the vertex to the vertices list
            vertices.add(vertex);
           
            // set the previous next edge to this left edge
            if (prevLeftEdge != null) {
                    prevLeftEdge.next = left;
            }
           
            // set the previous right edge origin to this vertex
            if (prevRightEdge != null) {
                    prevRightEdge.origin = vertex;
            }
           
            // set the new previous edges
            prevLeftEdge = left;
            prevRightEdge = right;

        }
        // set the last left edge's next pointer to the
        // first left edge we created
        HalfEdge firstLeftEdge = edges.get(0);
        prevLeftEdge.next = firstLeftEdge;
       
        // set the first right edge's next pointer
        // to the last right edge we created
        // (note that right edges are at odd indices)
        HalfEdge firstRightEdge = edges.get(1);
        firstRightEdge.next = prevRightEdge;
        // set the last right edge's origin to the first
        // vertex in the list
        prevRightEdge.origin = vertices.get(0);
       
        // set the edge of the only face to the first
        // left edge
        // (note that the interior of each face has CCW winding)
        face.edge = firstLeftEdge;
        
        //determines the types of the vertices
        calculateVerticesTypes(size);
    }
    protected void generatePartitions(int choice) {
    	 //search for horizontal events
        searchForEvents(EdgeType.HORIZONTAL);
        Collections.sort(events);
        //as the events are all horizontal it is an horizontal sweep line
        sweepLine();  
		
        //reset events
        events = new LinkedList<HalfEdge>();
		updateFaces();

		if(choice == 1){
	        searchForEvents(EdgeType.VERTICAL);
	        Collections.sort(events, new Comparator<HalfEdge>(){
	            public int compare(HalfEdge one, HalfEdge two) {
	                // ordena horizontalmente
	        		return  one.origin.point.x - two.origin.point.x ;

	            }
	        });
	        //as the events are all horizontal it is an vertical sweep line
	        sweepLine();
		}
		//after all partitions created updates the faces
		updateFaces();
	}
    
    /**
     * Goes through the edges and check if they are of the same type
     * of the events we want. And updates the event list.
     * @param EdgeType {HORIZONTAL, VERTICAL}
     * @return void 
     */
    protected void searchForEvents(EdgeType type) {
        for (int i = 0; i < edges.size(); i++) {
        	edges.get(i).calculateType();
    		if(edges.get(i).incidentFace != null || edges.get(i).isArtificial)
    			if(edges.get(i).type == type)
        			events.add(edges.get(i));
        }
	}
    /**
     * Goes through the vertices and check if they turn right or left
     */
    protected void calculateVerticesTypes(int size){
        for (int i = 0; i < size; i++) {
        	int r = (i -1) % size;
        	if (r < 0)
        	    r += size;
        	vertices.get(i).point.calculateType(vertices.get(r).point, vertices.get((i+1)%size).point);
        }
    }
    /**
     * Outputs the generated partitions
     */
    protected void print(){
    	System.out.println("------- VERTICES -------");
    	for (int i = 0; i < vertices.size(); i++) {
			Vert vertice = vertices.get(i);
			System.out.println("V"+(i+1)+ " (" + vertice.point.x + " , " + vertice.point.y + ") - " + vertice.point.type);
		}
    	System.out.println("------- HALF EDGES -------");
    	System.out.println("N: " + edges.size() + " edges");
    	for (int i = 0; i < edges.size(); i++) {
			HalfEdge edge = edges.get(i);
			System.out.println(edge.toString());

		}
    }
 

    protected void sweepLine(){
    	for (int i = 0; i < events.size(); i++) 
    	{   //if the destination point is reflex the it will generate a cut
        	if(events.get(i).twin.origin.point.type == dcel.Point.Type.REFLEX
    				&& events.get(i).origin.point.type == dcel.Point.Type.REFLEX){

	            resolveIntersections(getIntersectionPoints(events.get(i)),events.get(i));
	            resolveIntersections(getIntersectionPoints(events.get(i)),events.get(i));

        	}
        	else if(events.get(i).twin.origin.point.type == dcel.Point.Type.REFLEX
    				|| events.get(i).origin.point.type == dcel.Point.Type.REFLEX)
        	{
	            //gets the indexes of the intersected edges and calculates intersections
	            resolveIntersections(getIntersectionPoints(events.get(i)),events.get(i));
        	}
		}
    }
    private void resolveIntersections(List<Integer> interSectHedges, HalfEdge event ) {
        HalfEdge lastIntersectedEdge = null;
        HalfEdge interSectHedge = null;
        for (Integer it : interSectHedges) 
        {
	        Face fNova = new Face(); 
	        /*When the event intersects artificial edges the event edge is 
	          now the last edge that   */
        	if(lastIntersectedEdge != null && interSectHedge != null && interSectHedge.isArtificial)
        		event = lastIntersectedEdge;

        	interSectHedge = edges.get(it); 
        	HalfEdge oldInterSectHedge = new HalfEdge(edges.get(it));
    		Point interSect = generateIntersectionPoint(event,interSectHedge);
    		Vert vertex = new Vert(interSect);
    		// incoming is the edges that has the interSect vertex as its destination
    		HalfEdge incoming = new HalfEdge(null,true, event.type,null);
    		// incoming is the edges that has the interSect vertex as its origin
            HalfEdge outgoing = new HalfEdge(incoming,true, event.type,vertex);
            incoming.twin = outgoing;
            
            // calculates intersections accordingly to the sweep type
            if(event.type == EdgeType.HORIZONTAL)
            	calculateHorizontalEdgesSuccessions(event,  incoming,  outgoing,  fNova, interSectHedge.origin.point.x );
            else
            	lastIntersectedEdge = calculateVerticalEdgesSuccessions(event,  incoming,  outgoing,  fNova, interSectHedge.origin.point.y );
            
            //if intersection point already exists
            if( !interSectHedge.twin.origin.equals(vertex)  &&   !interSectHedge.origin.equals(vertex) )
            { 
            	resolveNewInterSection(vertex, interSectHedge, incoming, outgoing);
            }
            else 	
            { 
            	incoming.next = interSectHedge.next;
            	interSectHedge.next = outgoing;
            }
        	//if it doesn't exist 
            if(!edgeExists(incoming) && !edgeExists(outgoing) )
            {
            	edges.add(incoming);
            	edges.add(outgoing);
            	edges.set(it, interSectHedge);
                //add the new face created
                faces.add(fNova);	     
                
                vertex.point.type=Type.CONVEX;
                // add the vertex to the vertex list
            	
                if(!vertExists(vertex))
                	vertices.add(vertex);
            }
            else
            	edges.set(it, oldInterSectHedge);


        }	        
	}
	private HalfEdge calculateVerticalEdgesSuccessions(HalfEdge event, HalfEdge incoming, HalfEdge outgoing, Face fNova, int interSectHedgeoriginY) {
    	HalfEdge lastIntersectedEdge;
    	if( event.origin.point.y - event.twin.origin.point.y > 0){ //aponta para baixo
    		// se o evento estiver a cima do ponto de interseção
        	if (event.origin.point.y > interSectHedgeoriginY ) 
        	{
           		incoming.origin = event.twin.origin ;
	       		outgoing.next = event.next.twin.next;
	       		event.next.twin.next = incoming;
        		lastIntersectedEdge = incoming;
	       		fNova.setEdge(incoming);
	       		
        	}// se o evento estiver a abaixo do ponto de interseção
        	else{
           		incoming.origin = event.origin ;
	       		outgoing.next = event.twin.next.twin.next;
	       		event.twin.next.twin.next = incoming;	
        		lastIntersectedEdge = outgoing;
	       		fNova.setEdge(outgoing);
        	} 
    	}
    	else //aponta para cima
    	{
        	if (event.origin.point.y < interSectHedgeoriginY  ) 
        	{// se o evento estiver a baixo do ponto de interseção
           		incoming.origin = event.twin.origin ;
	       		outgoing.next = event.next.twin.next;
	       		event.next.twin.next = incoming;
        		lastIntersectedEdge = outgoing;
	       		fNova.setEdge(outgoing);
	       		
        	}// se o evento estiver a cima do ponto de interseção
        	else{
           		incoming.origin = event.origin ;
	       		outgoing.next = event.twin.next.twin.next;
	       		event.twin.next.twin.next = incoming;	
        		lastIntersectedEdge = incoming;
	       		fNova.setEdge(incoming);
        	} 
    	}
    	return lastIntersectedEdge;
    }
    private void calculateHorizontalEdgesSuccessions(HalfEdge event, HalfEdge incoming, HalfEdge outgoing, Face fNova, int interSectHedgeoriginX) {
    	// se a direcção for para a esquerda  
    	if( event.origin.point.x - event.twin.origin.point.x > 0)
    	{	      // se o evento estiver à direita 
           if( event.origin.point.x > interSectHedgeoriginX) 
           	{
           		incoming.origin = event.twin.origin ;
	       		outgoing.next = event.next;
	       		event.next = incoming;
	       		fNova.setEdge(outgoing);
           }
           else
           {    // se o evento estiver à esquerda 
               	incoming.origin = event.origin ;
	       		outgoing.next = event;
	       		event.twin.next.twin.next = incoming;	
	       		fNova.setEdge(incoming);
           }
    	}
    	else
    	{// se a direcção for para a direita  
               if( event.origin.point.x < interSectHedgeoriginX) 
               	{   // se o evento estiver à direita 
               		incoming.origin = event.twin.origin ;
		       		outgoing.next = event.next;
		       		event.next = incoming;
		       		fNova.setEdge(outgoing);
               }
               else
               {    // se o evento estiver à esquerda 
	               	incoming.origin = event.origin ;
		       		outgoing.next = event;
		       		event.twin.next.twin.next = incoming;	
		       		fNova.setEdge(outgoing);
               }
    	}
	}

	private Point generateIntersectionPoint(HalfEdge event, HalfEdge interSectHedge) {
    	Point interSect = new Point();
		if(event.type == EdgeType.HORIZONTAL )
		{
        	interSect.x = interSectHedge.origin.getPoint().x;
        	interSect.y = event.origin.getPoint().y;
        }
        else{
        	interSect.y = interSectHedge.origin.getPoint().y;
        	interSect.x = event.origin.getPoint().x;	
        }
		return interSect;
	}

	/**
     * When it intersects a new vertice it's necessary to generate 2 new edges 
     * (vInsideEdge & vOutsideEdge) in the intersected edge.
     * @param vertex  - intersection point
     * @param interSectHedge - intersected edge
     * @param incoming - edge that has the vertex as is destination
     * @param outgoing - edge that has the vertex as is destination
     */
    private void resolveNewInterSection(Vert vertex, HalfEdge interSectHedge, HalfEdge incoming, HalfEdge outgoing) {
        
    	HalfEdge vOutsideEdge = new HalfEdge();
        HalfEdge vInsideEdge = new HalfEdge();
        
        vOutsideEdge.origin = vertex;
        vOutsideEdge.next = interSectHedge.twin.next;
        vOutsideEdge.twin = interSectHedge;
        vOutsideEdge.type = interSectHedge.type;
        vOutsideEdge.isArtificial = interSectHedge.isArtificial;

        vInsideEdge.origin = vertex;
        vInsideEdge.next = interSectHedge.next;
        vInsideEdge.twin = interSectHedge.twin;
        vInsideEdge.type= interSectHedge.type;
        vInsideEdge.isArtificial = interSectHedge.isArtificial;
        
        
        interSectHedge.twin.next = vOutsideEdge;
        interSectHedge.twin.twin = vInsideEdge;
        interSectHedge.twin = vOutsideEdge;
        	    	                
        interSectHedge.next = outgoing;
    	incoming.next = vInsideEdge;

    		edges.add(vOutsideEdge);
    		edges.add(vInsideEdge);
	}

	protected void printFaces(){
		for (int i = 0; i < faces.size(); i++) {
    		System.out.println("------ FACE "+i+"------" );
    		HalfEdge f = faces.get(i).getEdge();
            System.out.println("EDGE: " + f.toString());
	    	HalfEdge it = f.getNext();
	    	while(!it.equal(f)){
	            System.out.println("EDGE: " + it.toString());
	    		it = it.getNext();
	
	    	}
    	}
    }
    protected void updateFaces() {
		for (Face it : faces) {
			updateFace(it);
		}
	}
    protected void updateFace(Face f){
    	HalfEdge it = f.getEdge().next;
    	while(!it.equals(f.getEdge())){
    		it.incidentFace = f;
    		it = it.next;
    	}
    }
    protected HalfEdge getRealEdge(HalfEdge faceEdge){
    	for (HalfEdge it : edges) {
			if(it.equal(faceEdge)){
				return it;
			}
		}
    	return null;
    }
  
    /** Method for future implementation
    public static boolean linesIntersect(Point q1,  Point p1, Point p2, boolean isHorizontal){
    	int x1 = q1.x;int y1 = q1.y;
    	int x2 = q1.x; int y2 = q1.y;
    	int x3 = p1.x;int y3 = p1.y;
    	int x4 = p2.x;int y4 = p2.y;
    	if(isHorizontal){
	    	x2 = p2.x;
    	}
    	else{
	    	y2 = p2.y;
    	}
        // Return false if either of the lines have zero length
        if (x1 == x2 && y1 == y2 ||
              x3 == x4 && y3 == y4){
           return false;
        }
        double ax = x2-x1;
        double ay = y2-y1;
        double bx = x3-x4;
        double by = y3-y4;
        double cx = x1-x3;
        double cy = y1-y3;

        double alphaNumerator = by*cx - bx*cy;
        double commonDenominator = ay*bx - ax*by;
        if (commonDenominator > 0){
           if (alphaNumerator < 0 || alphaNumerator > commonDenominator){
              return false;
           }
        }else if (commonDenominator < 0){
           if (alphaNumerator > 0 || alphaNumerator < commonDenominator){
              return false;
           }
        }
        double betaNumerator = ax*cy - ay*cx;
        if (commonDenominator > 0){
           if (betaNumerator < 0 || betaNumerator > commonDenominator){
              return false;
           }
        }else if (commonDenominator < 0){
           if (betaNumerator > 0 || betaNumerator < commonDenominator){
              return false;
           }
        }
        if (commonDenominator == 0){
           // The lines are parallel.
           // Check if they're collinear.
           double y3LessY1 = y3-y1;
           double collinearityTestForP3 = x1*(y2-y3) + x2*(y3LessY1) + x3*(y1-y2);  
           // If p3 is collinear with p1 and p2 then p4 will also be collinear, since p1-p2 is parallel with p3-p4
           if (collinearityTestForP3 == 0){
              // The lines are collinear. Now check if they overlap.
              if (x1 >= x3 && x1 <= x4 || x1 <= x3 && x1 >= x4 ||
                    x2 >= x3 && x2 <= x4 || x2 <= x3 && x2 >= x4 ||
                    x3 >= x1 && x3 <= x2 || x3 <= x1 && x3 >= x2){
                 if (y1 >= y3 && y1 <= y4 || y1 <= y3 && y1 >= y4 ||
                       y2 >= y3 && y2 <= y4 || y2 <= y3 && y2 >= y4 ||
                       y3 >= y1 && y3 <= y2 || y3 <= y1 && y3 >= y2){
                    return true;
                 }
              }
           }
           return false;
        }
        return true;
     }
     */
    protected List<Integer> getIntersectionPoints(HalfEdge event){

    	boolean foundIntersec = false;
		HalfEdge currentEdge = event.getNext();
		List<Integer> results = new ArrayList<Integer>();;
		
		while(!foundIntersec){
    		if(currentEdge.type != event.type && 
    				( (!currentEdge.origin.equals(event.origin)) && (!currentEdge.origin.equals(event.twin.origin)))&& 
    				( (!currentEdge.twin.origin.equals(event.origin)) && (!currentEdge.twin.origin.equals(event.twin.origin))) )
    		{
    			if(event.type == EdgeType.HORIZONTAL)
    			{
    				if((currentEdge.origin.getPoint().y > event.origin.getPoint().y 
    						&& event.origin.getPoint().y > currentEdge.twin.origin.getPoint().y)
    				||
    				(currentEdge.origin.getPoint().y < event.origin.getPoint().y &&
    				event.origin.getPoint().y < currentEdge.twin.origin.getPoint().y))
    				{
    					int tempIt = edges.lastIndexOf(currentEdge);
    					if(results.size() == 0 || Math.abs(currentEdge.origin.point.x - event.origin.point.x) < Math.abs(edges.get(results.get(0)).origin.point.x - event.origin.point.x) ){
    						if(checkIfContainsEdge(results,tempIt)){
    							if(results.size() != 0 )
    								results.remove(0);
	    						results.add(tempIt);   
    						}
    					}
    					else{
    						foundIntersec = true;
    					}
    				}
    			}
    			else
    			{
    				if((currentEdge.origin.getPoint().x > event.origin.getPoint().x 
    						&& event.origin.getPoint().x > currentEdge.twin.origin.getPoint().x)
    				||
    				(currentEdge.origin.getPoint().x < event.origin.getPoint().x &&
    				event.origin.getPoint().x < currentEdge.twin.origin.getPoint().x))
    				{
    					int tempIt = edges.lastIndexOf(currentEdge);
    					if(tempIt != -1 && checkIfContainsEdge(results,tempIt))
    						results.add(tempIt);
    					if(!currentEdge.isArtificial)
    						foundIntersec = true;
    				}
    			}
    		}
    		if( currentEdge.equals(event))
				foundIntersec = true;
    		
			if(currentEdge.isArtificial && currentEdge.type != event.type){
				currentEdge = currentEdge.twin;

			}
    		currentEdge = currentEdge.getNext();
    	}
    	return results;
    }
    
    protected boolean checkIfContainsEdge(List<Integer> results, int found){
    	for (Integer it : results) {
			if(edges.get(it).sameUndirectedEdge(edges.get(found)))
				return false;
		}
    	return true;
    }
    /**
     * Checks if an edge already exists
     * @param edge
     * @return true if exits : false if it doesn't
     */
    protected boolean edgeExists(HalfEdge edge){
    	for (HalfEdge it : edges) {
			if(it.equal(edge))
				return true;
		}
    	return false;
    } 
    /**
     * Checks if an vertice already exists
     * @param vert
     * @return true if exits : false if it doesn't
     */
    protected boolean vertExists(Vert vert){
    	for (Vert it : vertices) {
			if(it.equals(vert))
				return true;
		}
    	return false;
    } 
}
