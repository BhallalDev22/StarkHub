package StarkHub_MainPackage;

import java.util.ArrayList;

public class VaryingData {
    public ArrayList<String> VideoName = new ArrayList<String>();
    public ArrayList<String> UserName = new ArrayList<String>();
    public ArrayList<String> ChannelName = new ArrayList<String>();
    public ArrayList<Integer> VideoViews = new ArrayList<Integer>();
    public ArrayList<String> VideoPath = new ArrayList<String>();

    public static VaryingData vd = null;

    public static VaryingData getInstance() {
        if (vd == null) {
            vd = new VaryingData();
        }
        return vd;
    }

    public ArrayList<String> getVideoName() {
        return VideoName;
    }

    public void setVideoName(ArrayList<String> videoName) {
        VideoName = videoName;
    }

    public ArrayList<String> getUserName() {
        return UserName;
    }

    public void setUserName(ArrayList<String> userName) {
        UserName = userName;
    }

    public ArrayList<String> getChannelName() {
        return ChannelName;
    }

    public void setChannelName(ArrayList<String> channelName) {
        ChannelName = channelName;
    }

    public ArrayList<Integer> getVideoViews() {
        return VideoViews;
    }

    public void setVideoViews(ArrayList<Integer> videoViews) {
        VideoViews = videoViews;
    }

    public ArrayList<String> getVideoPath() {
        return VideoPath;
    }

    public void setVideoPath(ArrayList<String> videoPath) {
        VideoPath = videoPath;
    }

    public void Reset(){
        VideoName.clear();
        UserName.clear();
        ChannelName.clear();
        VideoViews.clear();
        VideoPath.clear();
    }
}
