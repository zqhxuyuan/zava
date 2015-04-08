package com.zqh.play.traffic;

// http://blog.csdn.net/mavs41/article/details/11908117
//S2N,S2W,E2W,E2S,N2S,N2E,W2E,W2N,S2E,E2N,N2W,W2S
public class MainClass
{
    public static void main(String[] args)
    {
		/* 产生12个方向的路线 */
        String[] directions = new String[] { "S2N", "S2W", "E2W", "E2S", "N2S", "N2E", "W2E", "W2N", "S2E", "E2N",
                "N2W", "W2S" };
        for (int i = 0; i < directions.length; i++)
        {
            new Road(directions[i]);
        }
		/* 产生整个交通灯系统 */
        new LampController();
    }
}