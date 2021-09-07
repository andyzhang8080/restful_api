package com.example.mobile.app.ws.security;

import com.example.mobile.app.ws.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000; //10 days in mili-seconds
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static final String H2_CONSOLE = "/h2-console/**";
    public static final String EMAIL_VERIFICATION_URL = "/users/email-verification";
    //moved to app properties
    //public static final String TOKEN_SECRET = "jf9i4jgu83nfl0";
    public static String getTokenSecret()
    {
        AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("appProperties");
        return appProperties.getTokenSecret();
    }
}
