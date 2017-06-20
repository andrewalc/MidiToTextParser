package MidiParser.view;

import java.awt.*;
import java.io.File;

import javax.swing.*;

/**
 * JFrame view for the MidiParser.
 */
public class MidiParserView extends JFrame implements IMidiParserView {


  File selectedFile;

  /**
   * Constructor for a
   */
  public MidiParserView() {
    super("CS3500 MIDIToTxt Converter");
    setSize(400, 300);
    setResizable(true);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setBackground(Color.WHITE);
    FileSelectorPanel fileChooser = new FileSelectorPanel();
    getContentPane().add(fileChooser);
    this.selectedFile = fileChooser.getSelectedFile();
  }

  @Override
  public File getSelectedFile() {
    return selectedFile;
  }

  @Override
  public void initialize() {
    setVisible(true);
  }
}
