package com.xtu.plugin.flutter.component.assets.code;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.flutter.service.StorageService;
import com.xtu.plugin.flutter.utils.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DartRFileGenerator {

    private static final DartRFileGenerator sInstance = new DartRFileGenerator();

    private DartRFileGenerator() {
    }

    public static DartRFileGenerator getInstance() {
        return sInstance;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void generate(Project project, List<String> assetList) {
        //create new res
        Map<String, List<String>> assetCategory = new HashMap<>();
        for (String assetFileName : assetList) {
            String assetDirName = assetFileName.substring(0, assetFileName.indexOf("/"));
            if (!assetCategory.containsKey(assetDirName))
                assetCategory.put(assetDirName, new ArrayList<>());
            List<String> assets = assetCategory.get(assetDirName);
            assets.add(assetFileName);
        }
        try {
            //virtual file
            File libDirectory = new File(project.getBasePath(), "lib");
            LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
            final VirtualFile libVirtualDirectory = localFileSystem.refreshAndFindFileByIoFile(libDirectory);
            assert libVirtualDirectory != null;
            VirtualFile resVirtualDirectory = libVirtualDirectory.findChild("res");
            if (assetCategory.size() > 0) {
                if (resVirtualDirectory == null) {
                    resVirtualDirectory = libVirtualDirectory.createChildDirectory(project, "res");
                }
                List<String> usefulFileNameList = new ArrayList<>();
                for (Map.Entry<String, List<String>> entry : assetCategory.entrySet()) {
                    String fileName = generateFile(project, resVirtualDirectory, entry.getKey(), entry.getValue());
                    usefulFileNameList.add(fileName);
                }
                //删除无用文件
                VirtualFile[] childrenFileList = resVirtualDirectory.getChildren();
                if (childrenFileList != null) {
                    for (VirtualFile virtualFile : childrenFileList) {
                        String subFileName = virtualFile.getName();
                        if (usefulFileNameList.contains(subFileName)) continue;
                        virtualFile.delete(project);
                    }
                }
            } else if (resVirtualDirectory != null) {
                //删除无用文件
                resVirtualDirectory.delete(project);
            }
        } catch (Exception e) {
            LogUtils.error("DartRFileGenerator generate: " + e.getMessage());
            ToastUtil.make(project, MessageType.ERROR, e.getMessage());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @NotNull
    private String generateFile(Project project, VirtualFile rDirectory, String assetDirName, List<String> assetFileNames) throws IOException {
        String className = StringUtil.upFirstChar(assetDirName) + "Res";
        StringBuilder fileStringBuilder = new StringBuilder();
        fileStringBuilder.append("/// Generated file. Do not edit.\n\n")
                .append("// ignore_for_file: constant_identifier_names\n")
                .append("// ignore_for_file: lines_longer_than_80_chars\n")
                .append("class ").append(className).append(" {\n");
        if (!CollectionUtils.isEmpty(assetFileNames)) {
            for (String assetFileName : assetFileNames) {
                if (needIgnoreAsset(project, assetFileName)) continue;
                String variantName = getResName(assetFileName);
                fileStringBuilder.append("  static const String ").append(variantName).append(" = '").append(assetFileName).append("';\n");
            }
        }
        fileStringBuilder.append("}\n");
        String fileName = assetDirName.toLowerCase() + "_res.dart";
        DartUtils.createDartFile(project, rDirectory, fileName, fileStringBuilder.toString(), new DartUtils.OnCreateDartFileListener() {
            @Override
            public void onSuccess(@NotNull VirtualFile virtualFile) {
                //ignore
            }

            @Override
            public void onFail(String message) {
                ToastUtil.make(project, MessageType.ERROR, message);
            }
        });
        return fileName;
    }

    private String getResName(String assetFileName) {
        int startIndex = assetFileName.lastIndexOf("/") + 1;
        int endIndex = assetFileName.lastIndexOf(".");
        if (endIndex < startIndex) {
            endIndex = assetFileName.length();
        }
        String variantName = assetFileName.substring(startIndex, endIndex)
                .toUpperCase()
                .replace("-", "_");
        //replace specific char
        variantName = variantName.replace("-", "_");
        return variantName;
    }

    private boolean needIgnoreAsset(Project project, String assetFileName) {
        if (assetFileName.endsWith("/")) return true;
        String extension = getAssetExtension(assetFileName);
        if (StringUtils.isEmpty(extension)) return false;
        StorageService storageService = StorageService.getInstance(project);
        List<String> ignoreResExtension = storageService.getState().ignoreResExtension;
        return ignoreResExtension.contains(extension);
    }

    private String getAssetExtension(String assetFileName) {
        int lastDotIndex = assetFileName.lastIndexOf(".");
        if (lastDotIndex < 0) return null;
        return assetFileName.substring(lastDotIndex);
    }
}
