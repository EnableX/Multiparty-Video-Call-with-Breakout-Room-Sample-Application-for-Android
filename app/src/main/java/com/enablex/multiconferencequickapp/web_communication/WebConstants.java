package com.enablex.multiconferencequickapp.web_communication;

public class WebConstants {


    /* To try the app with Enablex hosted service you need to set the kTry = true */
        public  static  final  boolean kTry = true;

    /*Your webservice host URL, Keet the defined host when kTry = true */

        public static final String kBaseURL = "https://demo.enablex.io/";

    /*The following information required, Only when kTry = true, When you hosted your own webservice remove these fileds*/

    /*Use enablex portal to create your app and get these following credentials*/
        public static final String kAppId = "60ffb1c23494a67df74a7c78";
        public static final String kAppkey = "ugyYahe8aQyVaAayetyuezyjuSuLypaPaYe2";


         public static final String getRoomId = "createRoom/";
         public static final int getRoomIdCode = 1;
         public static final String validateRoomId = "getRoom/";
         public static final int validateRoomIdCode = 2;
         public static final String getTokenURL = "createToken/";
         public static final int getTokenURLCode = 3;
}
