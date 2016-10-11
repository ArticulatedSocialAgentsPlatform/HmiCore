/**
 * Copyright 2011 Mark ter Maat, Human Media Interaction, University of Twente.
 * All rights reserved. This program is distributed under the BSD License.
 */

package hmi.flipper.behaviourselection;

import hmi.flipper.behaviourselection.template.Template;
import hmi.flipper.behaviourselection.template.TemplateState;
import hmi.flipper.behaviourselection.template.effects.Effect;
import hmi.flipper.behaviourselection.template.effects.Update;
import hmi.flipper.defaultInformationstate.DefaultRecord;
import hmi.flipper.exceptions.TemplateRunException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;


/**
 * The TemplateController, as the name suggests, controlls Templates.
 * It is given 1 or more TemplateFiles, uses this to create a list of Templates, and when asked (with a given InformationState),
 * it checks all Templates and selects a Template to execute.
 * 
 * It will only execute 1 Template which has a Behaviour, but Templates without Behaviours (InformationState updates),
 * are always executed.
 * 
 * @author Mark ter Maat
 * @version 0.1
 *
 */

public class TemplateController
{
	private static Logger logger = LoggerFactory.getLogger(TemplateController.class.getName());

    private static final int RECENCY_SIZE = 5;
    private static final boolean SHOW_EFFECT_CONFLICTS = true;
    private static final double HISTORY_MODIFIER = 0.1;
    
    /* The TemplateParser to parse Template-files */
    private TemplateParser templateParser = new TemplateParser();

    /* A list of classes which contain user-specified functions */
    private ArrayList<Object> functionClasses = new ArrayList<Object>();

    /* A list with all Templates it knows */
    protected ArrayList<Template> templates = new ArrayList<Template>();

    /* A list with recently executed Templates with Behaviour */
    private ArrayList<String> recentRunTemplates = new ArrayList<String>();

    protected DefaultRecord internalIS;

    /**
     * Creates a new TemplateController
     */
    public TemplateController( boolean useInternalIS )
    {
        if( useInternalIS ) {
            internalIS = new DefaultRecord();
        } else {
            internalIS = null;
        }
    }

    /**
     * Creates a new TemplateController
     */
    public TemplateController()
    {
        this(false);
    }

    /**
     * Processes the given TemplateFile, asks the parser to Parse it, and adds the returned list of Templates to its own list.
     * 
     * @param templateFile - the Template-filename
     * @return true if the parsing was succesfull, and false if it wasn't.
     */
    public boolean processTemplateFile( String templateFile )
    {
        if( templateFile.length() == 0 ) return false;
        try {
            ArrayList<Template> lijst = templateParser.parseFile(templateFile); 
            if( lijst != null ) templates.addAll(lijst);
            else return false;
        }catch( ParserConfigurationException e ) {
            e.printStackTrace();
            return false;
            // TODO: Handle exception
        }catch( SAXException e ) {
            e.printStackTrace();
            return false;
            // TODO: Handle exception
        }catch( IOException e ) {
            e.printStackTrace();
            return false;
            // TODO: Handle exception
        }
        return true;
    }



