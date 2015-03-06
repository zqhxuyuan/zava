package com.interview.design.pattern.structural;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 上午10:46
 *
 * Adapter pattern works as a bridge between two incompatible interfaces.
 */
public class AdapterPattern {

    /**
     *  Need to create a MediaPlayer play mp3, mp4 and vlc.
     *    1. Haven't another interface of AdvancedMediaPlayer could play vlc and mp4, but the interface is not incompatible.
     *    2. Use MediaAdapter to convert the interface, MediaAdapter composite a AdvancedMediaPlayer , and change the interface to play
     */
    static class ClassAdapterPattern{
        static interface MediaPlayer {
            public void play(String audioType, String fileName);
        }

        static interface AdvancedMediaPlayer {
            public void playVlc(String fileName);
            public void playMp4(String fileName);
        }

        static class VlcPlayer implements AdvancedMediaPlayer{
            @Override
            public void playVlc(String fileName) {
                System.out.println("Playing vlc file. Name: "+ fileName);
            }

            @Override
            public void playMp4(String fileName) {
                //do nothing
            }
        }

        static class Mp4Player implements AdvancedMediaPlayer{

            @Override
            public void playVlc(String fileName) {
                //do nothing
            }

            @Override
            public void playMp4(String fileName) {
                System.out.println("Playing mp4 file. Name: "+ fileName);
            }
        }

        /**
         * adapt AdvancedMediaPlayer's interface to play as MediaPlayer
         */
        static class MediaAdapter implements MediaPlayer {

            AdvancedMediaPlayer advancedMusicPlayer;

            public MediaAdapter(String audioType){
                if(audioType.equalsIgnoreCase("vlc") ){
                    advancedMusicPlayer = new VlcPlayer();
                } else if (audioType.equalsIgnoreCase("mp4")){
                    advancedMusicPlayer = new Mp4Player();
                }
            }

            @Override
            public void play(String audioType, String fileName) {
                if(audioType.equalsIgnoreCase("vlc")){
                    advancedMusicPlayer.playVlc(fileName);
                }else if(audioType.equalsIgnoreCase("mp4")){
                    advancedMusicPlayer.playMp4(fileName);
                }
            }
        }

        static class AudioPlayer implements MediaPlayer {
            MediaAdapter mediaAdapter;

            @Override
            public void play(String audioType, String fileName) {

                //inbuilt support to play mp3 music files
                if(audioType.equalsIgnoreCase("mp3")){
                    System.out.println("Playing mp3 file. Name: "+ fileName);
                }
                //mediaAdapter is providing support to play other file formats
                else if(audioType.equalsIgnoreCase("vlc")
                        || audioType.equalsIgnoreCase("mp4")){
                    mediaAdapter = new MediaAdapter(audioType);
                    mediaAdapter.play(audioType, fileName);
                }
                else{
                    System.out.println("Invalid media. "+
                            audioType + " format not supported");
                }
            }
        }

        public static void main(String[] args) {
            AudioPlayer audioPlayer = new AudioPlayer();

            audioPlayer.play("mp3", "beyond the horizon.mp3");
            audioPlayer.play("mp4", "alone.mp4");
            audioPlayer.play("vlc", "far far away.vlc");
            audioPlayer.play("avi", "mind me.avi");
        }
    }

    /**
     * 接口的适配器是这样的：有时我们写的一个接口中有多个抽象方法，当我们写该接口的实现类时，
     * 必须实现该接口的所有方法，这明显有时比较浪费，因为并不是所有的方法都是我们需要的，
     * 有时只需要某一些，此处为了解决这个问题，我们引入了接口的适配器模式，借助于一个抽象类，
     * 该抽象类实现了该接口，实现了所有的方法，而我们不和原始的接口打交道，只和该抽象类取得联系，
     * 所以我们写一个类，继承该抽象类，重写我们需要的方法就行。
     */
    static class InterfaceAdapter{
        static interface AdvancedMediaPlayer {
            public void playVlc(String fileName);
            public void playMp4(String fileName);
        }

        /**
         * create a Base class give the basic implementation
         * and the sub-class doesn't need to implement un-necessary method
         */
        static class AdvancedMediaPlayerBase implements AdvancedMediaPlayer{

            @Override
            public void playVlc(String fileName) {

            }

            @Override
            public void playMp4(String fileName) {

            }
        }

        static class VlcPlayer extends AdvancedMediaPlayerBase{
            @Override
            public void playVlc(String fileName) {
                System.out.println("Playing vlc file. Name: "+ fileName);
            }
        }

        static class Mp4Player extends AdvancedMediaPlayerBase{

            @Override
            public void playMp4(String fileName) {
                System.out.println("Playing mp4 file. Name: "+ fileName);
            }
        }
    }
}
