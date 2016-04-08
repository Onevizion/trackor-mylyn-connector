package net.vqs.mylyn.connector.core;

import java.util.Set;

import net.vqs.mylyn.connector.vo.Issue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.RepositoryResponse;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskDataHandler;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Field;
import org.eclipse.mylyn.tasks.core.data.DefaultTaskSchema;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMetaData;
import org.eclipse.mylyn.tasks.core.data.TaskData;

public class TaskDataHandler extends AbstractTaskDataHandler {

    private static final String ESTIM_HOURS_KEY = "task.common.estimate";

    private VqsRepositoryConnector connector;

    public TaskDataHandler(VqsRepositoryConnector connector) {
        this.connector = connector;
    }

    @Override
    public RepositoryResponse postTaskData(TaskRepository repository, TaskData taskData,
            Set<TaskAttribute> oldAttributes, IProgressMonitor monitor) throws CoreException {
        return null;
    }

    @Override
    public boolean initializeTaskData(TaskRepository repository, TaskData data, ITaskMapping initializationData,
            IProgressMonitor monitor) throws CoreException {
        createAttribute(data, TaskAttribute.TASK_KEY);
        createAttribute(data, TaskAttribute.SUMMARY);
        createAttribute(data, TaskAttribute.USER_ASSIGNED);
        createVqsAttribute(data, TaskAttribute.PRIORITY);
        createVqsAttribute(data, TaskAttribute.COMPONENT);
        createAttribute(data, TaskAttribute.TASK_URL);

        return true;
    }

    @Override
    public TaskAttributeMapper getAttributeMapper(TaskRepository repository) {
        return new TaskAttributeMapper(repository);
    }

    public TaskData getTaskData(TaskRepository repository, Issue issue, IProgressMonitor monitor) throws CoreException {
        String repositoryUrl = repository.getRepositoryUrl();

        TaskData data = new TaskData(getAttributeMapper(repository), repository.getConnectorKind(), repositoryUrl,
                issue.getId().toString());
        initializeTaskData(repository, data, null, monitor);
        setAttributeValue(data, TaskAttribute.TASK_KEY, issue.getKey());
        setAttributeValue(data, TaskAttribute.SUMMARY, issue.getSummary());
        setAttributeValue(data, TaskAttribute.USER_ASSIGNED, issue.getAssignedTo());
        setAttributeValue(data, TaskAttribute.PRIORITY, issue.getPriority());
        setAttributeValue(data, TaskAttribute.COMPONENT, issue.getComponent());
        setEstimHoursAttribute(data, issue.getEstimatedHours());

        if (!data.isNew()) {
            setAttributeValue(data, TaskAttribute.TASK_URL,
                    connector.getTaskUrl(repositoryUrl, issue.getId().toString()));
        }

        return data;
    }

    private void setAttributeValue(TaskData data, String attributeName, String attributeValue) {
        try {
            TaskAttribute attribute = data.getRoot().getAttribute(attributeName);
            data.getAttributeMapper().setValue(attribute, attributeValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private TaskAttribute createAttribute(TaskData data, String key) {
        TaskAttribute root = data.getRoot();
        TaskAttribute taskAttribute = root.createAttribute(key);
        return taskAttribute;
    }

    private TaskAttribute createVqsAttribute(TaskData data, String key) {
        Field field = DefaultTaskSchema.getField(key);
        TaskAttribute taskAttribute = data.getRoot().createAttribute(key);
        TaskAttributeMetaData metaData = taskAttribute.getMetaData();
        metaData.defaults();
        metaData.setLabel(field.getLabel());
        metaData.setType(field.getType());
        metaData.setKind(field.getKind());
        metaData.putValue("Vqs", key);
        return taskAttribute;
    }

    private void setEstimHoursAttribute(TaskData data, Double hours) {
        TaskAttribute taskAttribute = data.getRoot().createAttribute(ESTIM_HOURS_KEY);
        TaskAttributeMetaData metaData = taskAttribute.getMetaData();
        metaData.defaults();
        metaData.setLabel("Estimated Hours");
        metaData.setType(TaskAttribute.TYPE_SHORT_TEXT);
        metaData.putValue("Vqs", ESTIM_HOURS_KEY);
        setAttributeValue(data, ESTIM_HOURS_KEY, hours.toString());
    }

}
