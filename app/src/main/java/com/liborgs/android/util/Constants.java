package com.liborgs.android.util;

/**
 * Created by shashankm on 30/12/15.
 */
public class Constants {
    public static String NOTIFICATION_TYPE_APPROVED = "approved_return";
    public static String NOTIFICATION_TYPE_NEW_BOOK = "new_book";
    public static final int PIC_CAPTURED = 0;
    public static final int CROPPED_PIC = 1;
    public static final int PRIVATE_MODE = 2;
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_FIRST_NAME = "firstname";
    public static final String KEY_LAST_NAME = "lastname";
    public static final String GOOGLE_PROJECT_ID = "926498080108";
    public static final String KEY_AUTH = "auth_key" ;
    public static final String KEY_FNAME = "first name" ;
    public static final String KEY_LNAME = "last name" ;
    public static final String KEY_REG_ID = "reg_id";
    public static final String LIB_KEY = "Liborg Auth";
    public static final String SIGN_UP = "Sign Up";
    public static final String PASSWORD_NOT_MATCHING = "Password not matching";
    public static final String LIBORGS = "Liborgs";
    public static final String INTERNET_CONN = "Please connect to internet";
    public static final int REQ_CODE_SPEECH_INPUT = 100;
    private final static String BASE_URL = "https://liborgs-1139.appspot.com/";
    public static final String PASSWORD_RESET_URL = BASE_URL + "users/reset-password?";
    public static final String USER_LOGIN_URL = BASE_URL + "users/login?";
    public static final String USER_REGISTER_URL = BASE_URL + "users/register";
    public static final String DEVICE_GCM_REGISTER = BASE_URL + "device/register";
    public static final String GET_APP_VERSION = BASE_URL + "device/app_version_code";
    public static final String USER_REQUEST_BOOK = BASE_URL + "users/request_book";
    public static final String USERS_ISSUE_BOOK = BASE_URL + "users/issue";
    public static final String GET_ALL_BOOKS = BASE_URL + "books/get_all_books";
    public static final String LOCAL_BOOK_SEARCH = BASE_URL + "books/search";
    public static final String GOOGLE_BOOK_SEARCH = BASE_URL + "books/search_google";
    private static final String IMAGE_SEARCH_BASE_URL = "http://107.167.190.253/";
    public static final String BOOK_IMAGE_SEARCH = IMAGE_SEARCH_BASE_URL + "image_search/similar";
    public static final String USER_BOOK_RETURN = BASE_URL + "users/return";
    public static final String GET_USER_ISSUED_BOOKS = BASE_URL + "users/issued_books";
}
