package com.yz.local.transaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 本地事务验证
 *
 * @author yunze
 * @date 2023/11/26 0026 14:21
 */
public class LocalTransaction {

    private static final String url = "jdbc:mysql://localhost:3306/t_mall_account";
    private static final String username = "root";
    private static final String password = "123456";

    public static void main(String[] args) throws SQLException {
        String sql = "insert into t_account (id, name, cash_balance) values (?, ?, ?)";

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // 加载数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 建立数据库连接，获得连接对象Connection
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);    // 关闭自动提交，也就是开启手动控制事务的提交
            // 创建和执行PreparedStatement操作
            preparedStatement = connection.prepareStatement(sql);

            // 设置参数
            preparedStatement.setLong(1, 1L);
            preparedStatement.setString(2, "王五");
            preparedStatement.setBigDecimal(3, BigDecimal.valueOf(2000));

            // 执行插入sql语句
            preparedStatement.executeUpdate();

            // 提交事务
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            // 事务回滚
            assert connection != null;
            connection.rollback();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
            // 事务回滚
        } finally {
            // 关闭连接资源
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
