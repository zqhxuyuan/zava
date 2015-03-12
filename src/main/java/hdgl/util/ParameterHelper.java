package hdgl.util;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParameterHelper {

	private ParameterHelper(){
		
	}
	
	public static Map<String, String[]> parseParameters( String[] args){
		Map<String, String[]> groupArgs=new HashMap<String, String[]>();
		String currentKeyString = "default";
		ArrayList<String> currentKeyList = new ArrayList<String>();
		for (String argString : args) {
			if(argString.startsWith("-")){
				if(groupArgs.containsKey(currentKeyString)){
					for (String oldval : groupArgs.get(currentKeyString)) {
						currentKeyList.add(oldval);
					}
				}
				groupArgs.put(currentKeyString, currentKeyList.toArray(new String[0]));
				currentKeyString = argString.replaceAll("^--?", "");
				currentKeyList.clear();
			}else{
				currentKeyList.add(argString);
			}
		}
		if(groupArgs.containsKey(currentKeyString)){
			for (String oldval : groupArgs.get(currentKeyString)) {
				currentKeyList.add(oldval);
			}
		}
		groupArgs.put(currentKeyString, currentKeyList.toArray(new String[0]));
		return groupArgs;
	}
	
	public static <T> T parameterizedConstructuct(Class<T> objClass, String[] args){
		try{
			try{
				Constructor<T> constructor=objClass.getConstructor(Map.class);
				Map<String, String[]> groupArgs = parseParameters(args);				
				return constructor.newInstance(groupArgs);
			}catch (NoSuchMethodException e) {
				Constructor<T> constructor = objClass.getConstructor();
				return constructor.newInstance();
			}
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
