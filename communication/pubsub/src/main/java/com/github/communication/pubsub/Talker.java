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

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import burlap_msgs.*;
import java.util.List;
import java.util.ArrayList;

import burlap.oomdp.core.State;

/**
 * A simple {@link Publisher} {@link NodeMain}.
 */
public class Talker extends AbstractNodeMain {

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("rosjava/talker");
  }

  @Override
  public void onStart(final ConnectedNode connectedNode) {
    final Publisher<burlap_state> state_maker = connectedNode.newPublisher("chatter", burlap_state._TYPE);
    final Publisher<burlap_object> object_maker =
       	connectedNode.newPublisher("hatter", burlap_object._TYPE);
    final Publisher<burlap_value> val_maker = connectedNode.newPublisher("Chatter", burlap_value._TYPE);

    State s = new State();

    // This CancellableLoop will be canceled automatically when the node shuts
    // down.
    connectedNode.executeCancellableLoop(new CancellableLoop() {
      private int sequenceNumber;

      @Override
      protected void setup() {
        sequenceNumber = 0;
      }

      @Override
      protected void loop() throws InterruptedException {
	burlap_state state = state_maker.newMessage();
	List<burlap_object> objs = new ArrayList<burlap_object>();

        burlap_object obj0 = object_maker.newMessage();
	List<burlap_value> vals = new ArrayList<burlap_value>();
	
	burlap_value val01 = val_maker.newMessage();
	val01.setAttribute("color");
	val01.setValue("red");

	burlap_value val02 = val_maker.newMessage();
	val02.setAttribute("x");
	val02.setValue("20.0");

        burlap_value val03 = val_maker.newMessage();
	val03.setAttribute("y");
	val03.setValue("20.0");

	burlap_value val04 = val_maker.newMessage();
	val04.setAttribute("z");
	val04.setValue("20.0");

	vals.add(val01);
	vals.add(val02);
	vals.add(val03);
	vals.add(val04);
	
	obj0.setName("object0");
	obj0.setObjectClass("object");
	obj0.setValues(vals);
	objs.add(obj0);
	

        burlap_object obj1 = object_maker.newMessage();
	List<burlap_value> vals1= new ArrayList<burlap_value>();
	
	burlap_value val11 = val_maker.newMessage();
	val11.setAttribute("color");
	val11.setValue("green");

	burlap_value val12 = val_maker.newMessage();
	val12.setAttribute("x");
	val12.setValue("80.0");

        burlap_value val13 = val_maker.newMessage();
	val13.setAttribute("y");
	val13.setValue("20.0");

	burlap_value val14 = val_maker.newMessage();
	val14.setAttribute("z");
	val14.setValue("20.0");

	vals1.add(val11);
	vals1.add(val12);
	vals1.add(val13);
	vals1.add(val14);
	
	obj1.setName("object1");
	obj1.setObjectClass("object");
	obj1.setValues(vals1);
	objs.add(obj1);

        burlap_object obj2 = object_maker.newMessage();
	List<burlap_value> vals2 = new ArrayList<burlap_value>();
	
	burlap_value val21 = val_maker.newMessage();
	val21.setAttribute("color");
	val21.setValue("blue");

	burlap_value val22 = val_maker.newMessage();
	val22.setAttribute("x");
	val22.setValue("20.0");

        burlap_value val23 = val_maker.newMessage();
	val23.setAttribute("y");
	val23.setValue("20.0");

	burlap_value val24 = val_maker.newMessage();
	val24.setAttribute("z");
	val24.setValue("80.0");

	vals2.add(val21);
	vals2.add(val22);
	vals2.add(val23);
	vals2.add(val24);
	
	obj2.setName("object2");
	obj2.setObjectClass("object");
	obj2.setValues(vals);
	objs.add(obj2);


        burlap_object obj3 = object_maker.newMessage();
	List<burlap_value> vals3 = new ArrayList<burlap_value>();
	
	burlap_value val31 = val_maker.newMessage();
	val31.setAttribute("left");
	val31.setValue("0.0");

	burlap_value val32 = val_maker.newMessage();
	val32.setAttribute("height");
	val32.setValue("20.0");

        burlap_value val33 = val_maker.newMessage();
	val33.setAttribute("right");
	val33.setValue("33.3");

	burlap_value val34 = val_maker.newMessage();
	val34.setAttribute("top");
	val34.setValue("33.3");

	burlap_value val35 = val_maker.newMessage();
	val35.setAttribute("bottom");
	val35.setValue("0.00");

	vals3.add(val35);
	vals3.add(val31);
	vals3.add(val32);
	vals3.add(val33);
	vals3.add(val34);
	

	obj3.setName("region0");
	obj3.setObjectClass("region");
	obj3.setValues(vals3);
	objs.add(obj3);


        burlap_object obj4 = object_maker.newMessage();
	List<burlap_value> vals4 = new ArrayList<burlap_value>();
	
	burlap_value val41 = val_maker.newMessage();
	val41.setAttribute("left");
	val41.setValue("33.3");

	burlap_value val42 = val_maker.newMessage();
	val42.setAttribute("height");
	val42.setValue("20.0");

        burlap_value val43 = val_maker.newMessage();
	val43.setAttribute("right");
	val43.setValue("66.6");

	burlap_value val44 = val_maker.newMessage();
	val44.setAttribute("top");
	val44.setValue("33.3");

	burlap_value val45 = val_maker.newMessage();
	val45.setAttribute("bottom");
	val45.setValue("0.00");

	vals4.add(val45);
	vals4.add(val41);
	vals4.add(val42);
	vals4.add(val43);
	vals4.add(val44);
	
	obj4.setName("region1");
	obj4.setObjectClass("region");
	obj4.setValues(vals4);
	objs.add(obj4);



        burlap_object obj5 = object_maker.newMessage();
	List<burlap_value> vals5 = new ArrayList<burlap_value>();
	
	burlap_value val51 = val_maker.newMessage();
	val51.setAttribute("left");
	val51.setValue("66.6");

	burlap_value val52 = val_maker.newMessage();
	val52.setAttribute("height");
	val52.setValue("20.0");

        burlap_value val53 = val_maker.newMessage();
	val53.setAttribute("right");
	val53.setValue("100.0");

	burlap_value val54 = val_maker.newMessage();
	val54.setAttribute("top");
	val54.setValue("33.3");

	burlap_value val55 = val_maker.newMessage();
	val55.setAttribute("bottom");
	val55.setValue("0.00");

	vals5.add(val55);
	vals5.add(val51);
	vals5.add(val52);
	vals5.add(val53);
	vals5.add(val54);
	
	obj5.setName("region2");
	obj5.setObjectClass("region");
	obj5.setValues(vals5);
	objs.add(obj5);



        burlap_object obj6 = object_maker.newMessage();
	List<burlap_value> vals6 = new ArrayList<burlap_value>();
	
	burlap_value val61 = val_maker.newMessage();
	val61.setAttribute("left");
	val61.setValue("0.0");

	burlap_value val62 = val_maker.newMessage();
	val62.setAttribute("height");
	val62.setValue("20.0");

        burlap_value val63 = val_maker.newMessage();
	val63.setAttribute("right");
	val63.setValue("33.3");

	burlap_value val64 = val_maker.newMessage();
	val64.setAttribute("top");
	val64.setValue("66.6");

	burlap_value val65 = val_maker.newMessage();
	val65.setAttribute("bottom");
	val65.setValue("33.3");

	vals6.add(val65);
	vals6.add(val61);
	vals6.add(val62);
	vals6.add(val63);
	vals6.add(val64);
	
	obj6.setName("region3");
	obj6.setObjectClass("region");
	obj6.setValues(vals6);
	objs.add(obj6);



        burlap_object obj7 = object_maker.newMessage();
	List<burlap_value> vals7 = new ArrayList<burlap_value>();
	
	burlap_value val71 = val_maker.newMessage();
	val71.setAttribute("left");
	val71.setValue("33.3");

	burlap_value val72 = val_maker.newMessage();
	val72.setAttribute("height");
	val72.setValue("20.0");

        burlap_value val73 = val_maker.newMessage();
	val73.setAttribute("right");
	val73.setValue("66.6");

	burlap_value val74 = val_maker.newMessage();
	val74.setAttribute("top");
	val74.setValue("66.6");

	burlap_value val75 = val_maker.newMessage();
	val75.setAttribute("bottom");
	val75.setValue("33.3");

	vals7.add(val75);
	
	vals7.add(val71);
	vals7.add(val72);
	vals7.add(val73);
	vals7.add(val74);
	
	obj7.setName("region4");
	obj7.setObjectClass("region");
	obj7.setValues(vals7);
	objs.add(obj7);



        burlap_object obj8 = object_maker.newMessage();
	List<burlap_value> vals8 = new ArrayList<burlap_value>();
	
	burlap_value val81 = val_maker.newMessage();
	val81.setAttribute("left");
	val81.setValue("66.6");

	burlap_value val82 = val_maker.newMessage();
	val82.setAttribute("height");
	val82.setValue("20.0");

        burlap_value val83 = val_maker.newMessage();
	val83.setAttribute("right");
	val83.setValue("100.0");

	burlap_value val84 = val_maker.newMessage();
	val84.setAttribute("top");
	val84.setValue("66.6");
	burlap_value val85 = val_maker.newMessage();
	val85.setAttribute("bottom");
	val85.setValue("33.3");

	vals8.add(val85);
	
	vals8.add(val81);
	vals8.add(val82);
	vals8.add(val83);
	vals8.add(val84);
	
	obj8.setName("region5");
	obj8.setObjectClass("region");
	obj8.setValues(vals8);
	objs.add(obj8);


        burlap_object obj9 = object_maker.newMessage();
	List<burlap_value> vals9 = new ArrayList<burlap_value>();
	
	burlap_value val91 = val_maker.newMessage();
	val91.setAttribute("left");
	val91.setValue("0.0");

	burlap_value val92 = val_maker.newMessage();
	val92.setAttribute("height");
	val92.setValue("20.0");

        burlap_value val93 = val_maker.newMessage();
	val93.setAttribute("right");
	val93.setValue("33.3");

	burlap_value val94 = val_maker.newMessage();
	val94.setAttribute("top");
	val94.setValue("100.0");

	vals9.add(val91);
	vals9.add(val92);
	vals9.add(val93);
	vals9.add(val94);
		burlap_value val95 = val_maker.newMessage();
	val95.setAttribute("bottom");
	val95.setValue("66.6");

	vals9.add(val95);
	
	obj9.setName("region6");
	obj9.setObjectClass("region");
	obj9.setValues(vals9);
	objs.add(obj9);

        burlap_object obj10 = object_maker.newMessage();
	List<burlap_value> vals10 = new ArrayList<burlap_value>();
	
	burlap_value val101 = val_maker.newMessage();
	val101.setAttribute("left");
	val101.setValue("33.3");

	burlap_value val102 = val_maker.newMessage();
	val102.setAttribute("height");
	val102.setValue("20.0");

        burlap_value val103 = val_maker.newMessage();
	val103.setAttribute("right");
	val103.setValue("66.6");

	burlap_value val104 = val_maker.newMessage();
	val104.setAttribute("top");
	val104.setValue("100.0");

	vals10.add(val101);
	vals10.add(val102);
	vals10.add(val103);
	vals10.add(val104);
		burlap_value val105 = val_maker.newMessage();
	val105.setAttribute("bottom");
	val105.setValue("66.6");

	vals10.add(val105);
	
	obj10.setName("region7");
	obj10.setObjectClass("region");
	obj10.setValues(vals9);
	objs.add(obj10);

        burlap_object obj11 = object_maker.newMessage();
	List<burlap_value> vals11 = new ArrayList<burlap_value>();
	
	burlap_value val111 = val_maker.newMessage();
	val111.setAttribute("left");
	val111.setValue("66.6");

	burlap_value val112 = val_maker.newMessage();
	val112.setAttribute("height");
	val112.setValue("20.0");

        burlap_value val113 = val_maker.newMessage();
	val113.setAttribute("right");
	val113.setValue("100.0");

	burlap_value val114 = val_maker.newMessage();
	val114.setAttribute("top");
	val114.setValue("100.0");

	burlap_value val115 = val_maker.newMessage();
	val115.setAttribute("bottom");
	val115.setValue("66.6");

	vals11.add(val115);
	
	vals11.add(val111);
	vals11.add(val112);
	vals11.add(val113);
	vals11.add(val114);
	
	obj11.setName("region8");
	obj11.setObjectClass("region");
	obj11.setValues(vals11);
	objs.add(obj11);


	state.setObjects(objs);
        state_maker.publish(state);
        sequenceNumber++;
        Thread.sleep(2000);
      }
    });
  }
}
