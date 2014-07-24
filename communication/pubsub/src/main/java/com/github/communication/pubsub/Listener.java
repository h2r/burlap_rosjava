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
import java.util.AbstractMap;
import java.util.HashMap;

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

import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.RemoteException;
import org.ros.exception.ServiceNotFoundException;
import object_recognition_msgs.GetObjectInformation.*;

/**
 * A simple {@link Subscriber} {@link NodeMain}.
 */
public class Listener extends AbstractNodeMain {
  Domain domain;
  PickupAndPlaceDomain gen;
  String object_class, region_class, block_id, coconut_id;
  Subscriber<RecognizedObjectArray> subscriber;
  Publisher<moveObject> object_maker;
  Publisher<moveRegion> region_maker;
  Publisher<moveAction> moveAction_maker;
  Publisher<Point> point_maker;
  Publisher<Vector3> vector3_maker;


  public Listener() {
    PickupAndPlaceDomain gen = new PickupAndPlaceDomain(true);
    domain = gen.generateDomain();
    region_class = "region";
    object_class = "object";
  }
  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("rosjava/listener");
  }

  public void onStart(ConnectedNode connectedNode) {
    final Log log = connectedNode.getLog();
    Subscriber<RecognizedObjectArray> subscriber = connectedNode.newSubscriber("/recognized_object_array", RecognizedObjectArray._TYPE);

    final Publisher<moveObject> object_maker = connectedNode.newPublisher("move_objects", moveObject._TYPE);
    final Publisher<moveRegion> region_maker = connectedNode.newPublisher("move_region", moveRegion._TYPE);
    final Publisher<moveAction> moveAction_maker = connectedNode.newPublisher("move_Actions",moveAction._TYPE);

    final Publisher<Point> point_maker = connectedNode.newPublisher("PointMaker", Point._TYPE);
    final Publisher<Vector3> vector3_maker = connectedNode.newPublisher("Vector3Maker", Vector3._TYPE);

    final ServiceClient<GetObjectInformationRequest, GetObjectInformationResponse> serviceClient;
	try {
		serviceClient = connectedNode.newServiceClient("get_object_info", GetObjectInformation._TYPE);
	} catch (ServiceNotFoundException e) {
		throw new RosRuntimeException(e);
	}

    subscriber.addMessageListener(new MessageListener<RecognizedObjectArray>() {
      @Override
      public void onNewMessage(RecognizedObjectArray array) {

      	// Maps object ID to real object name
		final AbstractMap<String, String> object_map = new HashMap<String, String>();

		// maps objectInstance name (object0, object1...) to object ID.
		final AbstractMap<String, String> objectInstance_map = new HashMap<String, String>();

		// Iterate once to see how many disctinct objects ORK has recognized.
		for (final RecognizedObject obj : array.getObjects()) {
		  object_map.put(obj.getType().getKey(), null);
		}

      	if (object_map.size() == 0) {
      		System.out.println("ORK recognized no objects, planning not possible!");
      		return;
      	}

		// getCleanState takes in a domain, number of distint objects in state, and number of regions 
		// in the state
		final State s = gen.getCleanState(domain, object_map.size(), 9);
		// Splits off the table into 9 regions (3x3).
		PickupAndPlaceDomain.tileRegions(s, 3, 3, 0, 100., 0, 100, 20);

		for (final RecognizedObject obj : array.getObjects()) {
		  final String id = obj.getType().getKey();
		  final GetObjectInformationRequest request = serviceClient.newMessage();
		  request.setType(obj.getType());

		  try {
		  	serviceClient.call(request, new ServiceResponseListener<GetObjectInformationResponse>() {
		  		public void onSuccess(GetObjectInformationResponse response) {
		  			String name = response.getInformation().getName();
		  			System.out.println("Object recognized " + name);
		  			addObjectToDomain(s, obj, name, object_map.size(), objectInstance_map, id);
		  			object_map.put(id, name);
		  		}

		  		public void onFailure(RemoteException e) {
		  			throw new RosRuntimeException(e);
		  		}
		  	});
		  } catch (RosRuntimeException e) {
		  	throw new RosRuntimeException(e);
		  }
	    }
	    // wait for all of my name/id pairs to have been added
	    while (object_map.values().contains(null)) { }

	    InRegionGoal gc = new InRegionGoal();
		String domain_object = (object_map.size() > 1) ? "object1" : "object0";
		System.out.println("Goal object: " + domain_object);
		gc.addGP(new GroundedProp(domain.getPropFunction(PickupAndPlaceDomain.PFINREGION), new String[]{domain_object, "region8"}));

		TerminalFunction tf = new GoalConditionTF(gc);
		RewardFunction rf = new UniformCostRF();
		DiscretizingStateHashFactory hashingFactory = new DiscretizingStateHashFactory(30.);
		AStar planner = new AStar(domain, rf, gc, hashingFactory, new NullHeuristic());
		planner.planFromState(s);
		Policy p = new DDPlannerPolicy(planner);
		AbstractGroundedAction a = p.getAction(s);

		String action_string = a.toString();
		System.out.println(a.toString());
		action_string.split(" ");
		String[] action_split = action_string.split(" ");
		String object_name = action_split[1];
		String region_name = action_split[2];

		// Object has fields: NAME and HASHID
		int obj_number = Integer.parseInt(""+object_name.charAt(object_name.length()-1));
		ObjectInstance newobj_instance = s.getObjectsOfTrueClass(object_class).get(obj_number);
		String hash_id = objectInstance_map.get(object_name);
		String real_object_name = object_map.get(hash_id);
		System.out.println("Real object name: " + real_object_name);
		moveObject new_obj = object_maker.newMessage();
		new_obj.setHashID(hash_id);
		new_obj.setName(real_object_name);
		
		/* Region has fields: SHAPE, SCALE, NAME and ORIGIN
		 * SHAPE is an enum field of the message
		 * NAME is a STRING 
         * SCALE is a geometry_msg/Vector3, which has fields: X,Y and Z
         * ORIGIN is a geometry_msg/Point, which has fields: X,Y and Z
         */
		int region_number = Integer.parseInt(""+region_name.charAt(region_name.length()-1));
	    ObjectInstance region_obj = s.getObjectsOfTrueClass(region_class).get(region_number);
		
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
		action.setObject(new_obj);
		action.setRegion(region);
		moveAction_maker.publish(action);

		// return here to avoid planning twice. A race condition occurs when ork recognizes
		// two objects as the same object, and thus it can possibly try to plan twice.
		return;
      }
    });


  }

  public void addObjectToDomain(State s, RecognizedObject obj, String name, int disctinct_objects,
  	AbstractMap<String, String> objectInstance_map, String id) {
  	int number = (name.equals("coconut")) ? 0 : 1;
  	// If there's only one recognized object, then either object should have number 0, 
    // to avoid out of bound errors.
    number = (disctinct_objects == 1) ? 0 : number;
    objectInstance_map.put("object"+number, id);

    double x = obj.getPose().getPose().getPose().getPosition().getX();
    double y = obj.getPose().getPose().getPose().getPosition().getY();
    double z = obj.getPose().getPose().getPose().getPosition().getZ();

    // Red is default color, which isn't currently used at all. x,y,z are multiplied
    // by 100 to convert from meters -> centimeters.
    PickupAndPlaceDomain.setObject(s, number, x*100, y*100, z*100, "red");
  }
}
