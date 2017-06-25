package MidiParser.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import MidiParser.model.MidiParser;

/**
 * JFrame view for the MidiParser.
 */
public class MidiParserView extends JFrame {

  private Color clBackground = new Color(43, 43, 43);
  private Color clButton = new Color(74, 74, 74);
  private JButton openButton = new JButton("Convert MIDI");
  private JFileChooser jFileChooser = new JFileChooser();
  private JLabel label = new JLabel("Welcome to the CS3500 MIDI to Text converter!");


  /**
   * Constructor for a MidiParserView. A Swing Application to select a midifile to convert to a txt.
   */
  public MidiParserView() {
    super("CS3500 MIDIToTxt Converter");
    setSize(600, 175);
    setResizable(true);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    getContentPane().setBackground(clBackground);
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();


    // FileChooser settings
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Midi Files", "mid", "midi");
    jFileChooser.setFileFilter(filter);

    // Set up LookAndFeel of FileChooser.
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException ex) {
      ex.getMessage();
    } catch (InstantiationException ex) {
      ex.getMessage();
    } catch (IllegalAccessException ex) {
      ex.getMessage();
    } catch (UnsupportedLookAndFeelException ex) {
      ex.getMessage();
    }
    SwingUtilities.updateComponentTreeUI(jFileChooser);


    // Button Settings
    openButton.setPreferredSize(new Dimension(150, 30));
    openButton.setBackground(clButton);
    openButton.setForeground(Color.white);
    openButton.setFocusPainted(false);
    openButton.setBorder(BorderFactory.createLineBorder(Color.white));

    // Give button an action listener to open the file chooser.
    openButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() == openButton) {
          int returnVal = jFileChooser.showOpenDialog(MidiParserView.this);
          if (returnVal == JFileChooser.APPROVE_OPTION) {
            writeSelectedFile(jFileChooser.getSelectedFile());


          } else {
            System.out.println("A file was not selected.");
          }
        }
      }
    });

    // Label placement
    c.gridx = 0;
    c.gridy = 0;
    c.ipady = 50;
    c.anchor = GridBagConstraints.CENTER;
    label.setForeground(Color.WHITE);
    getContentPane().add(label, c);

    // Button placement
    c.gridy = 1;
    c.ipady = 0;
    c.anchor = GridBagConstraints.CENTER;
    getContentPane().add(openButton, c);
  }


  /**
   * Display the view.
   */
  public void initialize() {
    setVisible(true);
  }

  /**
   * Sends the given midifile to a MidiParser to convert it to a txt file. The txt file will be
   * written in the directory that the midi file came from.
   *
   * @param midiFile The midi file to convert to a txt file.
   */
  public void writeSelectedFile(File midiFile) {
    try {
      MidiParser parser = new MidiParser(midiFile);
      parser.writeMidiTextFile();
      // Update the gui to show conversion information.
      this.label.setForeground(Color.GREEN);
      this.label.setText(parser.getSuccessMessage());
      this.openButton.setText("Convert another MIDI");
    } catch (InvalidMidiDataException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    } catch (MidiUnavailableException e) {
      System.out.println(e.getMessage());
    }
  }
}
