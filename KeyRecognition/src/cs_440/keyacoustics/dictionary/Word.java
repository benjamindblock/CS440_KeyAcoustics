package cs_440.keyacoustics.dictionary;
/**
 * This class comprises words that will make up our dictionary.
 * Used when importing the dictionary and when we are checking against it.
 * 
 * @author Ben Block and Walker Bohannan
 *
 */
public class Word {


	private String keyWord;
	private Bigram[] bigramArray;
	private int wordLength;

	public Word(String word){
		keyWord = word;
		wordLength = keyWord.length();
		if (wordLength == 1) {
			bigramArray = new Bigram[wordLength];
		} else {
			bigramArray = new Bigram[wordLength-1];
		}
		createBigramArray();
	}

	public void createBigramArray(){
		boolean isSpecial = false;
		if (keyWord.length() == 1) {
			isSpecial = true;
			String pair = keyWord;
			Bigram big = new Bigram(pair, isSpecial);
			bigramArray[0] = big;
			//System.out.println(big.toString());
		} else {
			for(int i = 0; i < keyWord.length()-1; i++){
				char[] pairArray = new char [2];
				pairArray[0] = keyWord.charAt(i);
				pairArray[1] = keyWord.charAt(i+1);
				String pair = new String(pairArray);
				Bigram big = new Bigram(pair, isSpecial);
				bigramArray[i] = big;
				//System.out.println(big.toString());
			}
		}
	}

	/**
	 * Returns the name of this word.
	 * 
	 * @return The name
	 */
	public String getWordName(){
		return keyWord;
	}

	public int getWordLength(){
		return wordLength;
	} 

	/**
	 * Returns the bigram array for this word.
	 * 
	 * @return The array
	 */
	public Bigram[] getBigrams(){
		return bigramArray;
	}

}
