import java.io.*;
import java.net.URLEncoder;

import com.sap.aii.mapping.api.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JM01 extends AbstractTransformation {
	private static final String AND_FEED = "&";

	public void transform(TransformationInput inStream, TransformationOutput outStream)
			throws StreamTransformationException {
		AbstractTrace trace = (AbstractTrace) getTrace();
		trace.addInfo("attachment mapping started");
		String body = "";

		String timestamp = "";
		String appKey = "";
		String signature = "";
		String version = "";
		String requestBody = "";
		String traceID = "";
		String groupID = "";
		String shopID = "";

		// parse input stream
		InputStream inputstream = inStream.getInputPayload().getInputStream();
		DocumentBuilderFactory docBuildFactory = new com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl();
		DocumentBuilder docBuilder;
		try {
			// get data from input xml
			docBuilder = docBuildFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(inputstream);
			NodeList oElementTimestamp = doc.getElementsByTagName("timestamp");
			NodeList oElementAppKey = doc.getElementsByTagName("appKey");
			NodeList oElementSignature = doc.getElementsByTagName("signature");
			NodeList oElementVersion = doc.getElementsByTagName("version");
			NodeList oElementRequestBody = doc.getElementsByTagName("requestBody");
			NodeList oElementTraceID = doc.getElementsByTagName("traceID");
			NodeList oElementGroupID = doc.getElementsByTagName("groupID");
//			NodeList oElementShopID = doc.getElementsByTagName("shopID");

			timestamp = oElementTimestamp.item(0).getTextContent();
			appKey = oElementAppKey.item(0).getTextContent();
			signature = oElementSignature.item(0).getTextContent();
			version = oElementVersion.item(0).getTextContent();
			requestBody = oElementRequestBody.item(0).getTextContent();
			traceID = oElementTraceID.item(0).getTextContent();
			groupID = oElementGroupID.item(0).getTextContent();
//			shopID = oElementShopID.item(0).getTextContent();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			trace.addWarning("ERROR" + e1.getMessage());
		} catch (SAXException | IOException e1) {
			e1.printStackTrace();
			trace.addWarning("ERROR" + e1.getMessage());
		}
		// set dynamic conf
		String namespace = "http://sap.com/xi/XI/System/REST";
		DynamicConfiguration DynConfig = inStream.getDynamicConfiguration();
		DynamicConfigurationKey key1 = DynamicConfigurationKey.create(namespace, "XTRACE_ID"); // for header
		DynConfig.put(key1, traceID);
		DynamicConfigurationKey key2 = DynamicConfigurationKey.create(namespace, "XGROUP_ID");// for header
		DynConfig.put(key2, groupID);
		DynamicConfigurationKey key3 = DynamicConfigurationKey.create(namespace, "XSHOP_ID");// for header
		DynConfig.put(key3, shopID);
		// set payload
		body = this.addKey("timestamp", timestamp, body);
		body = this.addKey("appKey", appKey, body);
		body = this.addKey("signature", signature, body);
		body = this.addKey("version", version, body);
		body = this.addKey("requestBody", requestBody, body);

		// write to output stream
		try {
			OutputStream outputstream = outStream.getOutputPayload().getOutputStream();
			outputstream.write(body.getBytes());
			trace.addInfo("attachment mapping ended");
		} catch (IOException e) {
			throw new StreamTransformationException(e.getMessage());
		}
	}

	@SuppressWarnings("deprecation")
	private String addKey(final String key, final String value, final String body) {
		final String body_c;
		String e_value = URLEncoder.encode(value);
		if (body == "") {
			body_c = key + "=" + e_value;
		} else {
			body_c = body + AND_FEED + key + "=" + e_value;
		}
		return body_c;
	}
}
