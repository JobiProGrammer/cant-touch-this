package components;

import com.intellij.codeInsight.daemon.impl.EditorTracker;
import com.intellij.codeInsight.daemon.impl.EditorTrackerListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColorsListener;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.event.EditorEventMulticaster;
import com.intellij.openapi.editor.ex.EditorEventMulticasterEx;
import com.intellij.openapi.editor.ex.FocusChangeListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import data.Config;
import data.DataLoader;
import data.model.File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.FocusEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HighlightComponent implements ProjectComponent, FocusChangeListener {
    private Config config;
    private DataLoader dataLoader;
    private Set<Project> projects;

    public HighlightComponent(ConfigLoaderComponent configLoader) {
        this.config = configLoader.state;
        this.dataLoader = configLoader.dataLoader;
        this.projects = new HashSet<Project>();
    }

    @Override
    public void projectOpened() {
        for (Project p : ProjectManager.getInstance().getOpenProjects()) {
            if (!projects.contains(p)) {
                System.out.println("Registering for " + p.getName());
                final EditorEventMulticaster multicaster = EditorFactory.getInstance().getEventMulticaster();
                ((EditorEventMulticasterEx) multicaster).addFocusChangeListener(this, p);
                projects.add(p);
            }
        }
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
    public void focusGained(@NotNull Editor editor) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(editor.getDocument());
        String fileName = file.getName();
        System.out.println("Trying to get file: " + fileName);
        File cttFileInformation = this.dataLoader.getFile(fileName);
    }

    @Override
    public void focusLost(@NotNull Editor editor) {

    }
}
