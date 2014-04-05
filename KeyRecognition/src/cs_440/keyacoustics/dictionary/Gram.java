package cs_440.keyacoustics.dictionary;


public abstract class Gram {
	
	protected String gram;
	protected KEYBOARD_SIDE charSide;
	protected KeyPosition kp;
	
	public Gram(String str) {
		gram = str;
		
	}
}
