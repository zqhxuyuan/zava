package com.interview.books.question300;

import java.util.Arrays;

/**
 * Created_By: stefanie
 * Date: 14-12-15
 * Time: 下午8:20
 */
public class TQ14_StraightCards {
    public static int KING = 20;

    public boolean isStraight(String[] cards){
        if(cards.length != 5) return false;

        int[] numbers = new int[cards.length];
        for(int i = 0; i < cards.length; i++){
            numbers[i] = convert(cards[i]);
        }
        Arrays.sort(numbers);
        int countKing = 0;
        for(int i = numbers.length - 1; i >= 0 && numbers[i] == KING; i--){
            countKing++;
        }
        for(int i = 1; i < numbers.length && numbers[i] != KING; i++){
            if(numbers[i] != numbers[i - 1] + 1){
                countKing--;
                if(countKing < 0) return false;
            }
        }
        return true;
    }

    private int convert(String card){
        if(card.length() == 1 && card.charAt(0) >= '2' && card.charAt(0) <= '9') return card.charAt(0) - '0';
        switch (card){
            case "King": return KING;
            case "A": return 11;
            case "10": return 10;
            case "J": return 11;
            case "Q": return 12;
            case "K": return 13;
            default:  return -1;
        }
    }

    public static void main(String[] args){
        TQ14_StraightCards identifier = new TQ14_StraightCards();
        String[] card = new String[]{"2", "5", "King", "4", "6"};
        System.out.println(identifier.isStraight(card)); //true
        card = new String[]{"7", "8", "9", "10", "J"};
        System.out.println(identifier.isStraight(card)); //true
        card = new String[]{"1", "9", "3", "King", "5"};
        System.out.println(identifier.isStraight(card)); //false
    }
}
