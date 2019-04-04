/*
 * Copyright (c) 2019.  Artem Martus (upsage) All Rights Reserved
 */

package main.makelabs_bot.model;

import main.makelabs_bot.helper.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseManager {
    private static final String databaseUri =
            "jdbc:mysql://makelabsdatabase.cxkbfyhmxknq.eu-central-1.rds.amazonaws.com:3306/myDatabase";
    private static final String user = "makelabsRoot";
    private static final String password = "CctGse2SyrgbXke";
    private static DatabaseManager databaseManager;
    private final Connection connection;

    private DatabaseManager() {
        Connection tempConnection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            tempConnection = DriverManager.getConnection(databaseUri,
                    user, password);
            Statement statement = tempConnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT now();");
            if (resultSet.first()) {
                String time = Analytics.getTime(resultSet.getTimestamp(1).getTime());
                Log.Info(time, Log.DATABASE_MANAGER);
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        connection = tempConnection;
        if (connection != null) {
            Log.Info("DatabaseManager successfully initialized", Log.DATABASE_MANAGER);
        } else {
            Log.Info("DatabaseManager has some issue with starting", Log.DATABASE_MANAGER);
        }
    }


    public static DatabaseManager getInstance() {
        if (databaseManager == null) {
            synchronized (DatabaseManager.class) {
                if (databaseManager == null)
                    databaseManager = new DatabaseManager();
            }
        }
        return databaseManager;
    }

}
