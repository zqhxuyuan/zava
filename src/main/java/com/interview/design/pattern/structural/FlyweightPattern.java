package com.interview.design.pattern.structural;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午12:27
 *
 * Flyweight pattern is primarily used to reduce the number of objects created,
 * to decrease memory footprint and increase performance.
 * Flyweight pattern try to reuse already existing similar kind objects by storing them
 * and creates new object when no matching object is found.
 *
 * 享元模式的主要目的是实现对象的共享，即共享池，当系统中对象多的时候可以减少内存的开销，通常与工厂模式一起使用。
 *
 * 类似JDBCConnection Pool
 */
public class FlyweightPattern {

    static class ConnectionPool {

        private Vector<Connection> pool;

        /*公有属性*/
        private String url = "jdbc:mysql://localhost:3306/test";
        private String username = "root";
        private String password = "root";
        private String driverClassName = "com.mysql.jdbc.Driver";

        private int poolSize = 100;
        private static ConnectionPool instance = null;
        Connection conn = null;

        /*构造方法，做一些初始化工作*/
        private ConnectionPool() {
            pool = new Vector<Connection>(poolSize);

            for (int i = 0; i < poolSize; i++) {
                try {
                    Class.forName(driverClassName);
                    conn = DriverManager.getConnection(url, username, password);
                    pool.add(conn);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        /* 返回连接到连接池 */
        public synchronized void release() {
            pool.add(conn);
        }

        /* 返回连接池中的一个数据库连接 */
        public synchronized Connection getConnection() {
            if (pool.size() > 0) {
                Connection conn = pool.get(0);
                pool.remove(conn);
                return conn;
            } else {
                return null;
            }
        }
    }
}
