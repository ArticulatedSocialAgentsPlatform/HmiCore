/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package nl.utwente.hmi.middleware;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;


public class ExampleApp {

        private static Logger logger = LoggerFactory.getLogger(ExampleApp.class.getName());

        public static void main(String[] args){
            //sendBMLAMQtoASAP();
            //sendBMLUDPtoASAP();
            //sendBMLAMQtoASAPProps();
            testNewMiddleware();
        }

    private static void testNewMiddleware() {
            try {
                ObjectMapper mapper = new ObjectMapper();
                InputStream inputStream = ExampleApp.class.getClassLoader().getResourceAsStream("middleware.json");
                JsonNode props = mapper.readTree(inputStream);
                MiddlewareWrapper wrapper = MiddlewareWrapperFactory.createMiddlewareWrapper(props);

            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    private static void AMQ(String[] args){
            String defaultProperties = "defaultmiddleware.properties";
            String specificProperties = "ActiveMQ.properties";
            MiddlewareWrapper mw = null;

            if(args.length == 2){
                defaultProperties = args[0];
                specificProperties = args[1];
                logger.debug("Loading properties from arguments {} and {}",defaultProperties,specificProperties);
                mw = new MiddlewareWrapper(defaultProperties, specificProperties) {
                    @Override
                    public void processData(JsonNode jn) {
                        logger.info("Process: {}",jn.toString());
                    }
                };
            }
            else if(ExampleApp.class.getClassLoader().getResourceAsStream(defaultProperties) != null && ExampleApp.class.getClassLoader().getResourceAsStream(specificProperties) != null){
                logger.debug("Loading properties from {} and {}",defaultProperties,specificProperties);
                mw = new MiddlewareWrapper(defaultProperties, specificProperties) {
                    @Override
                    public void processData(JsonNode jn) {
                        logger.info("Process: {}",jn.toString());
                    }
                };
            }

            else {
                logger.debug("No property files or arguments found. Resorting to default properties, with AMQ");
                try{
                    Properties ps = new Properties();
                    ps.put("amqBrokerURI","tcp://localhost:61616");
                    ps.put("loader","nl.utwente.hmi.middleware.activemq.ActiveMQMiddlewareLoader");
                    ps.put("iTopic","input");
                    ps.put("oTopic","output");
                    mw = new MiddlewareWrapper(ps) {
                        @Override
                        public void processData(JsonNode jn) {
                            logger.info("Process: {}",jn.toString());
                        }
                    };
                }
                catch(Exception e){
                    logger.error("{} ActiveMQ is not active on the standard settings. Please start it and try again.",e);
                    e.printStackTrace();
                }

            }

            // Another wrapper for testing the connection.
            String flipperProperties = "flipper.properties";
            MiddlewareWrapper mwF = new MiddlewareWrapper(defaultProperties,flipperProperties) {
                @Override
                public void processData(JsonNode jn) {
                    logger.info("Process: {}", jn.toString());
                }
            };
            if(mw != null) {
                SendingMessages sm = new SendingMessages(mw);
                new Thread(sm).start();
            }
            else {
                logger.error("Cannot send messages, middleware does not exist.");
            }
        }


        /**
         * This class is for testing purposes only and sends 'Hello World!' over the middleware it's constructed with
         */
        private static class SendingMessages implements Runnable{

            private boolean running = true;
            private MiddlewareWrapper amlw;
            private ObjectNode node;

            public SendingMessages(MiddlewareWrapper amlw){
                this.amlw = amlw;
            }

            @Override
            public void run() {
                while(running){
                    try {
                        Thread.sleep(3000);
                        amlw.sendData(node.put("hello","world"));

                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

    /**
     * Test method for sending an example BML over UDP to ASAP with default settings
     */
    private static void sendBMLUDPtoASAP(){
            Properties mwUDP = new Properties();
            mwUDP.put("loader","nl.utwente.hmi.middleware.udp.UDPMiddlewareLoader");
            mwUDP.put("remotePort","6662");
            mwUDP.put("remoteIP","127.0.0.1");
            MiddlewareWrapper bmlOutput = new MiddlewareWrapper(mwUDP) {
                @Override
                public void processData(JsonNode jsonNode) {
                    logger.info("Feedback BML: "+ jsonNode.toString());
                }
            };
            sendBML(bmlOutput);
        }

    /**
     * Test method for sending an example BML over AMQ to ASAP with default settings
     * Note: AMQ service must be running in order for this to work
     */
    private static void sendBMLAMQtoASAPProps(){
        try {
            Properties mwps = new Properties();
            InputStream propStream = ExampleApp.class.getClassLoader().getResourceAsStream("defaultmiddleware.properties");
            InputStream specStream = ExampleApp.class.getClassLoader().getResourceAsStream("ActiveMQ.properties");
            mwps.load(propStream);
            mwps.load(specStream);
            MiddlewareWrapper mwAMQBML = new MiddlewareWrapper(mwps) {
                @Override
                public void processData(JsonNode jsonNode) {
                    logger.info("Feedback BML:" + jsonNode.toString());
                }
            };
            sendBML(mwAMQBML);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test method for sending an example BML over AMQ to ASAP with default settings
     * Note: AMQ service must be running in order for this to work
     */
        private static void sendBMLAMQtoASAP(){
            Properties mwps2 = new Properties();
            mwps2.put("amqBrokerURI", "tcp://localhost:61616");
            mwps2.put("loader", "nl.utwente.hmi.middleware.activemq.ActiveMQMiddlewareLoader");
            mwps2.put("iTopic", "couch.bml.feedback.ASAP");
            mwps2.put("oTopic", "couch.bml.request.ASAP");
            MiddlewareWrapper mwAMQBML = new MiddlewareWrapper(mwps2) {
                @Override
                public void processData(JsonNode jsonNode) {
                    logger.info("Feedback BML:" + jsonNode.toString());
                }
            };
            sendBML(mwAMQBML);
        }

    /**
     * Method for sending the BML in JsonNode format.
     * @param wrapper, the middleware to send over the BML
     */
    private static void sendBML(MiddlewareWrapper wrapper){
            try {
                String bml = "<bml id=\"bml1\" characterId=\"UMA\" xmlns=\"http://www.bml-initiative.org/bml/bml-1.0\" \n" +
                        "  xmlns:bmlt=\"http://hmi.ewi.utwente.nl/bmlt\">\n" +
                        "  <speech id=\"s1\">\n" +
                        "    <text>My name is Zira. Hello<sync id=\"hello\"/> there, how are you?</text>\n" +
                        "  </speech>\n" +
                        "</bml>";
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode bmlNode = mapper.createObjectNode();
                bmlNode.put("content", URLEncoder.encode(bml,"UTF-8"));
                ObjectNode contentNode = mapper.createObjectNode();
                contentNode.set("bml",bmlNode);
                wrapper.sendData(contentNode);
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

}


