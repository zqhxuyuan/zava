package com.interview.books.topcoder.string;

/**
 * Created_By: stefanie
 * Date: 15-1-19
 * Time: 下午3:05
 *
 * You are implementing the portion of an online dating site where members display their profiles. Most of the
 * profile content is automatically generated from member data. Part of a profile, however, is furnished directly
 * by the member as free-form text. Weblinks frequently crop up in this text, despite a site policy that forbids
 * advertisement and linking of any kind. Your job is to seek and destroy all weblinks in a given piece of text.
 *
 * For example: "espihttp://www.tv.org.superwww.cali.comaladocious"
 * Returns: "espiOMIT1aladocious"
 *
 * Assume the prefix consists of one of the three following strings: http://, http://www., www.
 * And the suffix is one of the five following strings: .com, .org, .edu, .info, .tv
 */
public class TC_S3_UnLinker {
    static String URL = "((http://)?www\\.|http://)[a-zA-Z0-9\\.]+\\.(com|org|edu|info|tv)";
    public String clean(String text){
        String []m = text.split(URL, -1);
        StringBuffer buffer = new StringBuffer();
        buffer.append(m[0]);
        for (int i = 1 ; i < m.length ; ++i) {
            buffer.append("OMIT");
            buffer.append(i);
            buffer.append(m[i]);
        };
        return buffer.toString() ;
    }

    public static void main(String[] args){
        TC_S3_UnLinker cleaner = new TC_S3_UnLinker();
        System.out.println(cleaner.clean("espihttp://www.tv.org.superwww.cali.comaladocious"));
    }

}
