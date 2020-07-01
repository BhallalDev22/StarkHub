package StarkHub_MainPackage;/* ------------------
   ClientVideo
   usage: java ClientVideo [Server hostname] [Server RTSP listening port] [Video file requested]
   ---------------------- */

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.event.*;
import javafx.util.Duration;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;


public class ClientVideo implements Initializable {
    //GUI
    //----
    @FXML
    ImageView video;
    @FXML
    Slider seekbar;
    @FXML
    JFXButton playBtn;
    @FXML
    JFXButton pauseBtn;
    @FXML
    JFXButton stopBtn;
    @FXML
    ProgressBar bufferProgress;

    //RTP variables:
    //----------------
    DatagramPacket rcvdp;            //UDP packet received from the server
    DatagramSocket RTPsocket;        //socket to be used to send and receive UDP packets
    static int RTP_RCV_PORT = 25000; //port where the client will receive the RTP packets

    Timeline timer; //timer used to receive data from the UDP socket
    byte[] buf;  //buffer used to store data received from the server

    //RTSP variables
    //----------------
    //rtsp states
    final static int INIT = 0;
    final static int READY = 1;
    final static int PLAYING = 2;
    static int state;            //RTSP state == INIT or READY or PLAYING
    Socket RTSPsocket;           //socket used to send/receive RTSP messages
    InetAddress ServerIPAddr;

    //input and output stream filters
    static BufferedReader RTSPBufferedReader;
    static BufferedWriter RTSPBufferedWriter;
    static String VideoFileName; //video file to request to the server
    int RTSPSeqNb = 0;           //Sequence number of RTSP messages within the session
    String RTSPid;              // ID of the RTSP session (given by the RTSP Server)

    final static String CRLF = "\r\n";
    final static String DES_FNAME = "session_info.txt";

    //RTCP variables
    //----------------
    DatagramSocket RTCPsocket;          //UDP socket for sending RTCP packets
    static int RTCP_RCV_PORT = 19001;   //port where the client will receive the RTP packets
    static int RTCP_PERIOD = 400;       //How often to send RTCP packets
    RtcpSender rtcpSender;


    //Statistics variables:
    //------------------
    double statDataRate;        //Rate of video data received in bytes/s
    int statTotalBytes;         //Total number of bytes received in a session
    double statStartTime;       //Time in milliseconds when start is pressed
    double statTotalPlayTime;   //Time in milliseconds of video playing since beginning
    float statFractionLost;     //Fraction of RTP data packets from sender lost since the prev packet was sent
    int statCumLost;            //Number of packets lost
    int statExpRtpNb;           //Expected Sequence number of RTP messages within the session
    int statHighSeqNb;          //Highest sequence number received in session

    FrameSynchronizer fsynch;
    ArrayList<Image> videoBuffer;
    static int bufferSize=200;
    int bufStart,bufEnd;
    boolean playingFromBuffer;
    int VIDEO_LENGTH;
    private int FRAME_PERIOD;
    Timeline tempTimer;
    private int SAMPLE_RATE;
    private SourceDataLine soundLine;
    private int AUDIO_CHANNELS;

