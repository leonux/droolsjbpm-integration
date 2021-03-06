/*
 * Copyright 2010 JBoss Inc
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

package org.drools.examples.broker;

import org.drools.examples.broker.events.Event;
import org.drools.examples.broker.events.EventReceiver;
import org.drools.examples.broker.model.Company;
import org.drools.examples.broker.model.CompanyRegistry;
import org.drools.examples.broker.model.StockTick;
import org.drools.examples.broker.ui.BrokerWindow;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KieBaseConfiguration;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.conf.MBeansOption;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.SessionEntryPoint;

/**
 * The broker application
 */
public class Broker implements EventReceiver, BrokerServices {

    private static final String[] ASSET_FILES = {
            "/org/drools/examples/broker/rules/broker.drl",
            "/org/drools/examples/broker/rules/notify.drl",
            "/org/drools/examples/broker/rules/position.rf",
            "/org/drools/examples/broker/rules/position.drl"};
    
    private BrokerWindow window;
    private CompanyRegistry companies;
    private StatefulKnowledgeSession session;
    private SessionEntryPoint tickStream;

    public Broker(BrokerWindow window,
                  CompanyRegistry companies) {
        super();
        this.window = window;
        this.companies = companies;
        this.session = createSession();
        this.tickStream = this.session.getEntryPoint( "StockTick stream" );
    }

    @SuppressWarnings("unchecked")
    public void receive(Event<?> event) {
        try {
            StockTick tick = ((Event<StockTick>) event).getObject();
            Company company = this.companies.getCompany( tick.getSymbol() );
            this.tickStream.insert( tick );
            this.session.getAgenda().getAgendaGroup( "evaluation" ).setFocus();
            this.session.fireAllRules();
            window.updateCompany( company.getSymbol() );
            window.updateTick( tick );
            
        } catch ( Exception e ) {
            System.err.println("=============================================================");
            System.err.println("Unexpected exception caught: "+e.getMessage() );
            e.printStackTrace();
        }
    }
    
    private StatefulKnowledgeSession createSession() {
        KnowledgeBase kbase = loadRuleBase();
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
//        session.addEventListener( new DebugAgendaEventListener() );
//        session.addEventListener( new DebugWorkingMemoryEventListener() );
//        session.addEventListener( new DebugProcessEventListener() );         
        
        //KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newConsoleLogger( session );
        session.setGlobal( "services", this );
        for( Company company : this.companies.getCompanies() ) {
            session.insert( company );
        }
        session.fireAllRules();
        return session;
    }

    private KnowledgeBase loadRuleBase() {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        try {
            for( int i = 0; i < ASSET_FILES.length; i++ ) {
                builder.add( ResourceFactory.newInputStreamResource( Broker.class.getResourceAsStream( ASSET_FILES[i] ) ),
                             ResourceType.determineResourceType( ASSET_FILES[i] ));
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 0 );
        }
        if( builder.hasErrors() ) {
            System.err.println(builder.getErrors());
            System.exit( 0 );
        }
        KieBaseConfiguration conf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        conf.setOption( EventProcessingOption.STREAM );
        conf.setOption( MBeansOption.ENABLED );
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( "Stock Broker", conf );
        kbase.addKnowledgePackages( builder.getKnowledgePackages() );
        return kbase;
    }

    public void log(String message) {
        window.log( message );
    }

}
