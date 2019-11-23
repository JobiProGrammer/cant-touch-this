package annotators;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import data.DataLoader;
import data.model.Change;
import data.model.File;
import data.model.Line;
import data.model.User;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class HighlightAnnotator extends ExternalAnnotator<HighlightAnnotator.FirstPassData, HighlightAnnotator.SecondPassData> {
    private DataLoader dataLoader;

    public HighlightAnnotator(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Nullable
    @Override
    public FirstPassData collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
        System.out.println("collectInfo");
        FirstPassData out = new FirstPassData();

        out.file = file;
        System.out.println("Filename: " + file.getName());

        out.editor = editor;

        return out;
    }

    @Nullable
    @Override
    public SecondPassData doAnnotate(FirstPassData collectedInfo) {
        System.out.println("doAnnotate");
        SecondPassData out = new SecondPassData();
        out.file = collectedInfo.file;
        out.editor = collectedInfo.editor;

        // TODO
        File loadedInformation = this.dataLoader.getFile("file.getName()");
        ArrayList<Integer> sections = new ArrayList<Integer>();
        ArrayList<User> users = new ArrayList<User>();

        for (Change c : loadedInformation.changes) {
            // TODO test this
            Arrays.sort(c.lines);
            sections.add(c.lines[0].line);
            sections.add(c.lines[c.lines.length - 1].line);
            users.add(c.user);
        }

        out.sections = ArrayUtils.toPrimitive((Integer[]) sections.toArray());
        out.users = (User[]) users.toArray();

        return out;
    }

    @Override
    public void apply(@NotNull PsiFile file, SecondPassData annotationResult, @NotNull AnnotationHolder holder) {
        System.out.println("apply");
        Project project = annotationResult.editor.getProject();
        if (project == null) {
            System.out.println("Project is null");
            return;
        }
        HighlightManager highlightManager = HighlightManager.getInstance(project);
        Editor editor = annotationResult.editor;
        if (editor == null) {
            System.out.println("Editor is null");
            return;
        }

        for (int i = 0; i < annotationResult.sections.length / 2; ++i) {
            holder.createWeakWarningAnnotation(
                    new TextRange(annotationResult.sections[i * 2], annotationResult.sections[i * 2 + 1]),
                    "Test: " + annotationResult.users[i]);
        }

//        final TextAttributes textAttributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EditorColors.LIVE_TEMPLATE_ATTRIBUTES);
//        textAttributes.setBackgroundColor(JBColor.decode("#F0A0A0"));
//        textAttributes.setEffectType(EffectType.SEARCH_MATCH);
//        highlightManager.addRangeHighlight(
//                editor,
//                0,
//                132,
//                textAttributes,
//                false,
//                null);
    }

    public class FirstPassData {
        PsiFile file;
        Editor editor;
    }

    public class SecondPassData {
        PsiFile file;
        Editor editor;
        // Sections and Users are sorted to match
        // Sections contains ints, alternating between starting and ending line
        // Users contains the associated users
        int[] sections;
        // TODO make list of all known users, so colors don't switch randomly
        User[] users;
    }
}
