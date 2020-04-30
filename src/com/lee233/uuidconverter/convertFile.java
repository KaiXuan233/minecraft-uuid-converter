package com.lee233.uuidconverter;

import java.io.*;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class convertFile {
    //create xml files from usernames
    public static void createXML(String[] usernames) throws IOException {
        //generate UUIDs array
        UUID[] offlineUUIDs = new UUID[usernames.length];
        String[] onlineUUIDs = new String[usernames.length];
        //create XML files
        //credit: https://blog.csdn.net/qq_39237801/article/details/78378486
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = factory.newDocumentBuilder();
            Document document = db.newDocument();
            document.setXmlStandalone(true);
            Element playerdata = document.createElement("playerdata");
            for (int i = 0; i < usernames.length; i++) {
                Player player1 = new Player(usernames[i]);
                offlineUUIDs[i] = player1.offlineUUID;
                onlineUUIDs[i] = player1.onlineUUID;
                //System.out.println(usernames[i] + " " + offlineUUIDs[i] + " " + onlineUUIDs[i]);
                Element player = document.createElement("player");
                Element username = document.createElement("username");
                Element offlineUUID = document.createElement("offlineUUID");
                Element onlineUUID = document.createElement("onlineUUID");
                username.setTextContent(usernames[i]);
                offlineUUID.setTextContent(offlineUUIDs[i].toString());
                onlineUUID.setTextContent(onlineUUIDs[i]);
                player.appendChild(username);
                player.appendChild(offlineUUID);
                player.appendChild(onlineUUID);
                playerdata.appendChild(player);
            }
            document.appendChild(playerdata);
            TransformerFactory tff = TransformerFactory.newInstance();
            Transformer tf = tff.newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            tf.transform(new DOMSource(document), new StreamResult(new File("uuids.xml")));
            System.out.println("生成xml文件成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("生成xml文件失败");
        }

    }

    //method for adding elements into String[]
    static String[] add(String arr[], String x) {
        String[] newarr = new String[arr.length+1];
        for (int i = 0; i < arr.length; i++) newarr[i] = arr[i];
        newarr[newarr.length-1] = x;
        return newarr;
    }

    //convert each lines into String[i]
    public static String[] convertFileIntoStringArray(File input) throws IOException {
        FileReader fr = new FileReader(input);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String[] usernames = new String[0];
        while ((line = br.readLine()) != null ) {
            usernames = add(usernames, line);
        }
        return usernames;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("请输入文件路径:");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            File input = new File(br.readLine());
            if (input.isFile()) {
                String[] usernames = convertFileIntoStringArray(input);
                //for (int i = 0; i < usernames.length; i++) System.out.println(usernames[i]);
                createXML(usernames);
            } else {
                System.out.println("文件不存在！");
                System.exit(-1);
            }
        } else if (args.length > 1) {
            System.out.println("仅支持每次一个文件！");
            System.exit(-1);
        } else {
            File input = new File(args[0]);
            String[] usernames = convertFileIntoStringArray(input);
            createXML(usernames);
        }
    }
}
