package StarkHub_MainPackage;//VideoStream



import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.io.*;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.util.Iterator;


public class VideoStream {

    FFmpegFrameGrabber grabber;
    int frame_nb; //current frame nb
    Frame im;
    Java2DFrameConverter frameConverter;

    //-----------------------------------
    //constructor
    //-----------------------------------
    public VideoStream(String filename) throws Exception{

        //init variables
        frameConverter=new Java2DFrameConverter();
        grabber=new FFmpegFrameGrabber(filename);
        grabber.start();
        frame_nb = 0;
    }

    //-----------------------------------
    // getnextframe
    //returns the next frame as an array of byte and the size of the frame
    //-----------------------------------
    public int getnextframe(byte[] frame) throws Exception
    {
        //int length = 0;
        //String length_string;
        //byte[] frame_length = new byte[5];

        //read current frame length
        //fis.read(frame_length,0,5);

        //transform frame_length to integer
        //length_string = new String(frame_length);
        //length = Integer.parseInt(length_string);
//        for(int i=0;i<50;i++)
//        {
//            im=grabber.grabImage();
//            System.out.println(im);
//            System.out.println(Java2DFrameConverter.getBufferedImageType(im));
//        }
        im=grabber.grabImage();
        BufferedImage img=frameConverter.getBufferedImage(im);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Iterator<ImageWriter> iter=ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer=iter.next();
        ImageWriteParam iwp=writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(0.01f);
        writer.setOutput(new MemoryCacheImageOutputStream(baos));
        writer.write(null,new IIOImage(img,null,null),iwp);
        writer.dispose();
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();
        System.arraycopy(bytes,0,frame,0,bytes.length>20000?20000:bytes.length);
        return(bytes.length>20000?20000:bytes.length);
    }

}