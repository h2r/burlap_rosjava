package move_msgs;

public interface moveAction extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "move_msgs/moveAction";
  static final java.lang.String _DEFINITION = "Header header\nmoveObject object\nmoveRegion region\n";
  std_msgs.Header getHeader();
  void setHeader(std_msgs.Header value);
  move_msgs.moveObject getObject();
  void setObject(move_msgs.moveObject value);
  move_msgs.moveRegion getRegion();
  void setRegion(move_msgs.moveRegion value);
}
