package com.xtu.plugin.flutter.component.assets.handler;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.xtu.plugin.flutter.utils.AssetUtils;
import com.xtu.plugin.flutter.utils.PluginUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public class AssetFileHandler {

    private final PubSpecFileHandler specFileHandler;

    public AssetFileHandler(PubSpecFileHandler specFileHandler) {
        this.specFileHandler = specFileHandler;
    }

    public void onFileAdded(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        String assetPath = getAssetFilePath(project, virtualFile);
        if (assetPath == null) return;
        specFileHandler.addAsset(project, List.of(assetPath));
    }

    public void onFileRemoved(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        String assetPath = getAssetFilePath(project, virtualFile);
        if (assetPath == null) return;
        File assetFile = new File(virtualFile.getPath());
        String projectPath = PluginUtils.getProjectPath(project);
        //判断是否还存在其他dimension资源,兼容IDEA自动引用处理，需要强制保留assetPath
        if (AssetUtils.hasOtherDimensionAsset(projectPath, assetFile)) {
            specFileHandler.addAsset(project, List.of(assetPath));
        } else {
            specFileHandler.removeAsset(project, assetPath);
        }
    }

    public void onFileChanged(@NotNull Project project,
                              @NotNull VirtualFile virtualFile,
                              @NotNull String oldValue,
                              @NotNull String newValue) {
        String newAssetPath = getAssetFilePath(project, virtualFile);
        if (newAssetPath == null) return;
        String projectPath = PluginUtils.getProjectPath(project);
        File newAssetFile = new File(virtualFile.getPath());
        File oldAssetFile = new File(newAssetFile.getParentFile(), oldValue);
        String oldAssetPath = newAssetPath.replace(newValue, oldValue);
        //判断是否还存在其他dimension资源,兼容IDEA自动引用处理，需要强制保留oldAssetPath
        if (AssetUtils.hasOtherDimensionAsset(projectPath, oldAssetFile)) {
            specFileHandler.addAsset(project, List.of(newAssetPath, oldAssetPath));
        } else {
            specFileHandler.changeAsset(project, oldAssetPath, newAssetPath);
        }
    }

    public void onFileMoved(@NotNull Project project,
                            @NotNull VirtualFile oldFile,
                            @NotNull VirtualFile newFile) {
        String oldAssetPath = getAssetFilePath(project, oldFile);
        String newAssetPath = getAssetFilePath(project, newFile);
        if (oldAssetPath != null && newAssetPath == null) {
            onFileRemoved(project, oldFile);
            return;
        }
        if (newAssetPath == null) return;
        if (oldAssetPath == null) {
            specFileHandler.addAsset(project, List.of(newAssetPath));
            return;
        }
        String projectPath = PluginUtils.getProjectPath(project);
        File oldAssetFile = new File(oldFile.getPath());
        //判断是否还存在其他dimension资源,兼容IDEA自动引用处理，需要强制保留assetPath
        if (AssetUtils.hasOtherDimensionAsset(projectPath, oldAssetFile)) {
            specFileHandler.addAsset(project, List.of(newAssetPath, oldAssetPath));
        } else {
            specFileHandler.changeAsset(project, oldAssetPath, newAssetPath);
        }
    }

    private String getAssetFilePath(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        if (AssetUtils.isAssetFile(project, virtualFile)) {
            String projectPath = PluginUtils.getProjectPath(project);
            File assetFile = new File(virtualFile.getPath());
            return AssetUtils.getAssetPath(projectPath, assetFile);
        }
        return null;
    }
}
