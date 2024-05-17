package tools.vitruv.framework.remote.common.deserializer;

import java.io.IOException;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;

import tools.vitruv.framework.remote.common.util.Mapper;
import tools.vitruv.framework.remote.common.util.ResourceUtil;
import tools.vitruv.framework.remote.common.util.constants.JsonFieldName;

public class ResourceSetDeserializer extends JsonDeserializer<ResourceSet> {
	
	private final IdTranformer transformation;
	private final Mapper mapper;
	
	public ResourceSetDeserializer(Mapper mapper, IdTranformer transformation) {
		this.transformation = transformation;
		this.mapper = mapper;
	}

	@Override
	public ResourceSet deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		var rootNode = (ArrayNode) parser.getCodec().readTree(parser);
		
		var resourceSet = ResourceUtil.createJsonResourceSet();
		for (var e : rootNode) {
			var resource = mapper.deserializeResource(e.get(JsonFieldName.CONTENT).toString(), 
					transformation.toGlobal(URI.createURI(e.get(JsonFieldName.URI).asText())).toString(), resourceSet);
			if (!resource.getURI().toString().equals(JsonFieldName.TEMP_VALUE)) {
				resource.save(Map.of());
			}
		}
	
		return resourceSet;
	}
}
