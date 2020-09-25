package com.app.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Hung on 8/3/2015.
 */
public class AppConstants {

    public static final int POSITION_DEFAULT = 1;

    public static final String UNIT_DEFAULT = "inches";

    public static final String SIGNATURE_HEADER_CLIENT_ID_EXTRACT_REGEX = "Credential=([^/|?]+)/";

    public static final String CHARSET_UTF8 = "UTF-8";

    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE_APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public static final String DESIGN_TYPE_FRONT = "front";

    public static final String DESIGN_TYPE_BACK = "back";

    public static final SimpleDateFormat DEFAULT_DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd\'T\'HHmmss\'Z\'");
    public static final String DEFAULT_DATE_TIME_FORMAT_VALUE = "yyyyMMdd\'T\'HHmmss\'Z\'";

    static {
        DEFAULT_DATE_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static final DateFormat MILLISECOND_DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMdd\'T\'HHmmssSSSS\'Z\'");
}
