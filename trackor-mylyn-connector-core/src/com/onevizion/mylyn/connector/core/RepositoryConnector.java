package com.onevizion.mylyn.connector.core;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.core.sync.ISynchronizationSession;

import com.onevizion.mylyn.connector.util.QueryUtils;
import com.onevizion.mylyn.connector.vo.Issue;

public class RepositoryConnector extends AbstractRepositoryConnector {

    private OvClient ovClient;

    private TaskDataHandler taskDataHandler;

    public RepositoryConnector() {
        taskDataHandler = new TaskDataHandler(this);
        ovClient = new OvClient();
    }

    public OvClient getClient() {
        return ovClient;
    }

    @Override
    public boolean canCreateNewTask(TaskRepository repository) {
        return true;
    }

    @Override
    public boolean canCreateTaskFromKey(TaskRepository repository) {
        return false;
    }

    @Override
    public String getConnectorKind() {
        return OvCorePlugin.CONNECTOR_KIND;
    }

    @Override
    public String getLabel() {
        return "OneVizion Repository";
    }

    @Override
    public String getRepositoryUrlFromTaskUrl(String taskUrl) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TaskData getTaskData(TaskRepository repository, String taskIdOrKey, IProgressMonitor monitor)
            throws CoreException {
        if (!NumberUtils.isDigits(taskIdOrKey)) {
            IStatus status = new Status(IStatus.ERROR, OvCorePlugin.PLUGIN_ID, "Task Id must be a number");
            throw new CoreException(status);
        }

        try {
            Long taskId = new Long(taskIdOrKey);
            Issue issue = ovClient.getIssue(repository, taskId);
            TaskData taskData = taskDataHandler.getTaskData(repository, issue, monitor);
            return taskData;
        } catch (CoreException e) {
            IStatus status = new Status(IStatus.ERROR, OvCorePlugin.PLUGIN_ID, e.getMessage(), e);
            throw new CoreException(status);
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, OvCorePlugin.PLUGIN_ID, e.getMessage(), e);
            throw new CoreException(status);
        }
    }

    @Override
    public String getTaskIdFromTaskUrl(String taskUrl) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTaskUrl(String repositoryUrl, String taskIdOrKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(repositoryUrl);
        sb.append("/form/ConfigAppForm.do?ttid=10009161&id=");
        sb.append(taskIdOrKey);
        return sb.toString();
    }

    @Override
    public boolean hasTaskChanged(TaskRepository taskRepository, ITask task, TaskData taskData) {
        TaskAttribute attribute = taskData.getRoot().getAttribute(TaskAttribute.DATE_MODIFICATION);
        if (attribute != null) {
            Date dataModificationDate = taskData.getAttributeMapper().getDateValue(attribute);
            if (dataModificationDate != null) {
                Date taskModificationDate = task.getModificationDate();
                if (taskModificationDate != null) {
                    return !taskModificationDate.equals(dataModificationDate);
                }
            }
        }
        return true;
    }

    @Override
    public IStatus performQuery(TaskRepository repository, IRepositoryQuery query, TaskDataCollector collector,
            ISynchronizationSession session, IProgressMonitor monitor) {
        List<Issue> issues = null;
        try {
            String filterName = query.getAttribute(QueryUtils.QueryAttributes.CAN.toString());
            issues = ovClient.getIssues(repository, filterName);
        } catch (Exception e) {
            return new Status(IStatus.ERROR, OvCorePlugin.PLUGIN_ID, "Can not get issues. " + e.getMessage(), e);
        }
        for (Issue issue : issues) {
            TaskData taskData;
            try {
                taskData = taskDataHandler.getTaskData(repository, issue, monitor);
                collector.accept(taskData);
            } catch (CoreException e) {
                String message = String.format("Can not get issue data from issue: \"%s\"", issue.getSummary());
                return new Status(IStatus.ERROR, OvCorePlugin.PLUGIN_ID, message, e);
            }
        }
        return Status.OK_STATUS;
    }

    @Override
    public void updateRepositoryConfiguration(TaskRepository taskRepository, IProgressMonitor monitor)
            throws CoreException {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateTaskFromTaskData(TaskRepository taskRepository, ITask task, TaskData taskData) {
        TaskMapper mapper = new TaskMapper(taskData, false);
        mapper.applyTo(task);
    }
}
