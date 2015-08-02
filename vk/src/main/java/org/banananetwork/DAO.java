package org.banananetwork;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author karyakin dmitry
 *         date 30.07.15.
 */
public class DAO {

    private BasicDataSource ds;

    public DAO() {
        ds = new BasicDataSource();
        ds.setUsername("friends");
        ds.setPassword("friends");
        ds.setUrl("jdbc:postgresql://localhost:5432/friends");
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setDefaultAutoCommit(false);
    }

    public List<User> fetchUsers(List<Long> ids) {
        List<User> users = new ArrayList<>();
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement() ) {
            String sql = "select * from users where id in (";
            boolean first = true;
            for (long id: ids) {
                if (first) {
                    first = false;
                } else {
                    sql += ", ";
                }
                sql += Long.toString(id);
            }
            sql += ")";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getLong(1));
                u.setPic(rs.getString(2));
                u.setName(rs.getString(3));
                int gender = rs.getInt(4);
                u.setGender(gender == 1 ? Friend.Gender.FEMALE : (gender == 2 ? Friend.Gender.MALE : Friend.Gender.OTHER));
                u.setWorkplace(rs.getString(5));
                u.setUniversityId(rs.getLong(6));
                u.setLastModified(rs.getTimestamp(7));
                u.setLastScan(rs.getTimestamp(8));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public List<User> fetchAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement() ) {
            String sql = "select * from users";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getLong(1));
                u.setPic(rs.getString(2));
                u.setName(rs.getString(3));
                int gender = rs.getInt(4);
                u.setGender(gender == 1 ? Friend.Gender.FEMALE : (gender == 2 ? Friend.Gender.MALE : Friend.Gender.OTHER));
                u.setWorkplace(rs.getString(5));
                u.setUniversityId(rs.getLong(6));
                u.setLastModified(rs.getTimestamp(7));
                u.setLastScan(rs.getTimestamp(8));
                users.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void updateLastScan(long userId, Timestamp lastScan) {
        try (Connection conn = ds.getConnection() ) {
            String sql = "update users set last_scan = ? where id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, lastScan);
            ps.setLong(2, userId);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User fetchUser(long id) {
        User u = null;
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement() ) {
            String sql = "select * from users where id = " + Long.toString(id);
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                u = new User();
                u.setId(rs.getLong(1));
                u.setPic(rs.getString(2));
                u.setName(rs.getString(3));
                int gender = rs.getInt(4);
                u.setGender(gender == 1 ? Friend.Gender.FEMALE : (gender == 2 ? Friend.Gender.MALE : Friend.Gender.OTHER));
                u.setWorkplace(rs.getString(5));
                u.setUniversityId(rs.getLong(6));
                u.setLastModified(rs.getTimestamp(7));
                u.setLastScan(rs.getTimestamp(8));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return u;
    }

    public void insertUsers(List<User> users) {
        try (Connection conn = ds.getConnection() ) {
            String sql = "insert into users(id, pic, name, gender, workplace, university_id, last_updated, last_scan) values (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            for (User u: users) {
                ps.setLong(1, u.getId());
                ps.setString(2, u.getPic());
                ps.setString(3, u.getName());
                ps.setInt(4, u.getGender() == Friend.Gender.FEMALE ? 1 : (u.getGender() == Friend.Gender.MALE ? 2 : 3));
                ps.setString(5, u.getWorkplace());
                ps.setLong(6, u.getUniversityId());
                ps.setTimestamp(7, u.getLastModified());
                ps.setTimestamp(8, u.getLastScan());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUsers(List<User> users) {
        try (Connection conn = ds.getConnection() ) {
            String sql = "update users set pic = ?, name = ?, gender = ?, workplace = ?, university_id = ?, last_updated = ? where id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            for (User u: users) {
                ps.setString(1, u.getPic());
                ps.setString(2, u.getName());
                ps.setInt(3, u.getGender() == Friend.Gender.FEMALE ? 1 : (u.getGender() == Friend.Gender.MALE ? 2 : 3));
                ps.setString(4, u.getWorkplace());
                ps.setLong(5, u.getUniversityId());
                ps.setTimestamp(6, u.getLastModified());
                ps.setLong(7, u.getId());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Long> fetchFriends(long userId) {
        List<Long> friendIds = new ArrayList<>();
        try (Connection conn = ds.getConnection()) {
            String sql = "select friend_id from friends where owner_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                friendIds.add(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendIds;
    }

    public void deleteFriends(long ownerId, List<Long> friendIds) {
        try (Connection conn = ds.getConnection() ) {
            String sql = "delete from friends where owner_id = " + Long.toString(ownerId) + " and friend_id in (";
            boolean first = true;
            for (long id: friendIds) {
                if (first) {
                    first = false;
                } else {
                    sql += ", ";
                }
                sql += Long.toString(id);
            }
            sql += ")";
            Statement st = conn.createStatement();
            st.executeUpdate(sql);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertFriends(List<CommonFriend> friends) {
        try (Connection conn = ds.getConnection() ) {
            String sql = "insert into friends(owner_id, friend_id, group_id, last_updated) values (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            for (CommonFriend f: friends) {
                ps.setLong(1, f.getOwnerId());
                ps.setLong(2, f.getFriendId());
                ps.setLong(3, f.getGroupId());
                ps.setTimestamp(4, f.getLastUpdated());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateFriends(List<CommonFriend> friends) {
        try (Connection conn = ds.getConnection() ) {
            String sql = "update friends set group_id = ?, last_updated = ? where owner_id = ? and friend_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            for (CommonFriend f: friends) {
                ps.setLong(1, f.getGroupId());
                ps.setTimestamp(2, f.getLastUpdated());
                ps.setLong(3, f.getOwnerId());
                ps.setLong(4, f.getFriendId());
                ps.addBatch();
            }
            ps.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<CommonFriend> fetchCommonFriends() {
        List<CommonFriend> commons = new ArrayList<>();
        try (Connection conn = ds.getConnection(); Statement st = conn.createStatement() ) {
            String sql = "select * from friends";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                CommonFriend common = new CommonFriend();
                common.setOwnerId(rs.getLong(2));
                common.setFriendId(rs.getLong(3));
                common.setGroupId(rs.getLong(4));
                common.setLastUpdated(rs.getTimestamp(5));
                commons.add(common);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commons;
    }

}
