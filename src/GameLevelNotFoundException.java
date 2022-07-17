/**
 * Class for Exception type
 * @author: Damian Curran
 *
 */

public class GameLevelNotFoundException extends Exception {

	/*
	 *  Constructor
	 */
	public GameLevelNotFoundException(){
		super("Map not found.");
	}

	/*
	 *  Constructor (overloaded)
	 */
	public GameLevelNotFoundException(String message){
		super(message);
	}
	
}
