package Program.Common;

import Program.Common.DataClasses.Transporter;

import java.io.*;

public class    Serializer {
    public byte[] serialize(Transporter object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        oos.writeObject(object);
        oos.flush();
        oos.close();

        return bos.toByteArray();
    }

    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        Object result;
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bis);

        result = ois.readObject();
        ois.close();

        return result;
    }
}
