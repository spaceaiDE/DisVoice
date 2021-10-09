package de.spaceai.disvoice.database;

import de.spaceai.disvoice.DisVoice;
import de.spaceai.disvoice.logging.LogPriority;

import java.sql.*;

public class Database {

    private String host, database, username, password;
    private int port;

    private Connection connection;

    private final DisVoice disVoice;

    public Database(DisVoice disVoice, String host, int port, String database, String username, String password) {
        this.disVoice = disVoice;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    public void connect() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://"+this.host+":"+this.port+"/"+this.database, this.username,
                    this.password);
            this.disVoice.getLog().log(LogPriority.DEBUG, "Connected to Database");
        } catch (Exception e) {
            this.disVoice.getLog().log(LogPriority.WARN, "Connection cannot be established");
        }
    }

    public void disconnect() {
        if(isConnected()) {
            try {
                this.connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void update(String query) {
        try {
            Statement statement = this.connection.createStatement();
            statement.execute(query);
            statement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ResultSet get(String query) {
        ResultSet rs = null;
        try {
            Statement st = connection.createStatement();
            rs = st.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }

    public boolean hasElement(String table, String identifier, String value) {
        ResultSet resultSet = get("SELECT * FROM "+table+" WHERE "+identifier+"='"+value+"'");
        try {
            if(resultSet.next())
                return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public Object getSingleElement(String query, String column) {
        ResultSet rs = null;
        try {
            Statement st = connection.createStatement();
            rs = st.executeQuery(query);
            if(rs.next()) return rs.getObject(column);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public boolean isConnected() {
        try {
            return this.connection != null || this.connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createTable(String tableName, String... parameter) {
        String tableParams = "";
        for (String s : parameter) {
            tableParams+=s+", ";
        }
        tableParams = tableParams.substring(0, tableParams.length()-2);
        update("CREATE TABLE IF NOT EXISTS "+tableName+"("+tableParams+")");
    }

    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Connection getConnection() {
        return connection;
    }
}
