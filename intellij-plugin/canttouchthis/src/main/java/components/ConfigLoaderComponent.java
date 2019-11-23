package components;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import data.Config;
import data.DataLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO set to reloadable and handle changes
@State(name = "Config", reloadable = false)
public class ConfigLoaderComponent implements PersistentStateComponent<data.Config> {
    @Nullable
    @State(name = "config")
    public Config state;

    public DataLoader dataLoader;

    @Nullable
    @Override
    public Config getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull Config state) {
        System.out.println("Config loaded with values: " + state.user + ", " + state.host);
        this.state = state;
        this.dataLoader= new DataLoader(state);
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
