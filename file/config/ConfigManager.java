package elixe.file.config;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import elixe.Elixe;
import elixe.utils.misc.LoggingUtils;

/**
 * Named, shareable module configs stored as JSON files under
 * {@code .minecraft/elixe/configs/}. Each one is the same format as the main
 * {@code modules.json}, so importing/exporting is just copying the file.
 */
public class ConfigManager {

	private final File dir;

	public ConfigManager(File parent) {
		dir = new File(parent, "configs");
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	public List<String> list() {
		List<String> names = new ArrayList<String>();
		File[] files = dir.listFiles();
		if (files != null) {
			for (File f : files) {
				String n = f.getName();
				if (f.isFile() && n.toLowerCase().endsWith(".json")) {
					names.add(n.substring(0, n.length() - 5));
				}
			}
		}
		Collections.sort(names);
		return names;
	}

	public void save(String name) {
		try {
			// full = include keybinds + which modules are toggled on
			Elixe.INSTANCE.FILE_MANAGER.MODULE_CONFIG.saveTo(file(name), true);
		} catch (IOException e) {
			LoggingUtils.out("error saving config: " + name);
		}
	}

	public void load(String name) {
		try {
			Elixe.INSTANCE.FILE_MANAGER.MODULE_CONFIG.loadFrom(file(name), true);
		} catch (IOException e) {
			LoggingUtils.out("error loading config: " + name);
		}
	}

	public void delete(String name) {
		File f = file(name);
		if (f.exists()) {
			f.delete();
		}
	}

	public boolean exists(String name) {
		return file(name).exists();
	}

	public void openFolder() {
		try {
			Desktop.getDesktop().open(dir);
		} catch (Exception e) {
			LoggingUtils.out("could not open the configs folder");
		}
	}

	private File file(String name) {
		return new File(dir, name + ".json");
	}
}
