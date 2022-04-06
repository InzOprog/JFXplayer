//
// TODO: DELETE THIS FILE
//

package com.example.jfxplayer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GridBag {
    public JFrame frame;
    private DefaultListModel<String> model = new DefaultListModel<String>();
    public JList<String> list = new JList<>(model);
    public JScrollPane scrollPane = new JScrollPane(list);
    public ArrayList<String> paths = new ArrayList<>();
    public JButton btn_previous = new JButton(rescaleImage("src/main/resources/images/previous.png", 30, 30)),
                   btn_pause = new JButton(rescaleImage("src/main/resources/images/play.png", 30, 30)),
                   btn_next = new JButton(rescaleImage("src/main/resources/images/next.png", 30, 30)),
                   btn_mute = new JButton(rescaleImage("src/main/resources/images/mute.png", 30, 30)),
                   btn_select = new JButton(rescaleImage("src/main/resources/images/album.png", 30, 30)),
                   btn_shuffle = new JButton(rescaleImage("src/main/resources/images/shuffle.png", 30, 30)),
                   btn_repeat = new JButton(rescaleImage("src/main/resources/images/repeat.png", 30, 30));
    public JSlider volume   = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
    JProgressBar progressBar = new JProgressBar();


    public GridBag() {
        //Create and set up the window.
        frame = new JFrame("KajPlayer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    //generic function add element to JPanel
    public void addToPanel(JPanel panel, JComponent component, int gridx, int gridy,
                           int gridwidth, int gridheight, int weightx, int weighty,
                           int ipadx, int ipady ,int fill) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = gridwidth;
        c.gridheight = gridheight;
        c.weightx = weightx;
        c.weighty = weighty;
        c.ipady = ipady;
        c.ipadx = ipadx;
        c.fill = fill;
        panel.add(component, c);
    }

    public ImageIcon rescaleImage(String image, int width, int height) {
        return new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(width, height,  Image.SCALE_SMOOTH));
    }

    public void addComponentsToPane(Container pane) {
        pane.setLayout(new GridBagLayout());

        addToPanel((JPanel) pane, scrollPane, 0, 0, 9, 1, 0, 0, 350, 200, GridBagConstraints.BOTH);
        addToPanel((JPanel) pane, progressBar, 0, 1, 9, 1, 1, 0, 0, 0, GridBagConstraints.HORIZONTAL);
        addToPanel((JPanel) pane, btn_previous, 0, 2, 1, 1, 1, 0, 10, 10, GridBagConstraints.NONE);
        addToPanel((JPanel) pane, btn_pause, 1, 2, 1, 1, 1, 0, 10, 10, GridBagConstraints.NONE);
        addToPanel((JPanel) pane, btn_next, 2, 2, 1, 1, 1, 0, 10, 10, GridBagConstraints.NONE);
        addToPanel((JPanel) pane, btn_select, 3, 2, 1, 1, 1, 0, 10, 10, GridBagConstraints.NONE);
        addToPanel((JPanel) pane, btn_shuffle, 4, 2, 1, 1, 1, 0, 10, 10, GridBagConstraints.NONE);
        addToPanel((JPanel) pane, btn_repeat, 5, 2, 1, 1, 1, 0, 10, 10, GridBagConstraints.NONE);
        addToPanel((JPanel) pane, volume, 6, 2, 2, 1, 1, 0, 20, 10, GridBagConstraints.NONE);
        addToPanel((JPanel) pane, btn_mute, 8, 2, 1, 1, 1, 0, 10, 10, GridBagConstraints.NONE);

    }
}