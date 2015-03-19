package com.github.shansun.concurrent.json;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-12
 */
public class JsonMain {
    private static final int	EXEC_TIMES	= 30000;

    static Map<String, Group>	map	= Maps.newHashMap();

    static Gson gson = new Gson();

    /**
     * @param args
     */
    public static void main(String[] args) {
        testGsonStackOverFlow();

        testSerializePerformance();
    }

    static void testSerializePerformance() {
        Group g1 = new Group();
        g1.groupId = 123L;
        g1.groupName = "Group-001";
        List<User> users1 = Lists.newArrayList();
        User user1 = new User();
        user1.nick = "lanbo";
        user1.userId = 12345L;
        user1.password = "abc123";
        User user2 = new User();
        user2.nick = "shansun";
        user2.userId = 12345L;
        user2.password = "abc123";
        users1.add(user1);
        users1.add(user2);
        g1.users = users1;
        Group g2 = new Group();
        g2.groupId = 234L;
        g2.groupName = "Group-002";
        g2.users = users1;
        map.put("g1", g1);
        map.put("g2", g2);

        System.out.println(testGson());

        System.out.println(testFastJson());

        System.out.println(testToStringBuilder());

        System.out.println("____________________________________________________\r\n");

        long start = System.currentTimeMillis();

        for(int i = 0; i < EXEC_TIMES; i++) {
            testFastJson();
        }

        System.err.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();

        for(int i = 0; i < EXEC_TIMES; i++) {
            testGson();
        }

        System.err.println(System.currentTimeMillis() - start);

        start = System.currentTimeMillis();

        for(int i = 0; i < EXEC_TIMES; i++) {
            testToStringBuilder();
        }

        System.err.println(System.currentTimeMillis() - start);
    }

    static String testGson() {
        return gson.toJson(map);
    }

    static String testFastJson() {
        return JSON.toJSONString(map);
    }

    static String testToStringBuilder() {
        return ToStringBuilder.reflectionToString(map);
    }

    static void testGsonStackOverFlow() {
        Group g = new Group();
        g.groupId = 123456L;
        g.groupName = "Group-00";
        List<User> users = Lists.newArrayList();

        for(int i = 0; i < 500000; i++) {
            User user = new User();
            user.nick = "lanbo-" + i;
            user.userId = 12345L;
            user.password = "abc123" + i;
            users.add(user);
        }

        g.users = users;

        JSON.toJSONString(g);

        gson.toJson(g);
    }

    static class User {
        private Long	userId;
        private String	nick;
        private String	password;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getNick() {
            return nick;
        }

        public void setNick(String nick) {
            this.nick = nick;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    static class Group {
        private Long		groupId;
        private String		groupName;
        private List<User>	users;

        public Long getGroupId() {
            return groupId;
        }

        public void setGroupId(Long groupId) {
            this.groupId = groupId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }
    }

}