package me.asumji.features;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.asumji.AsuAddons;
import me.asumji.gui.config.ConfigManager;
import me.asumji.util.HTTP;
import me.asumji.util.Shortcuts;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static me.asumji.AsuAddons.GSON;

//I hate this code so much but I'll probably come back to this at some point (I won't except it breaks (which it will)).
public class AutoUpdater {

    //Credit: https://www.digitalocean.com/community/tutorials/java-unzip-file-example
    private static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        if (!dir.exists()) dir.mkdirs();
        byte[] buffer = new byte[1024];
        try (FileInputStream fis = new FileInputStream(zipFilePath);
             ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                if (ze.isDirectory()) {
                    new File(newFile.getParent()).mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void init() {
        if (!ConfigManager.getConfig().mainCategory.downloaded.isEmpty()) {
            List<Path> result = List.of();
            try (Stream<Path> pathStream = Files.find(Path.of(FabricLoader.getInstance().getGameDir().toString()+"/mods/"),
                    Integer.MAX_VALUE,
                    (p, basicFileAttributes) -> p.getFileName().toString().matches("asuaddons-"+AsuAddons.MINECRAFT_VERSION+"-.*\\.jar") &! p.getFileName().toString().equals(ConfigManager.getConfig().mainCategory.downloaded))
            ) {
                result = pathStream.toList();
            } catch (Exception e) {
                e.printStackTrace();
                AsuAddons.LOGGER.info("Failed finding mods to delete.");
            }
            for (Path filePath : result) {
                try {
                    Files.deleteIfExists(filePath.toAbsolutePath());
                    ConfigManager.getConfig().mainCategory.downloaded = "";
                    ConfigManager.saveConfig("AutoUpdater");
                } catch (Exception e) {
                    e.printStackTrace();
                    AsuAddons.LOGGER.info("Failed to delete old mod after downloaded check.");
                }
            }
        }

        if (!ConfigManager.getConfig().mainCategory.autoUpdates) return;
        HTTP.GetRequest("https://api.github.com/repos/Asumji/asuaddons-fabric/actions/artifacts").thenAcceptAsync(res -> {
            JsonArray artifacts = GSON.fromJson(res.body(), JsonObject.class).getAsJsonObject().getAsJsonArray("artifacts");
            if (artifacts.get(0).getAsJsonObject().get("id").getAsString().equals(ConfigManager.getConfig().mainCategory.lastestAction)) return;
            AsuAddons.LOGGER.info("AU > Started Automatic Update.");
            Shortcuts.queueClientMessage(Text.literal(AsuAddons.MOD_PREFIX + "§aA new release has been found. Automatic Update has been started."));
            ConfigManager.getConfig().mainCategory.lastestAction = artifacts.get(0).getAsJsonObject().get("id").getAsString();
            String artifactId = artifacts.get(0).getAsJsonObject().get("id").getAsString();
            try {
                //You don't get my key my raspberry pi is too cool for that.
                URL url = new URL(AsuAddons.API_PROXY+"action?id="+artifactId);
                BufferedInputStream bis = new BufferedInputStream(url.openStream());
                FileOutputStream fis = new FileOutputStream(FabricLoader.getInstance().getGameDir().toString()+"/config/asuaddons/dwnld.zip");
                byte[] buffer = new byte[1024];
                int count;
                while((count = bis.read(buffer,0,1024)) != -1)
                {
                    fis.write(buffer, 0, count);
                }
                fis.close();
                bis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            File zipFile = new File(FabricLoader.getInstance().getGameDir().toString()+"/config/asuaddons/dwnld.zip");
            if (zipFile.exists()) {
                unzip(zipFile.getPath(), FabricLoader.getInstance().getGameDir().toString()+"/config/asuaddons");
                File jarFile = new File(FabricLoader.getInstance().getGameDir().toString()+"/config/asuaddons/asuaddons-"+AsuAddons.MINECRAFT_VERSION+"-9999999.jar");
                if (jarFile.exists()) {
                    try {
                        ConfigManager.getConfig().mainCategory.jarNumber -= 1;
                        String fileNumber = new StringBuilder(String.valueOf(ConfigManager.getConfig().mainCategory.jarNumber)).reverse().toString();
                        String fileName = "asuaddons-"+AsuAddons.MINECRAFT_VERSION+"-"+fileNumber+".jar";
                        Files.copy(jarFile.toPath(), Path.of(FabricLoader.getInstance().getGameDir().toString()+"/mods/"+fileName), StandardCopyOption.REPLACE_EXISTING);
                        ConfigManager.getConfig().mainCategory.downloaded = fileName;
                        Files.deleteIfExists(zipFile.toPath());
                        Files.deleteIfExists(jarFile.toPath());
                        Shortcuts.queueClientMessage(Text.literal(AsuAddons.MOD_PREFIX + "§aSuccessfully downloaded the newest version. Changes will apply on next restart."));
                        AsuAddons.LOGGER.info("AU > Finished Automatic Update.");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Shortcuts.queueClientMessage(Text.literal(AsuAddons.MOD_PREFIX + "§cFailed organizing files. Retrying on next restart. If this keeps happening consider updating to the newest version manually."));
                        ConfigManager.getConfig().mainCategory.lastestAction = "";
                        ConfigManager.getConfig().mainCategory.downloaded = "";
                    }
                } else {
                    Shortcuts.queueClientMessage(Text.literal(AsuAddons.MOD_PREFIX + "§cUnzipping failed. Retrying on next restart. If this keeps happening consider updating to the newest version manually."));
                    ConfigManager.getConfig().mainCategory.lastestAction = "";
                    ConfigManager.getConfig().mainCategory.downloaded = "";
                }
            } else {
                Shortcuts.queueClientMessage(Text.literal(AsuAddons.MOD_PREFIX + "§cThe download failed. Retrying on next restart. If this keeps happening consider updating to the newest version manually."));
                ConfigManager.getConfig().mainCategory.lastestAction = "";
                ConfigManager.getConfig().mainCategory.downloaded = "";
            }
            ConfigManager.saveConfig("AutoUpdater");
        });
    }
}
