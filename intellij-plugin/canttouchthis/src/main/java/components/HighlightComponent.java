package components;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.colors.EditorColorsListener;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import data.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HighlightComponent implements ProjectComponent, EditorColorsListener {
    private Config config;

    public HighlightComponent(ConfigLoaderComponent configLoader) {
        this.config = configLoader.state;
    }

    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "HighlightComponent";
    }

    @Override
    public void globalSchemeChange(@Nullable EditorColorsScheme scheme) {
    }
}
