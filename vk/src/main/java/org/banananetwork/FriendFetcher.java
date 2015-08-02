package org.banananetwork;

import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author karyakin dmitry
 *         date 30.07.15.
 */
public class FriendFetcher {

    public static List<Friend> requestFriends(long userId, String cookie) throws IOException {
        String url = "http://vk.com/al_friends.php?act=load_friends_silent&al=1&gid=0&id=";
        PostMethod initialMethod = new PostMethod(url + Long.toString(userId));

        initialMethod.addRequestHeader("Host", "vk.com");
        initialMethod.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        initialMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        initialMethod.addRequestHeader("Cookie", cookie);

        HttpClient client = new HttpClient();
        client.executeMethod(initialMethod);
        int statusCode = initialMethod.getStatusCode();
        if (statusCode != 200) {
            throw new IllegalStateException("Wrong response code: " + statusCode);
        }
        String resp = initialMethod.getResponseBodyAsString();
      //  System.out.println(resp);
        return parseFriends(resp);
    }

    @SuppressWarnings("unchecked")
    private static List<Friend> parseFriends(String json) {
        int p1 = json.indexOf("{");
        int p2 = json.indexOf("}<!>", p1);
        if (p1 == -1 || p2 == -1) {
            return new ArrayList<>();
        }
        json = json.substring(p1, p2 + 1);

      //  System.out.println(json);

        Gson gson = new Gson();
        Map root = gson.fromJson(json, Map.class);
        if (!root.containsKey("all"))
            return new ArrayList<>();
        List friends = (ArrayList) root.get("all");
        return (List<Friend>) friends.stream().map((f) -> {
            Friend friend = new Friend();
            List<String> fields = (List<String>) f;
            friend.id = Long.parseLong(fields.get(0));
            friend.pic = fields.get(1);
            friend.nickname = fields.get(2);
            friend.gender = (fields.get(3).equals("1")) ? Friend.Gender.FEMALE : ((fields.get(3).equals("2")) ?
                    Friend.Gender.MALE : Friend.Gender.OTHER);
            friend.unknown = Integer.parseInt(fields.get(4));
            friend.name = fields.get(5);
            friend.groupId = Long.parseLong(fields.get(6));
            friend.unknown2 = Long.parseLong(fields.get(7));
            String university = fields.get(8);
            friend.university = university.length() == 0 ? 0 : Long.parseLong(university);
            friend.workplace = fields.get(9);
            return friend;
        }).collect(Collectors.toList());
    }

}
