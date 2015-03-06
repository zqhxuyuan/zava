package com.interview.algorithms.design;

/**
 * Created_By: stefanie
 * Date: 14-7-27
 * Time: 下午3:05
 */
public class C9_2_MagicCube {
    // 6 faces, 9 chips each face
    static final int X = 0;
    static final int Y = 1;
    static final int Z = 2;

    private int[][] chips = new int[6][9];

    void transform(int direction, int level) {
        switch (direction) {
            case X:
                transformX(level);
                break;
            case Y:
                transformY(level);
                break;
            case Z:
                transformZ(level);
                break;
            default:
                throw new RuntimeException("Unknown Direction");
        }
    }
    private void transformX(int level){
        int begin = (level-1) * 3;

        for(int f = 0; f < 4; f++){
            for(int i = 0; i < 3; i++) {
                //chips[f][begin+i] = chips[]
            }
         }
    }

    private void transformY(int level){

    }

    private void transformZ(int level){

    }

    public boolean isFinished(){
        return false;
    }

    public void init(){

    }
}

