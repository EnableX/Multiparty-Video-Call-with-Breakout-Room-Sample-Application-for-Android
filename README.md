# EnableX Multiparty Video Calling with Breakout-room  Android App

This is a sample video calling app that uses EnableX infrastructure, APIs and Toolkit. It allows developers to try out real-time video and audio features on android.


This sample apps allows you to easily:
* Create a Virtual Room with REST video API 
* Gain the Room Credential (i.e. Room ID) 
* Join Virtual Room either as moderator or partipicant securely

You will also enjoy the following features: 
* Mute/Unmute Video
* Mute/Unmute Audio
* Record Session
* ActiveTalker 
* Breakout room 


> For more information, pls visit our Developer Center https://developer.enablex.io/



## 1. Get started

### 1.1 Pre-Requisites

#### 1.1.1 App Id and App Key 

You would need API Credential to access EnableX platform. To do that, simply create an account with us. It’s absolutely free!

* Create an account with EnableX - https://portal.enablex.io/cpaas/trial-sign-up/
* Create your Project
* Get your App ID and App Key delivered to your Email


#### 1.1.2 Sample Android Client 

* Clone or download this Repository [https://github.com/EnableX/Multiparty-Video-Call-with-Breakout-Room-Sample-Application-for-Android.git] 


#### 1.1.3 Test Application Server 

An Application Server is required for your android App to communicate with EnableX. We have different variant of Application Server Sample Code, pick one in your preferred language and follow instructions given in README.md file of respective Repository.

* NodeJS: https://github.com/EnableX/Video-Conferencing-Open-Source-Web-Application-Sample.git 
* PHP: https://github.com/EnableX/Group-Video-Call-Conferencing-Sample-Application-in-PHP

Note the following:
•    You need to use App ID and App Key to run this Service.
•    Your android Client End Point needs to connect to this Service to create Virtual Room and Create Token to join session.
•    Application Server is created using [EnableX Server API] (https://developer.enablex.io/video-api/server-api/), a Rest API Service helps in provisioning, session access and post-session reporting.

If you would like to test the quality of EnableX video call before setting up your own application server,  you may run the test on our pre-configured environment. Please refer to section 2 for details.



#### 1.1.4 Configure Android Client 

* Open the App
* Go to WebConstants and change the following:
``` 
    /* To try the App with Enablex Hosted Service you need to set the kTry = true When you setup your own Application Service, set kTry = false */
        
        public  static  final  boolean kTry = true;
        
    /* Your Web Service Host URL. Keet the defined host when kTry = true */
    
        String kBaseURL = "https://demo.enablex.io/"
        
    /* Your Application Credential required to try with EnableX Hosted Service
        When you setup your own Application Service, remove these */
        
        String kAppId = ""  
        String kAppkey = ""  
 ```


### 1.2 Test

#### 1.2.1 Open the App

* Open the App in your Device. You get a form to enter Credentials i.e. Name & Room Id.
* You need to create a Room by clicking the "Create Room" button.
* Once the Room Id is created, you can use it and share with others to connect to the Virtual Room to carry out a RTC Session either as a Moderator or a Participant (Choose applicable Role in the Form).

Note: Only one user with Moderator Role allowed to connect to a Virtual Room while trying with EnableX Hosted Service. Your Own Application Server may allow upto 5 Moderators. 
  
Note:- If you used any emulator/simulator your local stream will not create. It will create only on real device.


## 2. Testing Environment

If you would like to test the quality of EnableX video call before setting up your own application server,  you may run the test on our pre-configured environment.https://try.enablex.io/
In this environment, you will only be able to:

* Conduct a single session with a total duration of no more than 10 minutes
* Host a multiparty call with no more than 3 participants 

> More information on Testing Environment: https://developer.enablex.io/video/sample-code/#demo-app-server

Once you have tested them, it is important that you set up your own Application Server to continue building a multiparty android video calling app. Refer to section 1.1.3 on how to set up the application server. 
  

## 3 Android Toolkit

This Sample Applcation uses EnableX Android Toolkit to communicate with EnableX Servers to initiate and manage Real Time Communications. You might need to update your Application with latest version of EnableX Android Toolkit time as and when a new release is avaialble.   

* Documentation: https://developer.enablex.io/video-api/client-api/android-toolkit/
* Download Toolkit: https://developer.enablex.io/video/downloads/#android-toolkit

## Exploring the Breakout Room
To know more about breakout room implementation, refer our API Documentation at 

https://developer.enablex.io/video-api/client-api/android-toolkit/advance-features/#breakout-room

## 4. Support

EnableX provides a library of Documentations, How-to Guides and Sample Codes to help software developers get started. 

> Go to https://developer.enablex.io/. 

You may also write to us for additional support at support@enablex.io.   


