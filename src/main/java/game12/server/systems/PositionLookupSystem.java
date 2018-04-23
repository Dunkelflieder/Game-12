package game12.server.systems;

import de.nerogar.noise.util.Bounding;
import de.nerogar.noise.util.BoundingSphere;
import de.nerogar.noise.util.Vector3f;
import game12.core.LogicSystem;
import game12.core.components.BoundingComponent;
import game12.core.components.PositionComponent;
import game12.core.map.Entity;

import java.util.*;

public class PositionLookupSystem extends LogicSystem {

	//private SpaceOctree<BoundingComponent> components;
	private Set<BoundingComponent> components;

	private BoundingSphere sphereInstance;

	public PositionLookupSystem() {
		components = new HashSet<>();

		sphereInstance = new BoundingSphere(new Vector3f(), 0);
	}

	public void registerEntity(Entity entity) {
		BoundingComponent boundingComponent = entity.getComponent(BoundingComponent.class);
		if (boundingComponent != null) {
			boundingComponent.refreshBounding();
			components.add(boundingComponent);
		}
	}

	public void updateEntity(Entity entity) {
		BoundingComponent boundingComponent = entity.getComponent(BoundingComponent.class);
		if (boundingComponent != null) {
			boundingComponent.refreshBounding();
			//components.update(boundingComponent);
		}
	}

	public void unregisterEntity(Entity entity) {
		BoundingComponent boundingComponent = entity.getComponent(BoundingComponent.class);
		if (boundingComponent != null) {
			components.remove(boundingComponent);
		}
	}

	public Collection<BoundingComponent> getBoundings(Bounding bounding) {
		List<BoundingComponent> filteredList = new ArrayList<>(components);

		filteredList.removeIf(b -> !b.getBounding().overlapsBounding(bounding));

		return filteredList;
		//return components.getFilteredExact(new ArrayList<>(), bounding);
	}

	public Collection<BoundingComponent> getBoundingsAround(Vector3f center, float radius) {
		sphereInstance.setCenter(center);
		sphereInstance.setRadius(radius);

		return getBoundings(sphereInstance);
	}

	public Collection<BoundingComponent> getBoundingsAround(Entity entity, float radius) {
		PositionComponent positionComponent = entity.getComponent(PositionComponent.class);
		sphereInstance.setCenter(positionComponent.getX(), positionComponent.getY(), positionComponent.getZ());
		sphereInstance.setRadius(radius);

		return getBoundings(sphereInstance);
	}

}
