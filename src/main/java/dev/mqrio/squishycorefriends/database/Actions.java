package dev.mqrio.squishycorefriends.database;

import dev.mqrio.squishycorefriends.config.Configuration;
import dev.mqrio.squishycorefriends.models.Friendship;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

import static dev.mqrio.squishycorefriends.database.Database.GetDb;

public class Actions {
    Connection connection = GetDb();
    Configuration config = new Configuration();
    String TablesPrefix = config.GetConfig().getString("database.tables_prefix");

    public Actions() throws SQLException {
    }

    public String UsernameToUUID(String username) throws SQLException {
        String sql = "SELECT * FROM " + TablesPrefix + "players WHERE username = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, username);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            return results.getString("uuid");
        }

        return null;
    }

    public void SyncPlayerFriendsLimit(Player player) throws SQLException {
        int friendsToAdd = 0;
        int friendToSub = 0;
        List<Integer> maxFriendsComparator = new ArrayList<Integer>();

        String limitingPrefix = config.GetConfig().getString("permissions.limits_prefix");

        for (PermissionAttachmentInfo p : player.getEffectivePermissions()){
            if (p.getPermission().startsWith(limitingPrefix)) {
                String afterPrefix = p.getPermission().split(limitingPrefix)[1];

                if(afterPrefix.charAt(0) == '+') {
                    friendsToAdd += Integer.parseInt(afterPrefix.substring(1));
                    continue;
                }

                if(afterPrefix.charAt(0) == '-') {
                    friendToSub += Integer.parseInt(afterPrefix.substring(1));
                    continue;
                }

                maxFriendsComparator.add(Integer.parseInt(afterPrefix));
            }
        }

        int maxPlayerFriends = Collections.max(maxFriendsComparator);
        maxPlayerFriends += friendsToAdd;
        maxPlayerFriends -= friendToSub;

        String sql = "UPDATE " + TablesPrefix + "players SET max_friends = ? WHERE uuid = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, maxPlayerFriends);
        stmt.setString(2, player.getUniqueId().toString());
        stmt.executeUpdate();
    }

    public void NewPlayer(String uuid, String username) throws SQLException {
        String sql = "SELECT * FROM " + TablesPrefix + "players WHERE uuid = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, uuid);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            return;
        }

        sql = "INSERT IGNORE INTO " + TablesPrefix + "players (uuid, username, is_accepting, is_huggable, is_teleportable, max_friends, lastpos_x, lastpos_y, lastpos_z, lastpos_world) VALUES (?, ?, 1, 1, 1, 0, 0, 0, 0, '');";
        PreparedStatement stmtNew = connection.prepareStatement(sql);
        stmtNew.setString(1, uuid);
        stmtNew.setString(2, username);
        stmtNew.executeUpdate();
    }

    public void UpdatePlayer(String uuid, String username) throws SQLException {
        String sql = "UPDATE " + TablesPrefix + "players SET username = ? WHERE uuid = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, uuid);
        stmt.executeUpdate();

        sql = "SELECT * FROM " + TablesPrefix + "players WHERE username = ? AND uuid != ?;";
        stmt = connection.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, uuid);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            long id = results.getLong("id");
            sql = "UPDATE " + TablesPrefix + "players SET username = '' WHERE id = ?;";
            stmt = connection.prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }

        SyncPlayerFriendsLimit(Bukkit.getPlayer(UUID.fromString(uuid)));
    }

    public void UpdatePlayerLastPos(String uuid, Location location) throws SQLException {
        String sql = "UPDATE " + TablesPrefix + "players SET lastpos_x = ?, lastpos_y = ?, lastpos_z = ?, lastpos_world = ? WHERE uuid = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setDouble(1, location.getX());
        stmt.setDouble(2, location.getY());
        stmt.setDouble(3, location.getZ());
        stmt.setString(4, location.getWorld().getUID().toString());
        stmt.setString(5, uuid);
        stmt.executeUpdate();
    }

    public void DisableAccepting(String uuid) throws SQLException {
        String sql = "UPDATE " + TablesPrefix + "players SET is_accepting = 0 WHERE uuid = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, uuid);
        stmt.executeUpdate();
    }

    public void EnableAccepting(String uuid) throws SQLException {
        String sql = "UPDATE " + TablesPrefix + "players SET is_accepting = 1 WHERE uuid = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, uuid);
        stmt.executeUpdate();
    }

    public void DisableHugs(String uuid) throws SQLException {
        String sql = "UPDATE " + TablesPrefix + "players SET is_huggable = 0 WHERE uuid = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, uuid);
        stmt.executeUpdate();
    }

    public void EnableHugs(String uuid) throws SQLException {
        String sql = "UPDATE " + TablesPrefix + "players SET is_huggable = 1 WHERE uuid = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, uuid);
        stmt.executeUpdate();
    }

    public void DisableTeleports(String uuid) throws SQLException {
        String sql = "UPDATE " + TablesPrefix + "players SET is_teleportable = 0 WHERE uuid = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, uuid);
        stmt.executeUpdate();
    }

    public void EnableTeleports(String uuid) throws SQLException {
        String sql = "UPDATE " + TablesPrefix + "players SET is_teleportable = 1 WHERE uuid = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, uuid);
        stmt.executeUpdate();
    }

    public dev.mqrio.squishycorefriends.models.Player GetPlayer(String uuid, Integer id) throws SQLException {
        String sql;
        if(id <= 0 ) {
            sql = "SELECT * FROM " + TablesPrefix + "players WHERE uuid = ?;";
        } else {
            sql = "SELECT * FROM " + TablesPrefix + "players WHERE id = ?;";
        }
        PreparedStatement stmt = connection.prepareStatement(sql);
        if(id <= 0 ) {
            stmt.setString(1, uuid);
        } else {
            stmt.setInt(1, id);
        }
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            dev.mqrio.squishycorefriends.models.Player player = new dev.mqrio.squishycorefriends.models.Player();
            player.id = results.getInt("id");
            player.uuid = results.getString("uuid");
            player.username = results.getString("username");

            Boolean isAccepting = true;
            Boolean isHuggable = true;
            Boolean isTeleportable = true;
            if( results.getInt("is_accepting") <= 0 ) {
                isAccepting = false;
            }
            if( results.getInt("is_huggable") <= 0 ) {
                isHuggable = false;
            }
            if( results.getInt("is_teleportable") <= 0 ) {
                isTeleportable = false;
            }
            player.isAccepting = isAccepting;
            player.isHuggable = isHuggable;
            player.isTeleportable = isTeleportable;
            player.maxFriends = results.getInt("max_friends");

            player.LastPosX = results.getDouble("lastpos_x");
            player.LastPosY = results.getDouble("lastpos_y");
            player.LastPosZ = results.getDouble("lastpos_z");
            player.LastPosWorld = results.getString("lastpos_world");

            return player;
        }

        return new dev.mqrio.squishycorefriends.models.Player();
    }

    public int GetPlayerFriendshipsCount(String uuid) throws SQLException {
        dev.mqrio.squishycorefriends.models.Player player = GetPlayer(uuid, 0);

        String sql = "SELECT COUNT(*) AS recordCount FROM " + TablesPrefix + "friendships WHERE friend1 = ? OR friend2 = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, player.id);
        stmt.setInt(2, player.id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            return results.getInt("recordCount");
        }
        return 0;
    }

    public Friendship GetFriendship(String firstUUID, String secondUUID) throws SQLException {
        dev.mqrio.squishycorefriends.models.Player firstPlayer = GetPlayer(firstUUID, 0);
        dev.mqrio.squishycorefriends.models.Player secondPlayer = GetPlayer(secondUUID, 0);

        String sql = "SELECT * FROM " + TablesPrefix + "friendships WHERE friend1 = ? AND friend2 = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, firstPlayer.id);
        stmt.setInt(2, secondPlayer.id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            Friendship friendship = new Friendship();

            friendship.friendshipID = results.getInt("id");

            friendship.firstPlayerUsername = firstPlayer.username;
            friendship.firstPlayerUUID = firstPlayer.uuid;
            friendship.secondPlayerUsername = secondPlayer.username;
            friendship.secondPlayerUUID = secondPlayer.uuid;

            friendship.ActiveSince = results.getLong("since");
            friendship.HugsCount = results.getInt("hugs_count");

            friendship.HomePosX = results.getDouble("home_x");
            friendship.HomePosY = results.getDouble("home_y");
            friendship.HomePosZ = results.getDouble("home_z");
            friendship.HomeWorld = results.getString("home_world");

            return friendship;
        }

        sql = "SELECT * FROM " + TablesPrefix + "friendships WHERE friend1 = ? AND friend2 = ?;";
        PreparedStatement stmtAttempt2 = connection.prepareStatement(sql);
        stmtAttempt2.setInt(1, secondPlayer.id);
        stmtAttempt2.setInt(2, firstPlayer.id);
        ResultSet resultsAttempt2 = stmtAttempt2.executeQuery();
        if (resultsAttempt2.next()) {
            Friendship friendship = new Friendship();

            friendship.friendshipID = resultsAttempt2.getInt("id");

            // this time the first and second players are inverted because we inverted them in the SQL query
            friendship.firstPlayerUsername = secondPlayer.username;
            friendship.firstPlayerUUID = secondPlayer.uuid;
            friendship.secondPlayerUsername = firstPlayer.username;
            friendship.secondPlayerUUID = firstPlayer.uuid;

            friendship.ActiveSince = resultsAttempt2.getLong("since");
            friendship.HugsCount = resultsAttempt2.getInt("hugs_count");

            friendship.HomePosX = resultsAttempt2.getDouble("home_x");
            friendship.HomePosY = resultsAttempt2.getDouble("home_y");
            friendship.HomePosZ = resultsAttempt2.getDouble("home_z");
            friendship.HomeWorld = resultsAttempt2.getString("home_world");

            return friendship;
        }

        return new Friendship();
    }

    public Map<Integer, Friendship> GetFriendships(String uuid) throws SQLException {
        Map<Integer, Friendship> friendships = new HashMap<Integer, Friendship>();

        dev.mqrio.squishycorefriends.models.Player player = GetPlayer(uuid, 0);

        String sql = "SELECT * FROM " + TablesPrefix + "friendships WHERE friend1 = ? OR friend2 = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, player.id);
        stmt.setInt(2, player.id);
        ResultSet results = stmt.executeQuery();

        Integer i = 0;
        while (results.next()) {
            Friendship friendship = new Friendship();

            friendship.friendshipID = results.getInt("id");

            dev.mqrio.squishycorefriends.models.Player firstPlayer = GetPlayer("", results.getInt("friend1"));
            dev.mqrio.squishycorefriends.models.Player secondPlayer = GetPlayer("", results.getInt("friend2"));

            friendship.firstPlayerUsername = firstPlayer.username;
            friendship.firstPlayerUUID = firstPlayer.uuid;
            friendship.secondPlayerUsername = secondPlayer.username;
            friendship.secondPlayerUUID = secondPlayer.uuid;

            friendship.ActiveSince = results.getLong("since");
            friendship.HugsCount = results.getInt("hugs_count");

            friendship.HomePosX = results.getDouble("home_x");
            friendship.HomePosY = results.getDouble("home_y");
            friendship.HomePosZ = results.getDouble("home_z");
            friendship.HomeWorld = results.getString("home_world");

            friendships.put(i, friendship);

            i++;
        }

        return friendships;
    }

    public int NewFriendship(String firstUUID, String secondUUID) throws SQLException {
        /*
        Return values
          -1: Successful friendship
          0: First player has already reached their max friends limit
          1: Second player has already reached their max friends limit
          2: First player isn't accepting any friend requests
          3: Second player isn't accepting any friend requests
          4: Both players are already friends
         */

        Friendship currentFriendship = GetFriendship(firstUUID, secondUUID);
        if( currentFriendship.firstPlayerUUID != null ) {
            return 4;
        }

        Integer firstPlayerFriendsCount = GetPlayerFriendshipsCount(firstUUID);
        Integer secondPlayerFriendsCount = GetPlayerFriendshipsCount(secondUUID);

        dev.mqrio.squishycorefriends.models.Player firstPlayer = GetPlayer(firstUUID, 0);
        dev.mqrio.squishycorefriends.models.Player secondPlayer = GetPlayer(secondUUID, 0);

        if( firstPlayerFriendsCount >= firstPlayer.maxFriends ) {
            return 0;
        }
        if( secondPlayerFriendsCount >= secondPlayer.maxFriends ) {
            return 1;
        }

        if( !firstPlayer.isAccepting ) {
            return 2;
        }
        if( !secondPlayer.isAccepting ) {
            return 3;
        }

        long currTimestamp = Instant.now().getEpochSecond();

        String sql = "INSERT IGNORE INTO " + TablesPrefix + "friendships (friend1, friend2, home_x, home_y, home_z, home_world, hugs_count, since) VALUES (?, ?, 0, 0, 0, '', 0, ?);";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, firstPlayer.id);
        stmt.setInt(2, secondPlayer.id);
        stmt.setLong(3, currTimestamp);
        stmt.executeUpdate();

        return -1;
    }

    public int EndFriendship(String firstUUID, String secondUUID) throws SQLException {
        /*
        Return values
          -1: Success
          0: Both players aren't friends
         */

        Friendship currentFriendship = GetFriendship(firstUUID, secondUUID);
        if( currentFriendship.firstPlayerUUID == null ) {
            return 0;
        }

        String sql = "DELETE FROM " + TablesPrefix + "friendships WHERE id = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, currentFriendship.friendshipID);
        stmt.executeUpdate();

        return -1;
    }

    public int SetFriendshipHomeLocation(String firstUUID, String secondUUID, Location location) throws SQLException {
        /*
        Return values
          -1: Success
          0: Both players aren't friends
         */

        Friendship currentFriendship = GetFriendship(firstUUID, secondUUID);
        if( currentFriendship.firstPlayerUUID == null ) {
            return 0;
        }

        String sql = "UPDATE " + TablesPrefix + "friendships SET home_x = ?, home_y = ?, home_z = ?, home_world = ? WHERE id = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setDouble(1, location.getX());
        stmt.setDouble(2, location.getY());
        stmt.setDouble(3, location.getZ());
        stmt.setString(4, location.getWorld().getUID().toString());
        stmt.setInt(5, currentFriendship.friendshipID);
        stmt.executeUpdate();

        return -1;
    }

    public int AddFriendshipHug(String firstUUID, String secondUUID) throws SQLException {
        /*
        Return values
          -1: Success
          0: Both players aren't friends
         */

        Friendship currentFriendship = GetFriendship(firstUUID, secondUUID);
        if( currentFriendship.firstPlayerUUID == null ) {
            return 0;
        }

        String sql = "UPDATE " + TablesPrefix + "friendships SET hugs_count = hugs_count + 1 WHERE id = ?;";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, currentFriendship.friendshipID);
        stmt.executeUpdate();

        return -1;
    }
}
