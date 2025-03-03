/*
 * HeapUtils.java
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cuprak.demo.heap;

import org.netbeans.lib.profiler.heap.FieldValue;
import org.netbeans.lib.profiler.heap.Instance;
import org.netbeans.lib.profiler.heap.ObjectArrayInstance;
import org.netbeans.lib.profiler.heap.PrimitiveArrayInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sample set of utilities for processing internals of some key JDK classes including
 * Strings and Lists.
 * @author Ryan Cuprak
 */
@SuppressWarnings("unchecked")
public class HeapUtils {

    /**
     * Processes a ConcurrentHashMap
     * @param instance - ConcurrentHashMap instance
     */
    public static Map<Instance, Instance> processConcurrentHashMap(Instance instance) {
        Map<Instance, Instance> rval = new HashMap<>();
        ObjectArrayInstance table = (ObjectArrayInstance) instance.getValueOfField("table");
        if (table != null) {
            List<Instance> tableValues = table.getValues();
            for (Instance tableValue : tableValues) {
                processMapTable(tableValue, rval);
            }
        }
        return rval;
    }

    private static void processMapTable(Instance node, Map<Instance, Instance> collect){
        if (node == null ){
            return;
        }
        Instance key = (Instance) node.getValueOfField("key");
        Instance val = (Instance) node.getValueOfField("val");
        Instance next = (Instance) node.getValueOfField("next");
        collect.put(key, val);
        if (next != null){
            processMapTable(next, collect);
        }
    }


    /**
     * Processes a LinkedList
     * @param instance - LinkedList instance
     */
    public void processLinkedList(Instance instance) {
        Instance listEntry = (Instance) instance.getValueOfField("first");
        int i = 0;
        while (listEntry != null) {
            i++;
            Object item = listEntry.getValueOfField("item");
            System.out.println("Type: " + item);
            // process the item in the list
            listEntry = (Instance) listEntry.getValueOfField("next");
        }
    }

    /**
     * Processes an ArrayList
     * @param instance - ArrayList instance
     * @return instance list
     */
    public List<Instance> processArrayList(Instance instance) {
        ObjectArrayInstance data =
                (ObjectArrayInstance) instance.getValueOfField("elementData");
        return data.getValues();
    }

    public static String processString(Instance instance) {
        if(instance.getValueOfField("value") instanceof PrimitiveArrayInstance) {
            PrimitiveArrayInstance pi =
                    (PrimitiveArrayInstance) instance.getValueOfField("value");
            if (pi != null) {
                List<Object> entries = pi.getValues();
                StringBuilder builder = new StringBuilder();
                for (Object obj : entries) {
                    if(obj instanceof Character) {
                        builder.append((char)obj);
                    } else if (obj instanceof Integer) {
                        int charCode = Integer.valueOf((String) obj);
                        builder.append(Character.toString((char) charCode));
                    } else if (obj instanceof String) {
                        int charCode = Integer.valueOf((String) obj);
                        builder.append(Character.toString((char) charCode));
                    }
                }
                return builder.toString();
            }
        } else {
            return instance.getValueOfField("value").toString();
        }
        return "null";
    }

    public static void dump(Instance instance) {
        System.err.println("Class is: " + instance.getJavaClass().getName());
        System.err.println("Static Fields are: ");
        List<FieldValue> staticFieldValues = instance.getStaticFieldValues();
        for (FieldValue fieldValue : staticFieldValues) {
            System.err.println("\t" + fieldValue.getField().getName()
                    + ": " + fieldValue.getValue()
                    + " (type: " + fieldValue.getField().getType().getName() + ")");
        }
        System.err.println("Fields are: ");
        List<FieldValue> fieldValues = instance.getFieldValues();
        for (FieldValue fieldValue : fieldValues) {
            System.err.println("\t" + fieldValue.getField().getName()
                    + ": " + fieldValue.getValue()
                    + " (type: " + fieldValue.getField().getType().getName() + ")");
        }
    }
}
