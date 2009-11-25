package robotframework.jdiameter;

public class AvpTypeResolver extends CodeResolver {

    public AvpTypeResolver() {
	super("avptype.properties");
    }

    public DataType getType(int code) {
	String name = String.valueOf(code);
	return DataType.valueOf(props.getProperty(name).trim());
    }

}
