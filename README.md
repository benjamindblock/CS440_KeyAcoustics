KEYBOARD ACOUSTICS
==================

BEN BLOCK AND WALKER BOHANNAN - 5/15/14

Note: Our code will almost assuredly not run on your machine, unless some of the database settings are modified. Also, you will need to have a plain text dictionary on file to pass to the database. 

All of our code is located within the KeyRecognition project, which contains multiple packages of code. The specifics of what each package contains is displayed below:

Default Package:
The main method of our program is located in the LoadSpeechWaveform class, which calls all of the necessary operations.

cs_440.keyacoustics.database:
This package contains all the code necessary to create and establish a connection with our database. To do this we use MySQL and the Java Database Connection (JDBC).

cs_440.keyacoustics.dictionary:
This package contains code that receives text input in the form of a dictionary or a text stream of our training data. We use the classes contained to label our data with our enumerated types (DISTANCE and KEYBOARD_SIDE) and package words (represented in Words.java) into groups of Bigrams.java (pairs of characters) and Grams.java (single characters). TextStream.java is the first class in this chain and reads in the text files.

cs_440.keyacoustics.features:
This package contains the code to perform all of our audio processing. This includes scanning the audio file for peaks in PeakAnalysis.java, creating new Letter and LetterPair instances for each chunk of audio data that represents a single key press or two key presses, getting the MFCCs for these peaks in ComputeMFCC.java, and finally getting the standard deviation of both the FFT and the MFCC in StandardDeviationCalculator.java, which is the final input that we will pass to our neural networks. 

cs_440.keyacoustics.neuralnetwork:
This package contains our code to create neural networks and train them, put in input and receive output, and finally take the output and find the matching word.

Libraries:
Our libraries that we used are: jAudio (which we use for feature extraction of the audio files) and Neuroph (used for creation and use of neural networks).

Test Data:
If the database is implemented (you may use the google-10000-english.txt for the list of words), there is a folder containing test data that can be used to “train” and then “attack.”

Neural Networks:
Contains our two .nnet files which are where we store our neural networks. The file path in LoadSpeechWaveform in the field “FILE_PATH_LR” and “FILE_PATH_NF” will need to be changed to load and save the networks.