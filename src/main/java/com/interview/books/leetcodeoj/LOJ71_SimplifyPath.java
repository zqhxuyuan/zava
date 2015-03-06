package com.interview.books.leetcodeoj;

/**
 * Created_By: stefanie
 * Date: 14-12-23
 * Time: 下午2:29
 */
public class LOJ71_SimplifyPath {
    //edge cases: null, "/", "/.",
    //three different cases: ".", "..", "a"
    //1. string equals uisng .equals() not ==
    //2. when offset == 0, don't do offset-- when steps[i].equals("..");
    //3. return "/" when offset == 0
    public String simplifyPath(String path) {
        if(path == null) return "/";
        String[] steps = path.split("/");
        int offset = 0;
        for(int i = 0; i < steps.length; i++){
            if(steps[i].length() == 0 || steps[i].equals(".")) continue;
            else if(steps[i].equals("..")){
                if(offset > 0) offset--;
            }
            else steps[offset++] = steps[i];
        }
        if(offset == 0) return "/";
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < offset; i++){
            buffer.append("/");
            buffer.append(steps[i]);
        }
        return buffer.toString();
    }
}
