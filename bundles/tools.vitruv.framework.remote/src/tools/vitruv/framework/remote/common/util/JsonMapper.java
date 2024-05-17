package tools.vitruv.framework.remote.common.util;

import java.nio.file.Path;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emfcloud.jackson.annotations.EcoreIdentityInfo;
import org.eclipse.emfcloud.jackson.databind.EMFContext;
import org.eclipse.emfcloud.jackson.module.EMFModule;
import org.eclipse.emfcloud.jackson.module.EMFModule.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.framework.remote.common.deserializer.IdTranformer;
import tools.vitruv.framework.remote.common.deserializer.ReferenceDeserializerModifier;
import tools.vitruv.framework.remote.common.deserializer.ResourceSetDeserializer;
import tools.vitruv.framework.remote.common.deserializer.VitruviusChangeDeserializer;
import tools.vitruv.framework.remote.common.serializer.ReferenceSerializerModifier;
import tools.vitruv.framework.remote.common.serializer.ResourceSetSerializer;
import tools.vitruv.framework.remote.common.serializer.VitruviusChangeSerializer;

/**
 * This mapper can be used to serialize objects and deserialize json in the
 * context of vitruv. It has custom De-/Serializers for {@link ResourceSet}s,
 * {@link Resource}s and {@link VitruviusChange}s.
 */
public class JsonMapper implements Mapper {

	private final ObjectMapper mapper = new ObjectMapper();

	public JsonMapper(Path vsumPath) {
		final IdTranformer transformation = new IdTransformation(vsumPath);

		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		var module = new EMFModule();

		// Register serializer
		module.addSerializer(ResourceSet.class, new ResourceSetSerializer(transformation));
		module.addSerializer(VitruviusChange.class, new VitruviusChangeSerializer());

		// Register deserializer
		module.addDeserializer(ResourceSet.class, new ResourceSetDeserializer(this, transformation));
		module.addDeserializer(VitruviusChange.class, new VitruviusChangeDeserializer(this, transformation));

		// Register modifiers for references to handle HierarichalId
		module.setSerializerModifier(new ReferenceSerializerModifier(transformation));
		module.setDeserializerModifier(new ReferenceDeserializerModifier(transformation));

		// Use IDs to identify eObjects on client and server
		module.configure(Feature.OPTION_USE_ID, true);
		module.setIdentityInfo(new EcoreIdentityInfo("_id"));

		mapper.registerModule(module);
	}

	/**
	 * Serializes the given object.
	 *
	 * @param obj the object to serialize
	 * @return the json or {@code null}, if an {@link JsonProcessingException}
	 *         occurred.
	 */
	@Override
	public String serialize(Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj);
	}

	public ObjectMapper getObjectMapper() {
		return this.mapper;
	}

	/**
	 * Deserializes the given json string.
	 *
	 * @param json  the json to deserialize
	 * @param clazz the class of the jsons type.
	 * @return the object or {@code null}, if an {@link JsonProcessingException}
	 *         occurred.
	 */
	@Override
	public <T> T deserialize(String json, Class<T> clazz) throws JsonProcessingException {
		return mapper.reader().forType(clazz).readValue(json);
	}

	@Override
	public Resource deserializeResource(String json, String uri, ResourceSet parentSet) throws JsonProcessingException {
		//parentSet.getResourceFactoryRegistry().getProtocolToFactoryMap().put("http", new XMIResourceFactoryImpl());
		return mapper.reader().withAttribute(EMFContext.Attributes.RESOURCE_SET, parentSet)
				.withAttribute(EMFContext.Attributes.RESOURCE_URI, URI.createURI(uri)).forType(Resource.class)
				.readValue(json);
	}

	/**
	 * Deserializes the given json array to a list.
	 *
	 * @param json  the json array to deserialize
	 * @param clazz the class representing the json type of the objects in the json
	 *              array
	 * @return the list of objects or {@code null}, if an
	 *         {@link JsonProcessingException} occurred.
	 */
	@Override
	public <T> List<T> deserializeArrayOf(String json, Class<T> clazz) throws JsonProcessingException {
		var javaType = mapper.getTypeFactory().constructCollectionType(List.class, clazz);
		return mapper.readValue(json, javaType);
	}
}
