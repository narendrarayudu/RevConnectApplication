package com.revconnectapp.dao;

import com.revconnectapp.util.ConnectionUtil;
import com.revconnectapp.model.Connection;
import com.revconnectapp.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConnectionDAO {
    
    // ✅ Send connection request
    public boolean createRequest(int fromUserId, int toUserId) {
        String sql = "INSERT INTO connections (user1_id, user2_id, status) VALUES (?, ?, 'PENDING')";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, fromUserId);
            stmt.setInt(2, toUserId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ Keep your existing sendRequest method for compatibility
    public void sendRequest(int senderId, int receiverId) {
        createRequest(senderId, receiverId);
    }
    
    // ✅ Accept connection request
    public boolean updateStatus(int connectionId, String status) {
        String sql = "UPDATE connections SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, connectionId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ Keep your existing acceptRequest method for compatibility
    public void acceptRequest(int userId, int senderId) {
        String sql = "UPDATE connections SET status = 'ACCEPTED', updated_at = CURRENT_TIMESTAMP WHERE " +
                    "((user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)) " +
                    "AND status = 'PENDING'";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setInt(4, senderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // ✅ Get pending requests (returns Connection objects)
    public List<Connection> getPendingRequests(int userId) {
        List<Connection> requests = new ArrayList<>();
        String sql = "SELECT * FROM connections WHERE user2_id = ? AND status = 'PENDING' ORDER BY created_at DESC";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Connection connObj = new Connection();
                connObj.setId(rs.getInt("id"));
                connObj.setUser1Id(rs.getInt("user1_id"));
                connObj.setUser2Id(rs.getInt("user2_id"));
                connObj.setStatus(rs.getString("status"));
                connObj.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                requests.add(connObj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    // ✅ Keep your existing getPendingRequests method for compatibility
    public List<Integer> getPendingRequestsIds(int userId) {
        List<Integer> requests = new ArrayList<>();
        String sql = "SELECT user1_id FROM connections WHERE user2_id = ? AND status = 'PENDING'";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(rs.getInt("user1_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    // ✅ Get all accepted connections for a user
    public List<User> getConnections(int userId) {
        List<User> connections = new ArrayList<>();
        String sql = "SELECT DISTINCT u.* FROM users u " +
                    "INNER JOIN connections c ON (u.id = c.user1_id OR u.id = c.user2_id) " +
                    "WHERE (c.user1_id = ? OR c.user2_id = ?) " +
                    "AND c.status = 'ACCEPTED' " +
                    "AND u.id != ? " +
                    "ORDER BY u.username ASC";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                connections.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connections;
    }
    
    // ✅ Remove a connection
    public boolean removeConnection(int userId1, int userId2) {
        String sql = "DELETE FROM connections WHERE " +
                    "((user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)) " +
                    "AND status = 'ACCEPTED'";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ Check if connection already exists
    public boolean connectionExists(int userId1, int userId2) {
        String sql = "SELECT 1 FROM connections WHERE " +
                    "(user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ Check if two users are connected
    public boolean areConnected(int userId1, int userId2) {
        String sql = "SELECT 1 FROM connections WHERE " +
                    "((user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)) " +
                    "AND status = 'ACCEPTED'";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ Get connection by ID
    public Connection getConnectionById(int connectionId) {
        String sql = "SELECT * FROM connections WHERE id = ?";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, connectionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Connection connObj = new Connection();
                connObj.setId(rs.getInt("id"));
                connObj.setUser1Id(rs.getInt("user1_id"));
                connObj.setUser2Id(rs.getInt("user2_id"));
                connObj.setStatus(rs.getString("status"));
                connObj.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                if (rs.getTimestamp("updated_at") != null) {
                    connObj.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
                return connObj;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // ✅ Get connection between two users
    public Connection getConnectionBetweenUsers(int userId1, int userId2) {
        String sql = "SELECT * FROM connections WHERE " +
                    "(user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Connection connObj = new Connection();
                connObj.setId(rs.getInt("id"));
                connObj.setUser1Id(rs.getInt("user1_id"));
                connObj.setUser2Id(rs.getInt("user2_id"));
                connObj.setStatus(rs.getString("status"));
                connObj.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                if (rs.getTimestamp("updated_at") != null) {
                    connObj.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
                return connObj;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // ✅ NEW: Get connection by custom query
    public Connection getConnectionByQuery(String sql, Object... params) {
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Connection connObj = new Connection();
                connObj.setId(rs.getInt("id"));
                connObj.setUser1Id(rs.getInt("user1_id"));
                connObj.setUser2Id(rs.getInt("user2_id"));
                connObj.setStatus(rs.getString("status"));
                connObj.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                if (rs.getTimestamp("updated_at") != null) {
                    connObj.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
                return connObj;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // ✅ Get sent requests
    public List<Connection> getSentRequests(int userId) {
        List<Connection> requests = new ArrayList<>();
        String sql = "SELECT * FROM connections WHERE user1_id = ? AND status = 'PENDING' ORDER BY created_at DESC";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Connection connObj = new Connection();
                connObj.setId(rs.getInt("id"));
                connObj.setUser1Id(rs.getInt("user1_id"));
                connObj.setUser2Id(rs.getInt("user2_id"));
                connObj.setStatus(rs.getString("status"));
                connObj.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                requests.add(connObj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    // ✅ Delete connection by ID
    public boolean deleteConnection(int connectionId) {
        String sql = "DELETE FROM connections WHERE id = ?";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, connectionId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // ✅ Get connection count
    public int getConnectionCount(int userId) {
        String sql = "SELECT COUNT(*) FROM connections WHERE " +
                    "(user1_id = ? OR user2_id = ?) AND status = 'ACCEPTED'";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // ✅ Get pending requests count
    public int getPendingRequestsCount(int userId) {
        String sql = "SELECT COUNT(*) FROM connections WHERE user2_id = ? AND status = 'PENDING'";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // ✅ NEW: Get sent requests count
    public int getSentRequestsCount(int userId) {
        String sql = "SELECT COUNT(*) FROM connections WHERE user1_id = ? AND status = 'PENDING'";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // ✅ NEW: Get mutual connections between two users
    public List<User> getMutualConnections(int userId1, int userId2) {
        List<User> mutuals = new ArrayList<>();
        String sql = "SELECT DISTINCT u.* FROM users u " +
                    "INNER JOIN connections c1 ON (u.id = c1.user1_id OR u.id = c1.user2_id) " +
                    "INNER JOIN connections c2 ON (u.id = c2.user1_id OR u.id = c2.user2_id) " +
                    "WHERE ((c1.user1_id = ? OR c1.user2_id = ?) AND c1.status = 'ACCEPTED') " +
                    "AND ((c2.user1_id = ? OR c2.user2_id = ?) AND c2.status = 'ACCEPTED') " +
                    "AND u.id NOT IN (?, ?) " +
                    "GROUP BY u.id " +
                    "ORDER BY u.username ASC";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId1);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId2);
            stmt.setInt(5, userId1);
            stmt.setInt(6, userId2);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                mutuals.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mutuals;
    }
    
    // ✅ NEW: Get connection suggestions (friends of friends)
    public List<User> getConnectionSuggestions(int userId) {
        List<User> suggestions = new ArrayList<>();
        String sql = "SELECT DISTINCT u.*, COUNT(*) as mutual_count FROM users u " +
                    "INNER JOIN connections c1 ON (u.id = c1.user1_id OR u.id = c1.user2_id) " +
                    "INNER JOIN connections c2 ON ((c1.user1_id = c2.user2_id AND c1.user2_id = c2.user1_id) OR " +
                    "(c1.user1_id = c2.user1_id AND c1.user2_id = c2.user2_id)) " +
                    "WHERE (c2.user1_id = ? OR c2.user2_id = ?) " +
                    "AND u.id != ? " +
                    "AND u.user_type = 'PERSONAL' " +
                    "AND NOT EXISTS (SELECT 1 FROM connections c3 WHERE " +
                    "((c3.user1_id = ? AND c3.user2_id = u.id) OR (c3.user1_id = u.id AND c3.user2_id = ?))) " +
                    "GROUP BY u.id " +
                    "ORDER BY mutual_count DESC, u.username ASC " +
                    "LIMIT 10";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setInt(4, userId);
            stmt.setInt(5, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                suggestions.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suggestions;
    }
    
    // ✅ NEW: Get all connection requests (both sent and received)
    public List<Connection> getAllConnectionRequests(int userId) {
        List<Connection> allRequests = new ArrayList<>();
        String sql = "SELECT * FROM connections WHERE " +
                    "(user1_id = ? OR user2_id = ?) AND status = 'PENDING' " +
                    "ORDER BY created_at DESC";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Connection connObj = new Connection();
                connObj.setId(rs.getInt("id"));
                connObj.setUser1Id(rs.getInt("user1_id"));
                connObj.setUser2Id(rs.getInt("user2_id"));
                connObj.setStatus(rs.getString("status"));
                connObj.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                allRequests.add(connObj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allRequests;
    }
    
    // ✅ NEW: Get recently connected users
    public List<User> getRecentConnections(int userId, int limit) {
        List<User> recent = new ArrayList<>();
        String sql = "SELECT DISTINCT u.* FROM users u " +
                    "INNER JOIN connections c ON (u.id = c.user1_id OR u.id = c.user2_id) " +
                    "WHERE (c.user1_id = ? OR c.user2_id = ?) " +
                    "AND c.status = 'ACCEPTED' " +
                    "AND u.id != ? " +
                    "ORDER BY c.updated_at DESC LIMIT ?";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setInt(4, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                recent.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recent;
    }
    
    // ✅ NEW: Check if user has any connections
    public boolean hasConnections(int userId) {
        return getConnectionCount(userId) > 0;
    }
    
    // ✅ NEW: Get connection by exact match (user1 = sender, user2 = receiver)
    public Connection getExactConnection(int senderId, int receiverId) {
        String sql = "SELECT * FROM connections WHERE user1_id = ? AND user2_id = ?";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Connection connObj = new Connection();
                connObj.setId(rs.getInt("id"));
                connObj.setUser1Id(rs.getInt("user1_id"));
                connObj.setUser2Id(rs.getInt("user2_id"));
                connObj.setStatus(rs.getString("status"));
                connObj.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                if (rs.getTimestamp("updated_at") != null) {
                    connObj.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
                return connObj;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // ✅ NEW: Get all connections with their connection date
    public List<Object[]> getConnectionsWithDate(int userId) {
        List<Object[]> connectionsWithDate = new ArrayList<>();
        String sql = "SELECT u.*, c.updated_at as connected_date FROM users u " +
                    "INNER JOIN connections c ON (u.id = c.user1_id OR u.id = c.user2_id) " +
                    "WHERE (c.user1_id = ? OR c.user2_id = ?) " +
                    "AND c.status = 'ACCEPTED' " +
                    "AND u.id != ? " +
                    "ORDER BY c.updated_at DESC";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                
                Timestamp connectedDate = rs.getTimestamp("connected_date");
                connectionsWithDate.add(new Object[]{user, connectedDate});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connectionsWithDate;
    }
    
    // ✅ NEW: Get connection statistics
    public int[] getConnectionStatistics(int userId) {
        int[] stats = new int[3]; // [total, sent_pending, received_pending]
        
        // Total connections
        stats[0] = getConnectionCount(userId);
        
        // Sent pending requests
        stats[1] = getSentRequestsCount(userId);
        
        // Received pending requests
        stats[2] = getPendingRequestsCount(userId);
        
        return stats;
    }
    
    // ✅ NEW: Search connections by username
    public List<User> searchConnections(int userId, String searchQuery) {
        List<User> results = new ArrayList<>();
        String sql = "SELECT DISTINCT u.* FROM users u " +
                    "INNER JOIN connections c ON (u.id = c.user1_id OR u.id = c.user2_id) " +
                    "WHERE (c.user1_id = ? OR c.user2_id = ?) " +
                    "AND c.status = 'ACCEPTED' " +
                    "AND u.id != ? " +
                    "AND (u.username LIKE ? OR u.email LIKE ?) " +
                    "ORDER BY u.username ASC";
        try (java.sql.Connection dbConn = ConnectionUtil.getConnection();
             PreparedStatement stmt = dbConn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setString(4, "%" + searchQuery + "%");
            stmt.setString(5, "%" + searchQuery + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setUserType(rs.getString("user_type"));
                results.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
    
}