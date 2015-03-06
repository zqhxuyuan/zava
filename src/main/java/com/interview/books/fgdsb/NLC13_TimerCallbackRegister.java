package com.interview.books.fgdsb;

import java.util.TreeMap;

/**
 * Created_By: stefanie
 * Date: 15-2-2
 * Time: 上午11:30
 */
public class NLC13_TimerCallbackRegister {
    /* System Function */
    interface Callback{
        public void execute();
    }

    static class SysExecuation{
        public static void register_system_timer_callback(long time, Callback callback){

        }
    }

    /* Register Wrapper to register next callback when one is executed. */
    class RegisteWapper implements Callback{
        Callback callback;
        CallBackRegister register;
        public RegisteWapper(Callback callback, CallBackRegister register){
            this.callback = callback;
            this.register = register;
        }
        public void execute(){
            callback.execute();
            register.regNext();
        }
    }

    class CallBackRegister{
        TreeMap<Long, Callback> regMap = new TreeMap();
        long currentTimer = Long.MAX_VALUE;

        public void registerCallback(long time, Callback callback){
            if(time <= System.currentTimeMillis()) {
                callback.execute();
            } else {
                regMap.put(time, callback);
                if(time < currentTimer){
                    currentTimer = time;
                    SysExecuation.register_system_timer_callback(currentTimer, new RegisteWapper(callback, this));
                }
            }
        }

        private void regNext() {
            regMap.remove(currentTimer);
            currentTimer = regMap.firstKey();
            SysExecuation.register_system_timer_callback(currentTimer, new RegisteWapper(regMap.get(currentTimer), this));
        }
    }

}
