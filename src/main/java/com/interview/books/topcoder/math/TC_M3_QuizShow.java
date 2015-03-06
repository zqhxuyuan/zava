package com.interview.books.topcoder.math;

/**
 * Created_By: stefanie
 * Date: 15-1-18
 * Time: 下午12:48
 *
 * You are a contestant on a TV quiz show. Throughout the game, you and your 2 opponents have accumulated
 * points by answering trivia questions. At the end of the game, the three of you are given one final question.
 * Before you hear the question, each contestant must decide how many points he or she wishes to wager.
 * Each contestant who answers the question correctly will gain a number of points equal to his or her wager,
 * while the others will lose a number of points equal to their respective wagers. The contestant who ends up
 * with the highest score after the final question wins the game.
 *
 * It has come to the point in the game where you must select your wager. You can bet any amount between zero
 * and your current score, inclusive. Given your current score, the scores of your two opponents, and how much
 * you believe each of your opponents will wager, compute how much you should wager in order to have the highest
 * probability of winning the game. Assume that you and your opponents each independently have a 50% chance of
 * answering the final question correctly.
 *
 * You will be given the three scores as a int[], scores. The first element is your score, the next element is
 * your first opponent's score, and the last element is your second opponent's score. You will also be given
 * wager1 and wager2, the amount of your first and second opponents' wagers, respectively.
 *
 * Your goal is to maximize your chance of winning uncontested. As far as you're concerned, ending in a tie is
 * as bad as losing. If there are multiple wagers that give you the same highest probability of winning, return
 * the smallest such wager. If you have no chance of winning, return zero.
 */
public class TC_M3_QuizShow {

    public int wager(int[] scores, int[] wagers){
        int best = 0, bet = 0;

        for(int wager = 0; wager <= scores[0]; wager++){
            int odds = 0;
            for(int i = -1; i < 2; i += 2){
                for(int j = -1; j < 2; j += 2){
                    for(int k = -1; k < 2; k += 2){
                        int mine = scores[0] + i * wager;
                        int first = scores[1] + j * wagers[0];
                        int second = scores[2] + k * wagers[1];
                        if(mine > first && mine > second) odds++;
                    }
                }
            }
            if(odds > best) {
                bet = wager;
                best = odds;
            }
        }
        return bet;
    }


    public static void main(String[] args){
        TC_M3_QuizShow gamble = new TC_M3_QuizShow();

        int[] scores = new int[]{100, 100, 100 };
        int[] wagers = new int[]{25,75};

        System.out.println(gamble.wager(scores, wagers)); //76


        scores = new int[]{10, 50, 60 };
        wagers = new int[]{30,41};

        System.out.println(gamble.wager(scores, wagers)); //0

        scores = new int[] { 5824, 4952, 6230 };
        wagers = new int[]{364,287};
        System.out.println(gamble.wager(scores, wagers)); //694
    }
}
