package com.jcm.statistics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.ztools.conf.Environment;

public class Util {

    public static String NO_UPDATE = "_NU_";
    
    public static String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public static String START_FLG = "_S_";
    
    public static String TAIL_FLG = "_T_";

    public static Properties p = new Properties();
    
    public static void initResource() throws IOException {
        InputStream inputStream = new FileInputStream(new File(Environment.getContext()+"conf/jcm.properties"));
        p.load(inputStream);
    }
    
    public static String getRemoteTime() throws JSchException, IOException, InterruptedException {

        String command = p.getProperty("date_cmd");
        return execCmd(command);
    }
    
    public static String execCmd(String cmd) throws JSchException, IOException, InterruptedException {
        Session session = null;
        Channel channel = null;
        
        String user = p.getProperty("user");
        String password = p.getProperty("password");
        String host = p.getProperty("host");
        int port = Integer.valueOf(p.getProperty("port"));
        String time = "";
        try {
            JSch jsch = new JSch();

            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(cmd);
            InputStream in = channel.getInputStream();
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    time = new String(tmp, 0, i);
                }
                if (channel.isClosed()) {
                    break;
                }
                Thread.sleep(1000);
            }
            return time;
        } finally {
            channel.disconnect();
            session.disconnect();
        }
    }
    
    public static int getVersion(String prefix) {
        int version = 0;
        int i = prefix.lastIndexOf('_');
        String dir = prefix.substring(i + 1, i + 5);
        while (new File(p.getProperty("dir_output") + dir, prefix + "_" + version).exists()) version++;
        return version == 0 ? 0 : --version;
    }
}
