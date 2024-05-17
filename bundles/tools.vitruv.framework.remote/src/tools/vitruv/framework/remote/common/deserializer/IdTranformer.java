package tools.vitruv.framework.remote.common.deserializer;

import java.util.List;

import org.eclipse.emf.common.util.URI;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.hid.HierarchicalId;

public interface IdTranformer {

	void allToLocal(List<? extends EChange<HierarchicalId>> eChanges);

	void allToGlobal(List<? extends EChange<HierarchicalId>> eChanges);

	/**
	 * Transforms the given local id (relative path) to a global id (absolute path).
	 *
	 * @param local the id to transform
	 * @return the global id
	 */
	URI toGlobal(URI local);

	/**
	 * Transforms the given global (absolute path) id to a local id (relative path).
	 *
	 * @param global the id to transform
	 * @return the local id
	 */
	URI toLocal(URI global);

}
