package annotators;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import data.DataLoader;
import data.model.Change;
import data.model.File;
import data.model.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class HighlightAnnotator extends ExternalAnnotator<HighlightAnnotator.FirstPassData, HighlightAnnotator.SecondPassData> {
    private DataLoader dataLoader;

    public HighlightAnnotator(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Nullable
    @Override
    public FirstPassData collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
        FirstPassData out = new FirstPassData();

        out.file = file;
        System.out.println("Filename: " + file.getName());

        out.editor = editor;

        File loadedInformation =this.dataLoader.getFile(file.getName());
        ArrayList<Integer> sections = new ArrayList<Integer>();
        ArrayList<User> users = new ArrayList<User>();
        for(Change c : loadedInformation.changes){

        }
        return out;
    }

    @Nullable
    @Override
    public SecondPassData doAnnotate(FirstPassData collectedInfo) {
        SecondPassData out = new SecondPassData();
        return out;
    }

    @Override
    public void apply(@NotNull PsiFile file, SecondPassData annotationResult, @NotNull AnnotationHolder holder) {

    }

    public class FirstPassData {
        PsiFile file;
        Editor editor;
        int[] sections;
        // TODO make list of all known users, so colors don't switch randomly
        User[] users;
    }

    public class SecondPassData {
    }
}
