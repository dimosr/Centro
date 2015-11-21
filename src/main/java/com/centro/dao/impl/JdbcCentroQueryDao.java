package com.centro.dao.impl;

import com.centro.model.CentroQuery;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;


public class JdbcCentroQueryDao {
    
    private DataSource dataSource;
    
    public void setDataSource(DataSource dataSource) {
	this.dataSource = dataSource;
    }
    
    public int insert(CentroQuery centroQuery) {
        int queryID = 0;
        String sql = "INSERT INTO centro_query" +
                      "(id, starting_points, modes, meeting_type) VALUES ";
        Connection connection = null;
        
        try {
            connection = dataSource.getConnection();
            PreparedStatement prepStmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            prepStmt.setInt(1, centroQuery.getId());
            prepStmt.setString(2, centroQuery.getStartingPoints());
            prepStmt.setString(3, centroQuery.modes());
            prepStmt.setString(3, centroQuery.getMeetingType());
            prepStmt.executeUpdate();
            
            ResultSet keyResultSet = prepStmt.getGeneratedKeys();
            if (keyResultSet.next()) {
                queryID = (int) keyResultSet.getInt(1);
            }
            
            prepStmt.close();
            return queryID;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (connection != null) {
		try {
                    connection.close();
		} catch (SQLException e) {}
            }
        }
    }
    
    public CentroQuery findById(int id) {
        String sql = "SELECT * FROM centro_query WHERE id = ?";
        Connection connection = null;
        
        try {
            connection = dataSource.getConnection();
            PreparedStatement prepStmt = connection.prepareStatement(sql);
            prepStmt.setInt(1, id);
            
            CentroQuery query = null;
            ResultSet rs = prepStmt.executeQuery();
            if(rs.next()) {
                query = new CentroQuery(
                        rs.getInt("id"),
                        rs.getString("starting_points"),
                        rs.getString("modes"),
                        rs.getString("meeting_type")
                );
            }
            rs.close();
            prepStmt.close();
            return query;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (connection != null) {
		try {
                    connection.close();
		} catch (SQLException e) {}
            }
        }
    }
    
}
