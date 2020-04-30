package com.lee233.uuidconverter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Player {
    String username;
    UUID offlineUUID;
    String onlineUUID;

    //Constructor method
    public Player(String playerUsername) throws IOException {
        username = playerUsername;
        offlineUUID = usernameToOfflineUUID();
        onlineUUID = usernameToOnlineUUID();
    }

    public UUID usernameToOfflineUUID() {
        UUID playerOfflineUUID = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes());
        return playerOfflineUUID;
    }

    public String usernameToOnlineUUID() throws IOException {
        //credit: https://www.baeldung.com/java-http-request
        URL mojangAPIurl = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
        HttpURLConnection con = (HttpURLConnection) mojangAPIurl.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        Reader streamReader = null;
        if ( status > 299 ) {
            streamReader = new InputStreamReader(con.getErrorStream());
        } else {
            streamReader = new InputStreamReader(con.getInputStream());
        }
        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        //System.out.println(content.toString());
        String playerOnlineUUID = content.toString();
        playerOnlineUUID = playerOnlineUUID.substring(playerOnlineUUID.indexOf("id\":\""), playerOnlineUUID.indexOf("\"}")).replace("id\":\"", "");
        playerOnlineUUID = playerOnlineUUID.substring(0,8).concat("-").concat(playerOnlineUUID.substring(8,12)).concat("-").concat(playerOnlineUUID.substring(12,16)).concat("-").concat(playerOnlineUUID.substring(16,32));
        con.disconnect();
        return playerOnlineUUID;
    }

    //Operate according to mode selection
    public static void convert(String mode, String playerUsername) throws IOException {
        Player player = new Player(playerUsername);
        if (mode.equals("1")) {
            System.out.println(playerUsername + "的离线uuid是：" + player.offlineUUID.toString());
        } else if (mode.equals("2")) {
            System.out.println(playerUsername + "的正版uuid是：" + player.onlineUUID);
        } else if (mode.equals("3")) {
            System.out.println(playerUsername + "的离线uuid是：" + player.offlineUUID.toString());
            System.out.println(playerUsername + "的正版uuid是：" + player.onlineUUID);
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("请选择模式：（输入序号）");
        System.out.println("1. 玩家名 --> 离线UUID");
        System.out.println("2. 玩家名 --> 正版UUID");
        System.out.println("3. 我全都要！");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String mode = br.readLine();
        if (args.length == 0) {
            while (true) {
                BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("请输入玩家名(username)(输入quit退出)");
                String playerUsername = (String) br2.readLine();
                if (playerUsername.equals("quit")) { break;}
                convert(mode, playerUsername);
            }
        } else {
            for (int i = 0; i < args.length; i++) {
                convert(mode, args[i]);
            }
        }
    }
}
