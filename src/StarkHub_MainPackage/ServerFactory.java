package StarkHub_MainPackage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFactory implements Runnable{
    final static int RTSPPort = 1051;
    @Override
    public void run() {

        try {
            ServerSocket ss=new ServerSocket(RTSPPort);
            Socket sock;
            while(true){
                sock=ss.accept();
                new Thread(new Server(sock)).start();
//                ss.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
