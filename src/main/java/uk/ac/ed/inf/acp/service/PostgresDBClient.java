package uk.ac.ed.inf.acp.service;

import uk.ac.ed.inf.acp.config.PostgresConfig;
import uk.ac.ed.inf.acp.model.Drone;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
@Service
public class PostgresDBClient {

    public final String SID = "s2347484";
    public final DataSource postgresConfig;

    public PostgresDBClient(DataSource postgresConfig) {
        this.postgresConfig = postgresConfig;
    }
    public List<Map<String, Object>> getItems(String table) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT * FROM " + table;

        try (Connection conn = postgresConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error reading from Postgres table: " + table, e);
        }

        return results;
    }

    public Map<String, Object> getItem(String table, String key) {
        String sql = "SELECT * FROM " + table + " WHERE name = ?";
        try (Connection conn = postgresConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                return row;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error reading from Postgres table: " + table, e);
        }
    }

    public void saveDroneToPostgres(Drone drone, String table) {
        String sql = "INSERT INTO " + table
                + " (name, id, cooling, heating, capacity, max_moves, cost_per_move, cost_initial, cost_final, cost_per_100_moves)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                + " ON CONFLICT (name) DO UPDATE SET"
                + " id = EXCLUDED.id,"
                + " cooling = EXCLUDED.cooling,"
                + " heating = EXCLUDED.heating,"
                + " capacity = EXCLUDED.capacity,"
                + " max_moves = EXCLUDED.max_moves,"
                + " cost_per_move = EXCLUDED.cost_per_move,"
                + " cost_initial = EXCLUDED.cost_initial,"
                + " cost_final = EXCLUDED.cost_final,"
                + " cost_per_100_moves = EXCLUDED.cost_per_100_moves";

        try (Connection conn = postgresConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, drone.getName());
            stmt.setString(2, drone.getId());
            stmt.setObject(3, drone.getCapability().getCooling());
            stmt.setObject(4, drone.getCapability().getHeating());
            stmt.setObject(5, drone.getCapability().getCapacity());
            stmt.setObject(6, drone.getCapability().getMaxMoves());
            stmt.setObject(7, drone.getCapability().getCostPerMove());
            stmt.setObject(8, drone.getCapability().getCostInitial());
            stmt.setObject(9, drone.getCapability().getCostFinal());
            stmt.setDouble(10, drone.getCostPer100Moves());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving drone to Postgres", e);
        }
    }
}
