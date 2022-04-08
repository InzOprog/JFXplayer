package com.example.jfxplayer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import static javafx.scene.media.MediaPlayer.Status.PLAYING;

public class buttonController {
    public ListView<uniqueString> lv_fx_Title;
    public ListView<uniqueString> lv_fx_Artist;
    public ListView<uniqueString> lv_fx_Album;
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
    public Button btn_fx_volumeUp;
    public ImageView iv_fx_volumeUp;
    public Button btn_fx_volumeDown;
    public ImageView iv_fx_VolumeDown;
    public Slider pb_fx_volumeBar;

    private MediaPlayer mediaPlayer = new MediaPlayer( new Media( new File("src/main/resources/media/sample.mp3").toURI().toString() ) );
    private final ArrayList<String> pathList = new ArrayList<>();
    private int currentIndex = 0, repeatOn = 0;
    private double progress = 0;
    private final ColorAdjust off = new ColorAdjust();
    private final ColorAdjust on = new ColorAdjust();
    private boolean run = true;
    private Thread t;


    public void initScene() {
        //set toggleable 'off' buttons to gray
        off.setContrast(-1.0);
        on.setContrast(1.0);
        iv_fx_Repeat.setEffect(off);
        iv_fx_Mute.setEffect(off);

        //volume misc
        mediaPlayer.setVolume(0.5);
        pb_fx_volumeBar.setValue(mediaPlayer.getVolume()*100);
        pb_fx_volumeBar.valueProperty().addListener((observable, oldValue, newValue) -> mediaPlayer.setVolume(newValue.doubleValue()/100));

        //init misc
        musicBarUpdateThread(); //start the music bar update thread
        sync(); //sync the listviews scrollbar
        initialize(); //init the listviews listeners
        vBox_fx_Main.requestFocus();
    }

