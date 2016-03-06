/*
 * This file is part of AceQL
 * AceQL: Remote JDBC access over HTTP.                                     
 * Copyright (C) 2015,  KawanSoft SAS
 * (http://www.kawansoft.com). All rights reserved.  
 * AceQL is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * 
 * Any modifications to this file must keep this entire header
 * intact.                                                                          
 */
package com.aspirephile.parlayultimatum.db;

//import org.kawanfw.sql.api.client.RemoteDriver;

import com.aspirephile.parlayultimatum.db.tables.ParlayUser;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * This example:
 * <ul>
 * <li>Inserts a Customer and an Order on a remote database.</li>
 * </li>Displays the inserted raws on the console with two SELECT executed on
 * the remote database.</li>
 * </ul>
 *
 * @author Nicolas de Pomereu
 */
public class ParlayUltimatumConnection {

    /**
     * The JDBC Connection to the remote AceQL Server
     */
    Connection connection = null;

    /**
     * Constructor
     *
     * @param connection the AwakeConnection to use for this session
     */
    public ParlayUltimatumConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * RemoteConnection Quick Start client example. Creates a Connection to a
     * remote database.
     *
     * @return the Connection to the remote database
     * @throws SQLException if a database access error occurs
     */

    public static Connection remoteConnectionBuilder(String url, String username, String password) throws SQLException {

        // Required for Android, optional for others environments:
        try {
            //ClassNotFoundException if AceQL client jar is not in the classpath
            Class.forName("org.kawanfw.sql.api.client.RemoteDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Attempt to establish a connection to the remote database:

        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Example of 2 INSERT in the same transaction
     *
     * @param customerId the Customer Id
     * @param itemId     the Item Id
     * @throws SQLException
     */
    public void insertCustomerAndOrderLog(int customerId, int itemId)
            throws SQLException {

        // connection.setAutoCommit(false);

        try {
            // Create a Customer
            String sql = "INSERT INTO CUSTOMER VALUES ( ?, ?, ?, ?, ?, ?, ?, ? )";
            PreparedStatement prepStatement = connection.prepareStatement(sql);

            int i = 1;
            prepStatement.setInt(i++, customerId);
            prepStatement.setString(i++, "Sir");
            prepStatement.setString(i++, "Smith");
            prepStatement.setString(i++, "John");
            prepStatement.setString(i++, "55, rue Sainte-Anne");
            prepStatement.setString(i++, "Paris");
            prepStatement.setString(i++, "75016");
            prepStatement.setString(i, "(33) (0)1 73 39 00 18");

            prepStatement.executeUpdate();
            prepStatement.close();

            // Uncomment following line to test transaction behavior
            // if (true) throw new SQLException("Exception thrown.");

            // Create an Order for this Customer
            sql = "INSERT INTO ORDERLOG VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? )";

            // Create a new Prepared Statement
            prepStatement = connection.prepareStatement(sql);

            i = 1;
            long now = new java.util.Date().getTime();

            prepStatement.setInt(i++, customerId);
            prepStatement.setInt(i++, itemId);
            prepStatement.setString(i++, "Item Description");
            prepStatement.setBigDecimal(i++, new BigDecimal("99.99"));
            prepStatement.setDate(i++, new java.sql.Date(now));
            prepStatement.setTimestamp(i++, new Timestamp(now));
            prepStatement.setBytes(i++, null); // No Blob in this example.
            prepStatement.setInt(i++, 1);
            prepStatement.setInt(i, 2);

            prepStatement.executeUpdate();
            prepStatement.close();

            System.out.println("Insert done!");
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            throw e;
        }/* finally {
            // connection.setAutoCommit(true);
        }*/
    }

    /*

        public void selectCustomerAndOrderLog(int customerId, int itemId)
                throws SQLException {

            // Display the created Customer:
            String sql = "SELECT CUSTOMER_ID, FNAME, LNAME FROM CUSTOMER "
                    + " WHERE CUSTOMER_ID = ?";
            PreparedStatement prepStatement = connection.prepareStatement(sql);
            prepStatement.setInt(1, customerId);

            ResultSet rs = prepStatement.executeQuery();
            while (rs.next()) {
                int customerId2 = rs.getInt("customer_id");
                String fname = rs.getString("fname");
                String lname = rs.getString("lname");

                System.out.println();
                System.out.println("customer_id: " + customerId2);
                System.out.println("fname      : " + fname);
                System.out.println("lname      : " + lname);
            }

            prepStatement.close();
            rs.close();

            // Display the created Order
            sql = "SELECT * FROM ORDERLOG WHERE  customer_id = ? AND item_id = ? ";

            prepStatement = connection.prepareStatement(sql);

            int i = 1;
            prepStatement.setInt(i++, customerId);
            prepStatement.setInt(i, itemId);

            rs = prepStatement.executeQuery();

            System.out.println();

            if (rs.next()) {
                int customerId2 = rs.getInt("customer_id");
                int itemId2 = rs.getInt("item_id");
                String description = rs.getString("description");
                BigDecimal cost_price = rs.getBigDecimal("cost_price");
                Date date_placed = rs.getDate("date_placed");
                Timestamp date_shipped = rs.getTimestamp("date_shipped");
                byte[] jpeg_image = rs.getBytes("jpeg_image");
                boolean is_delivered = rs.getBoolean("is_delivered");
                int quantity = rs.getInt("quantity");

                System.out.println("customer_id : " + customerId2);
                System.out.println("item_id     : " + itemId2);
                System.out.println("description : " + description);
                System.out.println("cost_price  : " + cost_price);
                System.out.println("date_placed : " + date_placed);
                System.out.println("date_shipped: " + date_shipped);
                System.out.println("jpeg_image  : " + jpeg_image.length);
                System.out.println("is_delivered: " + is_delivered);
                System.out.println("quantity    : " + quantity);

                prepStatement.close();
                rs.close();
            }
        }
    */
    public List<ParlayUser> getParlayUsers() throws SQLException {
        // Display the created Customer:
        String sql = "SELECT * FROM ParlayUser";
        PreparedStatement prepStatement = connection.prepareStatement(sql);
        // prepStatement.setInt(1, customerId);

        ResultSet rs = prepStatement.executeQuery();
        ArrayList<ParlayUser> parlayUsers = new ArrayList<>();
        while (rs.next()) {
            parlayUsers.add(new ParlayUser(rs));
        }
        prepStatement.close();
        rs.close();
        return parlayUsers;
    }

    /**
     * Delete an existing customers
     *
     * @param connection Represents a java sql connection
     * @throws SQLException
     */

    public void deleteCustomer(Connection connection, int customerId)
            throws SQLException {
        // We delete the 1 and 2 customers if they exist
        String sql = "delete from customer where customer_id = ?";
        PreparedStatement prepStatement = connection.prepareStatement(sql);
        prepStatement.setInt(1, customerId);

        prepStatement.executeUpdate();
        prepStatement.close();

    }

    /**
     * Delete an existing orderlog
     *
     * @param connection Represents a java sql connection
     * @throws SQLException
     */
    public void deleteOrderlog(Connection connection, int customerId, int idemId)
            throws SQLException {
        // We delete the 1 and 2 orderlog if they exist
        String sql = "delete from orderlog where customer_id = ? and item_id = ? ";
        PreparedStatement prepStatement = connection.prepareStatement(sql);
        prepStatement.setInt(1, customerId);
        prepStatement.setInt(2, idemId);

        prepStatement.executeUpdate();
        prepStatement.close();

    }

    public void close() throws SQLException {
        connection.close();
    }

/**
 * Main
 *
 * @param args
 *            not used
 */
/*

	public static void main(String[] args) throws Exception {

		Connection connection = ParlayUltimatumConnection
				.remoteConnectionBuilder();
		ParlayUltimatumConnection remoteConnection = new ParlayUltimatumConnection(
				connection);

		// System.out.println("deleting customer...");
		// Delete previous instances, so that user can recall class
		// remoteConnection.deleteCustomer(connection, customerId);
		// System.out.println("deleting orderlog...");
		// remoteConnection.deleteOrderlog(connection, customerId, itemId);

		// remoteConnection.insertCustomerAndOrderLog(customerId, itemId);
		// remoteConnection.selectCustomerAndOrderLog(customerId, itemId);

		Log.d("Connection","ParlayUsers:");
		List<ParlayUser> parlayUsers = remoteConnection.getParlayUsers();
		for (ParlayUser p : parlayUsers) {
			Log.d("Connection", p.toString());
		}

		Log.d("Connection", "Points:");
		List<Point> points = remoteConnection.getPoints();
		for (Point p : points) {
			Log.d("Connection", p.toString());
		}
		System.out.println("Enter the PID: ");
//		Scanner s = new Scanner(System.in);
		Point point = remoteConnection.getPoint(21);
		Log.d("Connection", point.toString());

*/
/*
        System.out.println("Points:");
		List<Point> points = remoteConnection.getPoints();
		for (Point p : points) {
			System.out.println(p);
		}
		System.out.println("Enter the PID: ");
		Scanner s = new Scanner(System.in);
		Point point = remoteConnection.getPoint(s.nextInt());
		System.out.println(point);
		*//*

//		s.close();
	}
*/

}