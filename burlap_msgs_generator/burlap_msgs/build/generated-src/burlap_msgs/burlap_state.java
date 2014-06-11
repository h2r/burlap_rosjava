package burlap_msgs;

public interface burlap_state extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "burlap_msgs/burlap_state";
  static final java.lang.String _DEFINITION = "burlap_object[] objects\n";
  java.util.List<burlap_msgs.burlap_object> getObjects();
  void setObjects(java.util.List<burlap_msgs.burlap_object> value);
}
