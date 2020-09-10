package mpinspector.mqttlearner.mqtt;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import mpinspector.mqttlearner.StateLearnerSUL;
import net.automatalib.words.impl.SimpleAlphabet;

public class MQTTSUL implements StateLearnerSUL<String, String>{
	
	SimpleAlphabet<String> alphabet;
	MQTTTestService mqtt1;
	/**************************************
	 *perform MQTT protocol
	 ******************************************/
	MQTTTestService mqtt;
	String type;  //used to distinguish mqttaws and mqttbosch
	public  String HOST;  
    public static final String TOPIC1 = "command///req/#";
    public static final String TOPIC2 = "temperature";
    private String clientid; 

    private String userName;    //not neccessary
    private String passWord;  //not neccessary
    
    String tlsversion;
    public String clientCrtFilePath;
    String caFilePATH;
	String ClientKeyFilePAth;
	
    
    public int timeout;
    public int aliveInterval;
    int delaytime = 5000;  //query delaytime
    
	String subTopic;
	String pubTopic;
	
	String version;  // mqtt version
	Logger logmqtt = Logger.getLogger("logmqtt.log");
	FileHandler fileHandler;
	
	
	void initLog() {
		try {
			fileHandler = new FileHandler("output_server\\logmqtt.log");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	fileHandler.setLevel(Level.INFO);
        fileHandler.setFormatter(new Formatter() {
        	SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss S");
            
            public String format(LogRecord record) {
            	return format.format(record.getMillis()) +" " + record.getSourceClassName() +" "+ record.getSourceMethodName() + "\n" + record.getLevel() + ": " +" " + record.getMessage() +"\n";
            }
        });
        
        logmqtt.addHandler(fileHandler);
        logmqtt.setUseParentHandlers(false);
	}
	
	
	public MQTTSUL(MQTTConfig config) throws Exception {
		type = config.type;  //distingusish mqttaws and mqttbosch
		
		alphabet = new SimpleAlphabet<String> (Arrays.asList(config.alphabet.split(" ")));
		HOST = config.host;
		timeout = config.timeout;
		delaytime = config.delaytime;
		aliveInterval = config.aliveInterval;
		clientid = config.clientId;
		userName = config.userName;
		passWord = config.passWord;
		
		tlsversion = config.tlsversion;
		clientCrtFilePath = config.clientCrtFilePath;
		caFilePATH = config.caFilePATH;
		ClientKeyFilePAth = config.ClientKeyFilePAth;
		

		subTopic = config.subTopic;
		pubTopic = config.pubTopic;
		
		mqtt1= new MQTTTestService();
		mqtt1.initLog();
		// target setting server or client
		mqtt1.setTarget(config.target);
		
		//host and port setting
		mqtt1.setHost(config.host);
		System.out.println("subtopic: in sul is" + subTopic);
		System.out.println("subtopic: in sul is" + tlsversion);
		
		mqtt1.setSubTopic(subTopic);
		mqtt1.setPubTopic(pubTopic);
		mqtt1.setPubMsg(config.pubMsg);
		
		mqtt1.setType(config.type);
		mqtt1.setUsername(config.userName);
		mqtt1.setPassword(config.passWord);
		mqtt1.setClientID(config.clientId);
		
		mqtt1.setTlsversion(tlsversion);
		mqtt1.setClientCrtFilePAth(config.clientCrtFilePath);
		mqtt1.setCaFilePATH(config.caFilePATH);
		mqtt1.setClientKeyFilePAth(config.ClientKeyFilePAth);
		
		mqtt1.setRequireRestart(config.restart);
		
		mqtt1.setTimeout(timeout);
		mqtt1.setAliveInterval(aliveInterval);
		
	
		
	}
	public SimpleAlphabet<String> getAlphabet() {
		return alphabet;
	}	

	
	
	// A step function, choose string from alphbet，the letter in the string matches output letter
	@Override
	public String step(String symbol) {
		String result = null;
		try {
			Thread.sleep(delaytime);
			logmqtt.info("**********************************Processing input " + symbol);
			result = mqtt1.processSymbol(symbol);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// override reset
	@Override
	public void pre() {
		try {
			mqtt1.reset();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void post() {
	}

	

}
