package com.example.demo.mpesa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
//import org.bson.internal.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateException;
import javax.crypto.NoSuchPaddingException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.UUID;
import java.nio.charset.StandardCharsets;

@Slf4j
public class HelperUtility {

    /**
     * @param value the value to be converted to a base64 string
     * @return returns base64String
     */
    public static String toBase64String(String value) {
        byte[] data = value.getBytes(StandardCharsets.ISO_8859_1);
        return Base64.getEncoder().encodeToString(data);
    }

    public static String toJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException exception) {
            return null;
        }
    }

    @SneakyThrows
    public static String getSecurityCredentials(String initiatorPassword) {
        String encryptedPassword;

        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            byte[] input = initiatorPassword.getBytes();

            Resource resource = new ClassPathResource("SandboxCertificate.cer");
            InputStream inputStream = resource.getInputStream();

            FileInputStream fin = new FileInputStream(resource.getFile());
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(fin);
            PublicKey pk = certificate.getPublicKey();
            cipher.init(Cipher.ENCRYPT_MODE, pk);

            byte[] cipherText = cipher.doFinal(input);

            // Convert the resulting encrypted byte array into a string using base64 encoding
            encryptedPassword = Base64.getEncoder().encodeToString(cipherText).trim();
            return encryptedPassword;
        } catch (NoSuchAlgorithmException | CertificateException | InvalidKeyException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException | NoSuchProviderException | FileNotFoundException e) {
            log.error(String.format("Error generating security credentials ->%s", e.getLocalizedMessage()));
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String generateRandomString () {
         
        return UUID.randomUUID().toString();

    }


    }
