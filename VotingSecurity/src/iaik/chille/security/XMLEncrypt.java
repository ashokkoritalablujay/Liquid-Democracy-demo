/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iaik.chille.security;

import com.sun.org.apache.xml.internal.security.encryption.EncryptedData;
import com.sun.org.apache.xml.internal.security.encryption.EncryptedKey;
import com.sun.org.apache.xml.internal.security.encryption.XMLCipher;
import com.sun.org.apache.xml.internal.security.keys.KeyInfo;
import com.sun.org.apache.xml.internal.security.utils.EncryptionConstants;
import java.security.Key;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author chille
 */
public class XMLEncrypt
{
  public static EncryptedKey encryptKey()
  {
    return null;
  }

  public static Document encryptAES(
          Key symmetricKey,
          Key keyEncryptionKey,
          Document document,
          Element elementToEncrypt,
          boolean encryptContentsOnly
          ) throws Exception
  {
    // initialize cipher
    XMLCipher keyCipher = XMLCipher.getInstance(XMLCipher.RSA_OAEP);
    keyCipher.init(XMLCipher.WRAP_MODE, keyEncryptionKey);

    // encrypt symmetric key
    EncryptedKey encryptedKey = keyCipher.encryptKey(document, symmetricKey);


    // xml
    XMLCipher xmlCipher = XMLCipher.getInstance(XMLCipher.AES_128);
    xmlCipher.init(XMLCipher.ENCRYPT_MODE, symmetricKey);

    // add key info to encrypted data element
    EncryptedData encryptedDataElement = xmlCipher.getEncryptedData();
    KeyInfo keyInfo = new KeyInfo(document);
    keyInfo.add(encryptedKey);
    encryptedDataElement.setKeyInfo(keyInfo);

    // do the actual encryption
    document = xmlCipher.doFinal(document,
                      elementToEncrypt,
                      encryptContentsOnly);

    return document;
  }

  public static Document decryptAES(
          Document document,
          Key keyEncryptionKey
          ) throws Exception
  {
      // get the encrypted data element
      String namespaceURI = EncryptionConstants.EncryptionSpecNS;
      String localName = EncryptionConstants._TAG_ENCRYPTEDDATA;
      Element encryptedDataElement =
         (Element)document.getElementsByTagNameNS(namespaceURI,
                                                  localName).item(0);

      // initialize cipher
      XMLCipher xmlCipher = XMLCipher.getInstance();
      xmlCipher.init(XMLCipher.DECRYPT_MODE, null);
      xmlCipher.setKEK(keyEncryptionKey);

      // do the actual decryption
      document = xmlCipher.doFinal(document, encryptedDataElement);

      return document;
  }

}