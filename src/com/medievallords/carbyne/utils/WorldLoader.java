package com.medievallords.carbyne.utils;

import com.medievallords.carbyne.Carbyne;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by WE on 2017-09-27.
 */

public class WorldLoader {

    static Creator creator;

    static {
        creator = new Creator();
    }

    public static World createWorld(World template, int id) {
        return creator.createWorld(template, id);
    }

    public static void copyWorld(File source, File target) {
                try {
                    ArrayList<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.lock", "data"));
                    if (!ignore.contains(source.getName())) {
                        if (source.isDirectory()) {
                            if (!target.exists())
                                target.mkdirs();

                            String files[] = source.list();

                            if (files != null) {
                                for (String file : files) {
                                    File srcFile = new File(source, file);
                                    File destFile = new File(target, file);
                                    copyWorld(srcFile, destFile);
                                }
                            }
                        } else {
                            InputStream in = new FileInputStream(source);
                            OutputStream out = new FileOutputStream(target);
                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = in.read(buffer)) > 0)
                                out.write(buffer, 0, length);
                            in.close();
                            out.close();
                        }
                    }
                } catch (IOException ignored) {}
    }

    public static void deleteWorld(File path) {
        try {
            FileUtils.deleteDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
