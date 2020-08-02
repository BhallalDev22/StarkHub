package StarkHub_MainPackage;

import java.util.ArrayList;

public class PojoToClient {
    public String UserName;
    public int UserID;
    public String EmailID;
    public String AccountCreationTime;
    public String IPAddress;
    public ArrayList<String> ChannelName = new ArrayList<String>();
    public ArrayList<String> ChannelCreationTime = new ArrayList<String>();
    public ArrayList<String> VideoName = new ArrayList<String>();
    public ArrayList<String> VideoTag = new ArrayList<String>();
    public ArrayList<Integer> VideoViews = new ArrayList<Integer>();
    public ArrayList<Integer> VideoLikes = new ArrayList<Integer>();
    public ArrayList<Integer> VideoDislikes = new ArrayList<Integer>();
    public ArrayList<String> VideoCreationTime = new ArrayList<String>();
    public ArrayList<String> VideoPath = new ArrayList<String>();
    public ArrayList<String> Comment = new ArrayList<String>();
    public ArrayList<String> CommentUserName = new ArrayList<String>();
    public ArrayList<String> CommentCreationTime = new ArrayList<String>();
    public ArrayList<String> SubscribeTime = new ArrayList<String>();
    public ArrayList<String> WatchLaterTime = new ArrayList<String>();
    public ArrayList<String> WatchTime = new ArrayList<String>();
    public ArrayList<Integer> NumberOfSubscribers = new ArrayList<Integer>();
    public ArrayList<Integer> NumberOfVideos = new ArrayList<Integer>();
    public ArrayList<Integer> NumberOfComments = new ArrayList<Integer>();
    public int IsSubscribed;
    public int IsWatchLater;
    public int LikedDislikedStatus;

    public int getLikedDislikedStatus() {
        return LikedDislikedStatus;
    }

    public void setLikedDislikedStatus(int likedDislikedStatus) {
        LikedDislikedStatus = likedDislikedStatus;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmailID() {
        return EmailID;
    }

    public void setEmailID(String emailID) {
        EmailID = emailID;
    }

    public String getAccountCreationTime() {
        return AccountCreationTime;
    }

    public void setAccountCreationTime(String accountCreationTime) {
        AccountCreationTime = accountCreationTime;
    }

    public ArrayList<String> getChannelName() {
        return ChannelName;
    }

    public void setChannelName(ArrayList<String> channelName) {
        ChannelName = channelName;
    }

    public ArrayList<String> getChannelCreationTime() {
        return ChannelCreationTime;
    }

    public void setChannelCreationTime(ArrayList<String> channelCreationTime) {
        ChannelCreationTime = channelCreationTime;
    }

    public ArrayList<String> getVideoName() {
        return VideoName;
    }

    public void setVideoName(ArrayList<String> videoName) {
        VideoName = videoName;
    }

    public ArrayList<String> getVideoTag() {
        return VideoTag;
    }

    public void setVideoTag(ArrayList<String> videoTag) {
        VideoTag = videoTag;
    }

    public ArrayList<Integer> getVideoViews() {
        return VideoViews;
    }

    public void setVideoViews(ArrayList<Integer> videoViews) {
        VideoViews = videoViews;
    }

    public ArrayList<Integer> getVideoLikes() {
        return VideoLikes;
    }

    public void setVideoLikes(ArrayList<Integer> videoLikes) {
        VideoLikes = videoLikes;
    }

    public ArrayList<Integer> getVideoDislikes() {
        return VideoDislikes;
    }

    public void setVideoDislikes(ArrayList<Integer> videoDislikes) {
        VideoDislikes = videoDislikes;
    }

    public ArrayList<String> getVideoCreationTime() {
        return VideoCreationTime;
    }

    public void setVideoCreationTime(ArrayList<String> videoCreationTime) {
        VideoCreationTime = videoCreationTime;
    }

    public ArrayList<String> getComment() {
        return Comment;
    }

    public void setComment(ArrayList<String> comment) {
        Comment = comment;
    }

    public ArrayList<String> getCommentCreationTime() {
        return CommentCreationTime;
    }

    public void setCommentCreationTime(ArrayList<String> commentCreationTime) {
        CommentCreationTime = commentCreationTime;
    }

    public void setSubscribeTime(ArrayList<String> subscribeTime) {
        SubscribeTime = subscribeTime;
    }

    public ArrayList<String> getWatchLaterTime() {
        return WatchLaterTime;
    }

    public void setWatchLaterTime(ArrayList<String> watchLaterTime) {
        WatchLaterTime = watchLaterTime;
    }

    public ArrayList<String> getWatchTime() {
        return WatchTime;
    }

    public void setWatchTime(ArrayList<String> watchTime) {
        WatchTime = watchTime;
    }

    public ArrayList<Integer> getNumberOfSubscribers() {
        return NumberOfSubscribers;
    }

    public void setNumberOfSubscribers(ArrayList<Integer> numberOfSubscribers) {
        NumberOfSubscribers = numberOfSubscribers;
    }

    public ArrayList<Integer> getNumberOfVideos() {
        return NumberOfVideos;
    }

    public void setNumberOfVideos(ArrayList<Integer> numberOfVideos) {
        NumberOfVideos = numberOfVideos;
    }

    public ArrayList<Integer> getNumberOfComments() {
        return NumberOfComments;
    }

    public void setNumberOfComments(ArrayList<Integer> numberOfComments) {
        NumberOfComments = numberOfComments;
    }

    public int getIsSubscribed() {
        return IsSubscribed;
    }

    public void setIsSubscribed(int isSubscribed) {
        IsSubscribed = isSubscribed;
    }

    public int getIsWatchLater() {
        return IsWatchLater;
    }

    public void setIsWatchLater(int isWatchLater) {
        IsWatchLater = isWatchLater;
    }

    public ArrayList<String> getVideoPath() {
        return VideoPath;
    }

    public void setVideoPath(ArrayList<String> videoPath) {
        VideoPath = videoPath;
    }

    public ArrayList<String> getSubscribeTime() {
        return SubscribeTime;
    }

    public ArrayList<String> getCommentUserName() {
        return CommentUserName;
    }

    public void setCommentUserName(ArrayList<String> commentUserName) {
        CommentUserName = commentUserName;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }
}
