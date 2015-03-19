package com.github.shansun.guava.basicutilities;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

/**
 * @author: lanbo <br>
 * @version: 1.0 <br>
 * @date: 2012-7-2
 */
public class ObjectsUsage {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // Objects.equals使用
        Objects.equal("a", "a"); // returns true
        Objects.equal(null, "a"); // returns false
        Objects.equal("a", null); // returns false
        Objects.equal(null, null); // returns true

        // Objects.toStringHelper使用
        Login obj = new Login("lanbo", 123456);
        System.out.println(Objects.toStringHelper(obj) // 创建ToStringHelper
                .addValue(1234567l) // 添加一个值
                .add("name", "new_name") // 设置字段值
                .addValue("Hello World") // 添加一个值
                .toString());
    }

    // ComparisonChain.compare/compareTo使用
    static class Foo implements Comparable<Foo> {

        private String	aString;
        private int		anInt;
        private Enum<?>	anEnum;

        @Override
        public int compareTo(Foo that) {
            return ComparisonChain.start() //
                    .compare(aString, that.aString) //
                    .compare(anInt, that.anInt) //
                    //.compare(anEnum, that.anEnum, Ordering.natural().nullsFirst()) //
                    .compare(anEnum, that.anEnum)
                    .result();
        }
    }

    // 普通的compare用法
    static class Person implements Comparable<Person> {
        private String	lastName;
        private String	firstName;
        private int		zipCode;

        @Override
        public int compareTo(Person o) {
            int cmp = lastName.compareTo(o.lastName);

            if (cmp != 0)
                return cmp;

            cmp = firstName.compareTo(o.firstName);

            if (cmp != 0)
                return cmp;

            return Ints.compare(zipCode, o.zipCode);
        }
    }

    static class Login {
        private String	name;
        private Integer	password;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getPassword() {
            return password;
        }

        public void setPassword(Integer password) {
            this.password = password;
        }

        public Login(String name, Integer password) {
            super();
            this.name = name;
            this.password = password;
        }
    }
}