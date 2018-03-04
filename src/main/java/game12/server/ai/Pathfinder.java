package game12.server.ai;

import game12.core.utils.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class Pathfinder {

	private int width;
	private int height;

	private static class Node {

		private final float cost;

		private Node pointer;
		private int  posX, posY;
		private float costCalc;

		private byte state;
		private static byte STATE_INIT   = 0;
		private static byte STATE_OPEN   = 1;
		private static byte STATE_CLOSED = 2;

		private byte dir;
		private static byte DIR_UP        = 0;
		private static byte DIR_RIGHT     = 1;
		private static byte DIR_DOWN      = 2;
		private static byte DIR_LEFT      = 3;
		private static byte DIR_UPRIGHT   = 4;
		private static byte DIR_RIGHTDOWN = 5;
		private static byte DIR_DOWNLEFT  = 6;
		private static byte DIR_LEFTUP    = 7;

		Node(float cost, int posX, int posY) {
			this.cost = cost;
			this.posX = posX;
			this.posY = posY;
			reset();
		}

		void reset() {
			pointer = null;
			state = STATE_INIT;
			dir = DIR_UP;
		}

		float getTotalCost(int goalX, int goalY) {
			int diffX = Math.abs(goalX - posX);
			int diffY = Math.abs(goalY - posY);
			return getWayCost() + (diffX + diffY)*0.1f;
		}

		float getWayCost() {
			if (costCalc < 0) {
				float pointerCost = 0;
				float dirChangePenality = 0;
				if (pointer != null) {
					pointerCost = pointer.getWayCost();
					if (pointer.getDir() != dir) {
						dirChangePenality = 0.01f;
					}
				}
				costCalc = pointerCost + dirChangePenality + cost * (isDiagonal() ? 1.41421356f : 1);
			}
			return costCalc;
		}

		void setPointer(Node node) {
			costCalc = -1;
			pointer = node;
		}

		Node getPointer() {
			return pointer;
		}

		byte getState() {
			return state;
		}

		void setState(byte state) {
			this.state = state;
		}

		boolean isDiagonal() {
			return dir > DIR_LEFT;
		}

		byte getDir() {
			return dir;
		}

		void setDir(byte dir) {
			this.dir = dir;
			costCalc = -1;
		}

		boolean isWalkable() {
			return cost >= 0;
		}

	}

	private Node[] nodes;

	public Pathfinder(int width, int height) {
		this.width = width;
		this.height = height;
		nodes = new Node[width * height];
	}

	private Node getNodeAt(int posX, int posY) {
		if (posX < 0 || posY < 0 || posX >= width || posY >= height) {
			return null;
		}
		return nodes[posX + width * posY];
	}

	private void reset(List<Node> openList, List<Node> closedList) {
		for (Node node : closedList) {
			node.reset();
		}
		for (Node node : openList) {
			node.reset();
		}
	}

	private static List<Vector2i> nodeToArraylist(Node node) {
		List<Vector2i> a = new ArrayList<Vector2i>();
		a.add(Vector2i.of(node.posX, node.posY));
		byte prevDir = -1;
		while (node.getPointer() != null) {
			node = node.getPointer();
			// Skip points on straight lines.
			if (node.getDir() == prevDir) {
				// continue;
			}
			a.add(0, Vector2i.of(node.posX, node.posY));
			prevDir = node.getDir();
		}
		a.remove(0);
		return a;
	}

	private static void addNodeSorted(List<Node> list, Node node, int goalX, int goalY) {
		int il = 0;
		int ir = list.size();
		int mid = 0;
		while (il < ir) {
			if (node.getTotalCost(goalX, goalY) < list.get(mid).getTotalCost(goalX, goalY)) {
				ir = mid;
			} else {
				il = mid + 1;
			}
			mid = (il + ir) / 2;
		}
		list.add(il, node);
	}

	public List<Vector2i> getPath(Vector2i source, Vector2i target) {

		List<Node> openList = new ArrayList<>();
		List<Node> closedList = new ArrayList<>();

		Node current = getNodeAt(source.x, source.y);

		// check if start and goal are valid
		Node to = getNodeAt(target.x, target.y);
		if (current == null || to == null) {
			return null;
		}
		if (!to.isWalkable()) {
			return null;
		}

		current.setState(Node.STATE_OPEN);
		openList.add(current);

		Vector2i[] newPos = new Vector2i[8];

		// try finding a path, but abort if it's too expensive
		for (int i = 0; i < 10000; i++) {

			// no path possible
			if (openList.size() == 0) {
				reset(openList, closedList);
				return null;
			}

			// cheapest open node is always at 0
			current = openList.get(0);

			// goal reached
			if (current.posX == target.x && current.posY == target.y) {
				List<Vector2i> path = nodeToArraylist(current);
				reset(openList, closedList);
				return path;
			}

			newPos[Node.DIR_UP] = Vector2i.of(current.posX, current.posY - 1); // up
			newPos[Node.DIR_RIGHT] = Vector2i.of(current.posX + 1, current.posY); // right
			newPos[Node.DIR_DOWN] = Vector2i.of(current.posX, current.posY + 1); // down
			newPos[Node.DIR_LEFT] = Vector2i.of(current.posX - 1, current.posY); // left
			newPos[Node.DIR_UPRIGHT] = Vector2i.of(current.posX + 1, current.posY - 1); // up-right
			newPos[Node.DIR_RIGHTDOWN] = Vector2i.of(current.posX + 1, current.posY + 1); // right-down
			newPos[Node.DIR_DOWNLEFT] = Vector2i.of(current.posX - 1, current.posY + 1); // down-left
			newPos[Node.DIR_LEFTUP] = Vector2i.of(current.posX - 1, current.posY - 1); // left-up

			// remember if the nodes up, right, down and left were walkable (initialized with false)
			boolean[] walkable = new boolean[4];
			for (byte j = 0; j < newPos.length; j++) {

				if (j > 3) {
					// skip this diagonal nodes if the 2 corresponding straight nodes were not walkable
					if (!(walkable[j - 4] && walkable[j == 7 ? 0 : j - 3])) {
						// example: j = 4: up-right
						// j-4 = 0 => up
						// j-3 = 1 => right

						// example: j = 7: left-up
						// j-4 = 3 => left
						// j-3 = 4 => Out of Bounds => j==7: 0 => up
						continue;
					}
				}

				Node node = getNodeAt(newPos[j].x, newPos[j].y);

				// no such node OR node already in open or closed list OR node not walkable
				if (node == null || node.getState() > Node.STATE_INIT || !node.isWalkable()) {
					continue;
				}

				node.setPointer(current);
				node.setDir(j);

				// add to openList at the correct place (openList stays sorted)
				addNodeSorted(openList, node, target.x, target.y);
				node.setState(Node.STATE_OPEN);

				if (j < 4) {
					walkable[j] = true;
				}
			}

			// remove processed node from open list and add to pseudo closed list
			openList.remove(current);
			current.setState(Node.STATE_CLOSED);
			closedList.add(current);

		}

		reset(openList, closedList);
		return null;

	}

	public void update(int[] costSquare, Vector2i from, Vector2i to) {
		if (from.x < 0 || from.y < 0 || to.x > width || to.y > height || from.x > to.x || from.y > to.y) {
			throw new RuntimeException("Tried to update an invalid portion of the Pathfinder");
		}
		int costSquareWidth = to.x - from.x;
		for (int x = from.x; x < to.x; x++) {
			for (int y = from.y; y < to.y; y++) {
				// The tile's walking cost
				int adjustedX = x - from.x;
				int adjustedY = y - from.y;
				int cost = costSquare[adjustedX + adjustedY * costSquareWidth];
				nodes[x + y * width] = new Node(cost, x, y);
			}
		}
	}

}
