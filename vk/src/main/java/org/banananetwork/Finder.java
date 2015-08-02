package org.banananetwork;

import java.util.*;

/**
 * @author karyakin dmitry
 *         date 30.07.15.
 */
public class Finder {

    public static void main(String[] args) {
        new Finder().find(1, 2);
    }

    private DAO dao;

    public Finder() {
        dao = new DAO();
    }

    private void find(long fromUser, long toUser) {
        List<CommonFriend> commonFriends = dao.fetchCommonFriends();

        List<User> users = dao.fetchAllUsers();
        Map<Long, String> names = new HashMap<>();
        users.forEach((u) -> names.put(u.getId(), u.getName()));

        Map<Long, Set<Long>> links = new HashMap<>();

        for (CommonFriend f : commonFriends) {
            long userA = f.getOwnerId();
            long userB = f.getFriendId();

            if (links.containsKey(userA)) {
                links.get(userA).add(userB);
            } else {
                Set<Long> s = new HashSet<>();
                s.add(userB);
                links.put(userA, s);
            }

            if (links.containsKey(userB)) {
                links.get(userB).add(userA);
            } else {
                Set<Long> s = new HashSet<>();
                s.add(userA);
                links.put(userB, s);
            }

        }

        Map<Long, Integer> nFriends = new HashMap<>();
        for (Map.Entry<Long, Set<Long>> e: links.entrySet()) {
            nFriends.put(e.getKey(), e.getValue().size());
        }

        links.put(toUser, new HashSet<>());

        Map<Long, Integer> levels = new HashMap<>();
        Set<Long> currentLevel = new HashSet<>();
        Set<Long> nextLevel = new HashSet<>();
        currentLevel.add(fromUser);

        levels.put(fromUser, 0);

        Map<Long, Set<Long>> parents = new HashMap<>();

        Map<Integer, Set<Long>> ringValue = new HashMap<>();

        for (int ring = 1; ring < 5; ring++) {
            nextLevel.clear();
            ringValue.put(ring, new HashSet<>());
            for (Long id: currentLevel) {
                Set<Long> friends = links.get(id);
                for (Long friendId: friends) {
                    if (!levels.containsKey(friendId)) {
                        levels.put(friendId, ring);
                        nextLevel.add(friendId);
                        Set<Long> parent = new HashSet<>();
                        parent.add(id);
                        parents.put(friendId, parent);
                    } else {
                        if (levels.get(friendId) == ring) {
                            parents.get(friendId).add(id);
                        }
                    }
                    if (friendId == toUser) {
                        ringValue.get(ring).add(id);
                    }
                }
            }
            currentLevel.clear();
            currentLevel.addAll(nextLevel);
        }

        String tmp = names.get(toUser);

        for (int i = 1; i < 5; i++) {
            System.out.println("Ring " + i);
            Set<Long> ringVal = ringValue.get(i);
            for (long parId: ringVal) {
                processParents(names.get(parId) + "(" + nFriends.get(parId) + ")" + " <-> " + tmp, names, parents, nFriends, parId);
            }
        }

    }

    void processParents(String tmp, Map<Long, String> names, Map<Long, Set<Long>> parents, Map<Long, Integer> nFriends, long parentId) {
        Set<Long> pars = parents.get(parentId);
        if (pars == null) {
            System.out.println(tmp);
        } else {
            for(long parId: pars) {
                processParents(names.get(parId) + "(" + nFriends.get(parId) + ")" + " <-> " + tmp, names, parents, nFriends, parId);
            }
        }
    }

}
