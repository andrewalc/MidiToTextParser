package MidiParser.view;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
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
  final URI uri = new URI("https://github" +
          ".com/andrewalc/MidiParser");
  // Center of users screen
  private Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

  /**
   * Constructor for a MidiParserView. A Swing Application to select a midifile to convert to a txt.
   */
  public MidiParserView() throws URISyntaxException {
    super("CS3500 MIDIToTxt Converter");
    setSize(600, 175);
    setResizable(true);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize()
            .height / 2);


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

    //JMenu Bar
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Help");
    JMenuItem info = new JMenuItem("About");
    info.setHorizontalAlignment(SwingConstants.CENTER);
    info.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        JDialog aboutPopUp = new JDialog();
        aboutPopUp.setLocation(MidiParserView.this.getLocation());
        aboutPopUp.getContentPane().setBackground(clBackground);
        aboutPopUp.setLayout(new GridBagLayout());
        aboutPopUp.setSize(400, 100);
        aboutPopUp.setVisible(true);
        aboutPopUp.setResizable(false);
        aboutPopUp.setTitle("About CS3500 Midi to Text");
        JLabel githubInfo = new JLabel();
        githubInfo.setText("<HTML>GitHub: <font size=\"3\" " +
                "color=#33ccff><U>https://github" +
                ".com/andrewalc/MidiParser</U></font></HTML>");
        githubInfo.setForeground(Color.WHITE);
        githubInfo.addMouseListener(new LabelListener());
        c.anchor = GridBagConstraints.CENTER;
        aboutPopUp.add(githubInfo, c);
      }
    });


    // Set menu bars
    menu.add(info);
    menuBar.add(menu);
    this.setJMenuBar(menuBar);

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

  private static void open(URI uri) {
    if (Desktop.isDesktopSupported()) {
      try {
        Desktop.getDesktop().browse(uri);
      } catch (IOException e) { /* TODO: error handling */ }
    } else { /* TODO: error handling */ }
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

  private class LabelListener implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent e) {
      open(uri);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
  }
}
