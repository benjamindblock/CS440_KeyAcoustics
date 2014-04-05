package cs_440.keyacoustics.features;
import cs_440.keyacoustics.dictionary.DISTANCE;
import cs_440.keyacoustics.dictionary.KEYBOARD_SIDE;
import cs_440.keyacoustics.dictionary.KeyPosition;

/**
 * Represents a letter-pair, such as "ab." Contains data as to the name of the letter-pair,
 * the feature vector associated with it, and whether it is a near or far pair.
 * 
 * Used to train our Neural Network in the Near/Far determination.
 * 
 * Organization and method taken from: (sp)iPhone: Decoding Vibrations From Nearby Keyboards
 * Using Mobile Phone Accelerometers, by Philip Marquardt, et al.
 * 
 * @author Ben Block and Walker Bohannan. 3/26/14
 *
 */

public class LetterPair {
	
	private final int THRESHOLD = 3;
	
	public KEYBOARD_SIDE l1_side;
	public KEYBOARD_SIDE l2_side;
	private KeyPosition k1;
	private KeyPosition k2;
	public DISTANCE ds;
	public String name;
	public double[] fv; //Our feature vector of MFCC data
	
	public LetterPair(Letter l1, Letter l2){
		//The name of this letter pair. Will be a concatenated string such as "ab."
		this.name = l1.name + l2.name;
		
		//Our feature vector for this letter pair.
		fv = new double[l1.fv.length + l2.fv.length];
		
		for(int i = 0; i < l1.fv.length; i++){
			fv[i] = l1.fv[i];
		}
		
		for(int i = l1.fv.length; i < l2.fv.length; i++){
			fv[i] = l2.fv[i];
		}
		
		l1_side = l1.ks;
		l2_side = l2.ks;
		
		k1 = new KeyPosition(l1.name);
		k2 = new KeyPosition(l2.name);
		
		distance();
	}
	
	/**
	 * Determines if the given letter pair is a near-pair or a
	 * far-pair.
	 */
	public void distance(){
		int x = (k2.getXPos() - k1.getXPos());
		x = x * x;
		
		int y = (k2.getYPos() - k1.getYPos());
		y = y * y;
		
		double dist = Math.sqrt(x + y);
		
		if(dist > THRESHOLD){
			ds = ds.FAR;
		}else{
			ds = ds.NEAR;
		}
	}
}
