package com.onevizion.mylyn.connector.ui.wizard;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import com.onevizion.mylyn.connector.core.OvClient;
import com.onevizion.mylyn.connector.core.RepositoryConnector;
import com.onevizion.mylyn.connector.util.QueryUtils;

public class UserFilterQueryPage extends AbstractRepositoryQueryPage {

    private String selectedFilter;
    private Text txtTitle;

    public UserFilterQueryPage(TaskRepository taskRepository, IRepositoryQuery query) {
        super("New Issue Tracker Query", taskRepository, query);
        setTitle("New Issue Tracker Query");
        setDescription("Please select a filter and specify query title (not required)");
        setPageComplete(false);
    }

    @Override
    public String getQueryTitle() {
        Assert.isNotNull(txtTitle);
        return !txtTitle.getText().isEmpty() ? txtTitle.getText() : selectedFilter;
    }

    @Override
    public boolean isPageComplete() {
        if (selectedFilter == null) {
            return false;
        } else {
            return super.isPageComplete();
        }
    }

    @Override
    public void applyTo(IRepositoryQuery query) {
        String queryTitle = getQueryTitle();
        query.setSummary(queryTitle);

        query.getAttributes().clear();
        if (selectedFilter != null) {
            query.setAttribute(QueryUtils.QueryAttributes.CAN.toString(), selectedFilter);
        }
    }

    public OvClient getClient() {
        IRepositoryManager manager = TasksUi.getRepositoryManager();
        AbstractRepositoryConnector connector = manager.getRepositoryConnector(getTaskRepository().getConnectorKind());
        return ((RepositoryConnector) connector).getClient();
    }

    protected boolean restoreState(IRepositoryQuery query) {
        if (query != null) {
            this.txtTitle.setText(query.getAttribute("label"));
        } else {
            this.txtTitle.setText("");
        }
        return true;
    }

    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData());
        composite.setLayout(new GridLayout(2, false));

        Label lblTitle = new Label(composite, SWT.NONE);
        lblTitle.setText("Title:");
        lblTitle.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        txtTitle = new Text(composite, SWT.BORDER);
        txtTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        List identifiersList = new List(composite, SWT.V_SCROLL | SWT.BORDER);
        identifiersList.deselectAll();
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
        identifiersList.setLayoutData(data);
        identifiersList.setEnabled(true);

        ListViewer viewerQueries = new ListViewer(identifiersList);
        viewerQueries.setContentProvider(new FilterContentProvider());
        viewerQueries.getList().setEnabled(true);
        viewerQueries.setLabelProvider(new FilterLabelProvider());
        viewerQueries.setInput(new Object());
        viewerQueries.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                selectedFilter = (String) selection.getFirstElement();
                IWizardContainer container = getContainer();
                if (container != null && container.getCurrentPage() != null) {
                    container.updateButtons();
                }
            }
        });

        setControl(composite);
    }

    private class FilterLabelProvider extends LabelProvider {
        @Override
        public String getText(Object element) {
            String filter = (String) element;
            return filter;
        }
    }

    private class FilterContentProvider implements IStructuredContentProvider {
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public String[] getElements(Object inputElement) {
            try {
                return getClient().getFilters(getTaskRepository()).toArray(new String[] {});
            } catch (Exception e) {
                setMessage("Can not get filters from server, caused by " + e.getMessage(), IMessageProvider.ERROR);
            }
            return new String[] {};
        }
    }
}
