/**
 * Class for Terrain objects
 * @author: Damian Curran
 *
 */

public class Terrain implements Renderable {
	
	private char symbol;
	private boolean traversable;
	
	private final static char DEFAULT_SYMBOL = '.';
	private final static boolean DEFAULT_TRAVERSABLE = true;
	private final static char[] NON_TRAVERSABLE_TILES = {'#' , '~'};
	
	/*
	 *  Constructor (default)
	 */
	public Terrain () {
		this(DEFAULT_SYMBOL, DEFAULT_TRAVERSABLE);
		
	}	
	
	/*
	 *  Constructor (overloaded)
	 */
	public Terrain(char c, boolean b) {
		this.symbol = c;
		this.traversable = b;
		
	}	
	
	/*
	 *  Constructor (overloaded)
	 */
	public Terrain(char c) {
		this.symbol = c;
		this.traversable = true;
		for (char ntt: NON_TRAVERSABLE_TILES) {
			if (c == ntt) this.traversable = false;
		}
		
	}

	/*
	 *  Method to set symbol
	 */
	public void set(char c) {
		this.symbol = c;	
		this.traversable = true;	
		for (char ntt: NON_TRAVERSABLE_TILES) {
			if (c == ntt) this.traversable = false;
		}
		
	}
	
	/*
	 *  Method to set symbol (overloaded)
	 */
	public void set(char c, boolean b) {
		this.symbol = c;
		this.traversable = b;
		
	}
	
	/*
	 *  Renderable Interface method:
	 */
	public void render() {
		System.out.print(symbol);
		
	}
	
	/*
	 *  Getter methods
	 */
	public char getSymbol() {
		return this.symbol;
	}
	
	public boolean getTraversable() {
		return this.traversable;
	}

}
