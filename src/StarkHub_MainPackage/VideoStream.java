package StarkHub_MainPackage;//VideoStream



import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.*;

import java.io.*;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Iterator;


public class VideoStream {

    FFmpegFrameGrabber grabber;
    int frame_nb; //current frame nb
    int totalFrames; //Estimate total frames in video
    int framePeriod;//Time taken for 1 frame
    int sampleRate,channels;
    Frame im;
    Java2DFrameConverter frameConverter;

    //-----------------------------------
    //constructor
    //-----------------------------------
    public VideoStream(String filename) throws Exception{

        //init variables

        frameConverter=new Java2DFrameConverter();
        grabber=new FFmpegFrameGrabber(filename);
//        soundGrabber=new FFmpegFrameGrabber(filename);
        grabber.startUnsafe();
        totalFrames=grabber.getLengthInVideoFrames();
        framePeriod=(int)(1000/(grabber.getFrameRate()));
        sampleRate=grabber.getSampleRate();
        channels=grabber.getAudioChannels();
        frame_nb = 0;
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public int getFramePeriod() {
        return framePeriod;
    }
    public int getSampleRate(){
        return sampleRate;
    }
    public int getChannels(){
        return channels;
    }

    public void setFramePos(int p) {
        try {
            grabber.setVideoFrameNumber(p);
        }catch(FrameGrabber.Exception ex){ex.printStackTrace();}
    }

    //-----------------------------------
    // getnextframe
    //returns the next frame as an array of byte and the size of the frame
    //-----------------------------------
    public boolean getnextframe(FrameType fbuf) throws Exception
    {


        im=grabber.grab();
        if(im==null)
            return false;
        if(im.image != null) {
            BufferedImage img = frameConverter.convert(im);
            int len = getSize(img);
            System.out.println(len);
            float ratio = 63800.0f / len;
            if (ratio >= 1)
                ratio = 0.7f;
            else
                ratio = ratio * 0.35f;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
            ImageWriter writer = iter.next();
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwp.setCompressionQuality(ratio);
            writer.setOutput(new MemoryCacheImageOutputStream(baos));
            writer.write(null, new IIOImage(img, null, null), iwp);
            writer.dispose();
            baos.flush();
            byte[] bytes = baos.toByteArray();
            baos.close();
            System.out.println(bytes.length);
            fbuf.setVideoBuffer(bytes);
        }
        else if(im.samples !=null){
            final ShortBuffer channelSamplesShortBuffer = (ShortBuffer) im.samples[0];
            channelSamplesShortBuffer.rewind();

            final ByteBuffer outBuffer = ByteBuffer.allocate(channelSamplesShortBuffer.capacity() * 2);

            for (int i = 0; i < channelSamplesShortBuffer.capacity(); i++) {
                short val = channelSamplesShortBuffer.get(i);
                outBuffer.putShort(val);
            }
            fbuf.setSoundBuffer(outBuffer.array());
        }
        return true;
    }

    private int getSize(BufferedImage img){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "jpg", baos);
            baos.flush();
            int ans=baos.size();
            baos.close();
            return ans;
        }catch(Exception ex){ex.printStackTrace();return -1;}
    }

}