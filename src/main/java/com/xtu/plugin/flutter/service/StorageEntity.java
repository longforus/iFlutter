package com.xtu.plugin.flutter.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StorageEntity {

    //资源变化检测目录，用于实时生成R文件
    public List<String> resDir = Arrays.asList("assets", "images");
    //图片优化检测目录，用于根据图片名进行目录分类
    public List<String> imageDir = Collections.singletonList("images");
    //根据资源文件后缀，屏蔽R中某些字段的生成
    public List<String> ignoreResExtension = Collections.emptyList();
    //是否打开flutter2.0
    public boolean flutter2Enable = true;

    public StorageEntity() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorageEntity that = (StorageEntity) o;
        return flutter2Enable == that.flutter2Enable
                && Objects.equals(resDir, that.resDir)
                && Objects.equals(imageDir, that.imageDir)
                && Objects.equals(ignoreResExtension, that.ignoreResExtension);
    }

    @Override
    public String toString() {
        return "StorageEntity{" +
                "resDir=" + resDir +
                ", imageDir=" + imageDir +
                ", ignoreResExtension=" + ignoreResExtension +
                ", flutter2Enable=" + flutter2Enable +
                '}';
    }
}
