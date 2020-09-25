package com.app.util;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by HungDX on 27-Jan-16.
 */
public class AppUtil {

    public static String generateRandomNumber(int length) {

        Random random = new Random();

        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {

            int randomNumber = random.nextInt(9);

            stringBuilder.append(randomNumber);
        }

        return stringBuilder.toString();
    }

    public static String getContentLength(String payload) {
        try {
            return String.valueOf(payload.getBytes("UTF-8").length);
        } catch (UnsupportedEncodingException e) {
            return "0";
        }
    }

    public static String escapeSpecialCharacter(String src) {
        if (src == null) return null;
        return src.replaceAll("([^0-9a-zA-Z,. ]+)", " ");
    }

    public static String escapeUTF8(String s) {
        String result = "";
        char c;
        int pos;
        for (int i = 0; s != null && i < s.length(); i++) {
            c = s.charAt(i);
            if ((pos = UNICODE.indexOf(c)) != -1) {
                result += NOSIGN[pos];
                //System.out.println("pos=" + pos + ", c=" + c);
            } else {
                result += c;
            }
        }
        return result;
    }

    private static final String UNICODE = "áàảãạăắằẳẵặâấầẩẫậđéèẻẽẹêếềểễệíìỉĩịóòỏõọôốồổỗộơớờởỡợúùủũụưứừửữựýỳỷỹỵÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬĐÉÈẺẼẸÊẾỀỂỄỆÍÌỈĨỊÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢÚÙỦŨỤƯỨỪỬỮỰÝỲỶỸỴ";

    private static final String NOSIGN_CONST = "aaaaaaaaaaaaaaaaadeeeeeeeeeeeiiiiiooooooooooooooooouuuuuuuuuuuyyyyyAAAAAAAAAAAAAAAAADEEEEEEEEEEEIIIIIOOOOOOOOOOOOOOOOOUUUUUUUUUUUYYYYY";

    private static char[] NOSIGN;

    static {
        NOSIGN = new char[NOSIGN_CONST.length()];
        for (int i = 0; i < NOSIGN_CONST.length(); i++) {
            NOSIGN[i] = NOSIGN_CONST.charAt(i);
        }
    }

    private static final Logger LOGGER = Logger.getLogger(AppUtil.class.getName());
}
