package components;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.codeInsight.highlighting.HighlightManagerImpl;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.editor.event.EditorEventMulticaster;
import com.intellij.openapi.editor.ex.EditorEventMulticasterEx;
import com.intellij.openapi.editor.ex.FocusChangeListener;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import data.Config;
import data.DataLoader;
import data.model.Change;
import data.model.File;
import org.jetbrains.annotations.NotNull;
import resources.Colors;

import java.util.HashSet;
import java.util.Set;


public class HighlightComponent implements ProjectComponent, FocusChangeListener, CaretListener {
    private final static int UPDATE_INTERVAL = 2000;

    private Config config;
    private DataLoader dataLoader;
    private Set<Project> projects;
    private Editor currentEditor;
    private long lastUpdate;

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
                multicaster.addCaretListener(this, p);
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
        System.out.println("Updating Highlights");

        this.currentEditor = editor;
        this.dataLoader.reload();

        // Use Data
        final Document document = editor.getDocument();
        final VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        String fileName = null;
        try {
            fileName = file.getPath().substring(this.config.gitBasePath.length() + 1);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Trying to get file: " + fileName);
        File cttFileInformation = this.dataLoader.getFile(fileName);

        if (cttFileInformation == null) return;

        // Get required data keys
        final Project project = editor.getProject();
        final HighlightManager highlightManager = HighlightManager.getInstance(project);
        final TextAttributes textAttributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EditorColors.LIVE_TEMPLATE_ATTRIBUTES);
        textAttributes.setEffectType(EffectType.SEARCH_MATCH);

        final RangeHighlighter[] highlighters =
                ((HighlightManagerImpl) HighlightManager.getInstance(project)).getHighlighters(
                        editor);
        for (RangeHighlighter highlighter : highlighters) {
            highlightManager.removeSegmentHighlighter(editor, highlighter);
        }

        for (int i = 0; i < cttFileInformation.changes.length; ++i) {
            Change c = cttFileInformation.changes[i];
            if (!this.config.user.equals(c.email)) {
                TextAttributes newAttributes = textAttributes.clone();
                newAttributes.setBackgroundColor(Colors.userColors[i % Colors.userColors.length]);

                for (int line : c.lines)
                    highlightManager.addRangeHighlight(
                            editor,
                            document.getLineStartOffset(line - 1),
                            document.getLineEndOffset(line - 1) + 1,
                            newAttributes,
                            false,
                            null);
            }
        }

    }

    @Override
    public void focusLost(@NotNull Editor editor) {
    }

    @Override
    public void caretPositionChanged(@NotNull CaretEvent event) {
        if (this.currentEditor != null && System.currentTimeMillis() - this.lastUpdate > UPDATE_INTERVAL) {

            this.focusGained(this.currentEditor);

            this.lastUpdate = System.currentTimeMillis();
        }
    }
}
