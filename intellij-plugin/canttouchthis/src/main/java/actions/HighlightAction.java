package actions;

import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class HighlightAction extends AnAction {
    public HighlightAction() {
        super("Highlight Live Edits");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("actionPerformed");
        HighlightManager highlightManager = HighlightManager.getInstance(e.getProject());


        // Get required data keys
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);

        if (editor == null) {
            System.out.println("Editor is null");
            return;
        }

        // TODO What is this doing?
        //Set visibility only in case of existing project and editor and if a selection exists
        e.getPresentation().setEnabledAndVisible(project != null
                && editor != null
                && editor.getSelectionModel().hasSelection());

        final TextAttributes textAttributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EditorColors.LIVE_TEMPLATE_ATTRIBUTES);
        textAttributes.setBackgroundColor(JBColor.decode("#F0A0A0"));
        textAttributes.setEffectType(EffectType.SEARCH_MATCH);
        highlightManager.addRangeHighlight(
                editor,
                0,
                132,
                textAttributes,
                false,
                null);
    }
//
//    @Override
//    public void update(@NotNull AnActionEvent e) {
//        System.out.println("update");
//        // Get required data keys
//        final Project project = e.getProject();
//        final Editor editor = e.getData(CommonDataKeys.EDITOR);
//        final HighlightManager highlightManager = HighlightManager.getInstance(e.getProject());
//
//        if (editor == null) {
//            System.out.println("Editor is null");
//            return;
//        }
//
//        // TODO What is this doing?
//        //Set visibility only in case of existing project and editor and if a selection exists
//        e.getPresentation().setEnabledAndVisible(project != null
//                && editor != null
//                && editor.getSelectionModel().hasSelection());
//
//        final TextAttributes textAttributes = EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EditorColors.LIVE_TEMPLATE_ATTRIBUTES);
//        textAttributes.setBackgroundColor(JBColor.WHITE);
//        highlightManager.addRangeHighlight(
//                editor,
//                0,
//                100,
//                textAttributes,
//                false,
//                null);
//    }
}
