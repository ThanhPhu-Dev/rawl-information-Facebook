package vdq.com.main;

import java.sql.*;
import java.util.Properties;


public class DAO {
    public Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://45.119.82.18:1289;Database=dongbocuduoc";
            Properties connectionProps = new Properties();
            connectionProps.put("user", "vs");
            connectionProps.put("password", "vietSo@12890");
            connectionProps.put("characterEncoding", "UTF-8");
            return DriverManager.getConnection(url, connectionProps);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setParameter(PreparedStatement statement, Object... parameters) {
        try {
            for (int i = 0; i < parameters.length; i++) {
                Object parameter = parameters[i];
                int index = i + 1;
                if (parameter instanceof Long) {
                    statement.setLong(index, (Long) parameter);
                } else if (parameter instanceof String) {
                    statement.setString(index, (String) parameter);
                } else if (parameter instanceof Integer) {
                    statement.setInt(index, (Integer) parameter);
                } else if (parameter instanceof Date) {
                    statement.setDate(index, (Date) parameter);
                }else if (parameter == null) {
					statement.setNull(index, Types.NULL);
				}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Long Insert(String sql, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultset = null;
        try {
            Long id = null;
            connection = getConnection();
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setParameter(statement, parameters);
            statement.executeUpdate();
            resultset = statement.getGeneratedKeys();
            if (resultset.next()) {
                id = resultset.getLong(1);
            }
            connection.commit();
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (resultset != null) {
                    resultset.close();
                }

            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }

        return null;
    }

    public int count(String sql, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            int count = 0;
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            setParameter(statement, parameters);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                count = resultSet.getInt(1);
            }
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                return 0;
            }
        }
    }

}
