package tools.vitruv.framework.remote.common.util;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface Mapper {

	/**
	 * Deserializes the given json array to a list.
	 *
	 * @param json  the json array to deserialize
	 * @param clazz the class representing the json type of the objects in the json array
	 * @return the list of objects or {@code null}, if an {@link JsonProcessingException} occurred.
	 */
	<T> List<T> deserializeArrayOf(String json, Class<T> clazz) throws JsonProcessingException;

	Resource deserializeResource(String json, String uri, ResourceSet parentSet) throws JsonProcessingException;

	/**
	 * Deserializes the given json string.
	 *
	 * @param json  the json to deserialize
	 * @param clazz the class of the jsons type.
	 * @return the object or {@code null}, if an {@link JsonProcessingException} occurred.
	 */
	<T> T deserialize(String json, Class<T> clazz) throws JsonProcessingException;

	/**
	 * Serializes the given object.
	 *
	 * @param obj the object to serialize
	 * @return the json or {@code null}, if an {@link JsonProcessingException} occurred.
	 */
	String serialize(Object obj) throws JsonProcessingException;
	
	public ObjectMapper getObjectMapper();

}
