package cs_440.keyacoustics.features;
import java.util.ArrayList;

import cs_440.keyacoustics.dictionary.KEYBOARD_SIDE;
import cs_440.keyacoustics.dictionary.KeyPosition;

/**
 * Represents a letter. Contains data as to the name of the letter,
 * the feature vector associated with it, and whether it is on
 * the left or right side of the keyboard.
 * 
 * Used to train our Neural Network for the Left/Right determination.
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
	public KeyPosition kp;
	
	
	ArrayList<String> lKeys;
	
	public Letter(String name, double[] fv){
		//Initializing fields.
		this.name = name;
		this.fv = fv;
		
		//Checking if it is on the left or right side.
		kp = new KeyPosition(name);
		ks = kp.keyboardSide();
	}
	
}
