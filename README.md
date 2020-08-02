# StarkHub - A JavaFX App

Your personal video streaming platform using LAN socket connections and RTSP streaming through UDP.

# Team Members: 
	
	1. Bhanu Pratap Singh : https://github.com/BhallalDev22
	2. Aritra Chatterjee : https://github.com/Arc29

## Features
* Consists of a central always-on server running on port 5000 of server. Server includes socket endpoints for an SQL database used in Starkhub. For more details
  check the server's dedicated repo at: https://github.com/BhallalDev22/StarkHub_P2PManager
  
* Individual nodes connect to central server to get information of other connected nodes and access their hosted videos (All videos are shared peer-to-peer)

* Trending videos are set on basis of most viewed videos in current hour

* User can create his/her own __channels__ and upload videos to them

* User can manage his all channels and videos and can view their statistics

* User can __like__/__dislike__ videos, Add videos to __Watch Later__, Write/Remove __comments__ and __subscribe/unsubscribe__ to channels

* Users also have access to their __Watch history__, __Comment History__ and __Liked Videos__

* User gets __Notification__ when their is any activity in his subscribed channels

* Videos are shared through __RTSP__(Real Time Streaming Protocol) and __RTP__(Real-time Transport Protocol). The server node(serving the video)
  and the client node establish an RTSP socket to communicate control signals, while audio and video frames are sent through RTP (which is implemented using 
  UDP socket). Basic workflow: <img src="https://github.com/mutaphore/RTSP-Client-Server/blob/master/images/rtsp1.png" alt="drawing" width="600" height="400"/>
  
  (Congestion Control is not used in our project due to its limited use in local environments)
  
* Audio and video frames are extracted using FFMPEGFrameGrabber class of JavaCV (Java wrapper of OpenCV). Repo at: https://github.com/bytedeco/javacv  

* Beautiful Material Design created using JFoenix (https://github.com/jfoenixadmin/JFoenix)

## Screen records

<p>
  <img src="https://github.com/BhallalDev22/StarkHub/blob/master/Starkhub_1.gif" alt="drawing" width="800" height="400"/>
  <img src="https://github.com/BhallalDev22/StarkHub/blob/master/Starkhub_2.gif" alt="drawing" width="800" height="400"/>
  <img src="https://github.com/BhallalDev22/StarkHub/blob/master/Starkhub_3.gif" alt="drawing" width="800" height="400"/>
  <img src="https://github.com/BhallalDev22/StarkHub/blob/master/Starkhub_4.gif" alt="drawing" width="800" height="400"/>
  <img src="https://github.com/BhallalDev22/StarkHub/blob/master/Starkhub_5.gif" alt="drawing" width="800" height="400"/>
  <img src="https://github.com/BhallalDev22/StarkHub/blob/master/Starkhub_6.gif" alt="drawing" width="800" height="400"/>
  <img src="https://github.com/BhallalDev22/StarkHub/blob/master/Starkhub_7.gif" alt="drawing" width="800" height="400"/>
</p>  