    @Override
    public void initialize(URL url, ResourceBundle rb){
        try {



//            playBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("src/Graphics/play.png"))));
//            pauseBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("src/Graphics/pause.png"))));
//            stopBtn.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("src/Graphics/stop.png"))));
            playBtn.setOnAction(new playButtonListener());
            pauseBtn.setOnAction(new pauseButtonListener());
            stopBtn.setOnAction(new stopButtonListener());
            timer=new Timeline(
                    new KeyFrame(
                            Duration.millis(20),
                            new timerListener()
                    )

            );
            timer.setCycleCount(Timeline.INDEFINITE);

            seekbar.setDisable(true);
            seekbar.valueProperty().addListener(new sliderListener());
                            seekbar.setMin(0);
//                seekbar.setMax(VIDEO_LENGTH);
                seekbar.setValue(0);
                seekbar.setBlockIncrement(1);
//                seekbar.setDisable(false);

//            video.setImage(new Image(getClass().getResourceAsStream("src/Graphics/buffering.jpg")));
            //allocate enough memory for the buffer used to receive data from the server
            buf = new byte[63800];

            //init RTCP packet sender
            rtcpSender = new RtcpSender(400);

            //create the frame synchronizer
            fsynch = new FrameSynchronizer(100);

            bufStart=0;bufEnd=-1;
            videoBuffer=new ArrayList<>();
            playingFromBuffer=false;

            //Create a ClientVideo object
            ClientVideo theClient = new ClientVideo();

            //get server RTSP port and IP address from the command line
            //------------------
            int RTSP_server_port = 1051;
            String ServerHost = "localhost";
            theClient.ServerIPAddr = InetAddress.getByName(ServerHost);

            //get video filename to request:
            VideoFileName = "src/Graphics/TEST2.mkv";

            //Establish a TCP connection with the server to exchange RTSP messages
            //------------------
            theClient.RTSPsocket = new Socket(theClient.ServerIPAddr, RTSP_server_port);

            //Establish a UDP connection with the server to exchange RTCP control packets
            //------------------

            //Set input and output stream filters:
            RTSPBufferedReader = new BufferedReader(new InputStreamReader(theClient.RTSPsocket.getInputStream()));
            RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(theClient.RTSPsocket.getOutputStream()));


            //Init non-blocking RTPsocket that will be used to receive data
            try {
                //construct a new DatagramSocket to receive RTP packets from the server, on port RTP_RCV_PORT
                RTPsocket = new DatagramSocket(RTP_RCV_PORT);
                //UDP socket for sending QoS RTCP packets
                RTCPsocket = new DatagramSocket();
                //set TimeOut value of the socket to 5msec.
                RTPsocket.setSoTimeout(5);
            }
            catch (SocketException se)
            {
                System.out.println("Socket exception: "+se);
                System.exit(0);
            }

            //init RTSP sequence number
            RTSPSeqNb = 1;

            //Send SETUP message to the server
            sendRequest("SETUP");

            //Wait for the response
            if (parseServerResponse() != 200)
                System.out.println("Invalid Server Response");
            else
            {
                //change RTSP state and print new state
                state = READY;
                System.out.println("New RTSP state: READY");
                final AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 16, AUDIO_CHANNELS, true, true);

                final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                soundLine=(SourceDataLine) AudioSystem.getLine(info);
                soundLine.open(audioFormat);
                soundLine.start();
//                seekbar.setMin(0);
                seekbar.setMax(VIDEO_LENGTH);
//                seekbar.setValue(0);
//                seekbar.setBlockIncrement(1);
                seekbar.setDisable(false);
            }
        }catch(Exception ex){ex.printStackTrace();}
    }


    //------------------------------------
    //Handler for buttons
    //------------------------------------



    //Handler for Play button
    //-----------------------
    class playButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {

            System.out.println("Play Button pressed!");

            //Start to save the time in stats
            statStartTime = System.currentTimeMillis();

            if (state == READY) {
                if(playingFromBuffer) {
                    tempTimer.play();
                    state = PLAYING;
                }
                else {
                    System.out.println("Playing from stream");
                    //increase RTSP sequence number
                    RTSPSeqNb++;

                    //Send PLAY message to the server
                    sendRequest("PLAY", bufEnd);

                    //Wait for the response
                    if (parseServerResponse() != 200) {
                        System.out.println("Invalid Server Response");
                    } else {
                        //change RTSP state and print out new state
                        state = PLAYING;
                        System.out.println("New RTSP state: PLAYING");

                        //start the timer
                        timer.play();
                        rtcpSender.startSend();
                    }
                }
            }
            //else if state != READY then do nothing
        }
    }

    //Handler for Pause button
    //-----------------------
    class pauseButtonListener implements EventHandler<ActionEvent>  {

        @Override
        public void handle(ActionEvent e){

            System.out.println("Pause Button pressed!");

            if (state == PLAYING)
            {
                if(playingFromBuffer){
                    tempTimer.stop();
                    state=READY;
                }
                else {
                    //increase RTSP sequence number
                    RTSPSeqNb++;

                    //Send PAUSE message to the server
                    sendRequest("PAUSE");

                    //Wait for the response
                    if (parseServerResponse() != 200)
                        System.out.println("Invalid Server Response");
                    else {
                        //change RTSP state and print out new state
                        state = READY;
                        System.out.println("New RTSP state: READY");

                        //stop the timer
                        timer.stop();
                        rtcpSender.stopSend();
                    }
                }
            }
            //else if state != PLAYING then do nothing
        }
    }

    //Handler for Teardown button
    //-----------------------
    class stopButtonListener implements EventHandler<ActionEvent>  {

        @Override
        public void handle(ActionEvent e){

            System.out.println("Stop Button pressed !");

            //increase RTSP sequence number
            RTSPSeqNb++;

            //Send TEARDOWN message to the server
            sendRequest("TEARDOWN");

            //Wait for the response
            if (parseServerResponse() != 200)
                System.out.println("Invalid Server Response");
            else {
                //change RTSP state and print out new state
                state = INIT;
                System.out.println("New RTSP state: INIT");

                if(!playingFromBuffer) {
                    //stop the timer
                    timer.stop();
                    rtcpSender.stopSend();
                }

                //exit
                System.exit(0);
            }
        }
    }

    //Handler for slider

    class sliderListener implements ChangeListener<Number>{


        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
            if (state == PLAYING)
            {
                //increase RTSP sequence number
                RTSPSeqNb++;

                //Send PAUSE message to the server
                sendRequest("PAUSE");

                //Wait for the response
                if (parseServerResponse() != 200)
                    System.out.println("Invalid Server Response");
                else
                {
                    //change RTSP state and print out new state
                    state = READY;
                    System.out.println("New RTSP state: READY");

                    //stop the timer
                    timer.stop();
                    rtcpSender.stopSend();
                }
            }
            int newval=t1.intValue();
            if(newval>=bufStart&&newval<bufEnd){
                playingFromBuffer=true;
                tempTimer=new Timeline();
                for(int i=(int)seekbar.getValue();i<bufEnd;i++){
                    final int val=i;
                    tempTimer.getKeyFrames().add(new KeyFrame(
                            Duration.millis(FRAME_PERIOD),
                            actionEvent -> {video.setImage(videoBuffer.get(val));}
                    ));
                }
                tempTimer.play();
                state=PLAYING;
                tempTimer.setOnFinished(
                        actionEvent -> {
                            playingFromBuffer=false;

                                //increase RTSP sequence number
                                RTSPSeqNb++;

                                //Send PLAY message to the server
                                sendRequest("PLAY",bufEnd);

                                //Wait for the response
                                if (parseServerResponse() != 200) {
                                    System.out.println("Invalid Server Response");
                                }
                                else {
                                    //change RTSP state and print out new state
                                    state = PLAYING;
                                    System.out.println("New RTSP state: PLAYING");

                                    //start the timer
                                    timer.play();
                                    rtcpSender.startSend();
                                }

                        }
                );
            }
            else{
                if (state == READY) {
                    //increase RTSP sequence number
                    RTSPSeqNb++;

                    //Send PLAY message to the server
                    sendRequest("PLAY",newval);

                    //Wait for the response
                    if (parseServerResponse() != 200) {
                        System.out.println("Invalid Server Response");
                    }
                    else {
                        //change RTSP state and print out new state
                        state = PLAYING;
                        System.out.println("New RTSP state: PLAYING");

                        //start the timer
                        timer.play();
                        rtcpSender.startSend();
                    }
                }
            }
        }
    }



    //------------------------------------
    //Handler for timer
    //------------------------------------
    class timerListener implements EventHandler<ActionEvent>  {

        @Override
        public void handle(ActionEvent e){

            //Construct a DatagramPacket to receive data from the UDP socket
            rcvdp = new DatagramPacket(buf, buf.length);

            try {
                //receive the DP from the socket, save time for stats
                RTPsocket.receive(rcvdp);

                double curTime = System.currentTimeMillis();
                statTotalPlayTime += curTime - statStartTime;
                statStartTime = curTime;

                //create an RTPpacket object from the DP
                RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());
                int seqNb = rtp_packet.getsequencenumber();

                //this is the highest seq num received

                //print important header fields of the RTP packet received:
                System.out.println("Got RTP packet with SeqNum # " + seqNb
                        + " TimeStamp " + rtp_packet.gettimestamp() + " ms, of type "
                        + rtp_packet.getpayloadtype());

                //print header bitstream:
                rtp_packet.printheader();

                //get the payload bitstream from the RTPpacket object
                int payload_length = rtp_packet.getpayload_length();
                System.out.println(payload_length);
                byte [] payload = new byte[payload_length];
                rtp_packet.getpayload(payload);

                //compute stats and update the label in GUI
                statExpRtpNb++;
                if (seqNb > statHighSeqNb) {
                    statHighSeqNb = seqNb;
                }
                if (statExpRtpNb != seqNb) {
                    statCumLost++;
                }
                statDataRate = statTotalPlayTime == 0 ? 0 : (statTotalBytes / (statTotalPlayTime / 1000.0));
                statFractionLost = (float)statCumLost / statHighSeqNb;
                statTotalBytes += payload_length;

                if(rtp_packet.getpayloadtype()==Server.MJPEG_TYPE) {
                    //get an Image object from the payload bitstream
                    Image image = new Image(new ByteArrayInputStream(payload));
//                    fsynch.addFrame(image, seqNb);
//                    videoBuffer.add(image);
//                    bufEnd++;
//                    if (bufEnd - bufStart + 1 > bufferSize) {
//                        videoBuffer.remove(0);
//                        bufStart++;
//                    }

                    //display the image as an Image object

                    video.setImage(image);
                }
                else{
                    soundLine.write(payload,0,payload_length);
                }
//                seekbar.setValue(bufEnd);
//                System.out.println(seekbar.getValue()+"/"+seekbar.getMax()+" "+VIDEO_LENGTH);
            }
            catch (InterruptedIOException iioe) {
                System.out.println("Nothing to read");
            }
            catch (IOException ioe) {
                System.out.println("Exception caught: "+ioe);
            }
        }
    }

    //------------------------------------
    // Send RTCP control packets for QoS feedback
    //------------------------------------
    class RtcpSender extends TimerTask {

        private Timer rtcpTimer;
        int interval;

        // Stats variables
        private int numPktsExpected;    // Number of RTP packets expected since the last RTCP packet
        private int numPktsLost;        // Number of RTP packets lost since the last RTCP packet
        private int lastHighSeqNb;      // The last highest Seq number received
        private int lastCumLost;        // The last cumulative packets lost
        private float lastFractionLost; // The last fraction lost

        Random randomGenerator;         // For testing only

        public RtcpSender(int interval) {
            this.interval = interval;
            rtcpTimer = new Timer(true);

            randomGenerator = new Random();
        }



        @Override
        public void run() {

            // Calculate the stats for this period
            numPktsExpected = statHighSeqNb - lastHighSeqNb;
            numPktsLost = statCumLost - lastCumLost;
            lastFractionLost = numPktsExpected == 0 ? 0f : (float)numPktsLost / numPktsExpected;
            lastHighSeqNb = statHighSeqNb;
            lastCumLost = statCumLost;

            //To test lost feedback on lost packets
            // lastFractionLost = randomGenerator.nextInt(10)/10.0f;

            RTCPpacket rtcp_packet = new RTCPpacket(lastFractionLost, statCumLost, statHighSeqNb);
            int packet_length = rtcp_packet.getlength();
            byte[] packet_bits = new byte[packet_length];
            rtcp_packet.getpacket(packet_bits);

            try {
                DatagramPacket dp = new DatagramPacket(packet_bits, packet_length, ServerIPAddr, RTCP_RCV_PORT);
                RTCPsocket.send(dp);
            } catch (InterruptedIOException iioe) {
                System.out.println("Nothing to read");
            } catch (IOException ioe) {
                System.out.println("Exception caught: "+ioe);
            }
        }

        // Start sending RTCP packets
        public void startSend() {
            rtcpTimer.schedule(this,0,interval);
        }

        // Stop sending RTCP packets
        public void stopSend() {
            rtcpTimer.cancel();
        }
    }

    //------------------------------------
    //Synchronize frames
    //------------------------------------
    class FrameSynchronizer {

        private ArrayDeque<Image> queue;
        private int bufSize;
        private int curSeqNb;
        private Image lastImage;

        public FrameSynchronizer(int bsize) {
            curSeqNb = 1;
            bufSize = bsize;
            queue = new ArrayDeque<Image>(bufSize);
        }

        //synchronize frames based on their sequence number
        public void addFrame(Image image, int seqNum) {
            if (seqNum < curSeqNb) {
                queue.add(lastImage);
            }
            else if (seqNum > curSeqNb) {
                for (int i = curSeqNb; i < seqNum; i++) {
                    queue.add(lastImage);
                }
                queue.add(image);
            }
            else {
                queue.add(image);
            }

        }

        //get the next synchronized frame
        public Image nextFrame() {
            curSeqNb++;
            lastImage = queue.peekLast();
            return queue.remove();
        }
    }

    //------------------------------------
    //Parse Server Response
    //------------------------------------
    private int parseServerResponse() {
        int reply_code = 0;

        try {
            //parse status line and extract the reply_code:
            String StatusLine = RTSPBufferedReader.readLine();
            System.out.println("RTSP ClientVideo - Received from Server:");
            System.out.println(StatusLine);

            StringTokenizer tokens = new StringTokenizer(StatusLine);
            tokens.nextToken(); //skip over the RTSP version
            reply_code = Integer.parseInt(tokens.nextToken());

            //if reply code is OK get and print the 2 other lines
            if (reply_code == 200) {
                String SeqNumLine = RTSPBufferedReader.readLine();
                System.out.println(SeqNumLine);

                String SessionLine = RTSPBufferedReader.readLine();
                System.out.println(SessionLine);

                tokens = new StringTokenizer(SessionLine);
                String temp = tokens.nextToken();
                //if state == INIT gets the Session Id from the SessionLine
                if (state == INIT && temp.compareTo("Session:") == 0) {
                    RTSPid = tokens.nextToken();
                }
                else if (temp.compareTo("Content-Base:") == 0) {
                    // Get the DESCRIBE lines
                    String newLine;
                    for (int i = 0; i < 6; i++) {
                        newLine = RTSPBufferedReader.readLine();
                        System.out.println(newLine);
                    }
                }
                String VideoSizeLine=RTSPBufferedReader.readLine();
                System.out.println(VideoSizeLine);
                tokens = new StringTokenizer(VideoSizeLine);
                temp = tokens.nextToken();

                    VIDEO_LENGTH = Integer.parseInt(tokens.nextToken());

                String FramePeriodLine=RTSPBufferedReader.readLine();
                System.out.println(FramePeriodLine);
                tokens = new StringTokenizer(FramePeriodLine);
                temp = tokens.nextToken();

                    FRAME_PERIOD = Integer.parseInt(tokens.nextToken());
                 String SampleRateLine=RTSPBufferedReader.readLine();
                System.out.println(SampleRateLine);
                tokens = new StringTokenizer(SampleRateLine);
                tokens.nextToken();
                SAMPLE_RATE=Integer.parseInt(tokens.nextToken());
                String AudioChannelLine=RTSPBufferedReader.readLine();
                System.out.println(AudioChannelLine);
                tokens = new StringTokenizer(AudioChannelLine);
                tokens.nextToken();
                AUDIO_CHANNELS=Integer.parseInt(tokens.nextToken());

            }
        } catch(Exception ex) {
            System.out.println("Exception caught: "+ex);
            System.exit(0);
        }

        return(reply_code);
    }



    //------------------------------------
    //Send RTSP Request
    //------------------------------------

    private void sendRequest(String request_type) {
        try {
            //Use the RTSPBufferedWriter to write to the RTSP socket

            //write the request line:
            RTSPBufferedWriter.write(request_type + " " + VideoFileName + " RTSP/1.0" + CRLF);

            //write the CSeq line:
            RTSPBufferedWriter.write("CSeq: " + RTSPSeqNb + CRLF);

            //check if request_type is equal to "SETUP" and in this case write the
            //Transport: line advertising to the server the port used to receive
            //the RTP packets RTP_RCV_PORT
            if (request_type == "SETUP") {
                RTSPBufferedWriter.write("Transport: RTP/UDP; client_port= " + RTP_RCV_PORT + CRLF);
            }
            else if (request_type == "DESCRIBE") {
                RTSPBufferedWriter.write("Accept: application/sdp" + CRLF);
            }
            else {
                //otherwise, write the Session line from the RTSPid field
                RTSPBufferedWriter.write("Session: " + RTSPid + CRLF);
            }

            RTSPBufferedWriter.flush();
        } catch(Exception ex) {
            System.out.println("Exception caught: "+ex);
            System.exit(0);
        }
    }
    private void sendRequest(String request_type,int frameNo) {
        try {
            //Use the RTSPBufferedWriter to write to the RTSP socket

            //write the request line:
            RTSPBufferedWriter.write(request_type + " " + VideoFileName + " RTSP/1.0" + CRLF);

            //write the CSeq line:
            RTSPBufferedWriter.write("CSeq: " + RTSPSeqNb + CRLF);

            //check if request_type is equal to "SETUP" and in this case write the
            //Transport: line advertising to the server the port used to receive
            //the RTP packets RTP_RCV_PORT
            //the RTP packets RTP_RCV_PORT
            if (request_type == "PLAY") {
                RTSPBufferedWriter.write("Session: " + RTSPid +" Frame: "+frameNo+ CRLF);

            }
            RTSPBufferedWriter.flush();
        } catch (Exception ex) {
            System.out.println("Exception caught: " + ex);
            System.exit(0);
        }
    }
}