    /**
     * Given an InformationState, check all known Templates and select 0, 1, or more to execute.
     * If no Templates are found with all Preconditions fulfilled, none will be executed.
     * It a Template has all Preconditions except the Triggers fulfilled and contains a Behaviour, this Behaviour will be prepared.
     * Only 1 Template with a Behaviour will be executed with each call.
     * Templates without a Behaviour (IS-updates) are always executed.
     * 
     * @param is - the current InformationState.
     * @return true if everything went correctly, false if an error occurred.
     */
    public boolean checkTemplates( DefaultRecord is )
    {
        /* Lists of Templates which are only IS-updates, templates that should be prepared, and templates that could be executed */
        ArrayList<TemplateState> isUpdates = new ArrayList<TemplateState>();
        ArrayList<TemplateState> templatesToPrepare = new ArrayList<TemplateState>();
        ArrayList<TemplateState> templatesToRun = new ArrayList<TemplateState>();

        /* Check all Templates */
        for( Template template : templates ) {
            TemplateState state = template.checkTemplate(is);
            if( state.isComparesSatisfied() && state.isIndicatorsSatisfied() ) {
                if( state.isTriggersSatisfied() ) {
                    if( state.getEffects().size() > 0 && state.getBehaviour() == null ) {
                        isUpdates.add(state);
                    } else {
                        templatesToRun.add(state);
                    }
                } else if( state.getBehaviour() != null ) {
                    templatesToPrepare.add(state);
                }
            }
        }

        if( templatesToRun.size() > 0 ) {
            if( templatesToRun.size() == 1 ) {
                /* If there is only 1 Template to run, check for conflicting Effects, apply the Effects, and run the Behaviour */
                TemplateState state = templatesToRun.get(0);
                logger.info("Executing template with behaviour: {}", state.getTemplate().getName());
                checkConflictingEffects(state.getEffects(),isUpdates);

                for( Effect effect : state.getEffects() ) {
                    try {
                        effect.apply(is, this);
                    }catch( TemplateRunException e ) {
                        logger.error("Error while applying effect of Template {}({}) ", state.getTemplate().getId(), state.getTemplate().getName());
                        e.printStackTrace();
                        return false;
                    }
                }

                try {
                    state.getBehaviour().execute(is);
                }catch( TemplateRunException e ) {
                    logger.error("Error while executing behaviour of Template {}({}) ", state.getTemplate().getId(), state.getTemplate().getName());
                    e.printStackTrace();
                    return false;
                }
                addRecentBehaviour(state);
            } else {
                /* If there are more than 1 Template to run, select one, 
                    heck for conflicting Effects, apply the Effects, and run the selected Behaviour */
                /* Modify the quality-values based on the history */
                templatesToRun = modifyQualityBasedOnHistory( templatesToRun );
                /* Select the Behaviour with the highest quality-value */
                TemplateState state = getBestTemplate( templatesToRun );
                logger.info("Executing \"random\" template with behaviour: {}", state.getTemplate().getName());
                checkConflictingEffects(state.getEffects(),isUpdates);

                for( Effect effect : state.getEffects() ) {
                    try {
                        effect.apply(is, this);
                    }catch( TemplateRunException e ) {
                        logger.error("Error while applying effect of Template {}({}) ", state.getTemplate().getId(), state.getTemplate().getName());
                        e.printStackTrace();
                        return false;
                    }
                }

                try {
                    state.getBehaviour().execute(is);
                }catch( TemplateRunException e ) {
                    logger.error("Error while executing behaviour of Template {}({}) ", state.getTemplate().getId(), state.getTemplate().getName());
                    e.printStackTrace();
                    return false;
                }
                addRecentBehaviour(state);
            }
        } else {
            /* If there are no Behaviours to execute, check for conflicting Effects, 
             * apply the Effects, and prepare the Behaviours that can be prepared */
        	//TODO: the effects are not applied here... is this a bug... or intended behaviour to only apply effects AFTER triggers have been satisfied? 
            for( TemplateState state : templatesToPrepare ) {
                try {            
                    logger.info("Preparing template with behaviour: {}", state.getTemplate().getName());
                    state.getBehaviour().prepare(is);
                }catch( TemplateRunException e ) {
                    logger.error("Error while preparing behaviour of Template {}({}) ", state.getTemplate().getId(), state.getTemplate().getName());
                    e.printStackTrace();
                    return false;
                }
            }
            checkConflictingEffects(new ArrayList<Effect>(),isUpdates);
        }

        /* Apply all Effects of the IS-update Templates */
        for( TemplateState state : isUpdates ) {
            logger.info("Executing template without behaviour: {}", state.getTemplate().getName());
            for( Effect effect : state.getEffects() ) {
                try {
                    effect.apply(is, this);
                }catch( TemplateRunException e ) {
                    logger.error("Error while applying effect of Template {}({}) ", state.getTemplate().getId(), state.getTemplate().getName());
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    public void checkTemplates()
    {
        if( internalIS != null ) {
            checkTemplates(internalIS);
        }
    }

    /**
     * Update the list of recently executed Behaviours, based on the given TemplateState
     * @param state
     */
    public void addRecentBehaviour( TemplateState state )
    {
        recentRunTemplates.add(state.getTemplate().getId());
        if( recentRunTemplates.size() > RECENCY_SIZE ) {
            recentRunTemplates.remove(0);
        }
    }

    /**
     * Searches all given Effects for conflicting Effects, that is, Effects that modify the exact same variable in the InformationState
     * 
     * @param effects1
     * @param states
     */
    private void checkConflictingEffects( ArrayList<Effect> effects1, ArrayList<TemplateState> states )
    {
        ArrayList<String> names = new ArrayList<String>();
        for( Effect effect : effects1 ) {
            if( effect instanceof Update ) {
                Update u = (Update)effect;
                String name = u.getName();
                if( !names.contains(name) ) {
                    names.add(name);
                } else {
                    if( SHOW_EFFECT_CONFLICTS ) {
                        logger.warn("Warning, conflicting InformationState updates, may cause possible unexpected behaviour: {}",name);
                    }
                }
            }
        }
        for( TemplateState state : states ) {
            for( Effect effect : state.getEffects() ) {
                if( effect instanceof Update ) {
                    Update u = (Update)effect;
                    String name = u.getName();
                    if( !names.contains(name) ) {
                        names.add(name);
                    } else {
                        if( SHOW_EFFECT_CONFLICTS ) {
                            logger.warn("Warning, conflicting InformationState updates, may cause possible unexpected behaviour: {}",name);
                        }
                    }
                }
            }
        }
    }

    /**
     * Modifies the quality-value of the given TemplateStates based on the behaviour history.
     * The more recent a certain Behaviour was executed, the more its quality-value will be decreased.
     * 
     * @param states
     * @return
     */
    public ArrayList<TemplateState> modifyQualityBasedOnHistory( ArrayList<TemplateState> states )
    {
        for( TemplateState state : states ) {
            String id = state.getTemplate().getId();
            if( recentRunTemplates.contains(id) ) {
                int index = recentRunTemplates.indexOf(id);
                state.setQuality(state.getQuality() - ((index+1)*HISTORY_MODIFIER));
            }
        }
        return states;
    }

    /**
     * Returns the TemplateState which includes the Behaviour with the highest quality-value
     * @param states
     * @return
     */
    public TemplateState getBestTemplate( ArrayList<TemplateState> states )
    {
        TemplateState bestState = null;
        double quality = Double.MIN_VALUE;
        for( TemplateState state : states ) {
            if( state.getQuality() > quality ) {
                quality = state.getQuality();
                bestState = state;
            }
        }
        return bestState;
    }

    /**
     * Adds the given Object to the list of classes that contains user-specified functions.
     * @param obj
     */
    public void addFunction( Object obj)
    {
        functionClasses.add(obj);
    }

    /**
     * @return the list of classes tha contain user-specified functions.
     */
    public ArrayList<Object> getFunctionClasses()
    {
        return functionClasses;
    }

    /**
     * @return the internal InformationState (could be null)
     */
    public DefaultRecord getIS()
    {
        return internalIS;
    }
}