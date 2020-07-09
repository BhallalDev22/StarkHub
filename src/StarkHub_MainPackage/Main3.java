package StarkHub_MainPackage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

import static StarkHub_MainPackage.Main.IP_ADDRESS;
import static StarkHub_MainPackage.Main.PORT;

public class Main3 {
    public static void main(String[] args) {
        try(Socket socket=new Socket(IP_ADDRESS,PORT)){
            Socket sock2=socket;
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(sock2.getInputStream()));
            PrintWriter output = new PrintWriter(sock2.getOutputStream(), true);

            PojoFromClient pfc=new PojoFromClient();
            Gson gson = new GsonBuilder().serializeNulls().create();
            String trendingVids=input.readLine();

            System.out.println("Trending videos: "+trendingVids);
            URL url=new URL("http://bot.whatismyipaddress.com");
            BufferedReader sc=new BufferedReader(new InputStreamReader(url.openStream()));
            String ip=sc.readLine().trim();
            System.out.println(ip);
            pfc.setHeader(2);
            pfc.setIPAddress(ip);
            pfc.setUserName("Arc");
            pfc.setPassword("arc293");
            output.println(gson.toJson(pfc));
            System.out.println(input.readLine());


        }catch(Exception e){e.printStackTrace();}
    }
}
