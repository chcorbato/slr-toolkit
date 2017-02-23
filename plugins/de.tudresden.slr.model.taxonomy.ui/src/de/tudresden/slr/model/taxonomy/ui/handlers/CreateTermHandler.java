package de.tudresden.slr.model.taxonomy.ui.handlers;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import de.tudresden.slr.model.modelregistry.ModelRegistryPlugin;
import de.tudresden.slr.model.taxonomy.Model;
import de.tudresden.slr.model.taxonomy.Term;
import de.tudresden.slr.model.taxonomy.impl.TaxonomyFactoryImpl;
import de.tudresden.slr.model.utils.SearchUtils;
import dialog.CreateTermDialog;
import dialog.CreateTermDialog.TermPosition;

public class CreateTermHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelectionChecked(event);
		if (selection == null || !(selection instanceof IStructuredSelection)) {
			return null;
		}
		IStructuredSelection currentSelection = (IStructuredSelection) selection;
		if (currentSelection.size() == 1) {
			Term term = (Term) currentSelection.getFirstElement();	
			CreateTermDialog dialog = new CreateTermDialog(null, term.getName());
			dialog.setBlockOnOpen(true);
			if (dialog.open() == InputDialog.OK) {
				Term createdTerm = TaxonomyFactoryImpl.eINSTANCE.createTerm();
				createdTerm.setName(dialog.getTermName());
				if (dialog.getTermPosition() == TermPosition.SUBTERM) {
					term.getSubclasses().add(createdTerm);
				} else if (dialog.getTermPosition() == TermPosition.NEIGHBOR) {
					EObject container = term.eContainer();
					if (container instanceof Term) {
						((Term) container).getSubclasses().add(createdTerm);
					} else if (container instanceof Model) {
						((Model) container).getDimensions().add(createdTerm);
					}
				}
				Optional<Model> model = SearchUtils.getConainingModel(term);
				if (model.isPresent()) {
					Model newTaxonomy = EcoreUtil.copy(model.get());
					ModelRegistryPlugin.getModelRegistry().setActiveTaxonomy(newTaxonomy);
					try {					
						if (newTaxonomy.getResource() == null) newTaxonomy.setResource(model.get().eResource());
						newTaxonomy.getResource().save(Collections.EMPTY_MAP);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}	
		return null;
	}

}
