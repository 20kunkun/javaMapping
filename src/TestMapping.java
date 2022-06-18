import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import com.sap.aii.mapping.api.StreamTransformationException;

public class TestMapping {

	public static void main(String[] args) throws StreamTransformationException, FileNotFoundException,
			TransformerConfigurationException, TransformerFactoryConfigurationError {
		
		FileInputStream fin = new FileInputStream("C:\\Users\\hp\\Desktop\\PO映射\\test.xml");
		FileOutputStream fout = new FileOutputStream("C:\\Users\\hp\\Desktop\\PO映射\\test.txt");
		
		ZC01 zc01 = new ZC01();
		zc01.execute(fin, fout);
		
	}

}
