package com.ghostrun.driving;

public class NodePair {
	public int id1;
	public int id2;
	public NodePair(int id1, int id2) {
		this.id1 = id1;
		this.id2 = id2;
	}
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof NodePair))
			return false;
		
		NodePair p = (NodePair)o;
		return (p.id1 == this.id1 && p.id2 == this.id2) ||
				(p.id2 == this.id1 && p.id1 == this.id2);
	}
	
	public int hashCode() {
		return id1^id2;
	}
}