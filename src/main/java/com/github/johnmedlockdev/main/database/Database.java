package com.github.johnmedlockdev.main.database;

import com.github.johnmedlockdev.main.datastructures.FileStructure;

import java.io.*;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Database {
    private final String symbol;
    private final FileStructure fileSymbol;
    private File path;

    public Database(String function, String symbol) {
        this.symbol = symbol;
        this.fileSymbol = new FileStructure(symbol);
        logic(function); // select || insert
    }

// todo handle errors with logging

    private void logic(String function) {
        if (function.equals("select")) {
            try {
                getSql(function, symbol);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (function.equals("insert")) {
            try {
                getSql(function, symbol);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else  {
            System.out.println("Invalid function Exiting batch mode.");
        }
    }

    private void getSql(String function, String fileName) throws IOException, SQLException, ClassNotFoundException {

        Connection conn = connectToDatabase();

//      prepared statement setup
        PreparedStatement statement = conn.prepareStatement(getStatement(function)); // input sql statement with ?

//      Results set obj
        if (function.equals("select")) {
            ResultSet rs = statement.executeQuery();
            readData(rs, fileName);
            rs.close(); // close connection
        } else {
            writeData(statement, fileName);
        }
        statement.close(); // close connection
        conn.close(); // close connection
    }


    private void readData(ResultSet rs, String fileName) throws SQLException, IOException {
        path = new File(fileSymbol.getAbsolutePathStr());

        OutputStream outputStream = new FileOutputStream(path);

        while (rs.next()) {

            String ticker = rs.getString(2);
            double price = rs.getDouble(3);
            String output = ticker + "," + price;

            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(fileSymbol.getAbsolutePathStr(), true)
            );
            writer.write(output);
            writer.newLine();
            writer.close();
        }
    }

    private void writeData(PreparedStatement statement, String fileName) throws SQLException, IOException {
        // for reading
        path = new File(fileSymbol.getAbsolutePathStr());

        BufferedReader br = new BufferedReader(new FileReader(fileSymbol.getAbsolutePathStr()));
        String line = "";
        while ((line = br.readLine()) != null) {
            String[] values = line.split("\\s*,\\s*");
            statement.setString(1, values[0].toUpperCase()); // set params based of what they are
            statement.setDouble(2, Double.parseDouble(values[1])); // set params based of what they are
            statement.addBatch();
        }

        statement.executeBatch();
    }

    private Connection connectToDatabase() throws IOException, SQLException {
        String dbFile = "src/main/java/com/github/johnmedlockdev/main/database/db.properties";
        FileReader reader = new FileReader(dbFile);
        Properties p = new Properties();
        p.load(reader);

        //      db connection setup
        String username = p.getProperty("username");
        String password = p.getProperty("password");
        String url = p.getProperty("url");

        return DriverManager.getConnection(url, username, password);

    }

    // TODO: probably need to refactor the properties file.
    private String getStatement(String method) throws IOException {
        String preparedStatement = "";
        String propertiesFile = "src/main/java/com/github/johnmedlockdev/main/database/statements.properties";
        FileReader reader = new FileReader(propertiesFile);
        Properties p = new Properties();
        p.load(reader);

        //      prepared statement assignment
        if (method.equals("insert")) {
            preparedStatement = p.getProperty("insertStatement");

        }
        if (method.equals("select")) {
            preparedStatement = p.getProperty("selectAll");
        }

        return preparedStatement;
    }


}
