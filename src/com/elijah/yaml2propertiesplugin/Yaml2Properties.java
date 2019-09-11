package com.elijah.yaml2propertiesplugin;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Description:
 *
 * @author elijahliu
 * @Note Talk is cheap,just show me ur code.- -!
 * ProjectName:Yaml2PropertiesPlugin
 * PackageName: com.elijah.yaml2propertiesplugin
 * Date: 2019-09-10 15:53
 */
public class Yaml2Properties extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile file = DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        String path = file.getPath();
        if (!path.endsWith(".yml")) {
            throw new RuntimeException("This is not a yaml file");
        }
        YamlReader reader = null;
        try {
            reader = new YamlReader(new FileReader(path));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("loading yaml file error");
        }
        FileOutputStream fileOutputStream = null;
        try {
            Object yamlObject = reader.read();
            Properties properties = new Properties();
            buildProperties(properties, yamlObject, new String());
            File propertiesFile = new File(path.replace(".yml", ".properties"));
            propertiesFile.deleteOnExit();
            propertiesFile.createNewFile();
            fileOutputStream = new FileOutputStream(propertiesFile);
            properties.store(fileOutputStream,null);
            fileOutputStream.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException ex) {
                throw new RuntimeException("file stream close fail.");
            }
        }
    }
    private static void buildProperties(Properties properties, Object yaml, String str) {
        if (yaml == null || yaml instanceof String) {
            str = str.substring(0, str.length() - 1);
            if (yaml == null || "".equals(((String) yaml).trim())) {
                properties.setProperty(str, "");
            } else {
                properties.setProperty(str, yaml.toString());
            }
            return;
        }
        Map<String, Object> map = (Map<String, Object>) yaml;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            buildProperties(properties, entry.getValue(), str + entry.getKey() + ".");
        }

    }
}
