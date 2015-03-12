package hdgl.db.graph;

public interface Entity {
	
	public long getId();
	public String getType();
	public Iterable<LabelValue> getLabels();
	public byte[] getLabel(String name);
	
}
