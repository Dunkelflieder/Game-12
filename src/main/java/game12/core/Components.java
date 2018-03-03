package game12.core;

import de.nerogar.noise.serialization.NDSFile;
import de.nerogar.noise.serialization.NDSNodeObject;
import de.nerogar.noise.serialization.NDSReader;
import game12.Game12;
import game12.core.map.Component;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Components {

	private static Map<String, String>                     classNames       = new HashMap<>();
	private static Map<String, Class<? extends Component>> componentClasses = new HashMap<>();

	private static List<Class<? extends Component>>       componentFromID = new ArrayList<>();
	private static Map<Class<? extends Component>, Short> idFromComponent = new HashMap<>();

	public static void init() {
		NDSFile file;

		try {
			file = NDSReader.readJsonFile("data/components.json");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		NDSNodeObject[] coreArray = file.getData().getObjectArray("core");
		NDSNodeObject[] serverArray = file.getData().getObjectArray("server");
		NDSNodeObject[] clientArray = file.getData().getObjectArray("client");

		initArray("core", coreArray);
		initArray("server", serverArray);
		initArray("client", clientArray);

		// init IDs for core components
		for (NDSNodeObject ndsNodeObject : coreArray) {
			String componentName = ndsNodeObject.getStringUTF8("name");
			Class<? extends Component> componentClass = getComponentClass(componentName);

			componentFromID.add(componentClass);
			idFromComponent.put(componentClass, (short) (componentFromID.size() - 1));
		}

	}

	private static void initArray(String packageName, NDSNodeObject[] array) {
		packageName = Game12.PACKAGE_NAME + "." + packageName + ".components.";

		for (NDSNodeObject ndsNodeObject : array) {
			classNames.put(
					ndsNodeObject.getStringUTF8("name"),
					packageName + ndsNodeObject.getStringUTF8("class")
			              );

		}
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends Component> initSingle(String name) {
		try {
			return (Class<? extends Component>) Class.forName(classNames.get(name));
		} catch (ClassNotFoundException e) {
			e.printStackTrace(Game12.logger.getErrorStream());
			return null;
		}
	}

	public static Class<? extends Component> getComponentClass(String name) {
		return componentClasses.computeIfAbsent(name, Components::initSingle);
	}

	public static Class<? extends Component> getComponentByID(short componentID) {
		return componentFromID.get(componentID);
	}

	public static short getIDFromComponent(Class<? extends Component> componentClass) {
		return idFromComponent.get(componentClass);
	}

}
