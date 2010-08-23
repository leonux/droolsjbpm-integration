package org.drools.grid.remote;

import java.util.Collection;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.command.ExecuteCommand;
import org.drools.command.FinishedCommand;
import org.drools.command.KnowledgeContextResolveFromContextCommand;
import org.drools.command.runtime.GetGlobalCommand;
import org.drools.command.runtime.SetGlobalCommand;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.common.DefaultFactHandle;
import org.drools.common.DisconnectedFactHandle;
import org.drools.grid.internal.commands.RegisterRemoteWorkItemHandlerCommand;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.Calendars;
import org.drools.runtime.Channel;
import org.drools.runtime.Environment;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.Globals;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.rule.Agenda;
import org.drools.runtime.rule.AgendaFilter;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.LiveQuery;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.ViewChangedEventListener;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.grid.GenericNodeConnector;
import org.drools.time.SessionClock;
import org.drools.grid.internal.Message;
import org.drools.grid.internal.MessageSession;
import org.drools.grid.remote.internal.commands.GetWorkItemManagerCommand;
import org.drools.grid.remote.internal.commands.GetWorkingMemoryEntryPointRemoteCommand;
import org.drools.grid.remote.internal.commands.StartProcessRemoteCommand;


/*
 * @author: salaboy
 */
public class StatefulKnowledgeSessionRemoteClient
        implements
        StatefulKnowledgeSession {

    private GenericNodeConnector connector;
    private MessageSession messageSession;
    private String instanceId;

    public StatefulKnowledgeSessionRemoteClient(String instanceId,
            GenericNodeConnector connector, MessageSession messageSession) {
        this.instanceId = instanceId;
        this.connector = connector;
        this.messageSession = messageSession;

    }

    public String getInstanceId() {
        return this.instanceId;
    }

    public void dispose() {
        // TODO Auto-generated method stub
    }

    public int getId() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int fireAllRules() {
        String commandId = "ksession.fireAllRules" + messageSession.getNextId();
        String kresultsId = "kresults_" + messageSession.getSessionId();

        Message msg = new Message(messageSession.getSessionId(),
                messageSession.counter.incrementAndGet(),
                false,
                new KnowledgeContextResolveFromContextCommand(CommandFactory.newFireAllRules(commandId),
                null,
                null,
                instanceId,
                kresultsId));
        try {
            connector.connect();
            Object object = connector.write(msg).getPayload();

            if (object == null) {
                throw new RuntimeException("Response was not correctly received");
            }
            connector.disconnect();
            //return (Integer) ((ExecutionResults) object).getValue(commandId);
            return (Integer)  object;
        } catch (Exception e) {
            throw new RuntimeException("Unable to execute message",
                    e);
        }
    }

    public int fireAllRules(int max) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int fireAllRules(AgendaFilter agendaFilter) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void fireUntilHalt() {
        // TODO Auto-generated method stub
    }

    public void fireUntilHalt(AgendaFilter agendaFilter) {
        // TODO Auto-generated method stub
    }

    public ExecutionResults execute(Command command) {
        String commandId = "ksession.execute" + messageSession.getNextId();
        String kresultsId = "kresults_" + messageSession.getSessionId();

        Message msg = new Message(messageSession.getSessionId(),
                messageSession.counter.incrementAndGet(),
                false,
                new KnowledgeContextResolveFromContextCommand(new ExecuteCommand(commandId,
                command),
                null,
                null,
                instanceId,
                kresultsId));

        try {
            connector.connect();
            Object object = connector.write(msg).getPayload();
            if (object == null) {
                throw new RuntimeException("Response was not correctly received");
            }
            connector.disconnect();
            return (ExecutionResults) ((ExecutionResults) object).getValue(commandId);
        } catch (Exception e) {
            throw new RuntimeException("Unable to execute message",
                    e);
        }
    }

    public Environment getEnvironment() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getGlobal(String identifier) {
        String commandId = "ksession.execute" + messageSession.getNextId();
        String kresultsId = "kresults_" + messageSession.getSessionId();

        Message msg = new Message(messageSession.getSessionId(),
                messageSession.counter.incrementAndGet(),
                false,
                new KnowledgeContextResolveFromContextCommand(new GetGlobalCommand(identifier),
                null,
                null,
                instanceId,
                kresultsId));

        try {
            connector.connect();
            Object result = connector.write(msg).getPayload();
            if (result == null) {
                throw new RuntimeException("Response was not correctly received = null");
            }
            connector.disconnect();
            return result;


        } catch (Exception e) {
            throw new RuntimeException("Unable to execute message",
                    e);
        }
    }

    public Globals getGlobals() {
        // TODO Auto-generated method stub
        return null;
    }

    public KnowledgeBase getKnowledgeBase() {
        // TODO Auto-generated method stub
        return null;
    }

    public <T extends SessionClock> T getSessionClock() {
        // TODO Auto-generated method stub
        return null;
    }

    public void registerExitPoint(String name,
            ExitPoint exitPoint) {
        // TODO Auto-generated method stub
    }

    public void setGlobal(String identifier,
            Object object) {
        String commandId = "ksession.execute" + messageSession.getNextId();
        String kresultsId = "kresults_" + messageSession.getSessionId();

        Message msg = new Message(messageSession.getSessionId(),
                messageSession.counter.incrementAndGet(),
                false,
                new KnowledgeContextResolveFromContextCommand(new SetGlobalCommand(identifier,
                object),
                null,
                null,
                instanceId,
                kresultsId));

        try {
            connector.connect();
            Object result = connector.write(msg).getPayload();
            if (result == null) {
                throw new RuntimeException("Response was not correctly received = null");
            }

            if (!(result instanceof FinishedCommand)) {
                throw new RuntimeException("Response was not correctly received");
            }
            connector.disconnect();

        } catch (Exception e) {
            throw new RuntimeException("Unable to execute message",
                    e);
        }

    }

    public void unregisterExitPoint(String name) {
        // TODO Auto-generated method stub
    }

    public Agenda getAgenda() {
        // TODO Auto-generated method stub
        return null;
    }

    public QueryResults getQueryResults(String query) {
        // TODO Auto-generated method stub
        return null;
    }

    public QueryResults getQueryResults(String query,
            Object[] arguments) {
        // TODO Auto-generated method stub
        return null;
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        String commandId = "ksession.getWorkingMemoryEntryPoint" + messageSession.getNextId();
        String kresultsId = "kresults_" + messageSession.getSessionId();

        Message msg = new Message(messageSession.getSessionId(),
                messageSession.counter.incrementAndGet(),
                false,
                new KnowledgeContextResolveFromContextCommand(new GetWorkingMemoryEntryPointRemoteCommand(name),
                null,
                null,
                instanceId,
                name,
                kresultsId));

        try {
            connector.connect();
            Object object = connector.write(msg).getPayload();

            if (object == null) {
                throw new RuntimeException("Response was not correctly received");
            }
            connector.disconnect();
            return new WorkingMemoryEntryPointRemoteClient(name, connector, messageSession);
        } catch (Exception e) {
            throw new RuntimeException("Unable to execute message",
                    e);
        }
    }

    public Collection<? extends WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints() {
        // TODO Auto-generated method stub
        return null;
    }

    public void halt() {
        // TODO Auto-generated method stub
    }

    public FactHandle getFactHandle(Object object) {
        // TODO Auto-generated method stub
        return null;
    }

    public <T extends FactHandle> Collection<T> getFactHandles() {
        // TODO Auto-generated method stub
        return null;
    }

    public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getObject(FactHandle factHandle) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<Object> getObjects() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<Object> getObjects(ObjectFilter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    public FactHandle insert(Object object) {
        String commandId = "ksession.insert" + messageSession.getNextId();
        String kresultsId = "kresults_" + messageSession.getSessionId();


        Message msg = new Message(messageSession.getSessionId(),
                messageSession.counter.incrementAndGet(),
                false,
                new KnowledgeContextResolveFromContextCommand(new InsertObjectCommand(object, true),
                null,
                null,
                instanceId,
                kresultsId));

        try {
            connector.connect();
            Object result = connector.write(msg).getPayload();
            if (object == null) {
                throw new RuntimeException("Response was not correctly received");
            }
            DefaultFactHandle handle = (DefaultFactHandle) result;
            
            connector.disconnect();
            return handle;
        } catch (Exception e) {
            throw new RuntimeException("Unable to execute message",
                    e);
        }
    }

    public void retract(FactHandle handle) {
        // TODO Auto-generated method stub
    }

    public void update(FactHandle handle,
            Object object) {
        // TODO Auto-generated method stub
    }

    public void abortProcessInstance(long id) {
        // TODO Auto-generated method stub
    }

    public ProcessInstance getProcessInstance(long id) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<ProcessInstance> getProcessInstances() {
        // TODO Auto-generated method stub
        return null;
    }

    public WorkItemManager getWorkItemManager() {
        String kresultsId = "kresults_" + messageSession.getSessionId();
        Message msg = new Message(messageSession.getSessionId(),
                messageSession.counter.incrementAndGet(),
                true,
                new KnowledgeContextResolveFromContextCommand(new GetWorkItemManagerCommand(),
                null,
                null,
                instanceId,
                kresultsId));
        try {
            connector.connect();
            Object payload = connector.write(msg).getPayload();
            WorkItemManager workItemManager = (WorkItemManager) ((ExecutionResults) payload).getValue("workItemManager");
            ((WorkItemManagerRemoteClient) workItemManager).setConnector(connector);
            ((WorkItemManagerRemoteClient) workItemManager).setMessageSession(messageSession);
            ((WorkItemManagerRemoteClient) workItemManager).setInstanceId(instanceId);
            connector.disconnect();
            return workItemManager;
        } catch (Exception e) {
            throw new RuntimeException("Unable to execute message", e);
        }
    }

    public void registerWorkItemHandler(String name, String workItemHandler) {

        String kresultsId = "kresults_" + messageSession.getSessionId();

        Message msg = new Message(messageSession.getSessionId(),
                messageSession.counter.incrementAndGet(),
                false,
                new KnowledgeContextResolveFromContextCommand(new RegisterRemoteWorkItemHandlerCommand(name, workItemHandler),
                null,
                null,
                instanceId,
                kresultsId));

        try {
            connector.connect();
            connector.write(msg);
            connector.disconnect();
        } catch (Exception e) {
            throw new RuntimeException("Unable to execute message",
                    e);
        }


    }

    public void signalEvent(String type,
            Object event) {
        // TODO Auto-generated method stub
    }

    public ProcessInstance startProcess(String processId) {
        String commandId = "ksession.execute" + messageSession.getNextId();
        String kresultsId = "kresults_" + messageSession.getSessionId();

        Message msg = new Message(messageSession.getSessionId(),
                messageSession.counter.incrementAndGet(),
                false,
                new KnowledgeContextResolveFromContextCommand(new StartProcessRemoteCommand(processId),
                null,
                null,
                instanceId,
                kresultsId));

        try {
            connector.connect();
            Object object = connector.write(msg).getPayload();
            if (object == null) {
                throw new RuntimeException("Response was not correctly received");
            }
            connector.disconnect();
            return (ProcessInstance) ((ExecutionResults) object).getValue(processId);
        } catch (Exception e) {
            throw new RuntimeException("Unable to execute message",
                    e);
        }

    }

    public ProcessInstance startProcess(String processId,
            Map<String, Object> parameters) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addEventListener(WorkingMemoryEventListener listener) {
        // TODO Auto-generated method stub
    }

    public void addEventListener(AgendaEventListener listener) {
        // TODO Auto-generated method stub
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<WorkingMemoryEventListener> getWorkingMemoryEventListeners() {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        // TODO Auto-generated method stub
    }

    public void removeEventListener(AgendaEventListener listener) {
        // TODO Auto-generated method stub
    }

    public void addEventListener(ProcessEventListener listener) {
        // TODO Auto-generated method stub
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeEventListener(ProcessEventListener listener) {
        // TODO Auto-generated method stub
    }

    public String getEntryPointId() {
        // TODO Auto-generated method stub
        return null;
    }

    public long getFactCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void signalEvent(String type,
            Object event,
            long processInstanceId) {
        // TODO Auto-generated method stub
    }

    public Calendars getCalendars() {
        // TODO Auto-generated method stub
        return null;
    }

    public void registerChannel(String name, Channel channel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unregisterChannel(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, Channel> getChannels() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public LiveQuery openLiveQuery(String query, Object[] arguments, ViewChangedEventListener listener) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}