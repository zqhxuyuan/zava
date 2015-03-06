Interview Questions from Lintcode

1.  Given 2*n + 2 numbers, every numbers occurs twice except two, find them.
    - assume number is A and B, first find the xor of A and B, xor should at least have one bit is 1,
    - find the bit, it is the differentiation of A nd B.
    - number != 0 and number >>> 1 as the condition to find the lowest 1, for negative numbers. 

