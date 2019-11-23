package components;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import data.Config;

@State(name = "Config", reloadable = true)
public class ConfigLoaderComponent implements PersistentStateComponent<data.Config> {
    @Nullable
    @State(name = "config")
    private Config state;

    @Nullable
    @Override
    public Config getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull Config state) {
        System.out.println("Config loaded with values: " + state.user + ", " + state.host);
        this.state = state;
    }

    @Override
    public void noStateLoaded() {
        System.out.println("No config found");
        this.state = new Config();
        this.state.user = "";
        this.state.host = "";
    }

    @Override
    public void initializeComponent() {

    }
}