    private void musicBarUpdateThread() {
        t = new Thread(() -> {
            while (run) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progress = mediaPlayer.getCurrentTime().toMillis() / mediaPlayer.getTotalDuration().toMillis();
                pb_fx_musicBar.setProgress(progress);
            }
        });
        t.start();
    }

    public void killThread() {
        run = false;
        t.interrupt();
        Platform.exit();
    }

    private void initialize() {
        setSelectListener(lv_fx_Title);
        setSelectListener(lv_fx_Artist);
        setSelectListener(lv_fx_Album);
        serSelecdDoubleclickListener(lv_fx_Title);
        serSelecdDoubleclickListener(lv_fx_Artist);
        serSelecdDoubleclickListener(lv_fx_Album);
    }

    private void setSelectListener(ListView<uniqueString> lv_fx_title) {
        lv_fx_title.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    currentIndex = lv_fx_title.getSelectionModel().getSelectedIndex();
                    lv_fx_Title.getSelectionModel().select(currentIndex);
                    lv_fx_Artist.getSelectionModel().select(currentIndex);
                    lv_fx_Album.getSelectionModel().select(currentIndex);
                });
    }

    private void serSelecdDoubleclickListener(ListView<uniqueString> lv_fx_title) {
        lv_fx_title.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) playNewSong(lv_fx_title);
            iv_fx_Play.setImage(new Image("file:src/main/resources/media/pause.png"));
        });
    }

    private void playNewSong() {
        if (pathList.size() > 0) {
            double temp = mediaPlayer.getVolume();
            mediaPlayer.dispose();
            mediaPlayer = new MediaPlayer(new Media(new File(pathList.get(currentIndex)).toURI().toString()));
            mediaPlayer.setVolume(temp);
            mediaPlayer.play();
        }
    }

    private void playNewSong(ListView<uniqueString> lv_fx_title) {
        if (pathList.size() > 0) {
            currentIndex = lv_fx_title.getSelectionModel().getSelectedIndex();
            playNewSong();
        }
    }

    private void setMetadataToLists(MP3File mp3file, String e) {
        if (mp3file.hasID3v2Tag()) {
            AbstractID3v2 tag = mp3file.getID3v2Tag();
            if (tag.getSongTitle() != null && !tag.getSongTitle().equals("")) {
                lv_fx_Title.getItems().add(new uniqueString(tag.getSongTitle().substring(2)));}
            else lv_fx_Title.getItems().add(new uniqueString(new File(e).getName().split("\\.")[0]));

            if (tag.getLeadArtist() != null && !tag.getLeadArtist().equals("")) {
                lv_fx_Artist.getItems().add(new uniqueString(tag.getLeadArtist().substring(2)));}
            else lv_fx_Artist.getItems().add(new uniqueString("Unknown"));

            if (tag.getAlbumTitle() != null && !tag.getAlbumTitle().equals("")) {
                lv_fx_Album.getItems().add(new uniqueString(tag.getAlbumTitle().substring(2)));}
            else lv_fx_Album.getItems().add(new uniqueString("Unknown"));
        }
    }

    private void sync() {
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

    @FXML
    protected void clickPrevious() {
        if (pathList.size() > 0) {
            if (--currentIndex < 0) currentIndex = pathList.size() - 1;
            playNewSong();
            lv_fx_Title.getSelectionModel().select(currentIndex);
            lv_fx_Artist.getSelectionModel().select(currentIndex);
            lv_fx_Album.getSelectionModel().select(currentIndex);
        }
        vBox_fx_Main.requestFocus();
    }

    @FXML
    protected void clickPlay() {
        if (pathList.size() > 0)
            switch (mediaPlayer.getStatus()) {
                case PLAYING:
                    iv_fx_Play.setImage(new Image(new File("src/main/resources/media/play.png").toURI().toString()));
                    mediaPlayer.pause();
                    break;
                case PAUSED:
                    iv_fx_Play.setImage(new Image(new File("src/main/resources/media/pause.png").toURI().toString()));
                    Duration test = mediaPlayer.getCurrentTime();
                    mediaPlayer.play();
                    mediaPlayer.seek(test);
                    break;
                case STOPPED:
                case READY:
                    iv_fx_Play.setImage(new Image(new File("src/main/resources/media/pause.png").toURI().toString()));
                    mediaPlayer.play();
                default:
                    break;
            }
        vBox_fx_Main.requestFocus();
    }

    @FXML
    protected void clickNext() {
        if (pathList.size() > 0) {
            currentIndex = (++currentIndex) % pathList.size();
            playNewSong();
            lv_fx_Title.getSelectionModel().select(currentIndex);
            lv_fx_Artist.getSelectionModel().select(currentIndex);
            lv_fx_Album.getSelectionModel().select(currentIndex);
        }
        vBox_fx_Main.requestFocus();
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
            double temp = mediaPlayer.getVolume();
            mediaPlayer = new MediaPlayer(new Media(new File(pathList.get(0)).toURI().toString()));
            mediaPlayer.setVolume(temp);
            lv_fx_Title.getSelectionModel().select(currentIndex);
            lv_fx_Artist.getSelectionModel().select(currentIndex);
            lv_fx_Album.getSelectionModel().select(currentIndex);
            }
        }
        vBox_fx_Main.requestFocus();
    }

    @FXML
    protected void clickShuffle() {
        if (pathList.size() > 0) {
            lv_fx_Title.getItems().clear();
            lv_fx_Artist.getItems().clear();
            lv_fx_Album.getItems().clear();
            Collections.shuffle(pathList);
            for (String e : pathList) {
                MP3File mp3file = null;
                try {
                    mp3file = new MP3File(new File(e));
                } catch (IOException | TagException ex) {
                    ex.printStackTrace();
                }
                setMetadataToLists(Objects.requireNonNull(mp3file), e);
            }
        }
        currentIndex = 0;
        lv_fx_Title.getSelectionModel().select(currentIndex);
        lv_fx_Artist.getSelectionModel().select(currentIndex);
        lv_fx_Album.getSelectionModel().select(currentIndex);

        if (mediaPlayer.getStatus() == PLAYING)
            playNewSong();

        vBox_fx_Main.requestFocus();
    }

    @FXML
    protected void clickRepeat() {
        if (pathList.size() > 0) {
            switch (repeatOn) {
                case 0 -> {
                    mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(mediaPlayer.getStartTime()));
                    iv_fx_Repeat.setEffect(on);
                    repeatOn = 1;
                }
                case 1 -> {
                    mediaPlayer.setOnEndOfMedia(this::clickNext);
                    iv_fx_Repeat.setEffect(off);
                    repeatOn = 0;
                }
                default -> {
                    repeatOn = 0;
                    clickRepeat();
                }
            }
        }
        vBox_fx_Main.requestFocus();
    }

    @FXML
    protected void clickMute() {
        if (pathList.size() > 0) {
            if (mediaPlayer.isMute()) {
                mediaPlayer.setMute(false);
                iv_fx_Mute.setEffect(off);
            } else if (!(mediaPlayer.isMute())) {
                mediaPlayer.setMute(true);
                iv_fx_Mute.setEffect(on);
            }
        }
        vBox_fx_Main.requestFocus();
    }

    @FXML
    protected void clickvolumeUp() {
        if (mediaPlayer.getVolume() < 1)
            mediaPlayer.setVolume(mediaPlayer.getVolume() + 0.1);
        pb_fx_volumeBar.setValue(mediaPlayer.getVolume() * 100);
        vBox_fx_Main.requestFocus();
    }

    @FXML
    protected void clickvolumeDown() {
        if (mediaPlayer.getVolume() > 0)
            mediaPlayer.setVolume(mediaPlayer.getVolume() - 0.1);
        pb_fx_volumeBar.setValue(mediaPlayer.getVolume() * 100);
        vBox_fx_Main.requestFocus();
    }

    @FXML
    protected void keyboardHandler() {
        vBox_fx_Main.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case SPACE -> clickPlay();
                case RIGHT -> clickNext();
                case LEFT -> clickPrevious();
                case UP -> clickvolumeUp();
                case DOWN -> clickvolumeDown();
                case M -> clickMute();
                case R -> clickRepeat();
                case S -> clickShuffle();
                default -> {
                }
            }
        });
    }
}