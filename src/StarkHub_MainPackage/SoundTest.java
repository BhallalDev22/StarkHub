package StarkHub_MainPackage;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import javafx.util.Duration;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
//
///**
// * @author Dmitriy Gerashenko <d.a.gerashenko@gmail.com>
// */
//public class SoundTest extends Application {
//
//    private static final Logger LOG = Logger.getLogger(SoundTest.class.getName());
//
//    private static Timeline playTimer;
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//
//    @Override
//    public void start(final Stage primaryStage) throws Exception {
//        final StackPane root = new StackPane();
//        final ImageView imageView = new ImageView();
//
//        root.getChildren().add(imageView);
//        imageView.fitWidthProperty().bind(primaryStage.widthProperty());
//        imageView.fitHeightProperty().bind(primaryStage.heightProperty());
//
//        final Scene scene = new Scene(root, 640, 480);
//
//        primaryStage.setTitle("Video + audio");
//        primaryStage.setScene(scene);
//        primaryStage.show();
//
//        try {
//            final String videoFilename = "src/Graphics/TEST2.mkv";
//            final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilename);
//            grabber.start();
//            int framePeriod=(int)(1000/(grabber.getFrameRate()));
//            final AudioFormat audioFormat = new AudioFormat(grabber.getSampleRate(), 16, grabber.getAudioChannels(), true, true);
//            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
//            final SourceDataLine soundLine = (SourceDataLine) AudioSystem.getLine(info);
//            soundLine.open(audioFormat);
//            soundLine.start();
//
//            final Java2DFrameConverter converter = new Java2DFrameConverter();
//            playTimer = new Timeline(new KeyFrame(Duration.millis(1), actionEvent ->
//
//            {
//                try{
//                primaryStage.setWidth(grabber.getImageWidth());
//                primaryStage.setHeight(grabber.getImageHeight());
//
//
//
//
//
//
//
//                    Frame frame = grabber.grab();
//
//                    if (frame.image != null) {
//                        final Image image = SwingFXUtils.toFXImage(converter.convert(frame), null);
//
//                            imageView.setImage(image);
//
//                    } else if (frame.samples != null) {
//                        final ShortBuffer channelSamplesShortBuffer = (ShortBuffer) frame.samples[0];
//                        channelSamplesShortBuffer.rewind();
//
//                        final ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesShortBuffer.capacity() * 2);
//
//                        for (int i = 0; i < channelSamplesShortBuffer.capacity(); i++) {
//                            short val = channelSamplesShortBuffer.get(i);
//                            outBuffer.putShort(val);
//                        }
//
//                        /**
//                         * We need this because soundLine.write ignores
//                         * interruptions during writing.
//                         */
//
//
//                                soundLine.write(outBuffer.array(), 0, outBuffer.capacity());
//                                outBuffer.clear();
//
//
//
//                }
//
//
//
//            }
//             catch (Exception exception) {
//                LOG.log(Level.SEVERE, null, exception);
//                System.exit(1);
//            }
//            }));
//
//            playTimer.setCycleCount(Timeline.INDEFINITE);
//
//            playTimer.play();
//        } catch (Exception exception) {
//            LOG.log(Level.SEVERE, null, exception);
//            System.exit(1);
//        }
//
//    }
//
//    @Override
//    public void stop() throws Exception {
//
//        playTimer.stop();
//    }
//
//}
//
public class SoundTest extends Application {

    private static final Logger LOG = Logger.getLogger(SoundTest.class.getName());

    private static volatile Thread playThread;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        final StackPane root = new StackPane();
        final ImageView imageView = new ImageView();

        root.getChildren().add(imageView);
        imageView.fitWidthProperty().bind(primaryStage.widthProperty());
        imageView.fitHeightProperty().bind(primaryStage.heightProperty());

        final Scene scene = new Scene(root, 640, 480);

        primaryStage.setTitle("Video + audio");
        primaryStage.setScene(scene);
        primaryStage.show();

        playThread = new Thread(new Runnable() { public void run() {
            try {
                final String videoFilename = "src/Graphics/TEST2.mkv";
                final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilename);
                final FFmpegFrameGrabber soundGrabber = new FFmpegFrameGrabber(videoFilename);
                grabber.start();
                soundGrabber.start();
                primaryStage.setWidth(grabber.getImageWidth());
                primaryStage.setHeight(grabber.getImageHeight());
                final AudioFormat audioFormat = new AudioFormat(soundGrabber.getSampleRate(), 16, soundGrabber.getAudioChannels(), true, true);

                final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                final SourceDataLine soundLine = (SourceDataLine) AudioSystem.getLine(info);
                soundLine.open(audioFormat);
                soundLine.start();

                final Java2DFrameConverter converter = new Java2DFrameConverter();

                ExecutorService executor = Executors.newSingleThreadExecutor();

                while (!Thread.interrupted()) {
//                    Frame imageFrame=grabber.grabImage();
//                    if (imageFrame == null) {
//                        break;
//                    }
                    Frame frame = soundGrabber.grab();

                    if (frame == null) {
                        break;
                    }
//                    final Image image = SwingFXUtils.toFXImage(converter.convert(imageFrame), null);
//                        Platform.runLater(new Runnable() { public void run() {
//                            imageView.setImage(image);
//                        }});
//
                    if (frame.image != null) {
                        final Image image = SwingFXUtils.toFXImage(converter.convert(frame), null);
                        Platform.runLater(new Runnable() {
                            public void run() {
                                imageView.setImage(image);
                            }
                        });
                    }else if (frame.samples != null) {
                        final ShortBuffer channelSamplesShortBuffer = (ShortBuffer) frame.samples[0];
                        channelSamplesShortBuffer.rewind();

                        final ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesShortBuffer.capacity() * 2);

                        for (int i = 0; i < channelSamplesShortBuffer.capacity(); i++) {
                            short val = channelSamplesShortBuffer.get(i);
                            outBuffer.putShort(val);
                        }
                        System.out.println(outBuffer.array().length);

                        /**
                         * We need this because soundLine.write ignores
                         * interruptions during writing.
                         */
                        try {
                            executor.submit(new Runnable() { public void run() {
                                soundLine.write(outBuffer.array(), 0, outBuffer.capacity());
                                outBuffer.clear();
                            }}).get();
                        } catch (InterruptedException interruptedException) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
                executor.shutdownNow();
                executor.awaitTermination(10, TimeUnit.SECONDS);
                soundLine.stop();
                grabber.stop();
                grabber.release();
                soundGrabber.stop();
                soundGrabber.release();
                Platform.exit();
            } catch (Exception exception) {
                LOG.log(Level.SEVERE, null, exception);
                System.exit(1);
            }
        }});
        playThread.start();
    }

    @Override
    public void stop() throws Exception {
        playThread.interrupt();
    }

}