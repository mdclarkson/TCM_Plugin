package net.thecodemaster.evd.builder;

import java.util.List;
import java.util.Map;

import net.thecodemaster.evd.Manager;
import net.thecodemaster.evd.graph.CallGraph;
import net.thecodemaster.evd.helper.Creator;
import net.thecodemaster.evd.helper.Timer;
import net.thecodemaster.evd.logger.PluginLogger;
import net.thecodemaster.evd.ui.l10n.Message;
import net.thecodemaster.evd.visitor.VisitorCallGraph;
import net.thecodemaster.evd.visitor.VisitorPointsToAnalysis;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * This class performs its operations in a different(new) thread from the UI Thread. So the user will not be blocked.
 * 
 * @Author: Luciano Sampaio
 * @Date: 2014-05-07
 * @Version: 01
 */
public class BuilderJob extends Job {

	/**
	 * This object contains all the methods, variables and their interactions, on the project that is being analyzed. At
	 * any given time, we should only have one call graph per project.
	 */
	private static Map<IProject, CallGraph>	mapCallGraphs;
	private IProject												project;
	private IResourceDelta									delta;

	static {
		mapCallGraphs = Creator.newMap();
	}

	private BuilderJob(String name) {
		super(name);
	}

	public BuilderJob(String name, IProject project) {
		this(name);

		setProject(project);
		addProjectToList(project);
	}

	public BuilderJob(String name, IResourceDelta delta) {
		this(name);

		setDelta(delta);
	}

	private static Map<IProject, CallGraph> getMapCallGraphs() {
		return mapCallGraphs;
	}

	private IProject getProject() {
		return project;
	}

	private void setProject(IProject project) {
		this.project = project;
	}

	private IResourceDelta getDelta() {
		return delta;
	}

	private void setDelta(IResourceDelta delta) {
		this.delta = delta;
	}

	/**
	 * Add the project to the list of call graphs.
	 * 
	 * @param project
	 *          The project that will be added to the list.
	 */
	private void addProjectToList(IProject project) {
		if (!getMapCallGraphs().containsKey(project)) {
			getMapCallGraphs().put(project, new CallGraph());
		}
	}

	/**
	 * Returns the call graph of the current project.
	 * 
	 * @return The call graph of the current project.
	 */
	private CallGraph getCallGraph() {
		if (null != getDelta()) {
			return getMapCallGraphs().get(getDelta().getResource().getProject());
		} else {
			return getMapCallGraphs().get(getProject());
		}
	}

	/**
	 * Returns whether cancellation of current operation has been requested
	 * 
	 * @param reporter
	 * @return true if cancellation has been requested, and false otherwise.
	 */
	private boolean userCanceledProcess(IProgressMonitor monitor) {
		return ((null != monitor) && (monitor.isCanceled()));
	}

	public void run() {
		schedule();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			// 01 - The manager knows what are the actions that should be performed.
			Manager manager = Manager.getInstance();
			if (manager.shouldPerformVerifications()) {
				Timer timerCP = (new Timer("01 - Complete Process: ")).start();

				// 02 - Get the CallGraph instance for this project.
				CallGraph callGraph = getCallGraph();
				if (null != callGraph) {
					monitor.beginTask(Message.Plugin.TASK, IProgressMonitor.UNKNOWN);

					List<IResource> resourcesUpdated = Creator.newList();
					if (!userCanceledProcess(monitor)) {
						// 03 - Use the VISITOR pattern to create/populate the call graph.
						resourcesUpdated = createCallGraph(new VisitorCallGraph(monitor, callGraph));
					} else {
						return Status.CANCEL_STATUS;
					}

					if (!userCanceledProcess(monitor)) {
						// 04 - Link variables and methods to content.
						populateCallGraph(monitor, callGraph, resourcesUpdated);
					} else {
						return Status.CANCEL_STATUS;
					}

					if (!userCanceledProcess(monitor)) {
						// 05 - Run the plug-in's verifications.
						runDetection(monitor, callGraph, resourcesUpdated, manager);
					} else {
						return Status.CANCEL_STATUS;
					}
				} else {
					String projectName = (null != getProject()) ? getProject().getName() : "";
					PluginLogger.logError(String.format(Message.Error.CALL_GRAPH_DOES_NOT_CONTAIN_PROJECT, projectName), null);
				}
				PluginLogger.logInfo(timerCP.stop().toString());
			}
		} catch (CoreException e) {
			PluginLogger.logError(e);
			return e.getStatus();
		} catch (Exception e) {
			PluginLogger.logError(e);
			return Status.CANCEL_STATUS;
		} finally {
			if (null != monitor) {
				monitor.done();
			}
		}

		return Status.OK_STATUS;
	}

	private List<IResource> createCallGraph(VisitorCallGraph visitorCallGraph) throws CoreException {
		List<IResource> resourcesUpdated = Creator.newList();

		if (null != getDelta()) {
			Timer timer = (new Timer("01.1 - Call Graph Delta: ")).start();
			// 03 - Use the VISITOR pattern to create/populate the call graph.
			resourcesUpdated = visitorCallGraph.run(getDelta());
			PluginLogger.logIfDebugging(timer.stop().toString());
		} else if (null != getProject()) {
			Timer timer = (new Timer("01.1 - Call Graph Project: ")).start();
			// 03 - Use the VISITOR pattern to create/populate the call graph.
			resourcesUpdated = visitorCallGraph.run(getProject());
			PluginLogger.logIfDebugging(timer.stop().toString());
		}

		return resourcesUpdated;
	}

	private void populateCallGraph(IProgressMonitor monitor, CallGraph callGraph, List<IResource> resourcesUpdated) {
		Timer timer = (new Timer("01.2 - Points-to Analysis: ")).start();
		// 04 - Link variables and methods to content.
		VisitorPointsToAnalysis pointToAnalysis = new VisitorPointsToAnalysis();
		pointToAnalysis.run(monitor, callGraph, resourcesUpdated);
		PluginLogger.logIfDebugging(timer.stop().toString());
	}

	private void runDetection(IProgressMonitor monitor, CallGraph callGraph, List<IResource> resourcesUpdated,
			Manager manager) {
		Timer timer = (new Timer("01.3 - Plug-in verifications: ")).start();
		// 05 - Perform the plug-in's verifications.
		// manager.run(monitor, callGraph, resourcesUpdated);
		PluginLogger.logIfDebugging(timer.stop().toString());
	}

}
