import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import com.sap.aii.mapping.api.AbstractTrace;
import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;

public class ZC01 extends AbstractTransformation {

	public void transform(TransformationInput in, TransformationOutput out) throws StreamTransformationException {
		this.execute(in.getInputPayload().getInputStream(), out.getOutputPayload().getOutputStream());
	}

	public void execute(InputStream arg0, OutputStream arg1) throws StreamTransformationException {
		AbstractTrace trace = (AbstractTrace) getTrace();
		//trace.addInfo("----转换开始-----");
		try {
			String output = "";
			String fileContent = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(arg0));
			StringBuilder out = new StringBuilder();
			String line = reader.readLine();
			while (line != null) {
				out.append(line + "\n");
				line = reader.readLine();
			}
			fileContent = out.toString();
			System.out.println("转换前" + fileContent);
			JSONObject jObj = XML.toJSONObject(fileContent);
			jObj = jObj.getJSONObject("ns0:mt_zc01");
			this.chechKey(jObj, 1);
			JSONArray arr1 = this.arrayHaddler(jObj.get("item"));
			for (int i = 0; i < arr1.length(); i++) {
				JSONObject jso = arr1.getJSONObject(i);
				this.chechKey(jso, 2);
				JSONArray arr2 = this.arrayHaddler(jso.get("families"));
				jso.put("families", arr2);
				arr1.put(i, jso);
			}
			JSONObject jo1 = new JSONObject();
			jo1.put("sender", jObj.getString("sender"));
			jo1.put("receiver", jObj.getString("receiver"));
			jo1.put("traceid", jObj.getString("traceid"));
			JSONArray outArray = new JSONArray();
			outArray.put(jObj.getInt("ztype"));
			outArray.put(jObj.getString("zchar"));
			outArray.put(jo1);
			outArray.put(arr1);
			output = outArray.toString();
			System.out.println("转换后" + output);
			arg1.write(output.getBytes());
		} catch (Exception e) {
			trace.addInfo("错误" + e.getMessage());
			throw new StreamTransformationException(e.toString());
		}
		//trace.addInfo("----转换结束-----");
	}

	public void chechKey(JSONObject chObj, int type) {
		switch (type) {
		case 1:
			this.setObject(chObj, "item", "");
			this.setObject(chObj, "sender", "");
			this.setObject(chObj, "receiver", "");
			this.setObject(chObj, "traceid", "");
			this.setObject(chObj, "ztype", 0);
			this.setObject(chObj, "zchar", "");
			break;
		case 2:
			this.setObject(chObj, "families", "");
			break;
		}
	}

	public void setObject(JSONObject sobj, String key, Object value) {
		@SuppressWarnings("unused")
		Object oct;
		try {
			oct = sobj.get(key);
		} catch (Exception e) {
			sobj.put(key, value);
		}
	}

	public JSONArray arrayHaddler(Object jso) {
		String str = "";
		if (jso != null) {
			str = jso.toString();
			if (str != "") {
				String substr = str.substring(0, 1);
				if (!"[".equals(substr)) {
					str = "[" + str + "]";
				}
			} else {
				str = "[]";
			}
		}
		return new JSONArray(str);
	}
	
/*
转换前XML:
<?xml version="1.0" encoding="UTF-8"?>
<ns0:mt_zc01 xmlns:ns0="http://www.ijovo.com/mm">
   <sender>ZCCG</sender>
   <receiver>SAP</receiver>
   <traceid>uuid238000</traceid>
   <ztype>2</ztype>
   <zchar>测试</zchar>
   <item>
      <code>111</code>
      <name>西瓜</name>
      <sex>女</sex>
      <families>
         <relete>父亲</relete>
         <age>51</age>
         <work>董事长</work>
      </families>
      <families>
         <relete>母亲</relete>
         <age>46</age>
         <work>财务总监</work>
      </families>
   </item>
</ns0:mt_zc01>

转换后json:
[
    2,
    "测试",
    {
        "traceid": "uuid238000",
        "receiver": "SAP",
        "sender": "ZCCG"
    },
    [
        {
            "code": 111,
            "sex": "女",
            "name": "西瓜",
            "families": [
                {
                    "work": "董事长",
                    "relete": "父亲",
                    "age": 51
                },
                {
                    "work": "财务总监",
                    "relete": "母亲",
                    "age": 46
                }
            ]
        }
    ]
]
*/

}
