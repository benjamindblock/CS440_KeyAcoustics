/*
OC Volume - Java Speech Recognition Engine
Copyright (c) 2002-2004, OrangeCow organization
All rights reserved.

Redistribution and use in source and binary forms,
with or without modification, are permitted provided
that the following conditions are met:

* Redistributions of source code must retain the
  above copyright notice, this list of conditions
  and the following disclaimer.
* Redistributions in binary form must reproduce the
  above copyright notice, this list of conditions
  and the following disclaimer in the documentation
  and/or other materials provided with the
  distribution.
* Neither the name of the OrangeCow organization
  nor the names of its contributors may be used to
  endorse or promote products derived from this
  software without specific prior written
  permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

Contact information:
Please visit http://ocvolume.sourceforge.net.
*/

package org.oc.ocvolume;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.oc.ocvolume.audio.*;
import org.oc.ocvolume.wav.*;
import org.oc.ocvolume.dsp.*;
import org.oc.ocvolume.dsp.featureExtraction;
import org.oc.ocvolume.vq.*;
import org.oc.ocvolume.hmm.markov;

/**
 * training class for VQ recognition
 * last updated on June 15, 2002
 * @author Danny Su
 * @author Keith Fung
 */
public class train extends JFrame implements Runnable{
    /**
     * width of window
     */
    private final int WIDTH = 200;
    /**
     * height of window
     */
    private final int HEIGHT = 250;
    /**
     * microphone input
     */
    private micInput mic = new micInput();
    /**
     * words in the dictionary
     */
    private String words[];
    /**
     * thread to check whether recording is done
     */
    private Thread checking;
    /**
     * text area
     */
    private JTextArea txt = new JTextArea();
    private JScrollPane scrollPane;
    /**
     * menubar
     */
    private JMenuBar menuBar;
    /**
     * menu
     */
    private JMenu menu;
    /**
     * each item in the menu
     */
    private JMenuItem menuItem;
    /**
     * file chooser to open or save file
     */
    private JFileChooser fc = new JFileChooser();
    /**
     * option pane use to show msg box
     */
    private JOptionPane optionPane = new JOptionPane();
    /**
     * default font
     */
    private Font defaultFont = new Font("Arial", Font.PLAIN, 10);
    /**
     * pointer to this frame
     */
    private JFrame frame = this;
    
    /**
     * number of samples used to train a codebook<br>
     * default is 20
     */
    private int num_samples = 20;
    /**
     * number of samples recorded
     */
    private int num_recorded = 0;
    /**
     * file path to save the codebook to
     */
    private String word = "";
    /**
     * word being trained
     */
    private File file = null;
    /**
     * samples
     */
    private short samples[][];
    
    /**
     * creates new form volume
     */
    public train(){
        super("VQ codebook training");
        initComponents();
        setSize(WIDTH, HEIGHT);
        this.setLocation(WIDTH, HEIGHT);
        
        addText("num_samples: " + num_samples);
    }
    
