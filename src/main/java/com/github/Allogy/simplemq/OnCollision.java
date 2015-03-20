package com.github.Allogy.simplemq;

/**
 * 冲突的解决方式
 * User: robert
 * Date: 2014/01/24
 * Time: 3:17 PM
 */
public enum OnCollision
{
    //new和old表示冲突前后的两种状态. old是旧消息, new是新消息.

    //丢失: 新消息直接丢弃. 旧消息仍然在队列中
    DROP,    /* new message dies, old messages maintains it's place in the queue */

    //降级: 新消息直接丢弃, 旧消息被移动到队列尾部
    DEMOTE,  /* new message dies, but old message is moved to the end of the queue */

    //替换: 旧消息被丢弃, 新消息被加入到队列尾部
    REPLACE, /* old message dies, new message is placed at the end of the queue */

    //交换: 旧消息被丢弃, 新消息替换到原先旧消息的位置
    SWAP,    /* old message dies, but new message takes it's place in the queue (i.e. the queue time) */

    //排除: 新旧消息都被丢弃
    EXCLUDE, /* both messages die */
}
