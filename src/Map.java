/**
 * Class for Map objects
 * @author: Damian Curran
 *
 */

public class Map {

	private int height;
	private int width;
	private Terrain[][] elements;
	
	/*
	 *  Constructor
	 */
	public Map(int h, int w) {
		
		this.height = h;
		this.width = w;
		elements = new Terrain[height][width];
		
		//Initialise element array of default Terrain objects:
		for (int i = 0; i < height; i ++) {
			for (int j = 0; j < width; j ++) {
				elements[i][j] = new Terrain();
			}
		}
		
	}	

	/*
	 *  Setter methods:
	 */
	public void setElement(int y, int x, char c, boolean b) {
		elements[y][x].set(c, b);	
	}
	
	/*
	 *  Getter methods:
	 */
	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
	
	public Terrain getElement(int y, int x) {
		return elements[y][x];
	}
	
	/*
	 *  Update terrain element:
	 *  Called during map load from file to override default terrain:
	 */
	public void updateTerrain(int y, int x, char c) {
		elements[y][x].set(c);
		
	}
	
	/*
	 *  Check if terrain element is traversable
	 *  Used during unit movement operations to check if adjacent movement is possible
	 */
	public boolean isTraversable(int y, int x) {
		return elements[y][x].getTraversable();
	}
	
}
