package com.xtu.plugin.flutter.component.assets.handler;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.xtu.plugin.flutter.component.assets.code.DartRFileGenerator;
import com.xtu.plugin.flutter.utils.LogUtils;
import com.xtu.plugin.flutter.utils.PubspecUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

public class PubSpecFileHandler {

    void addAsset(@NotNull Project project, @NotNull List<String> assetNameList) {
        LogUtils.info("PubSpecFileHandler addAsset: " + assetNameList);
        PubspecUtils.readAsset(project, assetList -> {
            assetList.addAll(assetNameList);
            PubspecUtils.writeAsset(project, assetList);
        });
    }

    void removeAsset(@NotNull Project project, String assetName) {
        LogUtils.info("PubSpecFileHandler removeAsset: " + assetName);
        PubspecUtils.readAsset(project, assetList -> {
            assetList.remove(assetName);
            PubspecUtils.writeAsset(project, assetList);
        });
    }

    void changeAsset(Project project, String oldAssetName, String newAssetName) {
        LogUtils.info(String.format(Locale.ROOT, "PubSpecFileHandler changeAsset(%s -> %s)", oldAssetName, newAssetName));
        PubspecUtils.readAsset(project, assetList -> {
            assetList.remove(oldAssetName);
            assetList.add(newAssetName);
            PubspecUtils.writeAsset(project, assetList);
        });
    }


    public void onPsiFileChanged(PsiFile psiFile) {
        //root package pubspec.yaml changed
        if (PubspecUtils.isRootPubspecFile(psiFile)) {
            LogUtils.info("PubSpecFileHandler pubspec.yaml changed");
            final Project project = psiFile.getProject();
            PubspecUtils.readAsset(project, assetList ->
                    DartRFileGenerator.getInstance().generate(project, assetList));
        }
    }

}
