/*
 * Copyright (C) 2014 kofarrell.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.communication.pubsub;

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import java.util.List;
import java.util.ArrayList;

import burlap.oomdp.core.State;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.singleagent.explorer.TerminalExplorer;

import burlap.behavior.singleagent.planning.deterministic.informed.astar.AStar;
import burlap.behavior.statehashing.DiscretizingStateHashFactory; 
import burlap.behavior.singleagent.Policy;

import burlap.behavior.singleagent.planning.deterministic.GoalConditionTF;
import burlap.behavior.singleagent.planning.deterministic.informed.NullHeuristic;
import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.GroundedProp;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.core.AbstractGroundedAction;
import burlap.behavior.singleagent.planning.deterministic.DDPlannerPolicy;
import burlap.behavior.singleagent.planning.StateConditionTest;

import object_recognition_msgs.*;
import geometry_msgs.*;
import burlap_msgs.*;
import move_msgs.*;
import com.github.communication.pubsub.DiscretizedStatePlanTest.InRegionGoal;

import org.ros.node.topic.Publisher;

/**
 * A simple {@link Subscriber} {@link NodeMain}.
 */
public class Listener extends AbstractNodeMain {
  Domain domain;
  PickupAndPlaceDomain gen;

  public Listener() {
    PickupAndPlaceDomain gen = new PickupAndPlaceDomain(true);
    domain = gen.generateDomain();
  }
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("rosjava/listener");
  }

  // Actual communication with ORK via /recognized_object_array topic
  /*public void onStart(ConnectedNode connectedNode) {
    final Log log = connectedNode.getLog();
    Subscriber<RecognizedObjectArray> subscriber = connectedNode.newSubscriber("/recognized_object_array", RecognizedObjectArray._TYPE);
    subscriber.addMessageListener(new MessageListener<RecognizedObjectArray>() {
      @Override
      public void onNewMessage(RecognizedObjectArray array) {
	State s = new State();

	String coconut_id = "925c42faeac061f86fdbcf0b090efe57";
	String block_id = "925c42faeac061f86fdbcf0b09100a87";	

	for (RecognizedObject obj : array.getObjects()) {
	  String id = obj.getType().getKey();
	  if (id.equals(coconut_id) || id.equals(block_id)) {
	    String name = (id.equals(block_id)) ? "block" : "coconut_can";
	    String object_class = "object";
            double x = obj.getPose().getPose().getPose().getPosition().getX();
            double y = obj.getPose().getPose().getPose().getPosition().getY();
            double z = obj.getPose().getPose().getPose().getPosition().getZ();

	    ObjectInstance o = new ObjectInstance(domain.getObjectClass(object_class), name);
	    s.addObject(o);
	    o.setValue("x", x);
            o.setValue("y", y);
	    o.setValue("z", z);
            o.setValue("color", "red");
	  }	
        }
      }*/
 
  public void onStart(ConnectedNode connectedNode) {
    final Log log = connectedNode.getLog();
    Subscriber<RecognizedObjectArray> subscriber = connectedNode.newSubscriber("/recognized_object_array", RecognizedObjectArray._TYPE);

    //Subscriber<burlap_state> subscriber = connectedNode.newSubscriber("chatter", burlap_state._TYPE);

    final Publisher<moveObject> object_maker = connectedNode.newPublisher("move_objects", moveObject._TYPE);
    final Publisher<moveRegion> region_maker = connectedNode.newPublisher("move_region", moveRegion._TYPE);
    final Publisher<moveAction> moveAction_maker = connectedNode.newPublisher("move_Actions",moveAction._TYPE);

    final Publisher<Point> point_maker = connectedNode.newPublisher("PointMaker", Point._TYPE);
    final Publisher<Vector3> vector3_maker = connectedNode.newPublisher("Vector3Maker", Vector3._TYPE);
    
    // Tester to see that messages are being sent out correctly
    Subscriber<moveAction> action_subscriber = connectedNode.newSubscriber("move_Actions", moveAction._TYPE);
    action_subscriber.addMessageListener(new MessageListener<moveAction>() {	
	public void onNewMessage(moveAction action) {
	  System.out.println("Receiving move Action!");
	  moveObject object = action.getObject();
	  moveRegion region = action.getRegion();
	  System.out.println("Object: "+object.getName()+" with HashID: "+object.getHashID());
	  Point origin = region.getOrigin();
	  Vector3 scale = region.getScale();
	  System.out.println(region.getName()+"at "+origin.getX()+","+origin.getY()+","+origin.getZ());
	  System.out.println("Region has scale <"+scale.getX()+","+scale.getY()+","+scale.getZ()+">");
	}
      });

    subscriber.addMessageListener(new MessageListener<RecognizedObjectArray>() {
      @Override
      public void onNewMessage(RecognizedObjectArray array) {
	State s = gen.getCleanState(domain, 2, 9);
	PickupAndPlaceDomain.tileRegions(s, 3, 3, 0, 100., 0, 100, 20);

	String coconut_id = "925c42faeac061f86fdbcf0b090efe57";
	String block_id = "925c42faeac061f86fdbcf0b09100a87";	

	// TODO: Deal with Header/FrameIDs
	for (RecognizedObject obj : array.getObjects()) {
	  String id = obj.getType().getKey();
	  if (id.equals(coconut_id) || id.equals(block_id)) {
            //Block will be object0 and Coconut Can will be object1 in Burlap
	    int number = (id.equals(block_id)) ? 0 : 1;
	    String object_class = "object";
            double x = obj.getPose().getPose().getPose().getPosition().getX();
            double y = obj.getPose().getPose().getPose().getPosition().getY();
            double z = obj.getPose().getPose().getPose().getPosition().getZ();

	    PickupAndPlaceDomain.setObject(s, number, x, y, z, "red");

	  }	
        }
	// Plan an action given the state of objects and the goal
	InRegionGoal gc = new InRegionGoal();
	gc.addGP(new GroundedProp(domain.getPropFunction(PickupAndPlaceDomain.PFINREGION), new String[]{"object0", "region4"}));
	gc.addGP(new GroundedProp(domain.getPropFunction(PickupAndPlaceDomain.PFINREGION), new String[]{"object1", "region8"}));
	TerminalFunction tf = new GoalConditionTF(gc);
	RewardFunction rf = new UniformCostRF();
	DiscretizingStateHashFactory hasingFactory = new DiscretizingStateHashFactory(30.);
	AStar planner = new AStar(domain, rf, gc, hasingFactory, new NullHeuristic());
	planner.planFromState(s);
	Policy p = new DDPlannerPolicy(planner);
	AbstractGroundedAction a = p.getAction(s);
	String action_string = a.toString();
	System.out.println(a.toString());

        // object0 is block, object1 is coconut_can
	// Object has fields: NAME and HASHID
	String object_name = action_string.substring(5,12);
	int obj_number = Integer.parseInt(""+object_name.charAt(6));
	ObjectInstance newobj_instance = s.getObjectsOfTrueClass("object").get(obj_number);
        moveObject new_obj = object_maker.newMessage();

	String real_object_name = (obj_number == 0) ? "block" : "coconut_can";
	String hash_id = (obj_number == 0) ? block_id : coconut_id;
	new_obj.setHashID(hash_id);
	new_obj.setName(real_object_name);
	
	/* Region has fields: SHAPE, SCALE, NAME and ORIGIN
	 * SHAPE is an enum field of the message
	 * NAME is a STRING 
         * SCALE is a geometry_msg/Vector3, which has fields: X,Y and Z
         * ORIGIN is a geometry_msg/Point, which has fields: X,Y and Z
         */
	String region_name = a.toString().substring(13);
	int region_number = Integer.parseInt(""+region_name.charAt(6));
        ObjectInstance region_obj = s.getObjectsOfTrueClass("region").get(region_number);
	moveRegion region = region_maker.newMessage();
	region.setName(region_name);

	region.setShape(region.SHAPE_SQUARE);
	
	Point origin = point_maker.newMessage();
	origin.setX(region_obj.getRealValForAttribute("top"));
	origin.setY(region_obj.getRealValForAttribute("left"));
	origin.setZ(region_obj.getRealValForAttribute("height"));
	region.setOrigin(origin);


	Vector3 scale = vector3_maker.newMessage();
	scale.setX(0.333);
	scale.setY(0.333);
	scale.setZ(0.2);	
	region.setScale(scale);

        /* MoveAction has a HEADER and fields: OBJECT, REGION
         * HEADER has a Frame_ID, which is the same as the ORK message FramID
         * OBJECT is our custom defined object message from above
         * REGION is our custom defined region message from above
         */ 
	moveAction action = moveAction_maker.newMessage();
	//action.setHeader();
	action.setObject(new_obj);
	action.setRegion(region);
	moveAction_maker.publish(action);
      }
    });
  }
}
