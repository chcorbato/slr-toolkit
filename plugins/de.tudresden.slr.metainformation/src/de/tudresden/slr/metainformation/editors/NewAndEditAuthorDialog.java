package de.tudresden.slr.metainformation.editors;

import java.util.Optional;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.tudresden.slr.metainformation.data.Author;

/**
 * 
 * Class for creating a SWT-Dialog for adding or editing an author.
 */
public class NewAndEditAuthorDialog extends TitleAreaDialog {

	private Text textboxName;
	private Text textboxEmail;
	private Text textboxAffiliation;

	private String name = "";
	private String mail = "";
	private String affiliation = "";

	Button okButton;

	//optional with or without Author which is to be edited.
	//if empty, there is no author to be edited -> add new author
	private Optional<Author> optionalEditAuthor = Optional.empty();

	public NewAndEditAuthorDialog(Shell parentShell) {
		super(parentShell);
	}

	/*
	 * Initializes Optional which contains author.
	 */
	public void initEditAuthor(Author author) {
		if(author != null) optionalEditAuthor = Optional.of(author);
	}

	@Override
	public void create() {
		super.create();
		String infoText = optionalEditAuthor.isPresent() ? "Edit author":"Add author";
		setTitle(infoText);
		setMessage("At least the new author's name has to be specified.", IMessageProvider.INFORMATION);
		okButton = getButton(IDialogConstants.OK_ID);
		
		//ok button is disabled by default and just activated, if either author is edited and name is not empty
		//later, listener is added to track change in name field
		okButton.setEnabled(false);
		
		if(optionalEditAuthor.isPresent()) {
			if(!optionalEditAuthor.get().getName().isEmpty()) {
				okButton.setEnabled(true);
			}
		}

		//modigy listener which checks, whether the name field is empty or not. If not, ok button is enabled.
		textboxName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				Text text = (Text) e.widget;
				if(text.getText().isEmpty()) {
					okButton.setEnabled(false);
				}
				else {
					okButton.setEnabled(true);
				}
			}
		});
	}

	

	@Override
	protected Control createDialogArea(Composite parent) {
		if (optionalEditAuthor.isPresent()) {
			Author authorToEdit = optionalEditAuthor.get();
			this.name = authorToEdit.getName();
			this.mail = authorToEdit.getEmail();
			this.affiliation = authorToEdit.getOrganisation();
		}

		Composite control = (Composite) super.createDialogArea(parent);

		Composite container = new Composite(control, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		Label labelName = new Label(container, SWT.NONE);
		labelName.setText("Name");
		textboxName = new Text(container, SWT.BORDER);
		textboxName.setText(name);
		textboxName.setLayoutData(gridData);

		Label labelMail = new Label(container, SWT.NONE);
		labelMail.setText("Email");
		textboxEmail = new Text(container, SWT.BORDER);
		textboxEmail.setText(mail);
		textboxEmail.setLayoutData(gridData);

		Label labelAffiliation = new Label(container, SWT.NONE);
		labelAffiliation.setText("Affiliation");
		textboxAffiliation = new Text(container, SWT.BORDER);
		textboxAffiliation.setText(affiliation);
		textboxAffiliation.setLayoutData(gridData);

		return control;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	private void saveInput() {
		name = textboxName.getText();
		mail = textboxEmail.getText();
		affiliation = textboxAffiliation.getText();

	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getName() {
		return name;
	}

	public String getMail() {
		return mail;
	}

	public String getAffiliation() {
		return affiliation;
	}
}