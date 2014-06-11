package burlap_msgs;

public interface burlap_object extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "burlap_msgs/burlap_object";
  static final java.lang.String _DEFINITION = "string name\nstring object_class\nburlap_value[] values\n";
  java.lang.String getName();
  void setName(java.lang.String value);
  java.lang.String getObjectClass();
  void setObjectClass(java.lang.String value);
  java.util.List<burlap_msgs.burlap_value> getValues();
  void setValues(java.util.List<burlap_msgs.burlap_value> value);
}
