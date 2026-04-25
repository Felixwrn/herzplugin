package de.felix.lifeplugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

public class MySQLStorage implements Storage {

    private final Connection con;

    public MySQLStorage(Connection con) {
        this.con = con;
    }

    @Override
    public void loadPlayer(UUID uuid) {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT lives FROM player_lives WHERE uuid=?"
            );

            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                savePlayer(uuid, 10);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void savePlayer(UUID uuid, int lives) {
        try {
            PreparedStatement ps = con.prepareStatement(
                    "REPLACE INTO player_lives (uuid, lives) VALUES (?, ?)"
            );

            ps.setString(1, uuid.toString());
            ps.setInt(2, lives);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getLives(UUID uuid) {

        try {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT lives FROM player_lives WHERE uuid=?"
            );

            ps.setString(1, uuid.toString());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getInt("lives");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 10;
    }
}
