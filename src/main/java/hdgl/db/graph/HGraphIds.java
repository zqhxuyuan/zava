package hdgl.db.graph;

public class HGraphIds {

	public static boolean isVertexId(long id){
		return id<0;
	}
	
	public static long extractTypeId(long id){
		return id>>32;
	}
	
	public static long extractEntityId(long id){
		return id & 0x00000000ffffffff;
	}
	
	public static boolean isEdgeId(long id){
		return id>0;
	}
}
