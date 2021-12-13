package com.xtu.plugin.flutter.action.generate.json;

import com.intellij.util.containers.ContainerUtil;
import com.jetbrains.lang.dart.DartComponentType;
import com.jetbrains.lang.dart.ide.generation.BaseCreateMethodsFix;
import com.jetbrains.lang.dart.ide.generation.BaseDartGenerateHandler;
import com.jetbrains.lang.dart.psi.DartClass;
import com.jetbrains.lang.dart.psi.DartComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DartGenerateFromJsonAndToJsonCodeHandler extends BaseDartGenerateHandler {

    private final boolean hasFromJson;
    private final boolean hasToJson;

    public DartGenerateFromJsonAndToJsonCodeHandler(boolean hasFromJson, boolean hasToJson) {
        this.hasFromJson = hasFromJson;
        this.hasToJson = hasToJson;
    }

    @NotNull
    protected String getTitle() {
        return "IFlutter.FromJsonAndToJson";
    }

    @NotNull
    protected BaseCreateMethodsFix<DartComponent> createFix(@NotNull DartClass dartClass) {
        return new CreateFromJsonAndToJsonCodeFix(dartClass, this.hasFromJson, hasToJson);
    }

    protected void collectCandidates(@NotNull DartClass dartClass, @NotNull List<DartComponent> candidates) {
        candidates.addAll(ContainerUtil.findAll(
                this.computeClassMembersMap(dartClass, false).values(),
                (component) -> DartComponentType.typeOf(component) == DartComponentType.FIELD));
    }

    protected boolean doAllowEmptySelection() {
        return true;
    }
}
