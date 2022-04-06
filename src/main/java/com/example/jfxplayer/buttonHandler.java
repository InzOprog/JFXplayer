//
// TODO: DELETE THIS FILE
//

package com.example.jfxplayer;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class buttonHandler extends GridBag{
    MediaPlayer mediaPlayer;
    private int repeatOn = 0, curentIndex = 0;
    private String path = "";
    private File dirPath;
    public buttonHandler() {
        //BarThread
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    int value = progressBar.getValue();
                    progressBar.setValue((int) mediaPlayer.getCurrentTime().toMillis());
                    progressBar.setString(formatedTime((int) mediaPlayer.getCurrentTime().toMillis()));
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        //previous
            btn_previous.addActionListener(actionEvent -> {
                mediaPlayer.dispose();
                --curentIndex;
                if (curentIndex < 0) {
                    curentIndex = paths.size() - 1;
                }
                mediaPlayer = new MediaPlayer(new Media("file:///" + paths.get(curentIndex)));
                mediaPlayer.play();
                list.setSelectedIndex(curentIndex);
            });
        //pause/play
        btn_pause.addActionListener(actionEvent -> {
            if (paths.size() > 0)
                switch (mediaPlayer.getStatus()) {
                    case PLAYING:
                        btn_pause.setIcon(rescaleImage("src/main/resources/images/play.png", 20, 20));
                        mediaPlayer.pause();
                        break;
                    case PAUSED:
                        btn_pause.setIcon(rescaleImage("src/main/resources/images/pause.png", 20, 20));
                        Duration test = mediaPlayer.getCurrentTime();
                        mediaPlayer.play();
                        mediaPlayer.seek(test);
                        break;
                    case READY:
                        btn_pause.setIcon(rescaleImage("src/main/resources/images/pause.png", 20, 20));
                        mediaPlayer.play();
                        progressBar.setMinimum(0);
                        progressBar.setMaximum((int) mediaPlayer.getTotalDuration().toMillis());
                        progressBar.setValue(0);
                        t.start();
                    default:
                        break;
                }
        });

        //next
            btn_next.addActionListener(actionEvent -> {
                mediaPlayer.dispose();
                curentIndex = (++curentIndex)%paths.size();
                mediaPlayer = new MediaPlayer(new Media("file:///" + paths.get(curentIndex)));
                mediaPlayer.play();
                list.setSelectedIndex(curentIndex);
            });
        //select
            btn_select.addActionListener(actionEvent -> {
                curentIndex = 0;
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fc.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    path = fc.getSelectedFile().getAbsolutePath();
                }
                ArrayList<String> templist = new ArrayList<>();
                dirPath = new File(path);
                if (dirPath.exists()) {
                    String[] contents = dirPath.list();
                    System.out.println("List of files and directories in the specified directory:");
                    if (contents != null) {
                        templist.addAll(Arrays.asList(contents));
                        DefaultListModel<String> mod = new DefaultListModel<String>();
                        for (String s : templist)
                            mod.addElement(s);
                        list.setModel(mod);
                        for (String e : templist)
                            paths.add(path + "/" + e.replaceAll(" ", "%20"));
                    }
                }
                mediaPlayer = new MediaPlayer(new Media("file:///" + paths.get(0)));
                list.setSelectedIndex(curentIndex);
            });
        //shuffle
            btn_shuffle.addActionListener(actionEvent -> {
                if (paths.size() > 0) {
                    DefaultListModel<String> mod = new DefaultListModel<String>();
                    ArrayList<String> temp = new ArrayList<>();
                    for (int i = 0; i < list.getModel().getSize(); i++)
                        temp.add(list.getModel().getElementAt(i));
                    Collections.shuffle(temp);
                    paths.clear();
                    for (String e : temp)
                        paths.add(path + "/" + e);
                }
            });
        //repeat
            btn_repeat.addActionListener(actionEvent -> {
                switch (repeatOn) {
                    case 0 -> mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(mediaPlayer.getStartTime()));
                    case 1 -> mediaPlayer.setOnEndOfMedia(() -> {
                        mediaPlayer.dispose();
                        curentIndex = (++curentIndex)%paths.size();
                        mediaPlayer = new MediaPlayer(new Media("file:///" + paths.get(curentIndex)));
                    });
                    default -> {}
                }
            });
        //volSlider

        //volume

    }

    private String formatedTime(int mCuurentPosition) {
        String totalout;
        String totalnew;
        String seconds=String.valueOf(mCuurentPosition % 60);
        String mints =String.valueOf(mCuurentPosition / 60);
        totalout =mints + ":" + seconds;
        totalnew=mints + ":" + "0" + seconds;
        if (seconds.length()==1){
            return totalnew;
        } else {
            return totalout;

        }}
}
