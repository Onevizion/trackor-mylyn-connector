package net.vqs.mylyn.connector.ui;

import net.vqs.mylyn.connector.core.VqsCorePlugin;
import net.vqs.mylyn.connector.ui.wizard.RepositorySettingsPage;
import net.vqs.mylyn.connector.ui.wizard.UserFilterQueryPage;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;
import org.eclipse.mylyn.tasks.ui.wizards.RepositoryQueryWizard;

public class VqsConnectorUi extends AbstractRepositoryConnectorUi {

    public VqsConnectorUi() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String getConnectorKind() {
        return VqsCorePlugin.CONNECTOR_KIND;
    }

    @Override
    public ITaskRepositoryPage getSettingsPage(TaskRepository repository) {
        return new RepositorySettingsPage(repository);
    }

    @Override
    public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query) {
        RepositoryQueryWizard queryWizard = new RepositoryQueryWizard(repository);
        UserFilterQueryPage selectionQueryPage = new UserFilterQueryPage(repository, query);
        queryWizard.addPage(selectionQueryPage);
        return queryWizard;
    }

    @Override
    public IWizard getNewTaskWizard(TaskRepository repository, ITaskMapping selection) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasSearchPage() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getTaskKindLabel(ITask task) {
        return "Issue";
    }
}
