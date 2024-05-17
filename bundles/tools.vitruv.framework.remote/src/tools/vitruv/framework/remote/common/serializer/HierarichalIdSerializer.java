package tools.vitruv.framework.remote.common.serializer;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emfcloud.jackson.databind.ser.EcoreReferenceSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import tools.vitruv.change.atomic.hid.HierarchicalId;
import tools.vitruv.framework.remote.common.deserializer.IdTranformer;

public class HierarichalIdSerializer extends JsonSerializer<EObject>{
	
	private final EcoreReferenceSerializer standardSerializer;
	private final IdTranformer transformation;
	
	public HierarichalIdSerializer(EcoreReferenceSerializer standardDeserializer, IdTranformer transformation) {
		this.standardSerializer = standardDeserializer;
		this.transformation = transformation;
	}

	@Override
	public void serialize(EObject value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value instanceof HierarchicalId hid) {
			gen.writeString(transformation.toLocal(URI.createURI(hid.getId())).toString());
		} else {
			standardSerializer.serialize(value, gen, serializers);
		}
	}
}
