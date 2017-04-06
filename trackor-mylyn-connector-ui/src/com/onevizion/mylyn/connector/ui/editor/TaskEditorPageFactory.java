package com.onevizion.mylyn.connector.ui.editor;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.forms.editor.IFormPage;

import com.onevizion.mylyn.connector.core.OvCorePlugin;

public class TaskEditorPageFactory extends AbstractTaskEditorPageFactory {

    public TaskEditorPageFactory() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean canCreatePageFor(TaskEditorInput input) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public IFormPage createPage(TaskEditor parentEditor) {
        try {
            ITask task = parentEditor.getTaskEditorInput().getTask();
            IWebBrowser webBrowser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(task.getTaskId());
            webBrowser.openURL(new URL(task.getUrl()));
        } catch (PartInitException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new TaskEditorPage(parentEditor, OvCorePlugin.CONNECTOR_KIND);
    }

    @Override
    public Image getPageImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPageText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getConflictingIds(TaskEditorInput input) {
        if (!input.getTask().getConnectorKind().equals(OvCorePlugin.CONNECTOR_KIND)) {
            return new String[] { ITasksUiConstants.ID_PAGE_PLANNING };
        }
        return null;
    }

}
