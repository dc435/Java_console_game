/**
 * Class for Entity objects
 * @author: Damian Curran
 *
 */

public class Entity implements Renderable {

	private int posY;
	private int posX;
	private char symbol;
	
	/*
	 *  Default constructor:
	 */
	public Entity() {	//Default values:
		this.posY = 0;
		this.posX = 0;
		this.symbol = '\u0000'; 
	}
	
	/*
	 *  Constructor - overloaded:
	 */
	public Entity (int y, int x, char c) {
		this.posY = y;
		this.posX = x;
		this.symbol = c;
	}
	
	/*
	 *  Renderable Interface method:
	 */
	public void render() {
		System.out.print(symbol);
	}
	
	/*
	 *  Method to check if entity at position (x,y). Returns true if at position.
	 */
	public boolean atPosition(int x, int y) {
		if ((this.getPosX() == x) && (this.getPosY() == y)) {
			return true;
		} else {
			return false;
		}	
	}
		
	/*
	 *  Setter methods:
	 */
	public void setPosition(int x, int y) {
		this.posX = x;
		this.posY = y;
	}
	
	public void setPosX(int x) {
		this.posX = x;
	}
	
	public void setPosY(int y) {
		this.posY = y;
	}
	
	public void setSymbol(char c) {
		this.symbol = c;
	}
	
	/*
	 *  Getter methods:
	 */
	public int getPosX () {
		return posX;
	}
	
	public int getPosY() {
		return posY;
	}
	
	public char getSymbol() {
		return symbol;
	}	
	

}
