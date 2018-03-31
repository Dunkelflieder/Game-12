package game12.core.map;

import game12.core.event.EntityDespawnEvent;
import game12.core.event.EntitySpawnEvent;

import java.util.*;

public class EntityList {

	private Map<Integer, Entity>                                    entities;
	private Map<Entity, Map<Class<? extends Component>, Component>> entityComponentMap;
	private Map<Class<? extends Component>, Set<Component>>         allComponentsMap;

	private CoreMap map;

	public EntityList(CoreMap map) {
		this.map = map;

		entities = new HashMap<>();
		entityComponentMap = new HashMap<>();
		allComponentsMap = new HashMap<>();
	}

	public void put(Entity entity, List<Component> components) {
		entities.put(entity.getID(), entity);
		entityComponentMap.put(entity, new HashMap<>());

		// add components to maps
		for (Component component : components) {
			addComponent(entity, component);
		}

		// trigger the spawn event
		map.getEventManager().trigger(new EntitySpawnEvent(entity));

		// initialize the systems with the new entity
		for (Component component : components) {
			component.initSystems();
		}

		// initialize components
		for (Component component : components) {
			component.init();
		}
	}

	public void remove(int id) {
		Entity entity = entities.remove(id);
		Map<Class<? extends Component>, Component> components = entityComponentMap.get(entity);
		for (Map.Entry<Class<? extends Component>, Component> entry : components.entrySet()) {
			allComponentsMap.get(entry.getKey()).remove(entry.getValue());
			entry.getValue().cleanup();
		}

		entityComponentMap.remove(entity);

		map.getEventManager().trigger(new EntityDespawnEvent(entity, components));
	}

	public boolean containsID(int id) {
		return entities.containsKey(id);
	}

	public Entity get(int id) {
		return entities.get(id);
	}

	public Collection<Entity> getEntities() {
		return entities.values();
	}

	private Map<Class<? extends Component>, List<Class<? extends Component>>> superclassLookup = new HashMap<>();

	@SuppressWarnings("unchecked")
	private List<Class<? extends Component>> getWithSuperclasses(Class<? extends Component> clazz) {
		return superclassLookup.computeIfAbsent(clazz, c -> {
			List<Class<? extends Component>> classes = new ArrayList<>();
			while (c != Component.class) {
				classes.add(c);
				c = (Class<? extends Component>) c.getSuperclass();
			}
			classes.add(c);
			return classes;
		});
	}

	public void addComponent(Entity entity, Component component) {
		List<Class<? extends Component>> classes = getWithSuperclasses(component.getClass());
		Map<Class<? extends Component>, Component> componentMap = entityComponentMap.get(entity);
		for (Class<? extends Component> clazz : classes) {
			componentMap.put(clazz, component);
			allComponentsMap.computeIfAbsent(clazz, c -> new HashSet<>()).add(component);
		}

		component.setEntity(entity);
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Entity entity, Class<T> componentClass) {
		Map<Class<? extends Component>, Component> entityComponents = entityComponentMap.get(entity);
		return entityComponents != null ? (T) entityComponents.get(componentClass) : null;
	}

	public <T extends Component> boolean hasComponent(Entity entity, Class<T> componentClass) {
		Map<Class<? extends Component>, Component> entityComponents = entityComponentMap.get(entity);
		return entityComponents != null && entityComponentMap.get(entity).containsKey(componentClass);
	}

	@SuppressWarnings("unchecked")
	public <T extends Component> Set<T> getComponents(Class<T> componentClass) {
		return (Set<T>) allComponentsMap.computeIfAbsent(componentClass, c -> new HashSet<>());
	}

	public Collection<Component> getComponents(Entity entity) {
		return entityComponentMap.get(entity).values();
	}
}