    /**
     * this method is called from within the constructor to
     * initialize the form
     */
    private void initComponents(){
        //Create the menu bar.
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        //Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.setFont(defaultFont);
        menuBar.add(menu);
        
        menuItem = new JMenuItem("New", KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.setFont(defaultFont);
        menuItem.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent event){
                word = optionPane.showInputDialog(train.this, "Please type in the word you want to train");
                addText("word: " + word);
                
                int returnVal = fc.showSaveDialog(train.this);
                
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = fc.getSelectedFile();
                    addText("filepath: " + file.getPath());
                }
            }
        }
        );
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        menuItem.setFont(defaultFont);
        menuItem.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent event){
                exit();
            }
        }
        );
        menu.add(menuItem);
        
        // Format menu
        menu = new JMenu("Configuration");
        menu.setMnemonic(KeyEvent.VK_O);
        menu.setFont(defaultFont);
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Set Number Of Samples");
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.setFont(defaultFont);
        menuItem.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent event){
                num_samples = Integer.parseInt(optionPane.showInputDialog(train.this, "Please enter the number of training samples"));
                addText("num_samples: " + num_samples);
            }
        }
        );
        menu.add(menuItem);
        
        // Help menu
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.setFont(defaultFont);
        menuBar.add(menu);
        
        menuItem = new JMenuItem("About");
        menuItem.setFont(defaultFont);
        menuItem.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent event){
                JPanel panel = new JPanel();
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                
                JLabel name = new JLabel("OC Volume - training");
                name.setFont(defaultFont);
                panel.add(name);
                
                JLabel url = new JLabel("Visit http://ocvolume.sourceforge.net for more detail.");
                url.setFont(defaultFont);
                panel.add(url);
                
                optionPane.showMessageDialog(train.this, panel, "About", JOptionPane.PLAIN_MESSAGE);
            }
        }
        );
        menu.add(menuItem);
        
        txt.setWrapStyleWord(true);
        txt.setLineWrap(true);
        txt.setEditable(false);
        txt.setFont(defaultFont);
        scrollPane = new JScrollPane(txt);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        JButton button = new JButton("record");
        button.setFont(defaultFont);
        button.addActionListener(
        new ActionListener(){
            public void actionPerformed(ActionEvent event){
                JButton b = (JButton)event.getSource();
                if (word == ""){
                    addText("please set the word to be trained");
                }
                else if(file == null){
                    addText("please set a filepath");
                }
                else{
                    if (num_recorded == 0){
                        samples = new short[num_samples][];
                    }
                    
                    if ( b.getLabel().equals("record") == true ){
                        b.setLabel("stop");
                        mic = new micInput();
                        // always use discrete recording during training
                        mic.setDiscrete();
                        mic.start();
                        try{
                            checking.stop();
                        }
                        catch(NullPointerException e){}
                        checking = new Thread((Runnable)frame);
                        checking.start();
                    }
                    else if (b.getText().equals("stop") == true ){
                        b.setText("record");
                        mic.stopRecord();
                    }
                }
            }
        }
        );
        getContentPane().add(button, BorderLayout.SOUTH);
        
        addWindowListener(
        new WindowAdapter(){
            public void windowClosing(WindowEvent event) {
                exit();
            }
        }
        );
        
        pack();
    }
    
    /**
     *
     */
    public void run(){
        while(true){
            mic.removeOldWord();
            
            while(!mic.byteArrayComplete()){
                try{
                    checking.sleep(200);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            
            mic.newWord();
            num_recorded++;
            samples[num_recorded - 1] = mic.returnShortArray();
            
            if ( num_recorded == num_samples ){
                addText("start training");
                
                final int DIMENSION = 12;
                int ctr = 0;
                Vector v = new Vector(1, 1);
                for (int num = 0; num < num_samples; num++){
                    short input[] = endPt.absCut(samples[num]);
                    double mfcc[][] = featureExtraction.process(input);
                    
                    for (int j = 0; j < mfcc.length; j++){
                        String temp = "";
                        for (int k = 1; k < mfcc[j].length; k++){
                            temp += mfcc[j][k] + " ";
                        }
                        v.add(temp);
                        ctr++;
                    }
                }
                
                double mfcc[][] = new double[ctr][DIMENSION];
                
                // convert to double in order to be trained
                for (int i = 0; i < ctr; i++){
                    StringTokenizer st = new StringTokenizer((String)v.elementAt(i));
                    
                    for (int k = 0; k < DIMENSION; k++){
                        mfcc[i][k] = Double.parseDouble(st.nextToken());
                    }
                }
                
                point pts[] = new point[ctr];
                for (int j = 0; j < ctr; j++){
                    pts[j] = new point(mfcc[j]);
                }
                
                codebook cbk = new codebook(pts);
                cbk.saveToFile(file.getPath());
                
                file = null;
                word = "";
                samples = null;
                num_recorded = 0;
                addText("finished training");
            }
            else{
                addText(num_recorded + " samples recorded");
            }
        }
    }
    
    /**
     * append text to TextArea and add newline character to the end
     * @param text text to be added
     */
    private void addText(String text){
        txt.append(text + "\n");
    }
    
    /**
     * exit the program
     */
    private void exit(){
        System.exit(0);
    }
    
    /**
     * create an instance of train
     * @param args command line input
     */
    public static void main(String args[]) {
        new train().show();
    }
}