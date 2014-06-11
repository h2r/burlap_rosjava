package move_msgs;

public interface moveObject extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "move_msgs/moveObject";
  static final java.lang.String _DEFINITION = "string hashID\nstring name\n";
  java.lang.String getHashID();
  void setHashID(java.lang.String value);
  java.lang.String getName();
  void setName(java.lang.String value);
}
