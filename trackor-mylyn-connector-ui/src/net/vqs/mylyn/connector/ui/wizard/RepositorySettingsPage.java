package net.vqs.mylyn.connector.ui.wizard;

import java.net.URI;

import net.vqs.mylyn.connector.core.VqsCorePlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Composite;

public class RepositorySettingsPage extends AbstractRepositorySettingsPage {

    public RepositorySettingsPage(TaskRepository repository) {
        super("Issue Tracker Repository Settings", "Settings for Issue Tracker Repository", repository);
        setNeedsAnonymousLogin(false);
        setNeedsEncoding(false);
        setNeedsTimeZone(false);
        setNeedsAdvanced(false);
        setNeedsProxy(false);
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        addRepositoryTemplatesToServerUrlCombo();
    }

    @Override
    public String getConnectorKind() {
        return VqsCorePlugin.CONNECTOR_KIND;
    }

    @Override
    protected void createAdditionalControls(Composite parent) {

    }

    @Override
    protected void repositoryTemplateSelected(RepositoryTemplate template) {
        repositoryLabelEditor.setStringValue(template.label);
        setUrl(template.repositoryUrl);
        getContainer().updateButtons();
    }

    @Override
    protected Validator getValidator(TaskRepository repository) {
        return new Validator() {
            @Override
            public void run(IProgressMonitor monitor) throws CoreException {
                setStatus(Status.OK_STATUS);
                // TODO Check connection for username / password validation
            }
        };
    }

    @Override
    protected boolean isValidUrl(String url) {
        if (url.startsWith(URL_PREFIX_HTTPS) || url.startsWith(URL_PREFIX_HTTP)) {
            try {
                new URI(url);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
