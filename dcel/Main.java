package dcel;

import java.util.Scanner;

public class Main {
	private static Scanner sc;
	private static DoublyConnectedEdgeList dcel;
	
	public static void main(String []args){
		int top = Integer.MIN_VALUE;
		int left = Integer.MIN_VALUE;

		sc = new Scanner(System.in);
		int numPoints = sc.nextInt();
		Point[] points = new Point[numPoints];
		
		for(int i = 0; i < numPoints; i++ ){
			int x = sc.nextInt();
			int y = sc.nextInt();
			if(y > top) top = y;
			if(x > left) left = x;
			Point tPoint = new Point(x, y);
			points[i] = tPoint;
		}
		int option;
		option = sc.nextInt();
		switch (option) {
		case 0:
			dcel = new DoublyConnectedEdgeList(points);
			dcel.generatePartitions(option);
			dcel.print();
			dcel.printFaces();
			break;
		case 1:
			dcel = new DoublyConnectedEdgeList(points);
			dcel.generatePartitions(option);
			dcel.print();
			dcel.printFaces();
			break;
		case 2:
			break;
		default:
			System.out.println("No option was chosen!");
			break;
		}

	}
}
