package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ConfigAction extends AnAction {
    public ConfigAction() {
        super("Can't Touch This");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
    }

    public class ConfigDialogWrapper extends DialogWrapper{

        public ConfigDialogWrapper(){
            super(true);
            init();
            setTitle("Can't Touch This Configuration");
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel dialogPanel = new JPanel(new BorderLayout());
            JLabel labelName = new JLabel("Username");
            dialogPanel.add(labelName,BorderLayout.AFTER_LAST_LINE);
            return dialogPanel;
        }
    }
}
