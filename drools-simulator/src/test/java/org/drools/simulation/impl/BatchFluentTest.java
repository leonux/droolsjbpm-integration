/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.simulation.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.simulation.fluent.batch.FluentBatchExecution;
import org.drools.simulation.fluent.batch.imp.FluentBatchExecutionImpl;
import org.drools.core.io.impl.ByteArrayResource;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.runtime.StatelessKnowledgeSession;

public class BatchFluentTest {

    @Test
    public void testBatchSimple() {
        FluentBatchExecution f = new FluentBatchExecutionImpl();

        List list = new ArrayList();

        // @formatter:off          
        BatchExecutionCommand cmd = f.newBatchExecution()
                .setGlobal("list", list)
                .insert(new Person("yoda", 150)).set("y")
                .insert(new Person("salaboy", 28)).set("x")
                .fireAllRules()
                .getBatchExecution();
        // @formatter:on

        assertEquals(4, ((BatchExecutionCommandImpl) cmd).getCommands().size());

        ExecutionResults results = createStatelessSession().execute(cmd);

        assertEquals(2, results.getIdentifiers().size());
        assertEquals("y", (String) results.getIdentifiers().toArray()[0]);
        assertEquals("x", (String) results.getIdentifiers().toArray()[1]);
    }

    @Test
    public void testBatchSimpleWithSetInGlobal() {
        FluentBatchExecution f = new FluentBatchExecutionImpl();

        List list = new ArrayList();

        // @formatter:off          
        BatchExecutionCommand cmd = f.newBatchExecution()
                .setGlobal("list", list).set("myGlobal")
                .insert(new Person("yoda", 150)).set("y")
                .insert(new Person("salaboy", 28)).set("x")
                .fireAllRules()
                .getBatchExecution();
        // @formatter:on

        assertEquals(4, ((BatchExecutionCommandImpl) cmd).getCommands().size());

        ExecutionResults results = createStatelessSession().execute(cmd);

        assertEquals(3, results.getIdentifiers().size());
        assertEquals("myGlobal", (String) results.getIdentifiers().toArray()[0]);
        assertEquals("y", (String) results.getIdentifiers().toArray()[1]);
        assertEquals("x", (String) results.getIdentifiers().toArray()[2]);
    }

    private StatelessKnowledgeSession createStatelessSession() {
        String str = "package org.drools.simulation.test\n"
                + "import " + Person.class.getName() + "\n"
                + "global java.util.List list\n"
                + "rule updateAge no-loop\n"
                + "  when\n"
                + "    $p : Person()\n"
                + "  then\n"
                + "    modify( $p ) { setAge( $p.getAge() + 10 ) };\n"
                + "end\n";
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ByteArrayResource(str.getBytes()), ResourceType.DRL);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        return kbase.newStatelessKnowledgeSession();
    }

}
