package tools.vitruv.framework.remote.client;

import java.nio.file.Path;

import org.eclipse.emf.ecore.EPackage;

import edu.kit.ipd.sdq.metamodels.families.FamiliesFactory;
import edu.kit.ipd.sdq.metamodels.families.FamiliesPackage;
import edu.kit.ipd.sdq.metamodels.families.FamilyRegister;
import edu.kit.ipd.sdq.metamodels.persons.PersonsPackage;

public class VitruvClientTest {

	public static void main(String[] args) {
		EPackage.Registry.INSTANCE.put(FamiliesPackage.eNS_URI, FamiliesPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(PersonsPackage.eNS_URI, PersonsPackage.eINSTANCE);
		
		var client = VitruvClientFactory.create("localhost", 8069, Path.of("client-test/test"));
		var viewType = client.getViewTypes().stream().filter(eleme -> "families".equals(eleme.getName())).findFirst().get();
		var selector = client.createSelector(viewType);
		selector.getSelectableElements().forEach(el -> selector.setSelected(el, true));;
		
		var view = selector.createView();
		var changeRecordingView = view.withChangeRecordingTrait();
		var familyRegister =(FamilyRegister)changeRecordingView.getRootObjects().stream().findFirst().orElseThrow();
		
		var father = FamiliesFactory.eINSTANCE.createMember();
		father.setFirstName("Vater");
		
		familyRegister.getFamilies().get(0).setFather(null);
		changeRecordingView.commitChanges();
		
		
		System.out.println(view.getRootObjects());
	}

}
