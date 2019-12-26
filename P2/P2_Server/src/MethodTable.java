import java.io.Serializable;

public class MethodTable implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String threadName;
	private int id = -1;
	private String methodName;
	int count;
	
	//threads of clock and speaker use this constructor because each of speaker and clock is only one.
	public MethodTable(String threadName, String methodName, int count) {
		this.threadName = threadName;
		this.methodName = methodName;
		this.count = count;
	}
	
	
	//Visitor's thread should use this constructor because visitors are more than one.
	public MethodTable(String threadName, int id, String methodName, int count) {
		this.threadName = threadName;
		this.id = id;
		this.methodName = methodName;
		this.count = count;
	}
	
	public String getThreadName() {
		return threadName;
	}
	
	public int getID() {
		return id;
	}
	
	public String getMethodName() {
		return methodName;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public int getCount() {
		return count;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
}