package cs_440.keyacoustics.features;
import jAudioFeatureExtractor.AudioFeatures.MagnitudeSpectrum;
import jAudioFeatureExtractor.AudioFeatures.PeakFinder;


public class ComputePeakFinder {
	
	private final int SAMPLE_RATE = 44100;
	double[] audioData; //Our audio data
	
	/**
	 * A very simple constructor that takes an input of audio data and puts that
	 * into our local field that we will use when we compute the peaks
	 * 
	 * @param audioData The audio data passed to us that we will scan for peaks
	 */
	public ComputePeakFinder(double[] audioData){
		this.audioData = audioData; 
	}

	
	/**
	 * Takes the audio data passed to us in the constructor and then searches it
	 * for peaks by using the jAudio PeakFinder class, which returns a double[] with
	 * the location of the peaks, which we then return.
	 * 
	 * @return The double[] containing the location of the peaks.
	 */
	public double[] compute(){
		
		double[] peaks = new double[0]; //Initialize our array that we will return
		
		MagnitudeSpectrum ms = new MagnitudeSpectrum();
		PeakFinder pf = new PeakFinder();
		
		try {
			pf.setPeakThreshold(1.14);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println("Set peak failed");
			e1.printStackTrace();
		}

		try {
			/**
			 * Because the PeakFinder method makes use of the MagnitudeSpectrum of the
			 * audio, we must first compute the MagnitudeSpectrum and then pass that 
			 * data to the PeakFinder.
			 */
			double[] magnitudeSpec = ms.extractFeature(audioData, SAMPLE_RATE, null); //Get the Magnitude Spectrum
			double[][] otherFV = new double[1][magnitudeSpec.length]; //Initialize the array we will pass to PeakFinder
			
			for(int i = 0; i < 1; i++){
				for(int j = 0; j < magnitudeSpec.length; j++){
					otherFV[i][j] = magnitudeSpec[j]; //Populate the array with the data from the Magnitude Spectrum array.
				}
			}
			
			peaks = pf.extractFeature(audioData, SAMPLE_RATE, otherFV); //Extract the peak data from the audio data.
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Peak Finder failed.");
			e.printStackTrace();
		}
		
		return peaks; //Return
	}
}
