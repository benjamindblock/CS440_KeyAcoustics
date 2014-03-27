import java.util.ArrayList;

/**
 * Represents a letter. Contains data as to the name of the letter,
 * the feature vector associated with it, and whether it is on
 * the left or right side of the keyboard.
 * 
 * Organization and method taken from: (sp)iPhone: Decoding Vibrations From Nearby Keyboards
 * Using Mobile Phone Accelerometers, by Philip Marquardt, et al.
 * 
 * 
 * @author Ben Block and Walker Bohannan. 3/26/14
 *
 */

public class Letter {
	
	public KEYBOARD_SIDE ks; //Is this key on the left or right side.
	public String name; //What is character is this letter representing
	public double[] fv; //The feature vector containing the MFCC data we need to analyze this letter.
	
	
	ArrayList<String> lKeys;
	
	public Letter(String name, double[] fv){
		//Initializing fields.
		this.name = name;
		this.fv = fv;
		
		//Checking if it is on the left or right side.
		this.lKeys = new ArrayList<String>();
		populateKeys();
		if(lKeys.contains(name)){
			this.ks = ks.LEFT;
		}else{
			this.ks = ks.RIGHT;
		}
	}
	
	/**
	 * Quick method that puts all the keys that are on the left side of the keyboard 
	 * into our ArrayList so we can check if our given letter is on the left or right.
	 * 
	 * Split along t, g, and b inclusively.
	 */
	private void populateKeys(){
		lKeys.add("q");
		lKeys.add("w");
		lKeys.add("e");
		lKeys.add("r");
		lKeys.add("t");
		lKeys.add("a");
		lKeys.add("s");
		lKeys.add("d");
		lKeys.add("f");
		lKeys.add("g");
		lKeys.add("z");
		lKeys.add("x");
		lKeys.add("c");
		lKeys.add("v");
		lKeys.add("b");
	}
	
}
