package graphics;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;




public class Graphics3d extends Applet {
	
	//The list of lines to be drawn by the program
	List<Line3d> lines = new ArrayList<Line3d>();
	
	//camera location in world coordinates
	Loc3d cameraLocation;
	 	
	
	
	//camera vector
	
	Vector3D eye = new Vector3D(5.0, 5.0, 5.0);
	
	Vector3D up = new Vector3D(5.0,5.0,5.0);
	
	public void init(){
		
		setSize(500,500);
		setBackground(Color.BLACK);
		
		//these lines define a cube centered at the origin with side length 2
		lines.add(new Line3d(-1.0,-1.0,-1.0,-1.0,-1.0,1.0));
		lines.add(new Line3d(-1.0,-1.0,-1.0,-1.0,1.0,-1.0));
		lines.add(new Line3d(-1.0,-1.0,-1.0,1.0,-1.0,-1.0));
		
		lines.add(new Line3d(-1.0,1.0,1.0,-1.0,-1.0,1.0));
		lines.add(new Line3d(-1.0,1.0,1.0,-1.0,1.0,-1.0));
		lines.add(new Line3d(-1.0,1.0,1.0,1.0,1.0,1.0));
		
		lines.add(new Line3d(1.0,-1.0,-1.0,1.0,-1.0,1.0));
		lines.add(new Line3d(1.0,-1.0,-1.0,1.0,1.0,-1.0));
		lines.add(new Line3d(1.0,-1.0,-1.0,-1.0,-1.0,-1.0));

		
		lines.add(new Line3d(1.0,1.0,1.0,1.0,1.0,-1.0));
		lines.add(new Line3d(1.0,1.0,1.0, 1.0,-1.0,1.0));
		lines.add(new Line3d(1.0,1.0,1.0, -1.0,1.0,1.0));

		//set camera Location
		cameraLocation  = new Loc3d(5,5,5);
	}
	
	public void paint(Graphics page){
		page.setColor(Color.green);
		for(Line3d line : lines){
			
		}
			
	}
	
	public Line3d perspective(Line3d line){
		Array2DRowRealMatrix transform = new Array2DRowRealMatrix();
		return new Line3d(1,1,1,1,1,1);
	}
}
