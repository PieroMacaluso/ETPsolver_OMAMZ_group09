package it.polito.studenti.oma9;

import java.io.*;
import java.util.*;
import java.awt.*;
public class ObjectCloner
{
    /**
     * This is a class with a static method in order to do the clone with serialization
     */
    private ObjectCloner(){}

    /**
     * Returns a deep copy of the object
      * @param oldObj
     * @return new copy
     * @throws Exception
     */
    static public Object deepCopy(Object oldObj) throws Exception
    {
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try
        {
            ByteArrayOutputStream bos =
                    new ByteArrayOutputStream(); // A
            oos = new ObjectOutputStream(bos); // B
            // serialize and pass the object
            oos.writeObject(oldObj);   // C
            oos.flush();               // D
            ByteArrayInputStream bin =
                    new ByteArrayInputStream(bos.toByteArray()); // E
            ois = new ObjectInputStream(bin);                  // F
            // return the new object
            return ois.readObject(); // G
        }
        catch(Exception e)
        {
            System.out.println("Exception in ObjectCloner = " + e);
            throw(e);
        }
        finally
        {
            oos.close();
            ois.close();
        }
    }

}