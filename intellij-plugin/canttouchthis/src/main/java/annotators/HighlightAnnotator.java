package annotators;

import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;

public class HighlightAnnotator extends ExternalAnnotator<HighlightAnnotator.InitialInformation, Object> {

    public class InitialInformation {
        PsiFile file;
        Editor editor;
        int[] sections;
    }
}
