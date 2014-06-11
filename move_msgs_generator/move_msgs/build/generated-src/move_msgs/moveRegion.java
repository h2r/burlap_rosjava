package move_msgs;

public interface moveRegion extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "move_msgs/moveRegion";
  static final java.lang.String _DEFINITION = "string name\ngeometry_msgs/Vector3 scale\ngeometry_msgs/Point origin\nuint8 shape\nuint8 SHAPE_SQUARE=0\nuint8 SHAPE_CIRCLE=1\n";
  static final byte SHAPE_SQUARE = 0;
  static final byte SHAPE_CIRCLE = 1;
  java.lang.String getName();
  void setName(java.lang.String value);
  geometry_msgs.Vector3 getScale();
  void setScale(geometry_msgs.Vector3 value);
  geometry_msgs.Point getOrigin();
  void setOrigin(geometry_msgs.Point value);
  byte getShape();
  void setShape(byte value);
}
