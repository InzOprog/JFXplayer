package com.example.jfxplayer;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.farng.mp3.*;
import org.farng.mp3.id3.AbstractID3v2;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class buttonController {
    public ListView<String> lv_fx_Title;
    public ListView<String> lv_fx_Artist;
    public ListView<String> lv_fx_Album;
    public Button btn_fx_Previous;
    public Button btn_fx_Play;
    public Button btn_fx_Next;
    public Button btn_fx_Select;
    public Button btn_fx_Shuffle;
    public Button btn_fx_Repeat;
    public Button btn_fx_Mute;
    public VBox vBox_fx_Main;
    public ImageView iv_fx_Previous;
    public ImageView iv_fx_Play;
    public ImageView iv_fx_Next;
    public ImageView iv_fx_Select;
    public ImageView iv_fx_Shuffle;
    public ImageView iv_fx_Repeat;
    public ImageView iv_fx_Mute;
    public ProgressBar pb_fx_musicBar;

    private MediaPlayer mediaPlayer;
    private final ArrayList<String> pathList = new ArrayList<>();
    public int currentIndex = 0, repeatOn = 0;

    @FXML
    protected void clickPrevious() {
        if (pathList.size() > 0) {
            mediaPlayer.dispose();
            --currentIndex;
            if (currentIndex < 0) {
                currentIndex = pathList.size() - 1;
            }
            mediaPlayer = new MediaPlayer(new Media(new File(pathList.get(currentIndex)).toURI().toString()));
            mediaPlayer.play();
            lv_fx_Title.getSelectionModel().select(currentIndex);
            lv_fx_Artist.getSelectionModel().select(currentIndex);
            lv_fx_Album.getSelectionModel().select(currentIndex);
        }
    }

    @FXML
    protected void clickPlay() {
        if (pathList.size() > 0)
            switch (mediaPlayer.getStatus()) {
                case PLAYING:
                    iv_fx_Play.setImage(new Image("file:src/main/resources/images/play.png"));
                    mediaPlayer.pause();
                    break;
                case PAUSED:
                    iv_fx_Play.setImage(new Image("file:src/main/resources/images/pause.png"));
                    Duration test = mediaPlayer.getCurrentTime();
                    mediaPlayer.play();
                    mediaPlayer.seek(test);
                    break;
                case READY:
                    iv_fx_Play.setImage(new Image("file:src/main/resources/images/pause.png"));
                    mediaPlayer.play();
//                    progressBar.setMinimum(0);
//                    progressBar.setMaximum((int) mediaPlayer.getTotalDuration().toMillis());
//                    progressBar.setValue(0);
//                    t.start();
                default:
                    break;
            }
    }

    @FXML
    protected void clickNext() {
        if (pathList.size() > 0) {
            mediaPlayer.dispose();
            currentIndex = (++currentIndex) % pathList.size();
            mediaPlayer = new MediaPlayer(new Media(new File(pathList.get(currentIndex)).toURI().toString()));
            mediaPlayer.play();
            lv_fx_Title.getSelectionModel().select(currentIndex);
            lv_fx_Artist.getSelectionModel().select(currentIndex);
            lv_fx_Album.getSelectionModel().select(currentIndex);
        }
    }

    @FXML
    protected void clickSelect() throws TagException, IOException {
        String path = "";
        JFileChooser fc = new JFileChooser();
        ArrayList<String> tempList = new ArrayList<>();

        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        currentIndex = 0;
        int returnValue = fc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION)
            path = fc.getSelectedFile().getAbsolutePath();

        File dirPath = new File(path);
        if (dirPath.exists()) {
            String[] contents = dirPath.list((dir1, name) -> name.toLowerCase().endsWith(".mp3"));
            if (contents != null && contents.length > 0) {
                tempList.addAll(Arrays.asList(contents));
                lv_fx_Title.getItems().clear();
                lv_fx_Artist.getItems().clear();
                lv_fx_Album.getItems().clear();
                for (String e : tempList) {
                    pathList.add((dirPath + "/" + e));
                    MP3File mp3file = new MP3File(new File(pathList.get(pathList.size() - 1)));
                    setMetadataToLists(mp3file, e);
                }

            mediaPlayer = new MediaPlayer(new Media(new File(pathList.get(0)).toURI().toString()));
            lv_fx_Title.getSelectionModel().select(currentIndex);
            lv_fx_Artist.getSelectionModel().select(currentIndex);
            lv_fx_Album.getSelectionModel().select(currentIndex);
            }
        }
        sync();
    }

    @FXML
    protected void clickShuffle() throws TagException, IOException {
        if (pathList.size() > 0) {
            Collections.shuffle(pathList);
            for (String e : pathList) {
                MP3File mp3file = new MP3File(new File(e));
                setMetadataToLists(mp3file, e);
            }
        }
    }

    @FXML
    protected void clickRepeat() {
        if (pathList.size() > 0) {
            switch (repeatOn) {
                case 0 -> mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(mediaPlayer.getStartTime()));
                case 1 -> mediaPlayer.setOnEndOfMedia(this::clickNext);
                default -> {
                }
            }
        }
    }

    @FXML
    protected void clickMute() {
        if (pathList.size() > 0) {;
            if (mediaPlayer.isMute()) {
                mediaPlayer.setMute(false);
            } else if (!(mediaPlayer.isMute())) {
                mediaPlayer.setMute(true);
            }
        }
    }


    protected void setMetadataToLists(MP3File mp3file, String e) {
        if (mp3file.hasID3v2Tag()) {
            AbstractID3v2 tag = mp3file.getID3v2Tag();
            if (tag.getSongTitle() != null && !tag.getSongTitle().equals(""))
                lv_fx_Title.getItems().add(tag.getSongTitle().substring(2));
            else lv_fx_Title.getItems().add(e.split("\\.")[0]);
            if (tag.getLeadArtist() != null && !tag.getLeadArtist().equals(""))
                lv_fx_Artist.getItems().add(tag.getLeadArtist().substring(2));
            else lv_fx_Artist.getItems().add("Unknown");
            if (tag.getAlbumTitle() != null && !tag.getAlbumTitle().equals(""))
                lv_fx_Album.getItems().add(tag.getAlbumTitle().substring(2));
            else lv_fx_Album.getItems().add("Unknown");
        }
    }

    protected void sync() {
        Node n1 = lv_fx_Title.lookup(".scroll-bar");
        if (n1 instanceof final ScrollBar bar1) {
            Node n2 = lv_fx_Artist.lookup(".scroll-bar");
            if (n2 instanceof final ScrollBar bar2) {
                bar1.valueProperty().bindBidirectional(bar2.valueProperty());
                Node n3 = lv_fx_Album.lookup(".scroll-bar");
                if (n3 instanceof final ScrollBar bar3) {
                    bar2.valueProperty().bindBidirectional(bar3.valueProperty());
                }
            }
        }
    }
}