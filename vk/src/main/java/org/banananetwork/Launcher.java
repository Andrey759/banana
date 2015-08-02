package org.banananetwork;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author karyakin dmitry
 *         date 30.07.15.
 */
public class Launcher {

    private static final String COOKIE = "";

    private DAO dao;

    private Random rnd = new Random();

    public static void main(String[] args) throws IOException {

        Launcher launcher = new Launcher();

        long userId = 123456;

        launcher.scanUserFriends(userId);

   //    launcher.scanFriends(userId);

        System.out.println("done");
    }

    public Launcher() {
        dao = new DAO();
    }



    private boolean scanUserFriends(long id) throws IOException {

        long now = new Date().getTime();

        User user = dao.fetchUser(id);
        if (user != null) {
            Timestamp lastScan = user.getLastScan();
            if (lastScan != null) {
                long diff = now - lastScan.getTime();
                // week
                if (diff < 1000 * 7 * 86400) {
                    System.out.println("User " + id + " was scanned recently, skip");
                    return false;
                }
            }
        }
        System.out.println("Scan user " + id);

        List<Friend> friends = FriendFetcher.requestFriends(id, COOKIE);

        if (friends.size() == 0) {
            return true;
        }

        List<Long> ids = friends.stream().map((f) -> f.id).collect(Collectors.toList());
        List<Long> existing = dao.fetchUsers(ids).stream().map(User::getId).collect(Collectors.toList());

        List<User> toUpdate = new ArrayList<>();
        List<User> toInsert = new ArrayList<>();
        Timestamp current = new Timestamp(now);
        friends.forEach((f) -> {
            User u = new User();
            u.setGender(f.gender);
            u.setId(f.id);
            u.setLastModified(current);
            u.setName(f.name);
            u.setPic(f.pic);
            u.setUniversityId(f.university);
            u.setWorkplace(f.workplace);
            if (existing.contains(f.id)) {
                toUpdate.add(u);
            } else {
                toInsert.add(u);
            }
        });

        if (toUpdate.size() > 0) {
            dao.updateUsers(toUpdate);
        }
        if (toInsert.size() > 0) {
            dao.insertUsers(toInsert);
        }

        List<Long> currentFriends = dao.fetchFriends(id);

        List<CommonFriend> friendsToUpdate = new ArrayList<>();
        List<CommonFriend> friendsToAdd = new ArrayList<>();

        List<Long> newFriends = friends.stream().map((f) -> f.id).collect(Collectors.toList());
        List<Long> friendsToDelete = currentFriends.stream().filter((f) -> !newFriends.contains(f)).collect(Collectors.toList());

        friends.forEach((f) -> {
            CommonFriend common = new CommonFriend();
            common.setOwnerId(id);
            common.setFriendId(f.id);
            common.setGroupId(f.groupId);
            common.setLastUpdated(current);
            if (currentFriends.contains(f.id)) {
                friendsToUpdate.add(common);
            } else {
                friendsToAdd.add(common);
            }
        });

        if (friendsToDelete.size() > 0) {
            dao.deleteFriends(id, friendsToDelete);
        }
        if (friendsToAdd.size() > 0) {
            dao.insertFriends(friendsToAdd);
        }
        if (friendsToUpdate.size() > 0) {
            dao.updateFriends(friendsToUpdate);
        }

        if (user != null) {
            dao.updateLastScan(id, current);
        }

        return true;

    }

    private void scanFriends(long id) throws IOException {

        boolean actualScan = scanUserFriends(id);

        List<Long> friends = dao.fetchFriends(id);

        int total = friends.size();

        if (actualScan) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int count = 1;

        for (long friendId: friends) {
            System.out.print(count++ + " of " + total + ": ");
            if (scanUserFriends(friendId)) {
                try {
                    Thread.sleep(3500 + rnd.nextInt(3000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
